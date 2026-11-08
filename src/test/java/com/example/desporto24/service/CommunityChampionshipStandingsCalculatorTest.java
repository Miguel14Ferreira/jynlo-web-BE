package com.example.desporto24.service;

import com.example.desporto24.enumeration.*;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.dto.community.CommunityTournamentStandingRowResponse;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class CommunityChampionshipStandingsCalculatorTest {
    private CommunityTournament completed(Long id, Long championId, String label, String awards) {
        return CommunityTournament.builder().id(id).status(TournamentStatus.COMPLETED)
                .entryType(TournamentEntryType.INDIVIDUAL).format(TournamentFormat.LEAGUE)
                .championProfileId(championId).championDisplayName(label)
                .championshipPlacePoints(awards).championshipWinnerPoints(25).build();
    }

    private CommunityTournamentStandingRowResponse row(int rank, long id, String label) {
        return new CommunityTournamentStandingRowResponse(rank, id, label, label,
                2, 1, 0, 1, 2, 1, 1, rank == 1 ? 6 : 3);
    }

    @Test
    void awardsEachPlacementOnceInsteadOfAlsoCountingWinnerBonus() {
        var first = completed(1L, 10L, "Ana", "25,18,15");
        var second = completed(2L, 10L, "Ana", "25,18,15");
        var result = CommunityChampionshipStandingsCalculator.calculate(7L, List.of(first, second),
                Map.of(1L, List.of(row(1, 10L, "Ana"), row(2, 20L, "Rui")),
                       2L, List.of(row(1, 10L, "Ana"), row(2, 20L, "Rui"))));
        assertEquals(2, result.fullyScoredCompetitions());
        assertEquals(0, result.winnerOnlyCompetitions());
        assertEquals(50, result.standings().get(0).points());
        assertEquals(36, result.standings().get(1).points());
        assertEquals(2, result.standings().get(0).victories());
    }

    @Test
    void unsupportedEventsRetainWinnerOnlyAndPendingEventsAwardNothing() {
        var completed = completed(1L, 10L, "Ana", "25,18");
        var pending = completed(2L, 10L, "Ana", "25,18");
        pending.setStatus(TournamentStatus.IN_PROGRESS);
        var result = CommunityChampionshipStandingsCalculator.calculate(1L,
                List.of(completed, pending), Map.of());
        assertEquals(1, result.winnerOnlyCompetitions());
        assertEquals(1, result.pendingCompetitions());
        assertEquals(25, result.standings().get(0).points());
    }

    @Test
    void separatesTeamsAndIndividualsEvenWhenIdsMatch() {
        var athlete = completed(1L, 5L, "Atleta", null);
        var team = CommunityTournament.builder().id(2L).status(TournamentStatus.COMPLETED)
                .entryType(TournamentEntryType.TEAM).championTeamId(5L)
                .championTeamName("Equipa").championshipWinnerPoints(12).build();
        var result = CommunityChampionshipStandingsCalculator.calculate(1L,
                List.of(athlete, team), Map.of());
        assertEquals(2, result.standings().size());
    }
}
