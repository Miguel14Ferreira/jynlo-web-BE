package com.example.desporto24.service;

import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityChampionship;
import com.example.desporto24.model.community.CommunityChampionshipTeam;
import com.example.desporto24.repository.community.CommunityChampionshipTeamRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SportsTeamChampionshipHonoursServiceTest {
    private final SportsTeamRepository teams = mock(SportsTeamRepository.class);
    private final CommunityChampionshipTeamRepository seasonTeams =
            mock(CommunityChampionshipTeamRepository.class);
    private final SportsTeamChampionshipHonoursService service =
            new SportsTeamChampionshipHonoursService(teams, seasonTeams, new ObjectMapper());

    @Test
    void onlyClosedCertifiedSeasonTeamAwardsAreVisibleNotNamesakesOrIndividuals() {
        when(teams.existsById(44L)).thenReturn(true);
        var community = Community.builder().id(6L).name("Liga Lisboa").build();
        var championship = CommunityChampionship.builder().id(8L).name("Época 2026")
            .sport("Futebol").seasonLabel("2026/27").community(community)
            .closedAt(Instant.parse("2026-11-15T10:00:00Z")).build();
        championship.setFinalTrophiesJson("""
            [
             {"tournamentId":31,"tournamentName":"Liga","format":"LEAGUE","entryType":"TEAM",
              "winnerSeasonTeamId":15,"winnerName":"Leões"},
             {"tournamentId":null,"ownedEventId":29,"tournamentName":"Taça","format":"KNOCKOUT",
              "entryType":"TEAM","winnerSeasonTeamId":15,"winnerName":"Leões"},
             {"tournamentId":32,"tournamentName":"Liga","format":"LEAGUE","entryType":"TEAM",
              "winnerSeasonTeamId":99,"winnerName":"Leões"},
             {"tournamentId":33,"tournamentName":"Individual","format":"LEAGUE",
              "entryType":"INDIVIDUAL","winnerProfileId":2,"winnerName":"Leões"}
            ]
            """);
        var seasonTeam = CommunityChampionshipTeam.builder().id(15L)
                .championship(championship).name("Leões").build();
        when(seasonTeams.closedPublicSeasonsForGlobalTeam(44L)).thenReturn(List.of(seasonTeam));
        var honours = service.honours(44L);
        assertEquals(2, honours.size());
        assertEquals("Liga", honours.get(0).competitionName());
        assertEquals("Taça", honours.get(1).competitionName());
        assertEquals("2026/27", honours.get(0).seasonLabel());
        assertEquals(29L, honours.get(1).ownedEventId());
        assertEquals(6L, honours.get(0).communityId());
    }

    @Test
    void emptySeasonSnapshotsNeverInventTrophies() {
        when(teams.existsById(44L)).thenReturn(true);
        var season = CommunityChampionship.builder().id(8L)
            .community(Community.builder().id(6L).name("Liga Lisboa").build())
            .closedAt(Instant.now()).finalTrophiesJson("[]").build();
        when(seasonTeams.closedPublicSeasonsForGlobalTeam(44L)).thenReturn(
            List.of(CommunityChampionshipTeam.builder().id(15L).championship(season).build()));
        assertTrue(service.honours(44L).isEmpty());
    }

    @Test
    void missingPermanentTeamCannotReadChampionshipHonours() {
        when(teams.existsById(888L)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> service.honours(888L));
        verifyNoInteractions(seasonTeams);
    }
}
