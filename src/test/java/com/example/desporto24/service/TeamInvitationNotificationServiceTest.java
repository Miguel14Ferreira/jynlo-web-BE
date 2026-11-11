package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.Profile;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamInvitationNotificationServiceTest {
    @Mock NotificationsRepository repository;
    @InjectMocks TeamInvitationNotificationService notifications;

    private static Profile player(long id, String name) {
        Profile result = new Profile();
        result.setId(id);
        result.setUsername(name);
        return result;
    }

    @Test
    void membershipInviteIsAddressedOnlyToInvitedAccountAndCarriesCommunityScope() {
        notifications.memberInvitation(player(2L, "ana"), player(1L, "organizador"), 7L, "Águias");
        var captor = ArgumentCaptor.forClass(Notifications.class);
        verify(repository).save(captor.capture());
        Notifications sent = captor.getValue();
        assertEquals("ana", sent.getUsername());
        assertEquals("organizador", sent.getRelatedUsername());
        assertEquals(7L, sent.getCommunityId());
        assertEquals(NotificationType.TEAM_MEMBER_INVITE, sent.getType());
        assertFalse(sent.isRead());
        assertFalse(sent.isSessionInvite());
    }

    @Test
    void transferDoesNotNotifyTheCurrentAdministratorInsteadOfSuccessor() {
        notifications.adminTransfer(player(3L, "bea"), player(2L, "ana"), 7L, "Águias");
        var captor = ArgumentCaptor.forClass(Notifications.class);
        verify(repository).save(captor.capture());
        assertEquals("bea", captor.getValue().getUsername());
        assertEquals("ana", captor.getValue().getRelatedUsername());
        assertEquals(NotificationType.TEAM_ADMIN_TRANSFER, captor.getValue().getType());
    }

    @Test
    void confirmedAdministratorGetsItsOwnAlert() {
        notifications.assigned(player(3L, "bea"), 7L, "Águias");
        var captor = ArgumentCaptor.forClass(Notifications.class);
        verify(repository).save(captor.capture());
        assertEquals("bea", captor.getValue().getUsername());
        assertEquals(NotificationType.TEAM_ADMIN_ASSIGNED, captor.getValue().getType());
    }

    @Test
    void inAppPreferencesCanSuppressOptionalTeamNotifications() {
        var preferences = mock(NotificationPreferencesService.class);
        org.springframework.test.util.ReflectionTestUtils.setField(notifications, "preferences", preferences);
        when(preferences.allowsInApp(eq("ana"), any(Notifications.class))).thenReturn(false);
        notifications.memberInvitation(player(2L, "ana"), player(1L, "owner"), 7L, "Águias");
        verify(preferences).allowsInApp(eq("ana"), any(Notifications.class));
        verifyNoInteractions(repository);
    }

    @Test
    void missingRecipientUsernameNeverCreatesNotification() {
        notifications.memberInvitation(player(4L, null), player(1L, "organizador"), 7L, "Águias");
        verifyNoInteractions(repository);
    }
}
