package com.example.desporto24.service;

import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.community.CommunityTournament;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ChampionshipWinnerStandingsTest {
    private CommunityTournament individual(Long id, String name, Integer points,
                                             TournamentStatus status, boolean tied) {
        return CommunityTournament.builder().entryType(TournamentEntryType.INDIVIDUAL)
                .status(status).championProfileId(id).championDisplayName(name)
                .championshipWinnerPoints(points).championTied(tied).build();
    }

    @Test
    void aggregatesRepeatedWinnersAndOrdersByAwardPoints() {
        var standings = ChampionshipWinnerStandings.calculate(List.of(
                individual(3L, "Ana", 20, TournamentStatus.COMPLETED, false),
                individual(3L, "Ana", 7, TournamentStatus.COMPLETED, false),
                individual(4L, "Rui", 15, TournamentStatus.COMPLETED, false)));
        assertEquals(2, standings.size());
        assertEquals(3L, standings.get(0).participantId());
        assertEquals(27L, standings.get(0).points());
        assertEquals(2, standings.get(0).victories());
        assertEquals(15L, standings.get(1).points());
    }

    @Test
    void ignoresTiesUnfinishedAndUnknownWinners() {
        var standings = ChampionshipWinnerStandings.calculate(List.of(
                individual(3L, "Ana", 30, TournamentStatus.IN_PROGRESS, false),
                individual(4L, "Rui", 30, TournamentStatus.COMPLETED, true),
                individual(null, "Desconhecido", 20, TournamentStatus.COMPLETED, false),
                individual(5L, "Vencedor", 10, TournamentStatus.COMPLETED, false)));
        assertEquals(1, standings.size());
        assertEquals("Vencedor", standings.get(0).name());
    }

    @Test
    void separatesTeamsFromPlayersWithSameNumericId() {
        var individual = individual(7L, "Atleta", null, TournamentStatus.COMPLETED, false);
        var team = CommunityTournament.builder().entryType(TournamentEntryType.TEAM)
                .status(TournamentStatus.COMPLETED).championTeamId(7L).championTeamName("Equipa")
                .championshipWinnerPoints(25).build();
        var standings = ChampionshipWinnerStandings.calculate(List.of(individual, team));
        assertEquals(2, standings.size());
        assertEquals("TEAM", standings.get(0).entryType());
        assertEquals(25L, standings.get(0).points());
        assertEquals("INDIVIDUAL", standings.get(1).entryType());
        assertEquals(10L, standings.get(1).points());
    }
}
