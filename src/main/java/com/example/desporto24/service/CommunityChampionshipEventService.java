package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.community.CommunityChampionshipEvent;
import com.example.desporto24.model.community.CommunityChampionship;
import com.example.desporto24.model.community.CommunityChampionshipEventTeam;
import com.example.desporto24.model.community.CommunityChampionshipCupBye;
import com.example.desporto24.model.community.CommunityChampionshipTeam;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityChampionshipRepository;
import com.example.desporto24.repository.community.CommunityChampionshipEventRepository;
import com.example.desporto24.repository.community.CommunityChampionshipEventTeamRepository;
import com.example.desporto24.repository.community.CommunityChampionshipTeamRepository;
import com.example.desporto24.repository.community.CommunityChampionshipFixtureRepository;
import com.example.desporto24.repository.community.CommunityChampionshipCupByeRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Separate competition identities owned by the championship.
 * These draft competitions do not reuse community_tournament records or
 * count towards standings until their own fixtures/results engine is implemented.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityChampionshipEventService {
    private static final Set<String> FORMATS = Set.of("LEAGUE", "CUP", "SUPERCUP", "OTHER");

    private final CommunityChampionshipRepository championships;
    private final CommunityChampionshipEventRepository events;
    private final CommunityChampionshipEventTeamRepository eventTeams;
    private final CommunityChampionshipFixtureRepository fixtures;
    private final CommunityChampionshipCupByeRepository cupByes;
    private final CommunityChampionshipTeamRepository championshipTeams;
    private final CommunityMemberRepository members;
    private final ProfileRepository profiles;

    public record CreateEvent(String name, String format) {}
    public record Event(Long id, Long championshipId, String name, String format, String status, Instant createdAt) {}
    public record TeamRegistration(Long id, Long championshipTeamId, String name) {}

    @Transactional(readOnly = true)
    public List<Event> list(Long communityId, Long championshipId, String username) {
        requireMember(communityId, username, false);
        getChampionship(communityId, championshipId);
        return events.findByChampionshipIdOrderByCreatedAtAscIdAsc(championshipId)
                .stream().map(this::response).toList();
    }

    public Event create(Long communityId, Long championshipId, String username, CreateEvent request) {
        requireMember(communityId, username, true);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        ensureOpen(championship);
        validateRequest(request);

        return response(events.save(CommunityChampionshipEvent.builder()
                .championship(championship)
                .name(request.name().trim())
                .format(request.format())
                .status("DRAFT")
                .createdAt(Instant.now())
                .build()));
    }

    public Event update(Long communityId, Long championshipId, Long eventId,
                        String username, CreateEvent request) {
        requireMember(communityId, username, true);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        ensureOpen(championship);
        validateRequest(request);
        CommunityChampionshipEvent event = lockOwnedEvent(championshipId, eventId);
        ensureDraft(event);
        event.setName(request.name().trim());
        event.setFormat(request.format());
        return response(events.save(event));
    }

    public void delete(Long communityId, Long championshipId, Long eventId, String username) {
        requireMember(communityId, username, true);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        ensureOpen(championship);
        CommunityChampionshipEvent event = lockOwnedEvent(championshipId, eventId);
        ensureDraft(event);
        // A draft can be withdrawn together with its own registrations.
        // Championship-season team identities remain intact for other competitions.
        eventTeams.deleteAll(eventTeams.findByEventIdOrderByIdAsc(eventId));
        events.delete(event);
    }

    @Transactional(readOnly = true)
    public List<TeamRegistration> teams(Long communityId, Long championshipId,
                                        Long eventId, String username) {
        requireMember(communityId, username, false);
        getChampionship(communityId, championshipId);
        getOwnedEvent(championshipId, eventId);
        return eventTeams.findByEventIdOrderByIdAsc(eventId).stream()
                .map(this::teamResponse).toList();
    }

    public TeamRegistration addTeam(Long communityId, Long championshipId, Long eventId,
                                    Long seasonTeamId, String username) {
        requireMember(communityId, username, true);
        ensureOpen(getChampionship(communityId, championshipId));
        CommunityChampionshipEvent event = lockOwnedEvent(championshipId, eventId);
        ensureDraft(event);
        CommunityChampionshipTeam team = championshipTeams.findById(seasonTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Equipa não encontrada no campeonato"));
        if (!championshipId.equals(team.getChampionship().getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A equipa pertence a outro campeonato");
        if (eventTeams.existsByEventIdAndTeamId(eventId, seasonTeamId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Equipa já inscrita nesta prova");
        try {
            return teamResponse(eventTeams.saveAndFlush(CommunityChampionshipEventTeam.builder()
                    .event(event).team(team).build()));
        } catch (org.springframework.dao.DataIntegrityViolationException conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Equipa já inscrita nesta prova", conflict);
        }
    }

    public void removeTeam(Long communityId, Long championshipId, Long eventId,
                           Long seasonTeamId, String username) {
        requireMember(communityId, username, true);
        ensureOpen(getChampionship(communityId, championshipId));
        ensureDraft(lockOwnedEvent(championshipId, eventId));
        CommunityChampionshipEventTeam entry = eventTeams.findByEventIdAndTeamId(eventId, seasonTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Inscrição não encontrada nesta prova"));
        eventTeams.delete(entry);
    }

    public record LeagueFixture(Long id, int round, int match,
                                Long homeTeamId, String homeTeamName,
                                Long awayTeamId, String awayTeamName) {}

    @Transactional(readOnly = true)
    public List<LeagueFixture> fixtures(Long communityId, Long championshipId,
                                        Long eventId, String username) {
        requireMember(communityId, username, false);
        getChampionship(communityId, championshipId);
        getOwnedEvent(championshipId, eventId);
        return fixtures.findByEventIdOrderByRoundNumberAscMatchNumberAsc(eventId)
                .stream().map(this::fixtureResponse).toList();
    }

    public List<LeagueFixture> scheduleLeague(Long communityId, Long championshipId,
                                               Long eventId, int legs, String username) {
        requireMember(communityId, username, true);
        ensureOpen(getChampionship(communityId, championshipId));
        CommunityChampionshipEvent event = events.lockForScheduling(eventId, championshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Liga não encontrada neste campeonato"));
        if (!"LEAGUE".equals(event.getFormat()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Só as ligas podem gerar um calendário de jornadas");
        ensureDraft(event);
        if (fixtures.existsByEventId(eventId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este calendário já foi gerado");
        List<CommunityChampionshipEventTeam> registrations = eventTeams.findByEventIdOrderByIdAsc(eventId);
        var byId = new java.util.HashMap<Long, com.example.desporto24.model.community.CommunityChampionshipTeam>();
        for (CommunityChampionshipEventTeam registration : registrations) {
            var team = registration.getTeam();
            if (!championshipId.equals(team.getChampionship().getId()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Inscrição de equipa inconsistente com o campeonato");
            if (byId.putIfAbsent(team.getId(), team) != null)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Equipa repetida na liga");
        }
        final List<ChampionshipLeagueScheduler.Match> schedule;
        try {
            schedule = ChampionshipLeagueScheduler.generate(
                    new java.util.ArrayList<>(byId.keySet()), legs);
        } catch (IllegalArgumentException invalid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalid.getMessage());
        }
        var saved = new java.util.ArrayList<LeagueFixture>(schedule.size());
        for (ChampionshipLeagueScheduler.Match match : schedule) {
            var fixture = fixtures.save(com.example.desporto24.model.community.CommunityChampionshipFixture.builder()
                    .event(event).roundNumber(match.round()).matchNumber(match.match())
                    .homeTeam(byId.get(match.homeTeamId()))
                    .awayTeam(byId.get(match.awayTeamId())).build());
            saved.add(fixtureResponse(fixture));
        }
        event.setStatus("SCHEDULED");
        events.saveAndFlush(event);
        return List.copyOf(saved);
    }

    public record CupBye(Long id, int slot, Long teamId, String teamName) {}
    public record CupOpeningRound(List<LeagueFixture> matches, List<CupBye> byes) {}

    @Transactional(readOnly = true)
    public CupOpeningRound cupOpeningRound(Long communityId, Long championshipId,
                                           Long eventId, String username) {
        requireMember(communityId, username, false);
        getChampionship(communityId, championshipId);
        CommunityChampionshipEvent event = getOwnedEvent(championshipId, eventId);
        if (!"CUP".equals(event.getFormat()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A prova não é uma taça");
        return new CupOpeningRound(
                fixtures.findByEventIdOrderByRoundNumberAscMatchNumberAsc(eventId)
                        .stream().map(this::fixtureResponse).toList(),
                cupByes.findByEventIdOrderBySlotNumberAsc(eventId).stream().map(this::cupByeResponse).toList());
    }

    public CupOpeningRound drawCup(Long communityId, Long championshipId,
                                   Long eventId, String username) {
        requireMember(communityId, username, true);
        ensureOpen(getChampionship(communityId, championshipId));
        CommunityChampionshipEvent event = lockOwnedEvent(championshipId, eventId);
        if (!"CUP".equals(event.getFormat()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A prova não é uma taça");
        ensureDraft(event);
        if (fixtures.existsByEventId(eventId) || cupByes.existsByEventId(eventId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O sorteio desta taça já existe");

        var byId = new java.util.HashMap<Long, CommunityChampionshipTeam>();
        for (CommunityChampionshipEventTeam registration : eventTeams.findByEventIdOrderByIdAsc(eventId)) {
            CommunityChampionshipTeam team = registration.getTeam();
            if (!championshipId.equals(team.getChampionship().getId())
                    || byId.putIfAbsent(team.getId(), team) != null)
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Inscrições inconsistentes com o campeonato");
        }
        final ChampionshipCupOpeningDraw.Draw draw;
        try {
            draw = ChampionshipCupOpeningDraw.generate(
                    new java.util.ArrayList<>(byId.keySet()), new java.security.SecureRandom());
        } catch (IllegalArgumentException invalid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalid.getMessage());
        }
        var matches = new java.util.ArrayList<LeagueFixture>();
        for (ChampionshipCupOpeningDraw.Pairing pair : draw.matches()) {
            var fixture = fixtures.save(com.example.desporto24.model.community.CommunityChampionshipFixture.builder()
                    .event(event).roundNumber(1).matchNumber(pair.matchNumber())
                    .homeTeam(byId.get(pair.firstTeamId()))
                    .awayTeam(byId.get(pair.secondTeamId())).build());
            matches.add(fixtureResponse(fixture));
        }
        var automatic = new java.util.ArrayList<CupBye>();
        for (ChampionshipCupOpeningDraw.Bye bye : draw.byes()) {
            var saved = cupByes.save(CommunityChampionshipCupBye.builder()
                    .event(event).team(byId.get(bye.teamId())).slotNumber(bye.slotNumber()).build());
            automatic.add(cupByeResponse(saved));
        }
        event.setStatus("SCHEDULED");
        events.saveAndFlush(event);
        return new CupOpeningRound(List.copyOf(matches), List.copyOf(automatic));
    }

    private CupBye cupByeResponse(CommunityChampionshipCupBye bye) {
        return new CupBye(bye.getId(), bye.getSlotNumber(),
                bye.getTeam().getId(), bye.getTeam().getName());
    }

    private LeagueFixture fixtureResponse(com.example.desporto24.model.community.CommunityChampionshipFixture fixture) {
        return new LeagueFixture(fixture.getId(), fixture.getRoundNumber(), fixture.getMatchNumber(),
                fixture.getHomeTeam().getId(), fixture.getHomeTeam().getName(),
                fixture.getAwayTeam().getId(), fixture.getAwayTeam().getName());
    }

    private TeamRegistration teamResponse(CommunityChampionshipEventTeam registration) {
        return new TeamRegistration(registration.getId(), registration.getTeam().getId(),
                registration.getTeam().getName());
    }

    private CommunityChampionshipEvent lockOwnedEvent(Long championshipId, Long eventId) {
        // Serialize every roster/draft mutation with schedule generation.
        return events.lockForScheduling(eventId, championshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Prova não encontrada neste campeonato"));
    }

    private CommunityChampionshipEvent getOwnedEvent(Long championshipId, Long eventId) {
        return events.findByIdAndChampionshipId(eventId, championshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Prova não encontrada neste campeonato"));
    }

    private void ensureOpen(CommunityChampionship championship) {
        if (championship.getClosedAt() != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Campeonato encerrado");
    }

    private void ensureDraft(CommunityChampionshipEvent event) {
        if (!"DRAFT".equals(event.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta prova já não é um rascunho");
    }

    private void validateRequest(CreateEvent request) {
        if (request == null || request.name() == null || request.name().isBlank()
                || request.name().trim().length() > 120
                || request.format() == null || !FORMATS.contains(request.format()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome ou formato de prova inválido");
    }

    private CommunityChampionship getChampionship(Long communityId, Long championshipId) {
        return championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
    }

    private Event response(CommunityChampionshipEvent event) {
        return new Event(event.getId(), event.getChampionship().getId(), event.getName(),
                event.getFormat(), event.getStatus(), event.getCreatedAt());
    }

    private void requireMember(Long communityId, String username, boolean organizer) {
        var profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        CommunityMember member = members.findByCommunityIdAndProfileId(communityId, profile.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences à comunidade"));
        if (organizer && member.getRole() != CommunityRole.OWNER && member.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Apenas Owners e Admins podem gerir campeonatos");
    }
}
