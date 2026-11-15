package com.example.desporto24.service;

import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.community.CommunityTournamentTeam;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamHonoursServiceTest {
    @Mock private SportsTeamRepository teams;
    @Mock private ProfileRepository profiles;
    @Mock private CommunityTournamentTeamRepository tournamentTeams;
    @InjectMocks private SportsTeamService service;

    @Test
    void onlyUndisputedOfficiallyCompletedWinsCreateTrophies() {
        var owner = new Profile();
        owner.setId(2L);
        var global = SportsTeam.builder().id(8L).owner(owner).name("Leões").slug("leoes").build();
        when(teams.findById(8L)).thenReturn(Optional.of(global));
        var community = Community.builder().id(17L).name("Futeboladas").build();
        var finished = CommunityTournament.builder().id(31L).name("Taça").sport("Futebol")
                .community(community).status(TournamentStatus.COMPLETED)
                .completedAt(Instant.parse("2026-11-15T10:00:00Z"))
                .championTeamId(44L).championTied(false).build();
        var draw = CommunityTournament.builder().id(32L).name("Liga").sport("Futebol")
                .community(community).status(TournamentStatus.COMPLETED)
                .championTeamId(null).championTied(true).build();
        var ongoing = CommunityTournament.builder().id(33L).name("Época").sport("Futebol")
                .community(community).status(TournamentStatus.IN_PROGRESS)
                .championTeamId(46L).build();
        when(tournamentTeams.historyForGlobalTeam(8L)).thenReturn(List.of(
                CommunityTournamentTeam.builder().id(44L).tournament(finished).globalTeam(global).build(),
                CommunityTournamentTeam.builder().id(45L).tournament(draw).globalTeam(global).build(),
                CommunityTournamentTeam.builder().id(46L).tournament(ongoing).globalTeam(global).build()));
        var history = service.competitionHistory(8L);
        assertEquals(3, history.size());
        assertTrue(history.get(0).champion());
        assertTrue(history.get(0).archived());
        assertEquals("Futeboladas", history.get(0).communityName());
        assertFalse(history.get(1).champion());
        assertTrue(history.get(1).archived());
        assertFalse(history.get(2).champion());
        assertFalse(history.get(2).archived());
        verify(tournamentTeams).historyForGlobalTeam(8L);
    }

    @Test
    void unknownPermanentTeamDoesNotLeakCompetitionHistory() {
        when(teams.findById(700L)).thenReturn(Optional.empty());
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.competitionHistory(700L));
        verifyNoInteractions(tournamentTeams);
    }
}
