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
    void unifiesLinkedTeamsAcrossLeagueAndCupButKeepsUnlinkedDistinct() {
        var leagueWinner = CommunityTournament.builder().id(11L).name("Liga")
                .status(TournamentStatus.COMPLETED).entryType(TournamentEntryType.TEAM)
                .championTeamId(301L).championTeamName("Águias A")
                .championshipWinnerPoints(25).build();
        var cupWinner = CommunityTournament.builder().id(12L).name("Taça")
                .status(TournamentStatus.COMPLETED).entryType(TournamentEntryType.TEAM)
                .championTeamId(402L).championTeamName("Aguías")
                .championshipWinnerPoints(10).build();
        var unlinked = CommunityTournament.builder().id(13L).name("Outra Taça")
                .status(TournamentStatus.COMPLETED).entryType(TournamentEntryType.TEAM)
                .championTeamId(501L).championTeamName("Águias A")
                .championshipWinnerPoints(7).build();
        var result = CommunityChampionshipStandingsCalculator.calculate(9L,
                List.of(leagueWinner, cupWinner, unlinked), Map.of(),
                Map.of(301L, new CommunityChampionshipStandingsCalculator.CanonicalTeam(9L, "Águias"),
                       402L, new CommunityChampionshipStandingsCalculator.CanonicalTeam(9L, "Águias")));
        assertEquals(2, result.standings().size());
        assertEquals("TEAM", result.standings().get(0).entryType());
        assertEquals(9L, result.standings().get(0).participantId());
        assertEquals(35, result.standings().get(0).points());
        assertEquals(2, result.standings().get(0).victories());
        assertEquals("TEAM_UNLINKED", result.standings().get(1).entryType());
        assertEquals(7, result.standings().get(1).points());
    }

    @Test
    void awardsAllTeamPlacementsAndCombinesWithCupWithoutDoubleCount() {
        var league = CommunityTournament.builder().id(20L).name("Liga")
                .status(TournamentStatus.COMPLETED).entryType(TournamentEntryType.TEAM)
                .championTeamId(301L).championTeamName("Liga Águias")
                .championshipPlacePoints("25,18,15").championshipWinnerPoints(25).build();
        var cup = CommunityTournament.builder().id(21L).name("Taça")
                .status(TournamentStatus.COMPLETED).entryType(TournamentEntryType.TEAM)
                .championTeamId(402L).championTeamName("Taça Águias")
                .championshipWinnerPoints(10).build();
        var rows = List.of(
                new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingRowResponse(
                        1,301L,"Águias",3,3,0,0,5,0,5,9),
                new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingRowResponse(
                        2,302L,"Leões",3,2,0,1,3,2,1,6));
        var mapped = Map.of(
                301L,new CommunityChampionshipStandingsCalculator.CanonicalTeam(50L,"Águias"),
                302L,new CommunityChampionshipStandingsCalculator.CanonicalTeam(51L,"Leões"),
                402L,new CommunityChampionshipStandingsCalculator.CanonicalTeam(50L,"Águias"));
        var result = CommunityChampionshipStandingsCalculator.calculate(
                1L,List.of(league,cup),Map.of(),mapped,Map.of(20L,rows));
        assertEquals(1,result.fullyScoredCompetitions());
        assertEquals(1,result.winnerOnlyCompetitions());
        assertEquals(2,result.standings().size());
        assertEquals(35,result.standings().get(0).points());
        assertEquals(2,result.standings().get(0).victories());
        assertEquals(18,result.standings().get(1).points());
    }

    @Test
    void neverAwardsTheSameLeagueOrCupTwiceWhenInputContainsDuplicateIds() {
        var league = completed(31L, 10L, "Ana", "25,18");
        var cup = completed(32L, 10L, "Ana", null);
        cup.setChampionshipWinnerPoints(10);
        var result = CommunityChampionshipStandingsCalculator.calculate(
                7L, List.of(league, league, cup, cup),
                Map.of(31L, List.of(row(1, 10L, "Ana"), row(2, 20L, "Rui"))));
        assertEquals(1, result.fullyScoredCompetitions());
        assertEquals(1, result.winnerOnlyCompetitions());
        assertEquals(0, result.pendingCompetitions());
        assertEquals(35, result.standings().get(0).points());
        assertEquals(2, result.standings().get(0).victories());
        assertEquals(18, result.standings().get(1).points());
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
