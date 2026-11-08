package com.example.desporto24.service;

import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.dto.community.CommunityTournamentStandingRowResponse;
import com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse;
import java.util.*;

/**
 * Accumulates immutable completed-event placements. The caller supplies ONLY validated
 * league standings. Other event types use their final champion snapshots.
 * A tournament always contributes either placements or a winner, never both.
 */
public final class CommunityChampionshipStandingsCalculator {
    private CommunityChampionshipStandingsCalculator() {}

    public record CanonicalTeam(Long id, String name) {}

    public static CommunityChampionshipStandingsResponse calculate(
            Long championshipId, List<CommunityTournament> tournaments,
            Map<Long, List<CommunityTournamentStandingRowResponse>> verifiedPlacements) {
        return calculate(championshipId, tournaments, verifiedPlacements, Map.of(), Map.of());
    }

    public static CommunityChampionshipStandingsResponse calculate(
            Long championshipId, List<CommunityTournament> tournaments,
            Map<Long, List<CommunityTournamentStandingRowResponse>> verifiedPlacements,
            Map<Long, CanonicalTeam> mappedTeams) {
        return calculate(championshipId, tournaments, verifiedPlacements, mappedTeams, Map.of());
    }

    public static CommunityChampionshipStandingsResponse calculate(
            Long championshipId, List<CommunityTournament> tournaments,
            Map<Long, List<CommunityTournamentStandingRowResponse>> verifiedPlacements,
            Map<Long, CanonicalTeam> mappedTeams,
            Map<Long, List<com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingRowResponse>> verifiedTeamPlacements) {
        Map<String, Mutable> scores = new HashMap<>();
        int full = 0, winners = 0, pending = 0;
        for (CommunityTournament tournament : tournaments) {
            if (tournament.getStatus() != TournamentStatus.COMPLETED || tournament.isChampionTied()) {
                pending++;
                continue;
            }
            List<CommunityTournamentStandingRowResponse> placements = verifiedPlacements.get(tournament.getId());
            if (placements != null && tournament.getChampionshipPlacePoints() != null) {
                List<Integer> points = ChampionshipPlacementPointsPolicy.parse(tournament.getChampionshipPlacePoints());
                for (var row : placements) {
                    int award = ChampionshipPlacementPointsPolicy.pointsForPosition(points, row.position());
                    Mutable score = scores.computeIfAbsent("INDIVIDUAL:" + row.profileId(),
                            ignored -> new Mutable("INDIVIDUAL", row.profileId(), row.displayName()));
                    score.points += award;
                    score.awardedCompetitions++;
                    if (row.profileId().equals(tournament.getChampionProfileId())) score.victories++;
                }
                full++;
                continue;
            }
            var teamRows = verifiedTeamPlacements.get(tournament.getId());
            if (teamRows != null && tournament.getChampionshipPlacePoints() != null) {
                var points = ChampionshipPlacementPointsPolicy.parse(tournament.getChampionshipPlacePoints());
                for (var row : teamRows) {
                    CanonicalTeam mapped = mappedTeams.get(row.teamId());
                    // All team rows were explicitly validated and linked by the caller.
                    String key = "TEAM:" + mapped.id();
                    Mutable score = scores.computeIfAbsent(key,
                            ignored -> new Mutable("TEAM", mapped.id(), mapped.name()));
                    score.points += ChampionshipPlacementPointsPolicy.pointsForPosition(points, row.position());
                    score.awardedCompetitions++;
                    if (row.teamId().equals(tournament.getChampionTeamId())) score.victories++;
                }
                full++;
                continue;
            }
            boolean team = tournament.getEntryType() == TournamentEntryType.TEAM;
            Long id = team ? tournament.getChampionTeamId() : tournament.getChampionProfileId();
            String name = team ? tournament.getChampionTeamName() : tournament.getChampionDisplayName();
            if (id == null || name == null || name.isBlank()) {
                pending++;
                continue;
            }
            int award = tournament.getChampionshipWinnerPoints() == null ? 10
                    : tournament.getChampionshipWinnerPoints();
            if (award < 0 || award > 10000) { pending++; continue; }
            CanonicalTeam mapped = team ? mappedTeams.get(id) : null;
            String kind = team ? (mapped == null ? "TEAM_UNLINKED" : "TEAM") : "INDIVIDUAL";
            Long identity = mapped == null ? id : mapped.id();
            String displayName = mapped == null ? name : mapped.name();
            Mutable score = scores.computeIfAbsent(kind + ":" + identity,
                    ignored -> new Mutable(kind, identity, displayName));
            score.points += award;
            score.victories++;
            score.awardedCompetitions++;
            winners++;
        }
        List<CommunityChampionshipStandingsResponse.Standing> rows = scores.values().stream()
                .map(s -> new CommunityChampionshipStandingsResponse.Standing(
                        s.kind, s.id, s.name, s.points, s.victories, s.awardedCompetitions))
                .sorted(Comparator.comparingLong(CommunityChampionshipStandingsResponse.Standing::points).reversed()
                        .thenComparing(Comparator.comparingInt(
                                CommunityChampionshipStandingsResponse.Standing::victories).reversed())
                        .thenComparing(CommunityChampionshipStandingsResponse.Standing::entryType)
                        .thenComparing(CommunityChampionshipStandingsResponse.Standing::name)
                        .thenComparing(CommunityChampionshipStandingsResponse.Standing::participantId))
                .toList();
        return new CommunityChampionshipStandingsResponse(championshipId, rows, full, winners, pending);
    }

    private static final class Mutable {
        final String kind; final Long id; final String name;
        long points; int victories; int awardedCompetitions;
        Mutable(String kind, Long id, String name) {
            this.kind = kind; this.id = id; this.name = name;
        }
    }
}
