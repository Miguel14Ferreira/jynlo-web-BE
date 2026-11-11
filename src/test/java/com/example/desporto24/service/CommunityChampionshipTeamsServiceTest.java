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
    @Mock CommunityChampionshipTeamInviteRepository teamInvites;
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
    void firstConfirmedInviteeBecomesAdministrator() {
        var joining = new Profile();
        joining.setId(44L);
        joining.setUsername("jogador");
        var invite = CommunityChampionshipTeamInvite.builder()
                .id(1L).championship(season).team(canonical).profile(joining)
                .invitedAt(Instant.now()).build();
        when(profiles.findUserByUsername("jogador")).thenReturn(Optional.of(joining));
        when(members.existsByCommunityIdAndProfileId(7L, 44L)).thenReturn(true);
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(season));
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(season));
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(teamInvites.findByTeamIdAndProfileId(99L, 44L)).thenReturn(Optional.of(invite));
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 44L))
                .thenReturn(Optional.empty());
        when(playerRegistrations.findByTeamIdOrderByJoinedAtAscIdAsc(99L)).thenReturn(List.of());
        service.respondPlayerInvite(7L, 15L, 99L, true, "jogador");
        assertSame(joining, canonical.getAdminProfile());
        verify(playerRegistrations).saveAndFlush(org.mockito.ArgumentMatchers.any());
        verify(teamInvites).delete(invite);
    }

    @Test
    void decliningTeamInvitationDoesNotAssignPlayerOrAdministrator() {
        var joining = new Profile();
        joining.setId(44L);
        var invite = CommunityChampionshipTeamInvite.builder()
                .id(1L).championship(season).team(canonical).profile(joining).build();
        when(profiles.findUserByUsername("jogador")).thenReturn(Optional.of(joining));
        when(members.existsByCommunityIdAndProfileId(7L, 44L)).thenReturn(true);
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(season));
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(season));
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(teamInvites.findByTeamIdAndProfileId(99L, 44L)).thenReturn(Optional.of(invite));
        when(playerRegistrations.findByTeamIdOrderByJoinedAtAscIdAsc(99L)).thenReturn(List.of());
        service.respondPlayerInvite(7L, 15L, 99L, false, "jogador");
        assertNull(canonical.getAdminProfile());
        verify(playerRegistrations, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
        verify(teamInvites).delete(invite);
    }

    @Test
    void acceptedAdministratorCanAddToOwnRosterButNotAnotherTeam() {
        var admin = new Profile();
        admin.setId(44L);
        when(profiles.findUserByUsername("manager")).thenReturn(Optional.of(admin));
        when(members.findByCommunityIdAndProfileId(7L, 44L))
                .thenReturn(Optional.of(CommunityMember.builder().role(CommunityRole.MEMBER).build()));
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(season));
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        canonical.setAdminProfile(admin);
        when(members.existsByCommunityIdAndProfileId(7L, 55L)).thenReturn(true);
        var player = new Profile();
        player.setId(55L);
        player.setUsername("novo");
        when(profiles.findById(55L)).thenReturn(Optional.of(player));
        when(playerRegistrations.findByChampionshipIdAndProfileId(15L, 55L))
                .thenReturn(Optional.empty());
        // A roster read still requires community membership for the admin.
        when(members.existsByCommunityIdAndProfileId(7L, 44L)).thenReturn(true);
        when(playerRegistrations.findByTeamIdOrderByJoinedAtAscIdAsc(99L))
                .thenReturn(List.of());
        service.addPlayer(7L, 15L, 99L, 55L, "manager");
        verify(playerRegistrations).saveAndFlush(org.mockito.ArgumentMatchers.any());

        var otherTeam = CommunityChampionshipTeam.builder().id(100L).name("Rivais")
                .championship(season).build();
        when(seasonTeams.findById(100L)).thenReturn(Optional.of(otherTeam));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.addPlayer(7L, 15L, 100L, 56L, "manager"));
    }

    @Test
    void unrelatedMemberCannotAcceptAnotherPlayersTransfer() {
        var outsider = new Profile();
        outsider.setId(55L);
        when(profiles.findUserByUsername("outsider")).thenReturn(Optional.of(outsider));
        when(members.existsByCommunityIdAndProfileId(7L, 55L)).thenReturn(true);
        when(championships.findForClosing(15L, 7L)).thenReturn(Optional.of(season));
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
