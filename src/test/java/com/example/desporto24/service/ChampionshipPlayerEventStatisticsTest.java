package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ChampionshipPlayerEventStatisticsTest {
    private Profile player(long id, String name) {
        var p = new Profile(); p.setId(id); p.setFullName(name); p.setUsername("player" + id); return p;
    }

    @Test
    void aggregatesScorerAssisterAndCardsOnlyWhenActuallyRecorded() {
        var a = player(1, "Ana");
        var b = player(2, "Bruno");
        var team = CommunityChampionshipTeam.builder().id(10L).name("Águias").build();
        var goals = List.of(
                CommunityChampionshipGoal.builder().team(team).scorer(a).assister(b).build(),
                CommunityChampionshipGoal.builder().team(team).scorer(a).build());
        var cards = List.of(
                CommunityChampionshipDisciplinaryCard.builder().team(team).player(a).cardType("YELLOW").build(),
                CommunityChampionshipDisciplinaryCard.builder().team(team).player(b).cardType("RED").build());
        var result = ChampionshipPlayerEventStatistics.calculate(goals, cards);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).profileId());
        assertEquals(2, result.get(0).goals());
        assertEquals(0, result.get(0).assists());
        assertEquals(1, result.get(0).yellowCards());
        assertEquals(2L, result.get(1).profileId());
        assertEquals(1, result.get(1).assists());
        assertEquals(1, result.get(1).redCards());
    }

    @Test
    void keepsSameNamedPlayersDistinctByProfileAndTeam() {
        var a = player(1, "Rui");
        var b = player(2, "Rui");
        var home = CommunityChampionshipTeam.builder().id(10L).name("A").build();
        var away = CommunityChampionshipTeam.builder().id(20L).name("B").build();
        var result = ChampionshipPlayerEventStatistics.calculate(List.of(
                CommunityChampionshipGoal.builder().team(home).scorer(a).build(),
                CommunityChampionshipGoal.builder().team(away).scorer(b).build()), List.of());
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.profileId() == 1L && r.teamId() == 10L));
        assertTrue(result.stream().anyMatch(r -> r.profileId() == 2L && r.teamId() == 20L));
    }

    @Test
    void doesNotInventAppearancesOrUnrecordedPlayerStats() {
        assertTrue(ChampionshipPlayerEventStatistics.calculate(List.of(), List.of()).isEmpty());
    }
}
