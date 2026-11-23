package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CallNotificationServiceTest {
    private final NotificationsRepository notifications = mock(NotificationsRepository.class);
    private final NotificationPreferencesService preferences = mock(NotificationPreferencesService.class);
    private final ConversationNotificationMuteService mutes = mock(ConversationNotificationMuteService.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final CallNotificationService service = new CallNotificationService(notifications, preferences, mutes, events);

    @BeforeEach
    void setUp() {
        when(preferences.allowsInApp(anyString(), any(Notifications.class))).thenReturn(true);
        when(notifications.save(any(Notifications.class))).thenAnswer(i -> { Notifications n = i.getArgument(0); n.setId(5L); return n; });
    }

    @Test
    void ringingAlertsTheCallee() {
        service.incoming("ana", "rui");
        ArgumentCaptor<Notifications> saved = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications).save(saved.capture());
        assertEquals("rui", saved.getValue().getUsername());
        assertEquals("ana", saved.getValue().getRelatedUsername());
        assertEquals(NotificationType.CALL_INCOMING, saved.getValue().getType());
        verify(events).publishEvent(any(MobilePushDeliveryService.StoredNotification.class));
    }

    @Test
    void missedCallClearsRingingAndAddsMissed() {
        Notifications ringing = new Notifications();
        when(notifications.findByUsernameIgnoreCaseAndRelatedUsernameIgnoreCaseAndTypeAndReadFalse(
                "rui", "ana", NotificationType.CALL_INCOMING)).thenReturn(List.of(ringing));
        service.missed("ana", "rui");
        assertTrue(ringing.isRead());
        ArgumentCaptor<Notifications> saved = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications).save(saved.capture());
        assertEquals(NotificationType.CALL_MISSED, saved.getValue().getType());
    }

    @Test
    void mutedConversationGetsNoCallAlert() {
        when(mutes.muted("rui", "direct", "ana")).thenReturn(true);
        service.incoming("ana", "rui");
        verify(notifications, never()).save(any());
    }
}
