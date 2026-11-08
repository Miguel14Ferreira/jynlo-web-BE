package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.TournamentFormat;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.dto.community.CreateCommunityChampionshipRequest;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityChampionshipServiceTest {
    @Mock CommunityRepository communities;
    @Mock CommunityMemberRepository members;
    @Mock CommunityChampionshipRepository championships;
    @Mock CommunityTournamentRepository tournaments;
    @Mock ProfileRepository profiles;
    @Mock CommunityTournamentStandingsService leagueStandings;
    @InjectMocks CommunityChampionshipService service;

    private Community community;
    private CommunityChampionship championship;

    @BeforeEach
    void setup() {
        community = Community.builder().id(7L).name("Amigos").build();
        championship = CommunityChampionship.builder()
                .id(15L).community(community).name("Campeonato 2026")
                .sport("Futebol").seasonLabel("2026/27")
                .createdAt(Instant.parse("2026-11-08T12:00:00Z"))
                .competitions(new ArrayList<>()).build();
    }

    private void organizer() {
        Profile actor = new Profile();
        actor.setId(33L);
        actor.setUsername("organizer");
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(actor));
        when(members.findByCommunityIdAndProfileId(7L, 33L))
                .thenReturn(Optional.of(CommunityMember.builder().role(CommunityRole.OWNER).build()));
    }

    @Test
    void createsSeasonContainerWithoutGeneratingTournament() {
        organizer();
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        when(championships.save(any())).thenAnswer(invocation -> {
            CommunityChampionship saved = invocation.getArgument(0);
            saved.setId(15L);
            return saved;
        });
        var result = service.create(7L, "organizer",
                new CreateCommunityChampionshipRequest("Campeonato 2026", "Futebol", "2026/27"));
        assertEquals(15L, result.id());
        assertEquals("2026/27", result.seasonLabel());
        assertTrue(result.competitions().isEmpty());
        verifyNoInteractions(tournaments);
    }

    @Test
    void refusesCompetitionFromAnotherCommunity() {
        organizer();
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        Community another = Community.builder().id(8L).build();
        when(tournaments.findById(90L)).thenReturn(Optional.of(
                CommunityTournament.builder().id(90L).community(another).sport("Futebol").build()));
        assertThrows(ResponseStatusException.class,
                () -> service.addCompetition(7L, 15L, 90L, "organizer"));
        verify(championships, never()).saveAndFlush(any());
    }

    @Test
    void refusesSportMismatchWithoutChangingTournament() {
        organizer();
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        when(tournaments.findById(90L)).thenReturn(Optional.of(
                CommunityTournament.builder().id(90L).community(community).sport("Karting").build()));
        assertThrows(ResponseStatusException.class,
                () -> service.addCompetition(7L, 15L, 90L, "organizer"));
        assertTrue(championship.getCompetitions().isEmpty());
    }

    @Test
    void linksExistingCompetitionWithoutReplacingItsResults() {
        organizer();
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        CommunityTournament cup = CommunityTournament.builder().id(90L).name("Taça")
                .community(community).sport("Futebol").format(TournamentFormat.KNOCKOUT)
                .status(TournamentStatus.COMPLETED).build();
        when(tournaments.findById(90L)).thenReturn(Optional.of(cup));
        when(championships.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var result = service.addCompetition(7L, 15L, 90L, "organizer");
        assertEquals(1, result.competitions().size());
        assertEquals("Taça", result.competitions().get(0).name());
        assertEquals(TournamentStatus.COMPLETED, cup.getStatus());
        verify(tournaments, never()).save(any());
    }

    @Test
    void winnerAwardCanBeEditedOnlyBeforeCompetitionStarts() {
        organizer();
        CommunityTournament cup = CommunityTournament.builder().id(90L).name("Taça")
                .community(community).sport("Futebol").format(TournamentFormat.KNOCKOUT)
                .status(TournamentStatus.DRAFT).build();
        championship.getCompetitions().add(cup);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        when(tournaments.saveAndFlush(cup)).thenReturn(cup);
        var response = service.setWinnerPoints(7L, 15L, 90L, 35, "organizer");
        assertEquals(35, response.competitions().get(0).winnerPoints());
        assertEquals(35, cup.getChampionshipWinnerPoints());

        cup.setStatus(TournamentStatus.IN_PROGRESS);
        assertThrows(ResponseStatusException.class,
                () -> service.setWinnerPoints(7L, 15L, 90L, 99, "organizer"));
        assertEquals(35, cup.getChampionshipWinnerPoints());
    }

    @Test
    void updatesPlacementRuleBeforeStartingAndLocksItAfterward() {
        organizer();
        CommunityTournament cup = CommunityTournament.builder().id(91L).name("Liga")
                .community(community).sport("Futebol").format(TournamentFormat.LEAGUE)
                .status(TournamentStatus.DRAFT).build();
        championship.getCompetitions().add(cup);
        when(championships.findByIdAndCommunityId(15L, 7L))
                .thenReturn(Optional.of(championship));
        when(tournaments.saveAndFlush(cup)).thenReturn(cup);
        var response = service.setPlacementPoints(7L, 15L, 91L, "25, 18, 15", "organizer");
        assertEquals("25,18,15", response.competitions().get(0).pointsByPlace());
        assertEquals(25, cup.getChampionshipWinnerPoints());
        assertEquals(25, response.competitions().get(0).winnerPoints());

        assertThrows(ResponseStatusException.class,
                () -> service.setPlacementPoints(7L, 15L, 91L, "10,20", "organizer"));

        cup.setStatus(TournamentStatus.IN_PROGRESS);
        assertThrows(ResponseStatusException.class,
                () -> service.setPlacementPoints(7L, 15L, 91L, "30,20", "organizer"));
        assertEquals("25,18,15", cup.getChampionshipPlacePoints());
    }

    @Test
    void verifiedCompletedLeagueUsesPlacementsAndNeverDoubleCountsChampion() {
        Profile actor = new Profile();
        actor.setId(33L);
        actor.setUsername("organizer");
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(actor));
        when(members.existsByCommunityIdAndProfileId(7L, 33L)).thenReturn(true);
        var league = CommunityTournament.builder().id(99L).community(community).sport("Futebol")
                .name("Liga").format(TournamentFormat.LEAGUE)
                .entryType(com.example.desporto24.enumeration.TournamentEntryType.INDIVIDUAL)
                .status(TournamentStatus.COMPLETED).championProfileId(33L)
                .championDisplayName("Ana").championshipPlacePoints("25,18")
                .championshipWinnerPoints(25).build();
        championship.getCompetitions().add(league);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var first = new com.example.desporto24.model.dto.community.CommunityTournamentStandingRowResponse(
                1,33L,"ana","Ana",2,2,0,0,4,0,4,6);
        var second = new com.example.desporto24.model.dto.community.CommunityTournamentStandingRowResponse(
                2,44L,"rui","Rui",2,1,0,1,2,2,0,3);
        when(leagueStandings.standings(7L,99L,"organizer")).thenReturn(
                new com.example.desporto24.model.dto.community.CommunityTournamentStandingsResponse(
                        99L,TournamentStatus.COMPLETED,2,2,3,1,0,java.util.List.of(first,second)));
        var result = service.standings(7L,15L,"organizer");
        assertEquals(1,result.fullyScoredCompetitions());
        assertEquals(0,result.winnerOnlyCompetitions());
        assertEquals(25,result.standings().get(0).points());
        assertEquals(18,result.standings().get(1).points());
    }

    @Test
    void deniesCreationToNonOrganizer() {
        Profile actor = new Profile();
        actor.setId(33L);
        actor.setUsername("organizer");
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(actor));
        when(members.findByCommunityIdAndProfileId(7L, 33L))
                .thenReturn(Optional.of(CommunityMember.builder().role(CommunityRole.MEMBER).build()));
        assertThrows(AccessDeniedException.class, () -> service.create(7L, "organizer",
                new CreateCommunityChampionshipRequest("Liga", "Futebol", "2026/27")));
        verifyNoInteractions(championships);
    }
}
