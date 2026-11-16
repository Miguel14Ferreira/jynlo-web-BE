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
    void completeInvitationLifecycleGrantsChatOnlyAfterAcceptanceAndRevokesItAfterRemoval() {
        var captain=new Profile();
        captain.setId(1L);
        captain.setUsername("captain");
        var player=new Profile();
        player.setId(42L);
        player.setUsername("player");
        var team=com.example.desporto24.model.team.SportsTeam.builder()
            .id(7L).owner(captain).name("Atlético")
            .chatEnabled(true).feedEnabled(true).build();
        var membership=new java.util.concurrent.atomic.AtomicReference<SportsTeamMember>();
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(player));
        when(profiles.findById(42L)).thenReturn(Optional.of(player));
        when(members.findByTeamIdAndProfileId(7L,42L))
            .thenAnswer(call->Optional.ofNullable(membership.get()));
        when(members.findInvitationForUpdate(7L,42L))
            .thenAnswer(call->Optional.ofNullable(membership.get()));
        when(members.saveAndFlush(any(SportsTeamMember.class))).thenAnswer(call->{
            SportsTeamMember saved=call.getArgument(0);
            membership.set(saved);
            return saved;
        });
        doAnswer(call->{membership.set(null);return null;})
            .when(members).delete(any(SportsTeamMember.class));

        var contentRepo=mock(com.example.desporto24.repository.team.SportsTeamContentRepository.class);
        var teamContent=new SportsTeamContentService(teams,members,contentRepo,profiles);
        when(contentRepo.findByTeamIdAndKindOrderByIdDesc(eq(7L),
            eq(com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT),
            any(org.springframework.data.domain.Pageable.class))).thenReturn(java.util.List.of());
        when(contentRepo.saveAndFlush(any(com.example.desporto24.model.team.SportsTeamContent.class)))
            .thenAnswer(call->{
                var saved=(com.example.desporto24.model.team.SportsTeamContent)call.getArgument(0);
                saved.setId(8L);
                return saved;
            });

        service.invite(7L,42L,"captain");
        assertEquals(SportsTeamMember.Status.INVITED,membership.get().getStatus());
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
            ()->teamContent.recent(7L,
                com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT,"player"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
            ()->teamContent.post(7L,
                com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT,
                new SportsTeamContentService.Submit("antes do convite"),"player"));

        service.respond(7L,"player",true);
        assertEquals(SportsTeamMember.Status.ACTIVE,membership.get().getStatus());
        assertTrue(teamContent.recent(7L,
            com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT,"player").isEmpty());
        var sent=teamContent.post(7L,
            com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT,
            new SportsTeamContentService.Submit("Olá equipa"),"player");
        assertEquals("Olá equipa",sent.body());

        service.remove(7L,42L,"captain");
        assertNull(membership.get());
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
            ()->teamContent.recent(7L,
                com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT,"player"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
            ()->teamContent.post(7L,
                com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT,
                new SportsTeamContentService.Submit("mensagem bloqueada"),"player"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
            ()->teamContent.download(7L,
                com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT,8L,0,"player"));
        verify(contentRepo,times(1)).saveAndFlush(any());
    }

    @Test
    void suspendedCaptainCannotInviteRemoveOtherPlayersOrInspectPendingInvitations() {
        var captain=new Profile();
        captain.setId(1L);
        captain.setUsername("captain");
        captain.setModerationSuspendedAt(java.time.Instant.now());
        var team=com.example.desporto24.model.team.SportsTeam.builder()
            .id(7L).owner(captain).build();
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
            ()->service.invite(7L,42L,"captain"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
            ()->service.pendingInvitations(7L,"captain"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
            ()->service.remove(7L,42L,"captain"));
        verify(members,never()).saveAndFlush(any());
        verify(members,never()).delete(any());
        verify(members,never()).findByTeamIdAndStatusOrderByIdAsc(anyLong(),any());
    }

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

    @Test
    void ownerCanListOnlyPendingInvitationsWithoutExposingOtherStates() {
        var captain = new Profile();
        captain.setId(1L);
        captain.setUsername("captain");
        var invited = new Profile();
        invited.setId(42L);
        invited.setUsername("invited");
        var team = com.example.desporto24.model.team.SportsTeam.builder()
                .id(7L).owner(captain).build();
        var membership = SportsTeamMember.builder()
                .team(team).profile(invited).status(SportsTeamMember.Status.INVITED).build();
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));
        when(members.findByTeamIdAndStatusOrderByIdAsc(7L, SportsTeamMember.Status.INVITED))
                .thenReturn(java.util.List.of(membership));

        var pending = service.pendingInvitations(7L, "captain");
        assertEquals(1, pending.size());
        assertEquals(42L, pending.get(0).profileId());
        assertEquals("invited", pending.get(0).username());
        assertEquals("INVITED", pending.get(0).status());
        verify(members, never()).findByTeamIdAndStatusOrderByIdAsc(7L, SportsTeamMember.Status.ACTIVE);
    }

    @Test
    void nonOwnerCannotReadPendingInvitations() {
        var captain = new Profile();
        captain.setId(1L);
        var stranger = new Profile();
        stranger.setId(42L);
        stranger.setUsername("stranger");
        var team = com.example.desporto24.model.team.SportsTeam.builder()
                .id(7L).owner(captain).build();
        when(teams.findById(7L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("stranger")).thenReturn(Optional.of(stranger));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.pendingInvitations(7L, "stranger"));
        verify(members, never()).findByTeamIdAndStatusOrderByIdAsc(anyLong(), any());
    }
}
