package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityChampionshipTeamsServiceTest {
    @Mock CommunityChampionshipRepository championships;
    @Mock CommunityChampionshipTeamRepository seasonTeams;
    @Mock CommunityTournamentTeamRepository tournamentTeams;
    @Mock ProfileRepository profiles;
    @Mock CommunityMemberRepository members;
    @Mock CommunityChampionshipTeamPlayerRepository playerRegistrations;
    @Mock CommunityChampionshipGoalRepository goals;
    @Mock CommunityChampionshipDisciplinaryCardRepository disciplinaryCards;
    @Mock CommunityChampionshipSubstitutionRepository substitutions;
    @Mock CommunityChampionshipLineupPlayerRepository lineups;
    @InjectMocks CommunityChampionshipTeamsService service;
    private CommunityChampionship season;
    private CommunityTournament tournament;
    private CommunityChampionshipTeam canonical;

    @BeforeEach
    void setup() {
        season = CommunityChampionship.builder().id(15L).competitions(new ArrayList<>()).build();
        tournament = CommunityTournament.builder().id(70L).name("Taça").build();
        season.getCompetitions().add(tournament);
        canonical = CommunityChampionshipTeam.builder().id(99L).name("Águias")
                .normalizedName("águias").championship(season).createdAt(Instant.now()).build();
    }

    private void organizer() {
        var actor = new Profile();
        actor.setId(33L);
        when(profiles.findUserByUsername("owner")).thenReturn(Optional.of(actor));
        when(members.findByCommunityIdAndProfileId(7L,33L))
                .thenReturn(Optional.of(CommunityMember.builder().role(CommunityRole.OWNER).build()));
        lenient().when(members.existsByCommunityIdAndProfileId(7L,33L)).thenReturn(true);
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(season));
    }

    @Test
    void administratorInvitationRequiresExplicitConsentBeforeRosterPermissions() {
        organizer();
        var invited = new Profile();
        invited.setId(44L);
        invited.setUsername("jogador");
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(members.existsByCommunityIdAndProfileId(7L, 44L)).thenReturn(true);
        when(profiles.findById(44L)).thenReturn(Optional.of(invited));
        when(profiles.findUserByUsername("jogador")).thenReturn(Optional.of(invited));
        when(members.findByCommunityIdAndProfileId(7L, 44L))
                .thenReturn(Optional.of(CommunityMember.builder().role(CommunityRole.MEMBER).build()));
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 44L))
                .thenReturn(Optional.empty());
        service.inviteAdministrator(7L, 15L, 99L, 44L, "owner");
        assertNull(canonical.getAdminProfile());
        assertEquals(44L, canonical.getPendingAdminProfile().getId());
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.addPlayer(7L, 15L, 99L, 55L, "jogador"));
        verify(playerRegistrations, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
        var accepted = service.respondAdministrator(7L, 15L, 99L, true, "jogador");
        assertEquals(44L, accepted.adminProfileId());
        assertNull(accepted.pendingProfileId());
        assertTrue(accepted.canManage());
        verify(playerRegistrations).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void decliningInvitationDoesNotGrantAdministration() {
        organizer();
        var invited = new Profile();
        invited.setId(44L);
        invited.setUsername("jogador");
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(members.existsByCommunityIdAndProfileId(7L, 44L)).thenReturn(true);
        when(profiles.findById(44L)).thenReturn(Optional.of(invited));
        when(profiles.findUserByUsername("jogador")).thenReturn(Optional.of(invited));
        service.inviteAdministrator(7L, 15L, 99L, 44L, "owner");
        var response = service.respondAdministrator(7L, 15L, 99L, false, "jogador");
        assertNull(response.adminProfileId());
        assertNull(canonical.getPendingAdminProfile());
        verify(playerRegistrations, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void unrelatedMemberCannotAcceptSomeoneElsesAdministratorInvitation() {
        var outsider = new Profile();
        outsider.setId(55L);
        when(profiles.findUserByUsername("outsider")).thenReturn(Optional.of(outsider));
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(season));
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        var invited = new Profile();
        invited.setId(44L);
        canonical.setPendingAdminProfile(invited);
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.respondAdministrator(7L, 15L, 99L, true, "outsider"));
        assertNull(canonical.getAdminProfile());
        verify(playerRegistrations, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void addsRealCommunityMemberToSeasonTeam() {
        organizer();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(members.existsByCommunityIdAndProfileId(7L, 44L)).thenReturn(true);
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 44L)).thenReturn(Optional.empty());
        var player = new Profile();
        player.setId(44L);
        player.setUsername("jogador");
        player.setFullName("Jogador Teste");
        when(profiles.findById(44L)).thenReturn(Optional.of(player));
        when(playerRegistrations.saveAndFlush(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(playerRegistrations.findByTeamIdOrderByJoinedAtAscIdAsc(99L))
                .thenReturn(List.of(CommunityChampionshipTeamPlayer.builder()
                        .id(1L).championship(season).team(canonical).profile(player)
                        .joinedAt(Instant.now()).build()));
        var roster = service.addPlayer(7L, 15L, 99L, 44L, "owner");
        assertEquals(1, roster.size());
        assertEquals(44L, roster.get(0).profileId());
        assertEquals("Jogador Teste", roster.get(0).displayName());
    }

    @Test
    void rejectsProfileOutsideCommunity() {
        organizer();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(members.existsByCommunityIdAndProfileId(7L, 44L)).thenReturn(false);
        assertThrows(ResponseStatusException.class,
                () -> service.addPlayer(7L, 15L, 99L, 44L, "owner"));
        verifyNoInteractions(playerRegistrations);
    }

    @Test
    void rejectsProfileAlreadyOnAnotherSeasonTeam() {
        organizer();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(members.existsByCommunityIdAndProfileId(7L, 44L)).thenReturn(true);
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 44L))
                .thenReturn(Optional.of(CommunityChampionshipTeamPlayer.builder().id(1L).build()));
        assertThrows(ResponseStatusException.class,
                () -> service.addPlayer(7L, 15L, 99L, 44L, "owner"));
        verify(playerRegistrations, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void preventsRosterRemovalWhenPlayerHasRecordedGoal() {
        organizer();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        var registration = CommunityChampionshipTeamPlayer.builder()
                .id(2L).team(canonical).championship(season).build();
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 44L))
                .thenReturn(Optional.of(registration));
        when(goals.existsByFixtureEventChampionshipIdAndScorerId(15L, 44L)).thenReturn(true);
        assertThrows(ResponseStatusException.class,
                () -> service.removePlayer(7L, 15L, 99L, 44L, "owner"));
        verify(playerRegistrations, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void preventsRosterRemovalWhenPlayerAppearedOnlyInSubstitution() {
        organizer();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        var registration = CommunityChampionshipTeamPlayer.builder()
                .id(2L).team(canonical).championship(season).build();
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 44L))
                .thenReturn(Optional.of(registration));
        when(substitutions.existsByFixtureEventChampionshipIdAndPlayerInId(15L, 44L))
                .thenReturn(true);
        assertThrows(ResponseStatusException.class,
                () -> service.removePlayer(7L, 15L, 99L, 44L, "owner"));
        verify(playerRegistrations, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void refusesRosterRemovalWhenPlayerIsSelectedForMatch() {
        organizer();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        var registration = CommunityChampionshipTeamPlayer.builder()
                .id(2L).team(canonical).championship(season).build();
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 44L))
                .thenReturn(Optional.of(registration));
        when(lineups.existsByFixtureEventChampionshipIdAndPlayerId(15L, 44L)).thenReturn(true);
        assertThrows(ResponseStatusException.class,
                () -> service.removePlayer(7L, 15L, 99L, 44L, "owner"));
        verify(playerRegistrations, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void allowsRemovalWhenPlayerHasNoRecordedMatchEvents() {
        organizer();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        var registration = CommunityChampionshipTeamPlayer.builder()
                .id(2L).team(canonical).championship(season).build();
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 44L))
                .thenReturn(Optional.of(registration));
        service.removePlayer(7L, 15L, 99L, 44L, "owner");
        verify(playerRegistrations).delete(registration);
    }

    @Test
    void linksTeamOnlyWhenTournamentBelongsToSeason() {
        organizer();
        var participant = CommunityTournamentTeam.builder().id(88L).name("Águias na Taça")
                .tournament(tournament).build();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(tournamentTeams.findById(88L)).thenReturn(Optional.of(participant));
        when(tournamentTeams.findByTournamentIdOrderByIdAsc(70L)).thenReturn(List.of(participant));
        var response = service.link(7L,15L,88L,99L,"owner");
        assertEquals(99L,participant.getChampionshipTeam().getId());
        assertEquals(99L,response.competitionTeams().get(0).championshipTeamId());
        verify(tournamentTeams).saveAndFlush(participant);
    }

    @Test
    void refusesCanonicalTeamFromAnotherChampionship() {
        organizer();
        canonical.setChampionship(CommunityChampionship.builder().id(16L).build());
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        assertThrows(ResponseStatusException.class, () -> service.link(7L,15L,88L,99L,"owner"));
        verifyNoInteractions(tournamentTeams);
    }

    @Test
    void refusesTwoTournamentTeamsUsingSameSeasonIdentity() {
        organizer();
        var first = CommunityTournamentTeam.builder().id(88L).name("A").tournament(tournament).build();
        var other = CommunityTournamentTeam.builder().id(89L).name("B")
                .tournament(tournament).championshipTeam(canonical).build();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(tournamentTeams.findById(88L)).thenReturn(Optional.of(first));
        when(tournamentTeams.findByTournamentIdOrderByIdAsc(70L)).thenReturn(List.of(first,other));
        assertThrows(ResponseStatusException.class, () -> service.link(7L,15L,88L,99L,"owner"));
        verify(tournamentTeams,never()).saveAndFlush(first);
    }
}
