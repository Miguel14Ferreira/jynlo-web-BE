package com.example.desporto24.service;

import java.util.ArrayList;
import java.util.List;

/** Deterministic Berger/circle round-robin schedule of stable season-team IDs. */
public final class ChampionshipLeagueScheduler {
    private ChampionshipLeagueScheduler() {}

    public record Match(int round, int match, long homeTeamId, long awayTeamId) {}

    public static List<Match> generate(List<Long> teamIds, int legs) {
        if (teamIds == null || teamIds.size() < 2 || teamIds.size() > 64
                || teamIds.stream().anyMatch(id -> id == null || id <= 0)
                || teamIds.stream().distinct().count() != teamIds.size()
                || (legs != 1 && legs != 2)) {
            throw new IllegalArgumentException("São necessárias 2 a 64 equipas distintas e 1 ou 2 voltas");
        }
        List<Long> ring = new ArrayList<>(teamIds.stream().sorted().toList());
        if (ring.size() % 2 != 0) ring.add(null); // bye: no artificial fixture
        final int count = ring.size();
        final int rounds = count - 1;
        List<Match> firstLeg = new ArrayList<>();
        for (int round = 1; round <= rounds; round++) {
            int number = 0;
            for (int position = 0; position < count / 2; position++) {
                Long left = ring.get(position);
                Long right = ring.get(count - 1 - position);
                if (left == null || right == null) continue;
                // Alternate pair orientation so the fixed team is not always at home.
                boolean reverse = (round + position) % 2 == 0;
                firstLeg.add(new Match(round, ++number,
                        reverse ? right : left, reverse ? left : right));
            }
            Long last = ring.remove(count - 1);
            ring.add(1, last);
        }
        if (legs == 1) return List.copyOf(firstLeg);
        List<Match> result = new ArrayList<>(firstLeg);
        for (Match match : firstLeg) {
            result.add(new Match(match.round() + rounds, match.match(),
                    match.awayTeamId(), match.homeTeamId()));
        }
        return List.copyOf(result);
    }
}
