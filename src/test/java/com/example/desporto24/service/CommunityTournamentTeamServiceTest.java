package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.community.CommunityTournamentTeam;
import com.example.desporto24.model.community.CommunityTournamentTeamPlayer;
import com.example.desporto24.model.community.CommunityTournamentTeamInvite;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityTournamentRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamPlayerRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamInviteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityTournamentTeamServiceTest {
    @Mock private ProfileRepository profileRepository;
    @Mock private CommunityMemberRepository memberRepository;
    @Mock private CommunityTournamentRepository tournamentRepository;
    @Mock private CommunityTournamentTeamRepository teamRepository;
    @Mock private CommunityTournamentTeamPlayerRepository playerRepository;
    @Mock private CommunityTournamentTeamInviteRepository inviteRepository;
    @Mock private CommunityTournamentSuspensionService suspensionService;
    @InjectMocks private CommunityTournamentTeamService service;

    private final Profile owner = profile(1L, "owner");
    private final Profile player = profile(2L, "ana");
    private final Community community = Community.builder().id(7L).name("CNG").build();
    private final CommunityTournament teamTournament = CommunityTournament.builder()
            .id(19L).name("Liga 5x5").community(community)
            .entryType(TournamentEntryType.TEAM).playersPerTeam(2)
            .status(TournamentStatus.DRAFT).build();

    private static Profile profile(long id, String username) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        p.setFullName(username);
        return p;
    }

    private void manager() {
        when(profileRepository.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));
        when(memberRepository.findByCommunityIdAndProfileId(7L, 1L)).thenReturn(Optional.of(
                CommunityMember.builder().community(community).profile(owner)
                        .role(CommunityRole.OWNER).build()));
    }

    private void draft() {
        when(tournamentRepository.findByIdForUpdate(19L)).thenReturn(Optional.of(teamTournament));
    }

    private CommunityTournamentTeam team(long id, String name) {
        return CommunityTournamentTeam.builder().id(id).tournament(teamTournament)
                .name(name).normalizedName(name.toLowerCase()).createdAt(Instant.now()).build();
    }

    @Test
    void organizerCanReviewPendingTeamInvitationsWithoutRegisteringPlayers() {
        manager();
        when(tournamentRepository.findById(19L)).thenReturn(Optional.of(teamTournament));
        var selected = team(10L, "Azul");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(selected));
        var invite = CommunityTournamentTeamInvite.builder().team(selected)
                .tournament(teamTournament).profile(player)
                .invitedAt(Instant.parse("2026-11-12T10:00:00Z")).build();
        when(inviteRepository.findByTeamIdOrderByInvitedAtAscIdAsc(10L))
                .thenReturn(List.of(invite));
        var pending = service.pendingInvitations(7L, 19L, 10L, "owner");
        assertEquals(1, pending.size());
        assertEquals(2L, pending.get(0).profileId());
        assertEquals("ana", pending.get(0).displayName());
        verifyNoInteractions(playerRepository);
    }

    @Test
    void memberCannotInspectOrRevokeAnotherPlayersPendingInvitation() {
        when(profileRepository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(player));
        when(memberRepository.findByCommunityIdAndProfileId(7L, 2L))
                .thenReturn(Optional.of(CommunityMember.builder()
                        .community(community).profile(player).role(CommunityRole.MEMBER).build()));
        when(tournamentRepository.findById(19L)).thenReturn(Optional.of(teamTournament));
        when(tournamentRepository.findByIdForUpdate(19L)).thenReturn(Optional.of(teamTournament));
        var selected = team(10L, "Azul");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(selected));
        assertThrows(AccessDeniedException.class,
                () -> service.pendingInvitations(7L, 19L, 10L, "ana"));
        assertThrows(AccessDeniedException.class,
                () -> service.revokeInvitation(7L, 19L, 10L, 1L, "ana"));
        verify(inviteRepository, never()).delete(any());
        verify(inviteRepository, never()).findByTeamIdOrderByInvitedAtAscIdAsc(any());
    }

    @Test
    void cancellingPendingTournamentInvitationDoesNotChangeConfirmedRoster() {
        manager();
        draft();
        var selected = team(10L, "Azul");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(selected));
        var pending = CommunityTournamentTeamInvite.builder()
                .id(33L).team(selected).tournament(teamTournament).profile(player).build();
        when(inviteRepository.findByTeamIdAndProfileId(10L, 2L)).thenReturn(Optional.of(pending));
        service.revokeInvitation(7L, 19L, 10L, 2L, "owner");
        verify(inviteRepository).delete(pending);
        verifyNoInteractions(playerRepository);
    }

    @Test
    void deletingAnEmptyTeamAlsoCancelsOutstandingInvitations() {
        manager();
        draft();
        var selected = team(10L, "Azul");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(selected));
        service.deleteTeam(7L, 19L, 10L, "owner");
        var order = inOrder(inviteRepository, teamRepository);
        order.verify(inviteRepository).deleteByTeamId(10L);
        order.verify(teamRepository).delete(selected);
    }

    @Test
    void invitingAPlayerDoesNotAddThemOrAssignAdministratorBeforeAcceptance() {
        manager();
        draft();
        var azul = team(10L, "Azul");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(azul));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(profileRepository.findById(2L)).thenReturn(Optional.of(player));
        when(playerRepository.findByTournamentIdOrderByTeamIdAscIdAsc(19L)).thenReturn(List.of());

        var invited = service.invitePlayer(7L, 19L, 10L, "owner", 2L);

        assertTrue(invited.players().isEmpty());
        assertNull(azul.getAdminProfile());
        verify(inviteRepository).saveAndFlush(any(CommunityTournamentTeamInvite.class));
        verify(playerRepository, never()).save(any());
    }

    @Test
    void onlyTheInvitedAccountCanSeeOrDeclineItsPersonalInvitation() {
        var azul = team(10L, "Azul");
        when(profileRepository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(player));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(tournamentRepository.findById(19L)).thenReturn(Optional.of(teamTournament));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(azul));
        when(inviteRepository.findByTeamIdAndProfileId(10L, 2L)).thenReturn(Optional.of(
                CommunityTournamentTeamInvite.builder().id(12L).team(azul).profile(player).build()));
        assertTrue(service.hasInvitation(7L, 19L, 10L, "ana"));
        draft();
        service.declineInvitation(7L, 19L, 10L, "ana");
        verify(inviteRepository).delete(any(CommunityTournamentTeamInvite.class));
        verify(playerRepository, never()).save(any());
    }

    @Test
    void organizerCreatesRealTeamAndNormalizesItsName() {
        manager();
        draft();
        when(teamRepository.save(any())).thenAnswer(invocation -> {
            CommunityTournamentTeam t = invocation.getArgument(0);
            t.setId(10L);
            return t;
        });
        var result = service.createTeam(7L, 19L, "owner", "  Equipa   Azul  ");
        assertEquals("Equipa Azul", result.name());
        assertEquals(2, result.playersPerTeam());
        assertTrue(result.players().isEmpty());
        verify(teamRepository).existsByTournamentIdAndNormalizedName(19L, "equipa azul");
    }

    @Test
    void avoidsDuplicateTeamNamesCaseInsensitively() {
        manager();
        draft();
        when(teamRepository.existsByTournamentIdAndNormalizedName(19L, "equipa azul"))
                .thenReturn(true);
        var error = assertThrows(ResponseStatusException.class,
                () -> service.createTeam(7L, 19L, "owner", "EQUIPA AZUL"));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void managerCanAssignOnlyCommunityMemberOncePerCompetition() {
        manager();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(profileRepository.findById(2L)).thenReturn(Optional.of(player));
        var response = service.assignPlayer(7L, 19L, 10L, "owner", 2L);
        assertEquals("Azul", response.name());
        var captor = org.mockito.ArgumentCaptor.forClass(CommunityTournamentTeamPlayer.class);
        verify(playerRepository).save(captor.capture());
        assertEquals(19L, captor.getValue().getTournament().getId());
        assertEquals(10L, captor.getValue().getTeam().getId());
        assertEquals(2L, captor.getValue().getProfile().getId());
    }

    private void invitee() {
        when(profileRepository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(player));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
    }

    @Test
    void inviteeAcceptsOwnPlaceButCannotAssignAnyoneElse() {
        invitee();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        var response = service.joinInvitedTeam(7L, 19L, 10L, "ana");
        assertEquals("Azul", response.name());
        var captor = org.mockito.ArgumentCaptor.forClass(CommunityTournamentTeamPlayer.class);
        verify(playerRepository).save(captor.capture());
        assertSame(player, captor.getValue().getProfile());
        assertEquals(19L, captor.getValue().getTournament().getId());
        assertEquals(10L, captor.getValue().getTeam().getId());
        verify(memberRepository, never()).findByCommunityIdAndProfileId(7L, 2L);
    }

    @Test
    void firstPlayerWhoAcceptsBecomesTheOnlyAdministrator() {
        invitee();
        draft();
        var azul = team(10L, "Azul");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(azul));
        service.joinInvitedTeam(7L, 19L, 10L, "ana");
        assertEquals(2L, azul.getAdminProfile().getId());
        assertNull(azul.getPendingAdminProfile());
        verify(teamRepository).saveAndFlush(azul);
    }

    @Test
    void administratorCanTransferOnlyToConfirmedTeammateAfterAcceptance() {
        var azul = team(10L, "Azul");
        azul.setAdminProfile(player);
        var bea = profile(3L, "bea");
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(azul));
        when(profileRepository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(player));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(playerRepository.findByTeamIdAndProfileId(10L, 3L)).thenReturn(Optional.of(
                CommunityTournamentTeamPlayer.builder().team(azul).profile(bea).build()));
        when(profileRepository.findById(3L)).thenReturn(Optional.of(bea));
        var transfer = service.transferAdministration(7L, 19L, 10L, 3L, "ana");
        assertEquals(2L, transfer.adminProfileId());
        assertEquals(3L, transfer.pendingProfileId());
        assertEquals(2L, azul.getAdminProfile().getId(), "Pending handover must not revoke admin");

        when(profileRepository.findUserByUsernameIgnoreCase("bea")).thenReturn(Optional.of(bea));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 3L)).thenReturn(true);
        var accepted = service.respondAdministration(7L, 19L, 10L, true, "bea");
        assertEquals(3L, accepted.adminProfileId());
        assertNull(accepted.pendingProfileId());
        assertFalse(accepted.canAccept());
    }

    @Test
    void administratorCannotTransferToNonMemberOrSelf() {
        var azul = team(10L, "Azul");
        azul.setAdminProfile(player);
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(azul));
        when(profileRepository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(player));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.transferAdministration(7L, 19L, 10L, 3L, "ana")).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.transferAdministration(7L, 19L, 10L, 2L, "ana")).getStatusCode());
        assertSame(player, azul.getAdminProfile());
    }

    @Test
    void acceptingSameInviteTwiceIsIdempotent() {
        invitee();
        draft();
        var azul = team(10L, "Azul");
        when(teamRepository.findById(10L)).thenReturn(Optional.of(azul));
        when(playerRepository.findByTournamentIdAndProfileId(19L, 2L))
                .thenReturn(Optional.of(CommunityTournamentTeamPlayer.builder()
                        .team(azul).tournament(teamTournament).profile(player).build()));
        var response = service.joinInvitedTeam(7L, 19L, 10L, "ana");
        assertEquals(10L, response.id());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void invitationCannotMoveSomeoneAlreadyInDifferentTeam() {
        invitee();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(playerRepository.findByTournamentIdAndProfileId(19L, 2L))
                .thenReturn(Optional.of(CommunityTournamentTeamPlayer.builder()
                        .team(team(11L, "Verde")).tournament(teamTournament).profile(player).build()));
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.joinInvitedTeam(7L, 19L, 10L, "ana")).getStatusCode());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void invitationCannotOverfillTeamOrOverrideTournamentState() {
        invitee();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(playerRepository.countByTeamId(10L)).thenReturn(2L);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.joinInvitedTeam(7L, 19L, 10L, "ana")).getStatusCode());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void invitationCannotJoinTeamFromAnotherTournament() {
        invitee();
        draft();
        var unrelatedTournament = CommunityTournament.builder().id(20L).build();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(
                CommunityTournamentTeam.builder().id(10L).tournament(unrelatedTournament).build()));
        var error = assertThrows(ResponseStatusException.class,
                () -> service.joinInvitedTeam(7L, 19L, 10L, "ana"));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(playerRepository);
    }

    @Test
    void invitationCannotJoinAfterTournamentStarts() {
        invitee();
        teamTournament.setStatus(TournamentStatus.IN_PROGRESS);
        draft();
        var error = assertThrows(ResponseStatusException.class,
                () -> service.joinInvitedTeam(7L, 19L, 10L, "ana"));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verifyNoInteractions(teamRepository, playerRepository);
    }

    @Test
    void invitationCannotJoinAnIndividualTournament() {
        invitee();
        teamTournament.setEntryType(TournamentEntryType.INDIVIDUAL);
        draft();
        var error = assertThrows(ResponseStatusException.class,
                () -> service.joinInvitedTeam(7L, 19L, 10L, "ana"));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verifyNoInteractions(teamRepository, playerRepository);
    }

    @Test
    void invitationRequiresExistingCommunityMembership() {
        when(profileRepository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(player));
        assertThrows(AccessDeniedException.class,
                () -> service.joinInvitedTeam(7L, 19L, 10L, "ana"));
        verifyNoInteractions(tournamentRepository, teamRepository, playerRepository);
    }

    @Test
    void rejectsMemberOfAnotherCommunity() {
        manager();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        var error = assertThrows(ResponseStatusException.class,
                () -> service.assignPlayer(7L, 19L, 10L, "owner", 2L));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(profileRepository, never()).findById(2L);
    }

    @Test
    void preventsPlayerFromJoiningTwoTeamsOfSameCompetition() {
        manager();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(playerRepository.existsByTournamentIdAndProfileId(19L, 2L)).thenReturn(true);
        var error = assertThrows(ResponseStatusException.class,
                () -> service.assignPlayer(7L, 19L, 10L, "owner", 2L));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void cannotOverfillTeam() {
        manager();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(playerRepository.countByTeamId(10L)).thenReturn(2L);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.assignPlayer(7L, 19L, 10L, "owner", 2L)).getStatusCode());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void flexibleRosterAllowsInvitesAndManualAdditionsWithoutFixedSize() {
        teamTournament.setPlayersPerTeam(null);
        manager();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(profileRepository.findById(2L)).thenReturn(Optional.of(player));
        when(playerRepository.countByTeamId(10L)).thenReturn(5L);
        var assigned = service.assignPlayer(7L, 19L, 10L, "owner", 2L);
        assertNull(assigned.playersPerTeam());
        verify(playerRepository).save(any(CommunityTournamentTeamPlayer.class));

        // A player invited by WhatsApp or link can join even when the roster
        // already contains more players than the old default of five.
        when(profileRepository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(player));
        var accepted = service.joinInvitedTeam(7L, 19L, 10L, "ana");
        assertNull(accepted.playersPerTeam());
        verify(playerRepository, times(2)).save(any(CommunityTournamentTeamPlayer.class));
    }

    @Test
    void flexibleRosterEnforcesSafetyCapacityOfThirtyPlayers() {
        teamTournament.setPlayersPerTeam(null);
        manager();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(playerRepository.countByTeamId(10L)).thenReturn(30L);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.assignPlayer(7L, 19L, 10L, "owner", 2L)).getStatusCode());
        verify(playerRepository, never()).save(any());

        when(profileRepository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(player));
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.joinInvitedTeam(7L, 19L, 10L, "ana")).getStatusCode());
    }

    @Test
    void cannotManageTeamOwnedByAnotherTournament() {
        when(profileRepository.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));
        draft();
        var unrelatedTournament = CommunityTournament.builder().id(20L).build();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(
                CommunityTournamentTeam.builder().id(10L).tournament(unrelatedTournament).build()));
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> service.assignPlayer(7L, 19L, 10L, "owner", 2L)).getStatusCode());
        verifyNoInteractions(playerRepository);
    }

    @Test
    void rejectsNonOrganizers() {
        when(profileRepository.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));
        when(memberRepository.findByCommunityIdAndProfileId(7L, 1L)).thenReturn(Optional.of(
                CommunityMember.builder().role(CommunityRole.MEMBER).build()));
        assertThrows(AccessDeniedException.class, () -> service.createTeam(7L, 19L, "owner", "Azul"));
        verifyNoInteractions(tournamentRepository, teamRepository);
    }

    @Test
    void rejectsTeamChangesAfterCompetitionStarts() {
        manager();
        teamTournament.setStatus(TournamentStatus.IN_PROGRESS);
        draft();
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.createTeam(7L, 19L, "owner", "Azul")).getStatusCode());
        verifyNoInteractions(teamRepository);
    }

    @Test
    void rejectsTeamsForIndividualCompetition() {
        manager();
        teamTournament.setEntryType(TournamentEntryType.INDIVIDUAL);
        draft();
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.createTeam(7L, 19L, "owner", "Azul")).getStatusCode());
        verifyNoInteractions(teamRepository);
    }

    @Test
    void onlyCurrentCommunityMembersCanViewRosters() {
        when(profileRepository.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));
        assertThrows(AccessDeniedException.class, () -> service.list(7L, 19L, "owner"));
        verifyNoInteractions(tournamentRepository, teamRepository, playerRepository);
    }

    @Test
    void teamListLoadsAllRostersOnce() {
        when(profileRepository.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 1L)).thenReturn(true);
        when(tournamentRepository.findById(19L)).thenReturn(Optional.of(teamTournament));
        var azul = team(10L, "Azul");
        var verde = team(11L, "Verde");
        when(teamRepository.findByTournamentIdOrderByIdAsc(19L)).thenReturn(List.of(azul, verde));
        when(playerRepository.findByTournamentIdOrderByTeamIdAscIdAsc(19L)).thenReturn(
                List.of(CommunityTournamentTeamPlayer.builder()
                        .tournament(teamTournament).team(azul).profile(player).build()));

        var result = service.list(7L, 19L, "owner");
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).players().size());
        assertTrue(result.get(1).players().isEmpty());
        verify(playerRepository, times(1)).findByTournamentIdOrderByTeamIdAscIdAsc(19L);
    }

    @Test
    void cannotDeleteNonemptyTeam() {
        manager();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(playerRepository.countByTeamId(10L)).thenReturn(1L);
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.deleteTeam(7L, 19L, 10L, "owner")).getStatusCode());
        verify(teamRepository, never()).delete(any());
    }
    @Test
    void suspendedMemberCannotBeAddedToTournamentTeamByManager() {
        manager();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        when(memberRepository.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Suspenso"))
                .when(suspensionService).requireEligible(7L, 2L);

        var error = assertThrows(ResponseStatusException.class,
                () -> service.assignPlayer(7L, 19L, 10L, "owner", 2L));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void suspendedMemberCannotJoinTeamViaInvite() {
        invitee();
        draft();
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team(10L, "Azul")));
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Suspenso"))
                .when(suspensionService).requireEligible(7L, 2L);

        var error = assertThrows(ResponseStatusException.class,
                () -> service.joinInvitedTeam(7L, 19L, 10L, "ana"));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
        verify(playerRepository, never()).save(any());
    }

}
