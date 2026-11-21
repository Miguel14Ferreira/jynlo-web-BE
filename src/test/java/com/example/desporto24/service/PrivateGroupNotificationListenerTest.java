package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.PrivateGroup;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.PrivateGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PrivateGroupNotificationListenerTest {
    private final NotificationsRepository notifications = mock(NotificationsRepository.class);
    private final PrivateGroupRepository groups = mock(PrivateGroupRepository.class);
    private final NotificationPreferencesService preferences = mock(NotificationPreferencesService.class);
    private final ConversationNotificationMuteService mutes = mock(ConversationNotificationMuteService.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final PrivateGroupNotificationListener listener =
            new PrivateGroupNotificationListener(notifications, groups, preferences, mutes, events);

    @BeforeEach
    void setUp() {
        PrivateGroup g = new PrivateGroup();
        g.setId(5L); g.setTitle("Futebol de sábado");
        when(groups.findById(5L)).thenReturn(Optional.of(g));
        when(preferences.allowsInApp(anyString(), any(Notifications.class))).thenReturn(true);
        when(notifications.save(any(Notifications.class))).thenAnswer(i -> { Notifications n = i.getArgument(0); n.setId(100L); return n; });
    }

    private PrivateGroupService.GroupEvent message(String sender, Set<String> members) {
        return new PrivateGroupService.GroupEvent(5L, members,
                new PrivateGroupService.MessageView(9L, 5L, sender, "Levo a bola", Instant.now()));
    }

    @Test
    void alertsEveryOtherMemberButNotTheSender() {
        listener.onGroupEvent(message("miguel", Set.of("miguel", "ana", "rui")));
        ArgumentCaptor<Notifications> saved = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications, times(2)).save(saved.capture());
        assertTrue(saved.getAllValues().stream().noneMatch(n -> n.getUsername().equals("miguel")));
        Notifications n = saved.getAllValues().get(0);
        assertEquals(NotificationType.CHAT_MESSAGE, n.getType());
        assertEquals(5L, n.getPrivateGroupId());
        assertEquals("Nova mensagem em Futebol de sábado", n.getSubject());
        assertNull(n.getRelatedUsername(), "must not open the sender's direct chat");
        ArgumentCaptor<Object> pushed = ArgumentCaptor.forClass(Object.class);
        verify(events, times(2)).publishEvent(pushed.capture());
        var push = (MobilePushDeliveryService.StoredNotification) pushed.getValue();
        assertEquals(5L, push.privateGroupId());
    }

    @Test
    void mutedMembersAndExistingUnreadAlertsAreSkipped() {
        when(mutes.muted("ana", "private", "5")).thenReturn(true);
        when(notifications.existsByUsernameAndPrivateGroupIdAndReadFalse("rui", 5L)).thenReturn(true);
        listener.onGroupEvent(message("miguel", Set.of("miguel", "ana", "rui")));
        verify(notifications, never()).save(any(Notifications.class));
        verifyNoInteractions(events);
    }

    @Test
    void accountPreferencesAreRespected() {
        when(preferences.allowsInApp(eq("ana"), any(Notifications.class))).thenReturn(false);
        listener.onGroupEvent(message("miguel", Set.of("miguel", "ana")));
        verify(notifications, never()).save(any(Notifications.class));
    }

    @Test
    void rosterChangesDoNotAlert() {
        listener.onGroupEvent(new PrivateGroupService.GroupEvent(5L, Set.of("ana"), null));
        verifyNoInteractions(notifications, events);
    }

    @Test
    void openingTheGroupClearsItsAlert() {
        Notifications unread = new Notifications(); unread.setRead(false);
        when(notifications.findByUsernameIgnoreCaseAndPrivateGroupIdAndReadFalse("ana", 5L)).thenReturn(List.of(unread));
        listener.markRead("ana", 5L);
        assertTrue(unread.isRead());
        verify(notifications).saveAll(List.of(unread));
    }
}
