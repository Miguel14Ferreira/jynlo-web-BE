package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunitySeason;
import com.example.desporto24.model.dto.community.*;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommunityOverallStandingsTest {

    private static CommunityStandingResponse team(Long id, String user, int games, int wins) {
        return new CommunityStandingResponse(1, id, user, user, null, games, wins, 0,
                games - wins, wins * 3, 0, 0, 0, 0, 0, "", 0);
    }

    private static CommunityRacketStandingResponse racket(Long id, String user, int matches, int wins) {
        return new CommunityRacketStandingResponse(1, id, user, user, null, matches, wins,
                matches - wins, 0, 0, 0, 0, 0, 0);
    }

    @Test
    void combinesWinsAndGamesAcrossSportsWithoutSummingSportPoints() {
        var football = new CommunitySportStandingGroupResponse("Futebol",
                List.of(team(1L, "ana", 4, 3), team(2L, "rui", 4, 1)));
        var padel = new CommunityRacketStandingGroupResponse("Padel", "DOUBLES",
                List.of(racket(2L, "rui", 6, 4), racket(3L, "eva", 2, 2)));

        var rows = CommunityOverallStandings.calculate(List.of(football), List.of(padel), List.of());

        assertEquals(3, rows.size());
        assertEquals("rui", rows.get(0).username());
        assertEquals(5, rows.get(0).wins());
        assertEquals(10, rows.get(0).games());
        assertEquals(2, rows.get(0).sports());
        assertEquals(50.0, rows.get(0).winRate());
        assertEquals("ana", rows.get(1).username());
        assertEquals(1, rows.get(1).sports());
    }

    @Test
    void singleSportNeverProducesADuplicateGeneralTable() {
        var football = new CommunitySportStandingGroupResponse("Futebol",
                List.of(team(1L, "ana", 4, 3)));
        assertTrue(CommunityOverallStandings.calculate(List.of(football), List.of(), List.of()).isEmpty());
    }

    @Test
    void tiedPlayersShareThePosition() {
        var football = new CommunitySportStandingGroupResponse("Futebol",
                List.of(team(1L, "ana", 2, 1), team(2L, "rui", 2, 1)));
        var padel = new CommunityRacketStandingGroupResponse("Padel", "DOUBLES",
                List.of(racket(3L, "eva", 1, 0)));
        var rows = CommunityOverallStandings.calculate(List.of(football), List.of(padel), null);
        assertEquals(1, rows.get(0).position());
        assertEquals(1, rows.get(1).position());
        assertEquals(3, rows.get(2).position());
    }

    @Test
    void sessionSeasonPrefersSportSeasonThenSharedSeason() {
        CommunitySeason football = CommunitySeason.builder().id(1L).sport("Futebol").active(true).build();
        CommunitySeason shared = CommunitySeason.builder().id(2L).active(true).build();
        assertEquals(1L, CommunitySessionSeasonSelector.pick(List.of(shared, football), "futebol").orElseThrow().getId());
        assertEquals(2L, CommunitySessionSeasonSelector.pick(List.of(shared, football), "Padel").orElseThrow().getId());
        assertTrue(CommunitySessionSeasonSelector.pick(List.of(football), "Padel").isEmpty());
        assertTrue(CommunitySessionSeasonSelector.needsSportSeason(List.of(football)));
        assertFalse(CommunitySessionSeasonSelector.needsSportSeason(List.of()));
    }
}
