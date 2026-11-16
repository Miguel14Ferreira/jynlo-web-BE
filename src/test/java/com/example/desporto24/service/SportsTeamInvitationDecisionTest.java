package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamInvitationDecisionTest {
    @Mock SportsTeamRepository teams;
    @Mock SportsTeamMemberRepository members;
    @Mock ProfileRepository profiles;
    @InjectMocks SportsTeamMembershipService service;

    @Test
    void acceptingInvitationUsesLockedMembershipAndRejectsDuplicateDecision() {
        var profile = new Profile();
        profile.setId(42L);
        profile.setUsername("player");
        var invitation = SportsTeamMember.builder()
                .status(SportsTeamMember.Status.INVITED).build();
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(profile));
        when(members.findInvitationForUpdate(7L, 42L)).thenReturn(Optional.of(invitation));

        service.respond(7L, "player", true);
        assertEquals(SportsTeamMember.Status.ACTIVE, invitation.getStatus());
        assertNotNull(invitation.getRespondedAt());
        assertThrows(ResponseStatusException.class,
                () -> service.respond(7L, "player", false));

        verify(members, times(2)).findInvitationForUpdate(7L, 42L);
        verify(members, times(1)).saveAndFlush(invitation);
        verify(members, never()).findByTeamIdAndProfileId(7L, 42L);
    }

    @Test
    void decliningInvitationAlsoPreventsLaterAcceptance() {
        var profile = new Profile();
        profile.setId(42L);
        profile.setUsername("player");
        var invitation = SportsTeamMember.builder()
                .status(SportsTeamMember.Status.INVITED).build();
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(profile));
        when(members.findInvitationForUpdate(7L, 42L)).thenReturn(Optional.of(invitation));

        service.respond(7L, "player", false);
        assertEquals(SportsTeamMember.Status.DECLINED, invitation.getStatus());
        assertThrows(ResponseStatusException.class,
                () -> service.respond(7L, "player", true));
        verify(members, times(1)).saveAndFlush(invitation);
    }

    @Test
    void suspendedPlayerCannotAcceptButCanDeclineInvitation() {
        var profile = new Profile();
        profile.setId(42L);
        profile.setUsername("player");
        profile.setModerationSuspendedAt(java.time.Instant.now());
        var invitation = SportsTeamMember.builder()
                .status(SportsTeamMember.Status.INVITED).build();
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(profile));
        when(members.findInvitationForUpdate(7L, 42L)).thenReturn(Optional.of(invitation));

        assertThrows(ResponseStatusException.class,
                () -> service.respond(7L, "player", true));
        assertEquals(SportsTeamMember.Status.INVITED, invitation.getStatus());
        verify(members, never()).saveAndFlush(any());

        service.respond(7L, "player", false);
        assertEquals(SportsTeamMember.Status.DECLINED, invitation.getStatus());
        verify(members).saveAndFlush(invitation);
    }

    @Test
    void disabledPlayerCannotAcceptAnOldInvitation() {
        var profile = new Profile();
        profile.setId(42L);
        profile.setUsername("player");
        profile.setEnabled(false);
        var invitation = SportsTeamMember.builder()
                .status(SportsTeamMember.Status.INVITED).build();
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(profile));
        when(members.findInvitationForUpdate(7L, 42L)).thenReturn(Optional.of(invitation));

        assertThrows(ResponseStatusException.class,
                () -> service.respond(7L, "player", true));
        assertEquals(SportsTeamMember.Status.INVITED, invitation.getStatus());
        verify(members, never()).saveAndFlush(any());
    }

    @Test
    void removingConfirmedPlayerPublishesRevocationOnlyAfterMembershipDeletion() {
        var captain = new Profile();
        captain.setId(1L);
        captain.setUsername("captain");
        var player = new Profile();
        player.setId(42L);
        player.setUsername("player");
        var team = com.example.desporto24.model.team.SportsTeam.builder()
                .id(7L).owner(captain).build();
        var membership = SportsTeamMember.builder()
                .team(team).profile(player).status(SportsTeamMember.Status.ACTIVE).build();
        var publisher = mock(org.springframework.context.ApplicationEventPublisher.class);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "realtimeEvents", publisher);
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));
        when(members.findByTeamIdAndProfileId(7L, 42L)).thenReturn(Optional.of(membership));

        service.remove(7L, 42L, "captain");

        var order = inOrder(members, publisher);
        order.verify(members).delete(membership);
        order.verify(members).flush();
        order.verify(publisher).publishEvent(
                new com.example.desporto24.websocket.TeamMembershipRevokedEvent(7L, "player"));
    }

    @Test
    void removingPendingInviteDoesNotBroadcastPrivateChatRevocation() {
        var captain = new Profile();
        captain.setId(1L);
        captain.setUsername("captain");
        var player = new Profile();
        player.setId(42L);
        player.setUsername("player");
        var team = com.example.desporto24.model.team.SportsTeam.builder()
                .id(7L).owner(captain).build();
        var membership = SportsTeamMember.builder()
                .team(team).profile(player).status(SportsTeamMember.Status.INVITED).build();
        var publisher = mock(org.springframework.context.ApplicationEventPublisher.class);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "realtimeEvents", publisher);
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));
        when(members.findByTeamIdAndProfileId(7L, 42L)).thenReturn(Optional.of(membership));

        service.remove(7L, 42L, "captain");

        verify(members).delete(membership);
        verifyNoInteractions(publisher);
    }

    @Test
    void confirmedPlayerCanLeaveTheirOwnTeamAndRevokeChatAccess() {
        var captain = new Profile();
        captain.setId(1L);
        captain.setUsername("captain");
        var player = new Profile();
        player.setId(42L);
        player.setUsername("player");
        var team = com.example.desporto24.model.team.SportsTeam.builder()
                .id(7L).owner(captain).build();
        var membership = SportsTeamMember.builder()
                .team(team).profile(player).status(SportsTeamMember.Status.ACTIVE).build();
        var publisher = mock(org.springframework.context.ApplicationEventPublisher.class);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "realtimeEvents", publisher);
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(player));
        when(members.findByTeamIdAndProfileId(7L, 42L)).thenReturn(Optional.of(membership));

        service.remove(7L, 42L, "player");

        var order = inOrder(members, publisher);
        order.verify(members).delete(membership);
        order.verify(members).flush();
        order.verify(publisher).publishEvent(
                new com.example.desporto24.websocket.TeamMembershipRevokedEvent(7L, "player"));
    }

    @Test
    void nonOwnerCannotRemoveAnotherPlayerFromTheTeam() {
        var captain = new Profile();
        captain.setId(1L);
        var caller = new Profile();
        caller.setId(42L);
        caller.setUsername("caller");
        var team = com.example.desporto24.model.team.SportsTeam.builder()
                .id(7L).owner(captain).build();
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("caller")).thenReturn(Optional.of(caller));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.remove(7L, 99L, "caller"));
        verify(members, never()).delete(any());
    }

    @Test
    void captainCannotLeaveUsingMemberRemovalEndpoint() {
        var captain = new Profile();
        captain.setId(1L);
        captain.setUsername("captain");
        var team = com.example.desporto24.model.team.SportsTeam.builder()
                .id(7L).owner(captain).build();
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));

        assertThrows(ResponseStatusException.class,
                () -> service.remove(7L, 1L, "captain"));
        verify(members, never()).delete(any());
    }
}
