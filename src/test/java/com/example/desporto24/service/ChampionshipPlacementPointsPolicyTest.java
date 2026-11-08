package com.example.desporto24.service;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChampionshipPlacementPointsPolicyTest {
    @Test
    void acceptsOrganizerPointsAndNormalizesFormatting() {
        assertEquals(List.of(25, 18, 15, 10), ChampionshipPlacementPointsPolicy.parse(" 25, 18,15,10 "));
        assertEquals("25,18,15,10", ChampionshipPlacementPointsPolicy.canonical("25, 18, 15, 10"));
        assertEquals(18, ChampionshipPlacementPointsPolicy.pointsForPosition(
                ChampionshipPlacementPointsPolicy.parse("25,18,15"), 2));
        assertEquals(0, ChampionshipPlacementPointsPolicy.pointsForPosition(
                ChampionshipPlacementPointsPolicy.parse("25,18,15"), 5));
    }

    @Test
    void rejectsIncreasingNegativeOrMalformedRules() {
        for (String invalid : List.of("25", "25,30", "-1,0", "25,,15", "abc,1",
                "10001,1", "25,12.5", "25,5,")) {
            assertThrows(IllegalArgumentException.class,
                    () -> ChampionshipPlacementPointsPolicy.parse(invalid), invalid);
        }
    }

    @Test
    void rejectsExcessiveLengthsAndInvalidPosition() {
        assertThrows(IllegalArgumentException.class, () ->
                ChampionshipPlacementPointsPolicy.parse("1,".repeat(21) + "0"));
        assertThrows(IllegalArgumentException.class, () ->
                ChampionshipPlacementPointsPolicy.pointsForPosition(List.of(25,18), 0));
    }
}
