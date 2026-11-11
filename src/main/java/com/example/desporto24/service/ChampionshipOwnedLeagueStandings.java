package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunityChampionshipFixture;
import com.example.desporto24.model.community.CommunityChampionshipEventTeam;
import java.util.*;

/** Read-only provisional league table. Unplayed matches never award points.
 * Level teams retain the same position; no arbitrary champion is inferred.
 */
public final class ChampionshipOwnedLeagueStandings {
    private ChampionshipOwnedLeagueStandings() {}

    public record Row(int position, Long teamId, String teamName, int played, int wins,
                      int draws, int losses, int goalsFor, int goalsAgainst,
                      int goalDifference, int points) {}
    public record Table(Long eventId, int totalMatches, int playedMatches,
                        boolean completed, boolean firstPlaceTied, List<Row> standings) {}

    private static final class Stats {
        final Long id;
        final String name;
        int played, wins, draws, losses, scored, conceded, points;
        Stats(Long id, String name) { this.id = id; this.name = name; }
        int difference() { return scored - conceded; }
    }

    public static Table calculate(Long eventId, List<CommunityChampionshipEventTeam> registrations,
                                  List<CommunityChampionshipFixture> fixtures) {
        Map<Long, Stats> teams = new HashMap<>();
        for (var entry : registrations) {
            var team = entry.getTeam();
            if (teams.putIfAbsent(team.getId(), new Stats(team.getId(), team.getName())) != null)
                throw new IllegalArgumentException("Equipa duplicada na liga");
        }
        int played = 0;
        for (var match : fixtures) {
            Integer homeGoals = match.getHomeScore(), awayGoals = match.getAwayScore();
            if (homeGoals == null && awayGoals == null) continue;
            if (homeGoals == null || awayGoals == null || homeGoals < 0 || awayGoals < 0)
                throw new IllegalStateException("Resultado incompleto ou inválido");
            var home = teams.get(match.getHomeTeam().getId());
            var away = teams.get(match.getAwayTeam().getId());
            if (home == null || away == null || home == away)
                throw new IllegalStateException("Jogo com equipas fora da prova");
            played++;
            home.played++; away.played++;
            home.scored += homeGoals; home.conceded += awayGoals;
            away.scored += awayGoals; away.conceded += homeGoals;
            if (homeGoals > awayGoals) { home.wins++; away.losses++; home.points += 3; }
            else if (homeGoals < awayGoals) { away.wins++; home.losses++; away.points += 3; }
            else { home.draws++; away.draws++; home.points++; away.points++; }
        }
        var sorted = teams.values().stream()
                .sorted(Comparator.comparingInt((Stats t) -> t.points).reversed()
                        .thenComparing(Comparator.comparingInt(Stats::difference).reversed())
                        .thenComparing(Comparator.comparingInt((Stats t) -> t.scored).reversed())
                        .thenComparing(t -> t.name, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(t -> t.id))
                .toList();
        var rows = new ArrayList<Row>();
        Stats previous = null;
        int rank = 0;
        for (int i = 0; i < sorted.size(); i++) {
            var t = sorted.get(i);
            if (previous == null || previous.points != t.points
                    || previous.difference() != t.difference() || previous.scored != t.scored)
                rank = i + 1;
            rows.add(new Row(rank, t.id, t.name, t.played, t.wins, t.draws, t.losses,
                    t.scored, t.conceded, t.difference(), t.points));
            previous = t;
        }
        boolean tied = rows.size() > 1 && rows.get(0).position() == rows.get(1).position();
        return new Table(eventId, fixtures.size(), played,
                !fixtures.isEmpty() && played == fixtures.size(), tied, List.copyOf(rows));
    }
}
