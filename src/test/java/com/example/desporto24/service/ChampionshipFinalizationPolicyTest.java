package com.example.desporto24.service;

import com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse;
import com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse.Standing;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ChampionshipFinalizationPolicyTest {
    private CommunityChampionshipStandingsResponse result(
            int pending, Standing... entries) {
        return new CommunityChampionshipStandingsResponse(7L, List.of(entries), 1, 0, pending);
    }
    private Standing row(String type, long id, long points, int victories) {
        return new Standing(type, id, "Clube " + id, points, victories, 2);
    }
    @Test void acceptsConfirmedSoleLeader() {
        var winner = row("TEAM", 4L, 40, 2);
        assertEquals(winner, ChampionshipFinalizationPolicy.champion(
                result(0, winner, row("TEAM", 8L, 39, 3))));
    }
    @Test void blocksUnresolvedTopTie() {
        assertThrows(ResponseStatusException.class, () -> ChampionshipFinalizationPolicy.champion(
                result(0, row("TEAM", 4L, 40, 2), row("TEAM", 8L, 40, 2))));
    }
    @Test void blocksPendingOrUnlinkedCompetitions() {
        assertThrows(ResponseStatusException.class, () -> ChampionshipFinalizationPolicy.champion(
                result(1, row("TEAM", 4L, 40, 2))));
        assertThrows(ResponseStatusException.class, () -> ChampionshipFinalizationPolicy.champion(
                result(0, row("TEAM", 4L, 40, 2), row("TEAM_UNLINKED", 8L, 20, 1))));
        assertThrows(ResponseStatusException.class, () -> ChampionshipFinalizationPolicy.champion(result(0)));
    }
    @Test void blocksMixingIndividualAndTeamTitles() {
        assertThrows(ResponseStatusException.class, () -> ChampionshipFinalizationPolicy.champion(
                result(0, row("TEAM", 4L, 40, 2), row("INDIVIDUAL", 8L, 10, 1))));
    }
}
