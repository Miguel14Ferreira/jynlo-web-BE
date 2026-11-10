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
    private final CommunityChampionshipTeamPlayerRepository roster;
    private final CommunityChampionshipGoalRepository goals;
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
                                Long awayTeamId, String awayTeamName,
                                Integer homeScore, Integer awayScore) {
        public LeagueFixture(Long id, int round, int match,
                             Long homeTeamId, String homeTeamName,
                             Long awayTeamId, String awayTeamName) {
            this(id, round, match, homeTeamId, homeTeamName, awayTeamId, awayTeamName, null, null);
        }
    }

    public record MatchScore(int homeScore, int awayScore) {}

    /** The scorer must be a community organizer; fixtures are scoped to the season and event. */
    public LeagueFixture recordFixtureScore(Long communityId, Long championshipId, Long eventId,
                                            Long fixtureId, MatchScore score, String username) {
        requireMember(communityId, username, true);
        var championship = getChampionship(communityId, championshipId);
        ensureOpen(championship);
        // This first score contract represents goals. Other sports need their own
        // discipline-aware result types; do not apply football scores to karate.
        if (!Set.of("Futebol", "Futsal").contains(championship.getSport()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Resultados desta modalidade requerem um motor próprio");
        var event = lockOwnedEvent(championshipId, eventId);
        if (!"LEAGUE".equals(event.getFormat()) || !"SCHEDULED".equals(event.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A prova não tem calendário de liga ativo");
        if (score == null || score.homeScore() < 0 || score.awayScore() < 0
                || score.homeScore() > 999 || score.awayScore() > 999)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resultado inválido");
        var fixture = fixtures.findById(fixtureId)
                .filter(row -> row.getEvent().getId().equals(eventId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Jogo não encontrado nesta prova"));
        fixture.setHomeScore(score.homeScore());
        fixture.setAwayScore(score.awayScore());
        fixture.setResultRecordedAt(Instant.now());
        return fixtureResponse(fixtures.saveAndFlush(fixture));
    }

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

    public record GoalInput(Long profileId, Long teamId, Integer minute) {}
    public record GoalEvent(Long id, Long profileId, String scorerName, Long teamId,
                            String teamName, Integer minute) {}

    @Transactional(readOnly = true)
    public List<GoalEvent> goalScorers(Long communityId, Long championshipId, Long eventId,
                                       Long fixtureId, String username) {
        requireMember(communityId, username, false);
        getChampionship(communityId, championshipId);
        getOwnedEvent(championshipId, eventId);
        var fixture = fixtures.findById(fixtureId)
                .filter(row -> row.getEvent().getId().equals(eventId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));
        return goals.findByFixtureIdOrderByIdAsc(fixture.getId()).stream().map(this::goalResponse).toList();
    }

    public List<GoalEvent> addGoal(Long communityId, Long championshipId, Long eventId,
                                   Long fixtureId, GoalInput input, String username) {
        requireMember(communityId, username, true);
        var season = getChampionship(communityId, championshipId);
        ensureOpen(season);
        if (!Set.of("Futebol", "Futsal").contains(season.getSport()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Golos apenas nas modalidades suportadas");
        var event = lockOwnedEvent(championshipId, eventId);
        if (!"LEAGUE".equals(event.getFormat()) || !"SCHEDULED".equals(event.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A prova não tem jogos de liga ativos");
        var fixture = fixtures.findById(fixtureId)
                .filter(row -> row.getEvent().getId().equals(eventId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));
        if (fixture.getHomeScore() == null || fixture.getAwayScore() == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Regista primeiro o resultado do jogo");
        if (input == null || input.profileId() == null || input.teamId() == null
                || input.minute() != null && (input.minute() < 0 || input.minute() > 200))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do golo inválidos");
        boolean home = input.teamId().equals(fixture.getHomeTeam().getId());
        boolean away = input.teamId().equals(fixture.getAwayTeam().getId());
        if (!home && !away)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipa não pertence ao jogo");
        var registration = roster.findByChampionshipIdAndProfileId(championshipId, input.profileId())
                .filter(item -> item.getTeam().getId().equals(input.teamId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "O marcador não pertence à equipa nesta época"));
        long count = goals.findByFixtureIdOrderByIdAsc(fixtureId).stream()
                .filter(goal -> goal.getTeam().getId().equals(input.teamId())).count();
        if (count >= (home ? fixture.getHomeScore() : fixture.getAwayScore()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O número de marcadores não pode exceder o resultado da equipa");
        goals.saveAndFlush(CommunityChampionshipGoal.builder().fixture(fixture)
                .team(registration.getTeam()).scorer(registration.getProfile())
                .matchMinute(input.minute()).recordedAt(Instant.now()).build());
        return goals.findByFixtureIdOrderByIdAsc(fixtureId).stream().map(this::goalResponse).toList();
    }

    public void removeGoal(Long communityId, Long championshipId, Long eventId,
                           Long fixtureId, Long goalId, String username) {
        requireMember(communityId, username, true);
        ensureOpen(getChampionship(communityId, championshipId));
        lockOwnedEvent(championshipId, eventId);
        var fixture = fixtures.findById(fixtureId)
                .filter(row -> row.getEvent().getId().equals(eventId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));
        var goal = goals.findByIdAndFixtureId(goalId, fixture.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Golo não encontrado"));
        goals.delete(goal);
    }

    private GoalEvent goalResponse(CommunityChampionshipGoal goal) {
        var player = goal.getScorer();
        return new GoalEvent(goal.getId(), player.getId(),
                player.getFullName() == null || player.getFullName().isBlank()
                        ? player.getUsername() : player.getFullName(),
                goal.getTeam().getId(), goal.getTeam().getName(), goal.getMatchMinute());
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
                fixture.getAwayTeam().getId(), fixture.getAwayTeam().getName(),
                fixture.getHomeScore(), fixture.getAwayScore());
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
