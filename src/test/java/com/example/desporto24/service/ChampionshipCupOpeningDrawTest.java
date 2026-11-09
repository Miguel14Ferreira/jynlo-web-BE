package com.example.desporto24.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class ChampionshipCupOpeningDrawTest {
    @Test
    void threeTeamsProduceOneMatchAndOneDirectQualifier() {
        var draw = ChampionshipCupOpeningDraw.generate(List.of(10L, 20L, 30L), new Random(9L));
        assertEquals(1, draw.matches().size());
        assertEquals(1, draw.byes().size());
        assertEquals(3, allTeamIds(draw).stream().distinct().count());
    }

    @Test
    void fiveTeamsProduceOneMatchAndThreeByes() {
        var draw = ChampionshipCupOpeningDraw.generate(List.of(1L, 2L, 3L, 4L, 5L), new Random(3L));
        assertEquals(1, draw.matches().size());
        assertEquals(3, draw.byes().size());
        assertEquals(5, allTeamIds(draw).stream().distinct().count());
    }

    @Test
    void eightTeamsProduceFourMatchesWithoutByes() {
        var draw = ChampionshipCupOpeningDraw.generate(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L), new Random(3L));
        assertEquals(4, draw.matches().size());
        assertTrue(draw.byes().isEmpty());
        assertEquals(8, allTeamIds(draw).stream().distinct().count());
    }

    @Test
    void drawIsReproducibleWithSameSourceAndRandomSeed() {
        var one = ChampionshipCupOpeningDraw.generate(List.of(8L, 3L, 5L, 9L, 1L), new Random(42L));
        var two = ChampionshipCupOpeningDraw.generate(List.of(1L, 5L, 9L, 3L, 8L), new Random(42L));
        assertEquals(one, two);
    }

    @Test
    void rejectsInvalidAndDuplicateEntries() {
        assertThrows(IllegalArgumentException.class,
                () -> ChampionshipCupOpeningDraw.generate(List.of(1L), new Random(1L)));
        assertThrows(IllegalArgumentException.class,
                () -> ChampionshipCupOpeningDraw.generate(List.of(1L, 1L), new Random(1L)));
        assertThrows(IllegalArgumentException.class,
                () -> ChampionshipCupOpeningDraw.generate(List.of(1L, 0L), new Random(1L)));
    }

    private List<Long> allTeamIds(ChampionshipCupOpeningDraw.Draw draw) {
        var ids = new java.util.ArrayList<Long>();
        draw.matches().forEach(m -> { ids.add(m.firstTeamId()); ids.add(m.secondTeamId()); });
        draw.byes().forEach(b -> ids.add(b.teamId()));
        return ids;
    }
}
