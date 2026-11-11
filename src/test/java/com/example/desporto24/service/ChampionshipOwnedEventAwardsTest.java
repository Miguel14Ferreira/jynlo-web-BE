package com.example.desporto24.service;

import com.example.desporto24.model.community.*;
import com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ChampionshipOwnedEventAwardsTest {
    private final CommunityChampionship season = CommunityChampionship.builder().id(15L).build();
    private CommunityChampionshipEvent event(long id, CommunityChampionshipTeam team, String status) {
        return CommunityChampionshipEvent.builder().id(id).championship(season).name("Prova " + id)
                .format(id == 1 ? "LEAGUE" : "CUP").status(status)
                .winnerTeam("COMPLETED".equals(status) ? team : null)
                .winnerTeamName("COMPLETED".equals(status) ? team.getName() : null)
                .finalizedAt("COMPLETED".equals(status) ? Instant.now() : null).build();
    }
    private CommunityChampionshipStandingsResponse empty() {
        return new CommunityChampionshipStandingsResponse(15L, List.of(), 0, 0, 0);
    }

    @Test
    void leaguePlusCupWithSameWinnerAwardTwiceWithoutDuplicateRows() {
        var team = CommunityChampionshipTeam.builder().id(30L).name("Águias").championship(season).build();
        var result = ChampionshipOwnedEventAwards.merge(empty(),
                List.of(event(1, team, "COMPLETED"), event(2, team, "COMPLETED")), false);
        assertEquals(1, result.standings().size());
        assertEquals(20, result.standings().get(0).points());
        assertEquals(2, result.standings().get(0).victories());
        assertEquals(2, result.winnerOnlyCompetitions());
        assertEquals(0, result.pendingCompetitions());
    }

    @Test
    void uncompletedCupPreventsSeasonFinalization() {
        var team = CommunityChampionshipTeam.builder().id(30L).name("Águias").championship(season).build();
        var result = ChampionshipOwnedEventAwards.merge(empty(),
                List.of(event(1, team, "COMPLETED"), event(2, team, "SCHEDULED")), false);
        assertEquals(1, result.pendingCompetitions());
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> ChampionshipFinalizationPolicy.champion(result));
    }

    @Test
    void independentSeasonCountsPendingButNeverInventsOverallChampion() {
        var team = CommunityChampionshipTeam.builder().id(30L).name("Águias").championship(season).build();
        var result = ChampionshipOwnedEventAwards.merge(empty(),
                List.of(event(1, team, "COMPLETED"), event(2, team, "COMPLETED")), true);
        assertTrue(result.standings().isEmpty());
        assertEquals(0, result.pendingCompetitions());
    }

    @Test
    void rejectsWinnerFromAnotherSeasonAndDuplicatedEventIds() {
        var outsiderSeason = CommunityChampionship.builder().id(19L).build();
        var outsider = CommunityChampionshipTeam.builder().id(31L).name("Leões").championship(outsiderSeason).build();
        var valid = CommunityChampionshipTeam.builder().id(30L).name("Águias").championship(season).build();
        var result = ChampionshipOwnedEventAwards.merge(empty(),
                List.of(event(1, valid, "COMPLETED"), event(1, valid, "COMPLETED"),
                        event(2, outsider, "COMPLETED")), false);
        assertEquals(1, result.winnerOnlyCompetitions());
        assertEquals(1, result.pendingCompetitions());
        assertEquals(10, result.standings().get(0).points());
    }

    @Test
    void mergesOldLinkedTournamentTotalsWithOwnedTeamIdentity() {
        var team = CommunityChampionshipTeam.builder().id(30L).name("Águias").championship(season).build();
        var base = new CommunityChampionshipStandingsResponse(15L,
                List.of(new CommunityChampionshipStandingsResponse.Standing(
                        "TEAM", 30L, "Águias", 6, 1, 1)), 1, 0, 0);
        var result = ChampionshipOwnedEventAwards.merge(base,
                List.of(event(2, team, "COMPLETED")), false);
        assertEquals(1, result.standings().size());
        assertEquals(16, result.standings().get(0).points());
        assertEquals(2, result.standings().get(0).victories());
        assertEquals(1, result.fullyScoredCompetitions());
    }
}
