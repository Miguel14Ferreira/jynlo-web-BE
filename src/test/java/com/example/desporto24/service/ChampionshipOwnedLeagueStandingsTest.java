package com.example.desporto24.service;

import com.example.desporto24.model.community.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ChampionshipOwnedLeagueStandingsTest {
    private CommunityChampionshipTeam team(long id, String name) {
        return CommunityChampionshipTeam.builder().id(id).name(name).build();
    }

    private CommunityChampionshipEventTeam registration(CommunityChampionshipTeam team) {
        return CommunityChampionshipEventTeam.builder().team(team).build();
    }

    private CommunityChampionshipFixture match(CommunityChampionshipTeam home,
            CommunityChampionshipTeam away, Integer h, Integer a) {
        return CommunityChampionshipFixture.builder().homeTeam(home).awayTeam(away)
                .homeScore(h).awayScore(a).build();
    }

    @Test
    void computesThreeOneZeroWithoutCountingUnplayedMatches() {
        var a = team(1, "Águias");
        var b = team(2, "Leões");
        var c = team(3, "Tigres");
        var table = ChampionshipOwnedLeagueStandings.calculate(9L,
                List.of(registration(a), registration(b), registration(c)),
                List.of(match(a, b, 2, 1), match(a, c, 0, 0), match(b, c, null, null)));
        assertEquals(3, table.totalMatches());
        assertEquals(2, table.playedMatches());
        assertFalse(table.completed());
        assertEquals(List.of(4, 1, 1), table.standings().stream()
                .map(ChampionshipOwnedLeagueStandings.Row::points).toList());
        var leader = table.standings().get(0);
        assertEquals(2, leader.played());
        assertEquals(2, leader.goalsFor());
        assertEquals(1, leader.goalsAgainst());
        assertEquals(1, leader.goalDifference());
    }

    @Test
    void keepsJointPositionRatherThanInventingTiebreakWinner() {
        var a = team(1, "Águias");
        var b = team(2, "Leões");
        var table = ChampionshipOwnedLeagueStandings.calculate(9L,
                List.of(registration(a), registration(b)),
                List.of(match(a, b, 1, 1)));
        assertTrue(table.completed());
        assertTrue(table.firstPlaceTied());
        assertEquals(1, table.standings().get(0).position());
        assertEquals(1, table.standings().get(1).position());
    }

    @Test
    void rejectsForeignTeamInFixture() {
        var a = team(1, "Águias");
        var b = team(2, "Leões");
        var outsider = team(3, "Outra equipa");
        assertThrows(IllegalStateException.class, () ->
                ChampionshipOwnedLeagueStandings.calculate(9L,
                        List.of(registration(a), registration(b)),
                        List.of(match(a, outsider, 2, 0))));
    }

    @Test
    void rejectsPartialResults() {
        var a = team(1, "Águias");
        var b = team(2, "Leões");
        assertThrows(IllegalStateException.class, () ->
                ChampionshipOwnedLeagueStandings.calculate(9L,
                        List.of(registration(a), registration(b)),
                        List.of(match(a, b, 2, null))));
    }

    @Test
    void noGamesNeverQualifiesAsCompletedLeague() {
        var a = team(1, "Águias");
        var table = ChampionshipOwnedLeagueStandings.calculate(9L,
                List.of(registration(a)), List.of());
        assertEquals(0, table.playedMatches());
        assertFalse(table.completed());
        assertFalse(table.firstPlaceTied());
    }
}
