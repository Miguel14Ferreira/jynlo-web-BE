package com.example.desporto24.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static com.example.desporto24.service.ChampionshipCustomScoringPolicy.*;

class ChampionshipCustomScoringPolicyTest {
    @Test
    void scoresAreExplicitAndPlacesBeyondConfiguredPodiumGetZero() {
        Rules rules = new Rules("objetivos", RankingDirection.HIGHER_IS_BETTER,
                List.of(10, 7, 5), TieBreak.MOST_WINS);
        assertEquals(10, pointsForPlace(rules, 1));
        assertEquals(7, pointsForPlace(rules, 2));
        assertEquals(5, pointsForPlace(rules, 3));
        assertEquals(0, pointsForPlace(rules, 4));
        assertThrows(IllegalArgumentException.class, () -> pointsForPlace(rules, 0));
    }

    @Test
    void supportsTimeBasedRankingWithoutInventingPoints() {
        Rules rules = validate(new Rules("segundos", RankingDirection.LOWER_IS_BETTER,
                List.of(5, 3, 1), TieBreak.SHARED_POSITION));
        assertEquals(RankingDirection.LOWER_IS_BETTER, rules.direction());
        assertEquals(TieBreak.SHARED_POSITION, rules.tieBreak());
    }

    @Test
    void rejectsInvalidRulesAndNonMonotonicPoints() {
        assertThrows(IllegalArgumentException.class, () -> validate(null));
        assertThrows(IllegalArgumentException.class, () ->
                validate(new Rules("", RankingDirection.HIGHER_IS_BETTER,
                        List.of(10, 5), TieBreak.SHARED_POSITION)));
        assertThrows(IllegalArgumentException.class, () ->
                validate(new Rules("pontos", null, List.of(10, 5), TieBreak.SHARED_POSITION)));
        assertThrows(IllegalArgumentException.class, () ->
                validate(new Rules("pontos", RankingDirection.HIGHER_IS_BETTER,
                        List.of(10, 11), TieBreak.SHARED_POSITION)));
        assertThrows(IllegalArgumentException.class, () ->
                validate(new Rules("pontos", RankingDirection.HIGHER_IS_BETTER,
                        List.of(10, -1), TieBreak.SHARED_POSITION)));
        assertThrows(IllegalArgumentException.class, () ->
                validate(new Rules("pontos", RankingDirection.HIGHER_IS_BETTER,
                        List.of(10), TieBreak.SHARED_POSITION)));
    }

    @Test
    void returnsImmutableSnapshotOfPoints() {
        var mutable = new java.util.ArrayList<>(List.of(10, 5));
        Rules validated = validate(new Rules("pontos", RankingDirection.HIGHER_IS_BETTER,
                mutable, TieBreak.SHARED_POSITION));
        mutable.set(0, 99);
        assertEquals(10, validated.pointsByPlace().get(0));
        assertThrows(UnsupportedOperationException.class, () -> validated.pointsByPlace().add(1));
    }
}
