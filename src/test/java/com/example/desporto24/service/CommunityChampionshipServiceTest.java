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
    @Mock CommunityChampionshipEventRepository ownedEvents;
    @Mock CommunityTournamentRepository tournaments;
    @Mock ProfileRepository profiles;
    @Mock CommunityTournamentStandingsService leagueStandings;
    @Mock CommunityTournamentTeamStandingsService teamLeagueStandings;
    @Mock CommunityTournamentTeamRepository tournamentTeams;
    @Mock CommunityChampionshipTeamRepository championshipTeams;
    @Mock jakarta.persistence.EntityManager entityManager;
    @Mock jakarta.persistence.Query nativeQuery;
    @org.mockito.Spy com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
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
    void refusesToDeleteAClosedChampionshipEvenWhenItHasNoTeams() {
        organizer();
        championship.setClosedAt(Instant.parse("2026-11-11T18:00:00Z"));
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(championship));
        var error = assertThrows(ResponseStatusException.class,
                () -> service.deleteUnplayed(7L, 15L, "organizer"));
        assertEquals(org.springframework.http.HttpStatus.CONFLICT, error.getStatusCode());
        verifyNoInteractions(entityManager);
    }

    @Test
    void refusesToDeleteAChampionshipWithIndependentTournaments() {
        organizer();
        championship.getCompetitions().add(CommunityTournament.builder().id(21L).build());
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(championship));
        assertThrows(ResponseStatusException.class,
                () -> service.deleteUnplayed(7L, 15L, "organizer"));
        verifyNoInteractions(entityManager);
    }

    @Test
    void refusesToDeleteAChampionshipWithCompletedInternalEvents() {
        organizer();
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(championship));
        when(ownedEvents.findByChampionshipIdOrderByCreatedAtAscIdAsc(15L))
                .thenReturn(java.util.List.of(CommunityChampionshipEvent.builder().status("COMPLETED").build()));
        assertThrows(ResponseStatusException.class,
                () -> service.deleteUnplayed(7L, 15L, "organizer"));
        verifyNoInteractions(entityManager);
    }

    @Test
    void refusesToDeleteAScheduledChampionshipWithRecordedMatchResults() {
        organizer();
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(championship));
        when(ownedEvents.findByChampionshipIdOrderByCreatedAtAscIdAsc(15L))
                .thenReturn(java.util.List.of(CommunityChampionshipEvent.builder().status("SCHEDULED").build()));
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", 15L)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(1L);
        var error = assertThrows(ResponseStatusException.class,
                () -> service.deleteUnplayed(7L, 15L, "organizer"));
        assertEquals(org.springframework.http.HttpStatus.CONFLICT, error.getStatusCode());
        verify(nativeQuery, never()).executeUpdate();
    }

    @Test
    void deletesUnplayedCompetitionFixturesAndTeamsInOneAuthorizedTransaction() {
        organizer();
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(championship));
        when(ownedEvents.findByChampionshipIdOrderByCreatedAtAscIdAsc(15L))
                .thenReturn(java.util.List.of(CommunityChampionshipEvent.builder().status("SCHEDULED").build()));
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter("id", 15L)).thenReturn(nativeQuery);
        when(nativeQuery.getSingleResult()).thenReturn(0L);
        service.deleteUnplayed(7L, 15L, "organizer");
        verify(entityManager).createNativeQuery("DELETE FROM desporto24.community_championship WHERE id = :id");
        verify(nativeQuery, times(8)).executeUpdate();
    }

    @Test
    void canSwitchToIndependentWinnersBeforeAnyCompetitionStarts() {
        organizer();
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        when(ownedEvents.findByChampionshipIdOrderByCreatedAtAscIdAsc(15L))
                .thenReturn(java.util.List.of());
        when(championships.saveAndFlush(championship)).thenReturn(championship);
        var result = service.setStandingsMode(7L, 15L, "INDEPENDENT", "organizer");
        assertEquals("INDEPENDENT", result.standingsMode());
        assertTrue(result.winnerStandings().isEmpty());
    }

    @Test
    void refusesToChangeRankingModeAfterACompetitionBegins() {
        organizer();
        championship.getCompetitions().add(CommunityTournament.builder()
                .id(91L).name("Taça").status(TournamentStatus.IN_PROGRESS).build());
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        assertThrows(ResponseStatusException.class,
                () -> service.setStandingsMode(7L, 15L, "INDEPENDENT", "organizer"));
        assertEquals("AGGREGATE", championship.getStandingsMode());
        verify(championships, never()).saveAndFlush(any());
    }

    @Test
    void independentSeasonHasNoAggregateLeaderboard() {
        Profile actor = new Profile();
        actor.setId(33L);
        actor.setUsername("organizer");
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(actor));
        when(members.existsByCommunityIdAndProfileId(7L, 33L)).thenReturn(true);
        championship.setStandingsMode("INDEPENDENT");
        championship.getCompetitions().add(CommunityTournament.builder()
                .id(99L).status(TournamentStatus.COMPLETED).championProfileId(33L).build());
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var result = service.standings(7L, 15L, "organizer");
        assertTrue(result.standings().isEmpty());
        assertEquals(0, result.pendingCompetitions());
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
    void createsIndependentSeasonWithoutGlobalChampion() {
        organizer();
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        when(championships.save(any())).thenAnswer(invocation -> {
            CommunityChampionship saved = invocation.getArgument(0);
            saved.setId(15L);
            return saved;
        });
        var result = service.create(7L, "organizer",
                new CreateCommunityChampionshipRequest("Taças 2026", "Futebol", "2026/27", "INDEPENDENT"));
        assertEquals("INDEPENDENT", result.standingsMode());
        assertTrue(result.winnerStandings().isEmpty());
    }

    @Test
    void rejectsUnknownSeasonRankingMode() {
        organizer();
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        assertThrows(ResponseStatusException.class,
                () -> service.create(7L, "organizer",
                        new CreateCommunityChampionshipRequest("Época", "Futebol", "2026/27", "UNSUPPORTED")));
        verify(championships, never()).save(any());
    }

    @Test
    void supportsEveryCatalogSportAsSeasonContainer() {
        organizer();
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        when(championships.save(any())).thenAnswer(invocation -> {
            CommunityChampionship value = invocation.getArgument(0);
            value.setId(20L);
            return value;
        });
        for (String sport : com.example.desporto24.constant.SportsCatalog.ALLOWED) {
            var result = service.create(7L, "organizer",
                    new CreateCommunityChampionshipRequest("Época", sport, "2026/27"));
            assertEquals(sport, result.sport());
        }
        assertTrue(com.example.desporto24.constant.SportsCatalog.ALLOWED.size() >= 121);
    }

    @Test
    void rejectsUnknownChampionshipSport() {
        organizer();
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        assertThrows(ResponseStatusException.class, () -> service.create(7L, "organizer",
                new CreateCommunityChampionshipRequest("Época", "ModalidadeInventada", "2026/27")));
        verify(championships, never()).save(any());
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
    void completedTeamLeagueScoresPositionsOnlyForFullyMappedTeams() {
        Profile actor = new Profile();
        actor.setId(33L);
        actor.setUsername("organizer");
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(actor));
        when(members.existsByCommunityIdAndProfileId(7L,33L)).thenReturn(true);
        var league = CommunityTournament.builder().id(81L).community(community).name("Liga")
                .sport("Futebol").format(TournamentFormat.LEAGUE)
                .entryType(com.example.desporto24.enumeration.TournamentEntryType.TEAM)
                .status(TournamentStatus.COMPLETED).championTeamId(501L)
                .championTeamName("Águias B").championshipPlacePoints("25,18")
                .championshipWinnerPoints(25).build();
        championship.getCompetitions().add(league);
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(championship));
        var canonical1 = CommunityChampionshipTeam.builder().id(71L).name("Águias").championship(championship).build();
        var canonical2 = CommunityChampionshipTeam.builder().id(72L).name("Leões").championship(championship).build();
        var team1 = CommunityTournamentTeam.builder().id(501L).tournament(league).championshipTeam(canonical1).build();
        var team2 = CommunityTournamentTeam.builder().id(502L).tournament(league).championshipTeam(canonical2).build();
        when(tournamentTeams.findByTournamentIdOrderByIdAsc(81L)).thenReturn(java.util.List.of(team1,team2));
        var row1 = new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingRowResponse(
                1,501L,"Águias",2,2,0,0,4,0,4,6);
        var row2 = new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingRowResponse(
                2,502L,"Leões",2,1,0,1,2,2,0,3);
        when(teamLeagueStandings.standings(7L,81L,"organizer")).thenReturn(
                new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingsResponse(
                        81L,TournamentStatus.COMPLETED,2,2,3,1,0,java.util.List.of(row1,row2)));
        var full = service.standings(7L,15L,"organizer");
        assertEquals(1,full.fullyScoredCompetitions());
        assertEquals(0,full.winnerOnlyCompetitions());
        assertEquals(25,full.standings().get(0).points());
        assertEquals(18,full.standings().get(1).points());
    }

    @Test
    void teamLeagueWithUnmappedRunnerUpFallsBackToWinnerOnly() {
        Profile actor = new Profile();
        actor.setId(33L);
        actor.setUsername("organizer");
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(actor));
        when(members.existsByCommunityIdAndProfileId(7L,33L)).thenReturn(true);
        var league = CommunityTournament.builder().id(82L).community(community).name("Liga")
                .sport("Futebol").format(TournamentFormat.LEAGUE)
                .entryType(com.example.desporto24.enumeration.TournamentEntryType.TEAM)
                .status(TournamentStatus.COMPLETED).championTeamId(601L)
                .championTeamName("Águias").championshipPlacePoints("25,18")
                .championshipWinnerPoints(25).build();
        championship.getCompetitions().add(league);
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(championship));
        var team1 = CommunityTournamentTeam.builder().id(601L).tournament(league).build();
        var team2 = CommunityTournamentTeam.builder().id(602L).tournament(league).build();
        when(tournamentTeams.findByTournamentIdOrderByIdAsc(82L)).thenReturn(java.util.List.of(team1,team2));
        var row1 = new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingRowResponse(
                1,601L,"Águias",2,2,0,0,4,0,4,6);
        var row2 = new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingRowResponse(
                2,602L,"Leões",2,1,0,1,2,2,0,3);
        when(teamLeagueStandings.standings(7L,82L,"organizer")).thenReturn(
                new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingsResponse(
                        82L,TournamentStatus.COMPLETED,2,2,3,1,0,java.util.List.of(row1,row2)));
        var result = service.standings(7L,15L,"organizer");
        assertEquals(0,result.fullyScoredCompetitions());
        assertEquals(1,result.winnerOnlyCompetitions());
        assertEquals(1,result.standings().size());
        assertEquals("TEAM_UNLINKED",result.standings().get(0).entryType());
    }

    @Test
    void closesCompletedSeasonAndReadsFrozenStandingsAfterSourceChanges() {
        organizer();
        Profile actor = new Profile();
        actor.setId(33L);
        actor.setUsername("organizer");
        when(members.existsByCommunityIdAndProfileId(7L, 33L)).thenReturn(true);
        var cup = CommunityTournament.builder().id(91L).name("Taça")
                .community(community).sport("Futebol")
                .format(TournamentFormat.KNOCKOUT)
                .entryType(com.example.desporto24.enumeration.TournamentEntryType.INDIVIDUAL)
                .status(TournamentStatus.COMPLETED).championProfileId(44L)
                .championDisplayName("Ana").championshipWinnerPoints(25).build();
        championship.getCompetitions().add(cup);
        when(championships.findForClosing(15L,7L)).thenReturn(Optional.of(championship));
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(championship));
        when(championships.findByCommunityIdOrderByCreatedAtDescIdDesc(7L))
                .thenReturn(java.util.List.of(championship));
        when(championships.saveAndFlush(championship)).thenReturn(championship);

        var closed = service.close(7L,15L,"organizer");
        assertNotNull(closed.closedAt());
        assertEquals("Ana", closed.championDisplayName());
        assertEquals(44L, closed.championParticipantId());
        assertNotNull(championship.getFinalStandingsJson());
        assertNotNull(championship.getFinalCompetitionsJson());
        assertNotNull(championship.getFinalTrophiesJson());
        assertTrue(championship.getFinalTrophiesJson().contains("Taça"));
        assertEquals("Taça", closed.competitions().get(0).name());
        assertEquals(25, service.standings(7L,15L,"organizer").standings().get(0).points());

        cup.setName("Prova alterada");
        cup.setChampionshipWinnerPoints(999);
        assertEquals("Taça", service.list(7L,"organizer").get(0).competitions().get(0).name());
                cup.setChampionDisplayName("Nome alterado");
        cup.setChampionshipWinnerPoints(999);
        var historical = service.standings(7L,15L,"organizer");
        assertEquals(25, historical.standings().get(0).points());
        assertEquals("Ana", historical.standings().get(0).name());
        assertThrows(ResponseStatusException.class,
                () -> service.close(7L,15L,"organizer"));
        when(tournaments.findById(99L)).thenReturn(Optional.of(
                CommunityTournament.builder().id(99L).name("Outra prova")
                        .sport("Futebol").community(community).build()));
        assertThrows(ResponseStatusException.class,
                () -> service.addCompetition(7L,15L,99L,"organizer"));
    }

    @Test
    void refusesToArchiveLeagueWhenRequiredPlacementsAreUnverified() {
        organizer();
        Profile actor = new Profile();
        actor.setId(33L);
        actor.setUsername("organizer");
        when(members.existsByCommunityIdAndProfileId(7L,33L)).thenReturn(true);
        var league = CommunityTournament.builder().id(93L).name("Liga")
                .community(community).sport("Futebol").format(TournamentFormat.LEAGUE)
                .entryType(com.example.desporto24.enumeration.TournamentEntryType.TEAM)
                .status(TournamentStatus.COMPLETED).championTeamId(500L)
                .championTeamName("Águias").championshipPlacePoints("25,18")
                .championshipWinnerPoints(25).build();
        championship.getCompetitions().add(league);
        when(championships.findForClosing(15L,7L)).thenReturn(Optional.of(championship));
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(championship));
        when(tournamentTeams.findByTournamentIdOrderByIdAsc(93L)).thenReturn(java.util.List.of());
        var standings = new com.example.desporto24.model.dto.community.CommunityTournamentTeamStandingsResponse(
                93L,TournamentStatus.COMPLETED,0,0,3,1,0,java.util.List.of());
        when(teamLeagueStandings.standings(7L,93L,"organizer")).thenReturn(standings);
        assertThrows(ResponseStatusException.class, () -> service.close(7L,15L,"organizer"));
        assertNull(championship.getClosedAt());
        verify(championships,never()).saveAndFlush(any());
    }

    @Test
    void closesSeasonFromCompletedOwnLeagueAndCupAndArchivesBothTrophies() {
        organizer();
        when(members.existsByCommunityIdAndProfileId(7L, 33L)).thenReturn(true);
        var team = CommunityChampionshipTeam.builder().id(30L).name("Águias")
                .championship(championship).build();
        var league = CommunityChampionshipEvent.builder().id(100L).name("Liga")
                .championship(championship).format("LEAGUE").status("COMPLETED")
                .winnerTeam(team).winnerTeamName("Águias")
                .finalizedAt(Instant.now()).build();
        var cup = CommunityChampionshipEvent.builder().id(101L).name("Taça")
                .championship(championship).format("CUP").status("COMPLETED")
                .winnerTeam(team).winnerTeamName("Águias")
                .finalizedAt(Instant.now()).build();
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(championship));
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        when(ownedEvents.findByChampionshipIdOrderByCreatedAtAscIdAsc(15L))
                .thenReturn(java.util.List.of(league, cup));
        when(championships.saveAndFlush(championship)).thenReturn(championship);
        var result = service.close(7L, 15L, "organizer");
        assertNotNull(result.closedAt());
        assertEquals("TEAM", result.championEntryType());
        assertEquals(30L, result.championParticipantId());
        assertTrue(championship.getFinalTrophiesJson().contains("ownedEventId"));
        assertTrue(championship.getFinalTrophiesJson().contains("Liga"));
        assertTrue(championship.getFinalTrophiesJson().contains("Taça"));
        assertEquals(20, service.standings(7L, 15L, "organizer").standings().get(0).points());
    }

    @Test
    void independentSeasonCanCloseWithOwnCupWithoutCreatingOverallChampion() {
        organizer();
        when(members.existsByCommunityIdAndProfileId(7L, 33L)).thenReturn(true);
        championship.setStandingsMode("INDEPENDENT");
        var team = CommunityChampionshipTeam.builder().id(30L).name("Águias")
                .championship(championship).build();
        var cup = CommunityChampionshipEvent.builder().id(101L).name("Taça")
                .championship(championship).format("CUP").status("COMPLETED")
                .winnerTeam(team).winnerTeamName("Águias")
                .finalizedAt(Instant.now()).build();
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(championship));
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        when(ownedEvents.findByChampionshipIdOrderByCreatedAtAscIdAsc(15L))
                .thenReturn(java.util.List.of(cup));
        when(championships.saveAndFlush(championship)).thenReturn(championship);
        var closed = service.close(7L, 15L, "organizer");
        assertNotNull(closed.closedAt());
        assertNull(closed.championParticipantId());
        assertTrue(championship.getFinalStandingsJson().contains("standings"));
        assertTrue(championship.getFinalTrophiesJson().contains("Taça"));
    }

    @Test
    void unfinishedOwnCupPreventsSeasonClose() {
        organizer();
        var cup = CommunityChampionshipEvent.builder().id(101L).name("Taça")
                .championship(championship).format("CUP").status("SCHEDULED").build();
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(championship));
        when(ownedEvents.findByChampionshipIdOrderByCreatedAtAscIdAsc(15L))
                .thenReturn(java.util.List.of(cup));
        assertThrows(ResponseStatusException.class,
                () -> service.close(7L, 15L, "organizer"));
        verify(championships, never()).saveAndFlush(any());
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
