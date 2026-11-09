package com.example.desporto24.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class ChampionshipLeagueSchedulerTest {
    @Test
    void threeTeamsPlayOnceEachWithOneByePerRound() {
        var matches = ChampionshipLeagueScheduler.generate(List.of(30L, 10L, 20L), 1);
        assertEquals(3, matches.size());
        assertEquals(Set.of(1, 2, 3), matches.stream().map(m -> m.round()).collect(Collectors.toSet()));
        assertEquals(3, matches.stream().map(m -> pair(m.homeTeamId(), m.awayTeamId()))
                .collect(Collectors.toSet()).size());
        assertTrue(matches.stream().allMatch(m -> m.homeTeamId() != m.awayTeamId()));
    }

    @Test
    void fourTeamsProduceThreeRoundsWithTwoFixturesEach() {
        var matches = ChampionshipLeagueScheduler.generate(List.of(4L, 1L, 3L, 2L), 1);
        assertEquals(6, matches.size());
        for (int round = 1; round <= 3; round++) {
            final int r = round;
            var games = matches.stream().filter(m -> m.round() == r).toList();
            assertEquals(2, games.size());
            assertEquals(4, games.stream().flatMap(m -> List.of(m.homeTeamId(), m.awayTeamId()).stream())
                    .distinct().count());
        }
        assertEquals(6, matches.stream().map(m -> pair(m.homeTeamId(), m.awayTeamId()))
                .collect(Collectors.toSet()).size());
    }

    @Test
    void secondLegReversesEveryMatchAtLaterRound() {
        var games = ChampionshipLeagueScheduler.generate(List.of(1L, 2L, 3L, 4L), 2);
        assertEquals(12, games.size());
        for (int i = 0; i < 6; i++) {
            var first = games.get(i);
            var returnMatch = games.get(i + 6);
            assertEquals(first.round() + 3, returnMatch.round());
            assertEquals(first.homeTeamId(), returnMatch.awayTeamId());
            assertEquals(first.awayTeamId(), returnMatch.homeTeamId());
        }
    }

    @Test
    void rejectsTooFewTooManyDuplicateAndInvalidTeamsOrLegs() {
        assertThrows(IllegalArgumentException.class, () -> ChampionshipLeagueScheduler.generate(List.of(1L), 1));
        assertThrows(IllegalArgumentException.class, () -> ChampionshipLeagueScheduler.generate(List.of(1L, 1L), 1));
        assertThrows(IllegalArgumentException.class, () -> ChampionshipLeagueScheduler.generate(List.of(1L, 2L), 3));
        assertThrows(IllegalArgumentException.class, () -> ChampionshipLeagueScheduler.generate(List.of(0L, 2L), 1));
    }

    private String pair(long a, long b) {
        return Math.min(a, b) + ":" + Math.max(a, b);
    }
}
