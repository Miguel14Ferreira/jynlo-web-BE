package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunityChampionshipEvent;
import com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse;
import com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse.Standing;
import java.util.*;

/** Adds completed championship-owned League/Cup trophies to the season ranking.
 * They are separate from linked CommunityTournament records, and each trophy
 * contributes exactly once. Until configurable event weights are persisted,
 * a validated owned trophy carries the established default of 10 winner points.
 */
public final class ChampionshipOwnedEventAwards {
    private ChampionshipOwnedEventAwards() {}
    public static final int DEFAULT_WINNER_POINTS = 10;

    public static CommunityChampionshipStandingsResponse merge(
            CommunityChampionshipStandingsResponse base,
            List<CommunityChampionshipEvent> events,
            boolean independent) {
        if (events.isEmpty()) return base;
        int pending = base.pendingCompetitions();
        int winners = base.winnerOnlyCompetitions();
        Map<String, Mutable> ranking = new HashMap<>();
        if (!independent) {
            for (Standing row : base.standings()) {
                ranking.put(row.entryType() + ":" + row.participantId(),
                        new Mutable(row.entryType(), row.participantId(), row.name(),
                                row.points(), row.victories(), row.awardedCompetitions()));
            }
        }
        Set<Long> seen = new HashSet<>();
        for (var event : events) {
            if (event.getId() != null && !seen.add(event.getId())) continue;
            var winner = event.getWinnerTeam();
            boolean valid = "COMPLETED".equals(event.getStatus())
                    && ("LEAGUE".equals(event.getFormat()) || "CUP".equals(event.getFormat()))
                    && event.getFinalizedAt() != null && winner != null
                    && winner.getId() != null && winner.getChampionship() != null
                    && event.getChampionship() != null
                    && Objects.equals(winner.getChampionship().getId(), base.championshipId())
                    && Objects.equals(event.getChampionship().getId(), base.championshipId())
                    && event.getWinnerTeamName() != null && !event.getWinnerTeamName().isBlank();
            if (!valid) { pending++; continue; }
            if (independent) continue;
            String key = "TEAM:" + winner.getId();
            var row = ranking.computeIfAbsent(key,
                    ignored -> new Mutable("TEAM", winner.getId(), event.getWinnerTeamName(), 0L, 0, 0));
            row.points += DEFAULT_WINNER_POINTS;
            row.victories++;
            row.awards++;
            winners++;
        }
        if (independent)
            return new CommunityChampionshipStandingsResponse(base.championshipId(), List.of(),
                    base.fullyScoredCompetitions(), 0, pending);
        var standings = ranking.values().stream().map(Mutable::asRow)
                .sorted(Comparator.comparingLong(Standing::points).reversed()
                        .thenComparing(Comparator.comparingInt(Standing::victories).reversed())
                        .thenComparing(Standing::entryType)
                        .thenComparing(Standing::name)
                        .thenComparing(Standing::participantId))
                .toList();
        return new CommunityChampionshipStandingsResponse(base.championshipId(), standings,
                base.fullyScoredCompetitions(), winners, pending);
    }

    private static class Mutable {
        final String kind; final Long id; final String name;
        long points; int victories; int awards;
        Mutable(String kind, Long id, String name, long points, int victories, int awards) {
            this.kind = kind; this.id = id; this.name = name;
            this.points = points; this.victories = victories; this.awards = awards;
        }
        Standing asRow() { return new Standing(kind, id, name, points, victories, awards); }
    }
}
