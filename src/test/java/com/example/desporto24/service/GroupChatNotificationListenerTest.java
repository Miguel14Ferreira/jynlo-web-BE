package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.dto.session.SessionMessageResponse;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.websocket.CommunityChatRecipientResolver;
import com.example.desporto24.websocket.InboxOwnReadUpdatedEvent;
import com.example.desporto24.websocket.SessionChatMessageCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GroupChatNotificationListenerTest {
    private final NotificationsRepository notifications = mock(NotificationsRepository.class);
    private final SessionRepository sessions = mock(SessionRepository.class);
    private final CommunityRepository communities = mock(CommunityRepository.class);
    private final CommunityChatRecipientResolver resolver = mock(CommunityChatRecipientResolver.class);
    private final NotificationPreferencesService preferences = mock(NotificationPreferencesService.class);
    private final ConversationNotificationMuteService mutes = mock(ConversationNotificationMuteService.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final GroupChatNotificationListener listener = new GroupChatNotificationListener(
            notifications, sessions, communities, resolver, preferences, mutes, events);

    @BeforeEach
    void setUp() {
        Session session = mock(Session.class);
        when(session.getSport()).thenReturn("Padel");
        when(sessions.findById(7L)).thenReturn(Optional.of(session));
        when(preferences.allowsInApp(anyString(), any(Notifications.class))).thenReturn(true);
        when(notifications.save(any(Notifications.class))).thenAnswer(i -> { Notifications n = i.getArgument(0); n.setId(55L); return n; });
    }

    private static SessionChatMessageCreatedEvent message(Instant editedAt) {
        var m = new SessionMessageResponse(1L, 7L, "ana", "Chego às 19h", Instant.now(), null, null, null,
                null, null, null, editedAt, null, false);
        return new SessionChatMessageCreatedEvent(m, new LinkedHashSet<>(List.of("ana", "rui", "joao")));
    }

    @Test
    void gameChatAlertsEveryMessageButKeepsOneInboxEntryPerConversation() {
        Notifications existing = new Notifications();
        existing.setUsername("joao");
        existing.setMessage("ana: antiga");
        when(notifications.findByUsernameIgnoreCaseAndSessionIdAndTypeAndReadFalse("joao", 7L, NotificationType.CHAT_MESSAGE))
                .thenReturn(List.of(existing));
        when(notifications.save(any(Notifications.class))).thenAnswer(inv -> {
            Notifications n = inv.getArgument(0);
            if (n.getId() == null) n.setId(n == existing ? 40L : 41L);
            return n;
        });
        listener.onSessionMessage(message(null));
        ArgumentCaptor<Notifications> saved = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications, times(2)).save(saved.capture());
        Notifications rui = saved.getAllValues().stream().filter(n -> "rui".equals(n.getUsername())).findFirst().orElseThrow();
        assertEquals(7L, rui.getSessionId());
        assertEquals(NotificationType.CHAT_MESSAGE, rui.getType());
        // joao's unread entry is reused with the latest text instead of a second row.
        assertTrue(saved.getAllValues().contains(existing));
        assertFalse(existing.getMessage().contains("antiga"));
        verify(events, times(2)).publishEvent(any(MobilePushDeliveryService.StoredNotification.class));
    }

    @Test
    void editsAndMutedChatsDoNotAlert() {
        listener.onSessionMessage(message(Instant.now()));
        verify(notifications, never()).save(any());
        when(mutes.silenced(anyString(), eq("session"), eq("7"))).thenReturn(true);
        listener.onSessionMessage(message(null));
        verify(notifications, never()).save(any());
    }

    @Test
    void communityChatOnlyAlertsCurrentMembers() {
        Community c = mock(Community.class);
        when(c.getName()).thenReturn("Futebol de 4ª");
        when(communities.findById(3L)).thenReturn(Optional.of(c));
        when(resolver.currentRecipients(3L)).thenReturn(java.util.Set.of("ana", "rui"));
        var entry = new com.example.desporto24.model.dto.community.CommunityTimelineEntryResponse(
                9L, 3L, "CHAT", "ana", "Quem vem?", Instant.now(), null, null, null, null, null,
                null, null, null, null, null, List.of(), List.of());
        listener.onCommunityMessage(new com.example.desporto24.websocket.CommunityChatMessageCreatedEvent(
                entry, new LinkedHashSet<>(List.of("ana", "rui", "saiu"))));
        ArgumentCaptor<Notifications> saved = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications, times(1)).save(saved.capture());
        assertEquals("rui", saved.getValue().getUsername());
        assertEquals(3L, saved.getValue().getCommunityId());
    }

    @Test
    void readingTheChatClearsTheAlert() {
        Notifications n = new Notifications();
        when(notifications.findByUsernameIgnoreCaseAndSessionIdAndTypeAndReadFalse("rui", 7L, NotificationType.CHAT_MESSAGE))
                .thenReturn(List.of(n));
        listener.onRead(new InboxOwnReadUpdatedEvent("rui", "session", "7", 10L));
        assertTrue(n.isRead());
        verify(notifications).saveAll(List.of(n));
    }
}
