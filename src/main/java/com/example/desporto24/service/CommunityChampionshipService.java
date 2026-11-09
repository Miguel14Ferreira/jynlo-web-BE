package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.dto.community.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityChampionshipService {
    private final CommunityRepository communities;
    private final CommunityMemberRepository members;
    private final CommunityChampionshipRepository championships;
    private final CommunityChampionshipEventRepository ownedEvents;
    private final CommunityTournamentRepository tournaments;
    private final ProfileRepository profiles;
    private final CommunityTournamentStandingsService leagueStandings;
    private final CommunityTournamentTeamStandingsService teamLeagueStandings;
    private final CommunityTournamentTeamRepository tournamentTeams;
    private final CommunityChampionshipTeamRepository championshipTeams;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public CommunityChampionshipResponse create(Long communityId, String username,
                                                  CreateCommunityChampionshipRequest request) {
        requireOrganizer(communityId, username);
        Community community = communities.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidade não encontrada"));
        String sport = request.sport().trim();
        String season = request.seasonLabel().trim();
        String name = request.name().trim();
        if (sport.isEmpty() || name.isEmpty() || season.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, época e modalidade obrigatórios");
        CommunityChampionship championship = CommunityChampionship.builder()
                .community(community).name(name).sport(sport)
                .seasonLabel(season).createdAt(Instant.now()).build();
        return toResponse(championships.save(championship));
    }

    @Transactional(readOnly = true)
    public List<CommunityChampionshipResponse> list(Long communityId, String username) {
        requireMember(communityId, username);
        return championships.findByCommunityIdOrderByCreatedAtDescIdDesc(communityId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CommunityChampionshipHonoursResponse honours(Long communityId, String username) {
        requireMember(communityId, username);
        var seasons = championships.findByCommunityIdOrderByCreatedAtDescIdDesc(communityId);
        var ids = seasons.stream().filter(s -> s.getClosedAt() != null)
                .map(CommunityChampionship::getId).toList();
        java.util.Map<Long, CommunityChampionshipHonours.PermanentTeam> linked = new java.util.HashMap<>();
        java.util.Map<Long, CommunityChampionshipHonours.PermanentTeam> permanentBySeasonTeam =
                new java.util.HashMap<>();
        java.util.Map<Long, java.util.List<CommunityChampionshipTrophySnapshot>> trophySnapshots =
                new java.util.HashMap<>();
        for (CommunityChampionship season : seasons) {
            if (season.getClosedAt() == null || season.getFinalTrophiesJson() == null) continue;
            try {
                trophySnapshots.put(season.getId(), objectMapper.readValue(
                        season.getFinalTrophiesJson(),
                        new com.fasterxml.jackson.core.type.TypeReference<
                                java.util.List<CommunityChampionshipTrophySnapshot>>() {}));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new IllegalStateException("Palmarés arquivado inválido", e);
            }
        }
        if (!ids.isEmpty()) {
            for (CommunityChampionshipTeam team : championshipTeams.findByChampionshipIdIn(ids)) {
                var season = team.getChampionship();
                var permanent = team.getPermanentTeam();
                if (permanent != null && permanent.getCommunity().getId().equals(communityId)) {
                    permanentBySeasonTeam.put(team.getId(),
                            new CommunityChampionshipHonours.PermanentTeam(
                                    permanent.getId(), permanent.getName()));
                }
                if (permanent != null && season.getClosedAt() != null
                        && "TEAM".equals(season.getChampionEntryType())
                        && team.getId().equals(season.getChampionParticipantId())
                        && permanent.getCommunity().getId().equals(communityId)) {
                    linked.put(season.getId(),
                            new CommunityChampionshipHonours.PermanentTeam(
                                    permanent.getId(), permanent.getName()));
                }
            }
        }
        return CommunityChampionshipHonours.calculate(
                communityId, seasons, linked, permanentBySeasonTeam, trophySnapshots);
    }

    public CommunityChampionshipResponse addCompetition(Long communityId, Long championshipId,
                                                         Long tournamentId, String username) {
        requireOrganizer(communityId, username);
        CommunityChampionship championship = championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
        CommunityTournament tournament = tournaments.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada"));
        if (!tournament.getCommunity().getId().equals(communityId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A prova pertence a outra comunidade");
        if (tournament.getSport() == null || !championship.getSport().equalsIgnoreCase(tournament.getSport().trim()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A prova deve ter a modalidade do campeonato");
        if (championship.getCompetitions().stream().anyMatch(t -> t.getId().equals(tournamentId)))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prova já associada ao campeonato");
        ensureOpen(championship);
        // Unique database constraint prevents the same tournament being attached to two championships.
        tournament.setChampionshipWinnerPoints(10);
        championship.getCompetitions().add(tournament);
        return toResponse(championships.saveAndFlush(championship));
    }

    public CommunityChampionshipResponse setWinnerPoints(Long communityId, Long championshipId,
                                                         Long tournamentId, int points, String username) {
        requireOrganizer(communityId, username);
        if (points < 0 || points > 10000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Os pontos devem estar entre 0 e 10000");
        CommunityChampionship championship = championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
        CommunityTournament tournament = championship.getCompetitions().stream()
                .filter(t -> t.getId().equals(tournamentId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não associada"));
        ensureOpen(championship);
        if (tournament.getStatus() != com.example.desporto24.enumeration.TournamentStatus.DRAFT)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Os pontos devem ser definidos antes do início da prova");
        if (tournament.getChampionshipPlacePoints() != null) {
            var awards = new java.util.ArrayList<>(
                    ChampionshipPlacementPointsPolicy.parse(tournament.getChampionshipPlacePoints()));
            if (awards.size() > 1 && points < awards.get(1)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Os pontos do vencedor não podem ser inferiores aos do segundo lugar");
            }
            awards.set(0, points);
            tournament.setChampionshipPlacePoints(awards.stream().map(String::valueOf)
                    .collect(java.util.stream.Collectors.joining(",")));
        }
        tournament.setChampionshipWinnerPoints(points);
        tournaments.saveAndFlush(tournament);
        return toResponse(championship);
    }

    public CommunityChampionshipResponse setPlacementPoints(Long communityId, Long championshipId,
                                                            Long tournamentId, String pointsCsv,
                                                            String username) {
        requireOrganizer(communityId, username);
        final String validated;
        final java.util.List<Integer> parsed;
        try {
            parsed = ChampionshipPlacementPointsPolicy.parse(pointsCsv);
            validated = ChampionshipPlacementPointsPolicy.canonical(pointsCsv);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        CommunityChampionship championship = championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
        CommunityTournament tournament = championship.getCompetitions().stream()
                .filter(t -> t.getId().equals(tournamentId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não associada"));
        ensureOpen(championship);
        if (tournament.getStatus() != com.example.desporto24.enumeration.TournamentStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A pontuação por posição só pode ser alterada antes do início da prova");
        }
        tournament.setChampionshipPlacePoints(validated);
        tournament.setChampionshipWinnerPoints(parsed.get(0));
        tournaments.saveAndFlush(tournament);
        return toResponse(championship);
    }

    /**
     * Applies a full placement table only to completed, numeric individual
     * leagues with independently confirmed sporting order. A draw unresolved
     * by sporting statistics never receives arbitrary position awards.
     */
    @Transactional(readOnly = true)
    public CommunityChampionshipStandingsResponse standings(Long communityId,
                                                               Long championshipId, String username) {
        requireMember(communityId, username);
        CommunityChampionship championship = championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
        if (championship.getClosedAt() != null) {
            try {
                return objectMapper.readValue(championship.getFinalStandingsJson(),
                        CommunityChampionshipStandingsResponse.class);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new IllegalStateException("Classificação final guardada inválida", e);
            }
        }
        java.util.Map<Long, java.util.List<CommunityTournamentStandingRowResponse>> accepted =
                new java.util.HashMap<>();
        for (CommunityTournament t : championship.getCompetitions()) {
            if (t.getStatus() != com.example.desporto24.enumeration.TournamentStatus.COMPLETED
                    || t.isChampionTied() || t.getChampionshipPlacePoints() == null
                    || t.getFormat() != com.example.desporto24.enumeration.TournamentFormat.LEAGUE
                    || t.getEntryType() != com.example.desporto24.enumeration.TournamentEntryType.INDIVIDUAL
                    || t.getScoringMode() != null || t.getChampionProfileId() == null
                    || t.getHistoricalArchiveId() != null) continue;
            var table = leagueStandings.standings(communityId, t.getId(), username);
            if (table.totalMatches() <= 0 || table.completedMatches() != table.totalMatches()
                    || table.standings().isEmpty()) continue;
            var rows = table.standings();
            if (!t.getChampionProfileId().equals(rows.get(0).profileId())) continue;
            java.util.Set<Long> seen = new java.util.HashSet<>();
            boolean consistent = true;
            for (int i = 0; i < rows.size(); i++) {
                var row = rows.get(i);
                if (row.profileId() == null || row.position() != i + 1
                        || !seen.add(row.profileId())) { consistent = false; break; }
                if (i > 0) {
                    var prior = rows.get(i - 1);
                    // The existing league sorter uses a stable profile ID as its
                    // final display tie-breaker. Never pay different awards on that basis.
                    if (row.points() == prior.points()
                            && row.goalDifference() == prior.goalDifference()
                            && row.goalsFor() == prior.goalsFor()
                            && row.won() == prior.won()) {
                        consistent = false;
                        break;
                    }
                }
            }
            if (consistent) accepted.put(t.getId(), rows);
        }
        java.util.Map<Long, CommunityChampionshipStandingsCalculator.CanonicalTeam> teamIdentity =
                new java.util.HashMap<>();
        for (CommunityTournament tournament : championship.getCompetitions()) {
            if (tournament.getEntryType() != com.example.desporto24.enumeration.TournamentEntryType.TEAM) continue;
            for (CommunityTournamentTeam team : tournamentTeams.findByTournamentIdOrderByIdAsc(tournament.getId())) {
                CommunityChampionshipTeam canonical = team.getChampionshipTeam();
                if (canonical != null && championshipId.equals(canonical.getChampionship().getId())) {
                    teamIdentity.put(team.getId(),
                            new CommunityChampionshipStandingsCalculator.CanonicalTeam(
                                    canonical.getId(), canonical.getName()));
                }
            }
        }
        java.util.Map<Long, java.util.List<CommunityTournamentTeamStandingRowResponse>> teamPlacements =
                new java.util.HashMap<>();
        for (CommunityTournament t : championship.getCompetitions()) {
            if (t.getStatus() != com.example.desporto24.enumeration.TournamentStatus.COMPLETED
                    || t.isChampionTied() || t.getChampionshipPlacePoints() == null
                    || t.getFormat() != com.example.desporto24.enumeration.TournamentFormat.LEAGUE
                    || t.getEntryType() != com.example.desporto24.enumeration.TournamentEntryType.TEAM
                    || t.getScoringMode() != null || t.getChampionTeamId() == null
                    || t.getHistoricalArchiveId() != null) continue;
            // Read existing league standings; never calculate placements from team names.
            var table = teamLeagueStandings.standings(communityId, t.getId(), username);
            if (table.totalMatches() <= 0 || table.completedMatches() != table.totalMatches()
                    || table.standings().isEmpty()) continue;
            var rows = table.standings();
            if (!t.getChampionTeamId().equals(rows.get(0).teamId())) continue;
            java.util.Set<Long> seen = new java.util.HashSet<>();
            java.util.Set<Long> canonicalSeen = new java.util.HashSet<>();
            boolean consistent = true;
            for (int i = 0; i < rows.size(); i++) {
                var row = rows.get(i);
                var mapping = row.teamId() == null ? null : teamIdentity.get(row.teamId());
                // Require EVERY ranked team to have a unique explicit season identity.
                if (mapping == null || row.position() != i + 1
                        || !seen.add(row.teamId()) || !canonicalSeen.add(mapping.id())) {
                    consistent = false;
                    break;
                }
                if (i > 0) {
                    var prev = rows.get(i - 1);
                    // The underlying ranking ultimately uses team ID for display.
                    // Equal sporting metrics cannot earn different placement awards.
                    if (row.points() == prev.points()
                            && row.goalDifference() == prev.goalDifference()
                            && row.goalsFor() == prev.goalsFor()
                            && row.won() == prev.won()) {
                        consistent = false;
                        break;
                    }
                }
            }
            if (consistent) teamPlacements.put(t.getId(), rows);
        }
        return CommunityChampionshipStandingsCalculator.calculate(
                championshipId, championship.getCompetitions(), accepted, teamIdentity, teamPlacements);
    }

    public CommunityChampionshipResponse close(Long communityId, Long championshipId, String username) {
        requireOrganizer(communityId, username);
        CommunityChampionship championship = championships.findForClosing(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
        ensureOpen(championship);
        // Owned-event drafts have no fixtures or certified results yet.
        // Never crown a champion while they are pending.
        if (!ownedEvents.findByChampionshipIdOrderByCreatedAtAscIdAsc(championshipId).isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Existem provas próprias em rascunho; só é possível encerrar após implementar e validar os resultados");
        CommunityChampionshipStandingsResponse result = standings(communityId, championshipId, username);
        long leaguesRequiringFullPositions = championship.getCompetitions().stream()
                .filter(t -> t.getFormat() == com.example.desporto24.enumeration.TournamentFormat.LEAGUE
                        && t.getChampionshipPlacePoints() != null).count();
        if (result.fullyScoredCompetitions() < leaguesRequiringFullPositions)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Existem ligas sem todas as posições verificadas; completa os resultados e associa as equipas");
        var champion = ChampionshipFinalizationPolicy.champion(result);
        try {
            championship.setFinalStandingsJson(objectMapper.writeValueAsString(result));
            championship.setFinalCompetitionsJson(objectMapper.writeValueAsString(
                    competitionSummaries(championship)));
            championship.setFinalTrophiesJson(objectMapper.writeValueAsString(
                    trophySummaries(championship)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Não foi possível guardar a classificação", e);
        }
        championship.setChampionEntryType(champion.entryType());
        championship.setChampionParticipantId(champion.participantId());
        championship.setChampionDisplayName(champion.name());
        championship.setClosedAt(Instant.now());
        return toResponse(championships.saveAndFlush(championship));
    }

    private void ensureOpen(CommunityChampionship championship) {
        if (championship.getClosedAt() != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O campeonato está encerrado e o histórico não pode ser alterado");
    }

    private void requireMember(Long communityId, String username) {
        Profile profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        if (!members.existsByCommunityIdAndProfileId(communityId, profile.getId()))
            throw new AccessDeniedException("Não pertences a esta comunidade");
    }

    private void requireOrganizer(Long communityId, String username) {
        Profile profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        CommunityMember member = members.findByCommunityIdAndProfileId(communityId, profile.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences a esta comunidade"));
        if (member.getRole() != CommunityRole.OWNER && member.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Apenas Owners e Admins podem gerir campeonatos");
    }

    private java.util.List<CommunityChampionshipTrophySnapshot> trophySummaries(
            CommunityChampionship championship) {
        java.util.List<CommunityChampionshipTrophySnapshot> snapshots = new java.util.ArrayList<>();
        for (CommunityTournament tournament : championship.getCompetitions()) {
            if (tournament.getStatus() != com.example.desporto24.enumeration.TournamentStatus.COMPLETED
                    || tournament.isChampionTied()) continue;
            String kind = tournament.getEntryType() == com.example.desporto24.enumeration.TournamentEntryType.TEAM
                    ? "TEAM" : "INDIVIDUAL";
            Long playerId = null, seasonTeamId = null;
            String name;
            if ("TEAM".equals(kind)) {
                Long tournamentTeamId = tournament.getChampionTeamId();
                if (tournamentTeamId == null) continue;
                name = tournament.getChampionTeamName();
                for (CommunityTournamentTeam team : tournamentTeams.findByTournamentIdOrderByIdAsc(tournament.getId())) {
                    if (tournamentTeamId.equals(team.getId()) && team.getChampionshipTeam() != null
                            && championship.getId().equals(team.getChampionshipTeam().getChampionship().getId())) {
                        seasonTeamId = team.getChampionshipTeam().getId();
                        break;
                    }
                }
            } else {
                playerId = tournament.getChampionProfileId();
                if (playerId == null) continue;
                name = tournament.getChampionDisplayName();
            }
            if (name == null || name.isBlank()) continue;
            snapshots.add(new CommunityChampionshipTrophySnapshot(
                    tournament.getId(), tournament.getName(), tournament.getFormat().name(),
                    kind, playerId, seasonTeamId, name));
        }
        return java.util.List.copyOf(snapshots);
    }

    private java.util.List<CommunityChampionshipResponse.ChampionshipCompetition> competitionSummaries(
            CommunityChampionship championship) {
        return championship.getCompetitions().stream()
                .map(t -> new CommunityChampionshipResponse.ChampionshipCompetition(
                        t.getId(), t.getName(), t.getFormat().name(), t.getStatus().name(),
                        t.getChampionshipWinnerPoints() == null ? 10 : t.getChampionshipWinnerPoints(),
                        t.getStatus() == com.example.desporto24.enumeration.TournamentStatus.COMPLETED
                                && !t.isChampionTied()
                                && (t.getChampionProfileId() != null || t.getChampionTeamId() != null),
                        t.getChampionshipPlacePoints()))
                .toList();
    }

    private java.util.List<CommunityChampionshipResponse.ChampionshipCompetition> historicalCompetitions(
            CommunityChampionship championship) {
        if (championship.getClosedAt() == null || championship.getFinalCompetitionsJson() == null)
            return competitionSummaries(championship);
        try {
            return objectMapper.readValue(championship.getFinalCompetitionsJson(),
                    new com.fasterxml.jackson.core.type.TypeReference<
                            java.util.List<CommunityChampionshipResponse.ChampionshipCompetition>>() {});
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Provas arquivadas inválidas", e);
        }
    }

    private CommunityChampionshipResponse toResponse(CommunityChampionship championship) {
        return new CommunityChampionshipResponse(
                championship.getId(), championship.getCommunity().getId(), championship.getName(),
                championship.getSport(), championship.getSeasonLabel(), championship.getCreatedAt(),
                historicalCompetitions(championship),
                championship.getClosedAt() == null
                        ? ChampionshipWinnerStandings.calculate(championship.getCompetitions())
                        : java.util.List.of(),
                championship.getClosedAt(), championship.getChampionEntryType(),
                championship.getChampionParticipantId(), championship.getChampionDisplayName());
    }
}
