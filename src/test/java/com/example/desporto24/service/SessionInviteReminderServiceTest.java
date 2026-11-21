package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.enumeration.SessionInviteStatus;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionInvite;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.session.SessionInviteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SessionInviteReminderServiceTest {
    private static final Instant NOW = Instant.parse("2026-11-21T18:00:00Z");
    private final SessionInviteRepository invites = mock(SessionInviteRepository.class);
    private final NotificationsRepository notifications = mock(NotificationsRepository.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final SessionInviteReminderService service = new SessionInviteReminderService(
            invites, notifications, events, mock(PlatformTransactionManager.class), true);
    private SessionInvite invite;

    private static Profile profile(long id, String username) {
        Profile p = new Profile(); p.setId(id); p.setUsername(username); return p;
    }

    @BeforeEach
    void setUp() {
        Session game = new Session();
        game.setId(50L); game.setSport("Padel"); game.setCity("Lisboa"); game.setStatus(SessionStatus.OPEN);
        game.setGameDate(Date.from(NOW.plus(Duration.ofDays(3))));
        invite = new SessionInvite();
        invite.setId(7L); invite.setSession(game); invite.setInviter(profile(1, "org")); invite.setInvitee(profile(2, "ana"));
        invite.setStatus(SessionInviteStatus.PENDING); invite.setCreatedAt(NOW.minus(Duration.ofHours(25)));
        when(invites.findById(7L)).thenReturn(Optional.of(invite));
        when(invites.claimReminder(7L, NOW)).thenReturn(1);
        when(notifications.save(any())).thenAnswer(i -> { Notifications n = i.getArgument(0); n.setId(9L); return n; });
    }

    @Test
    void remindsOnceAfter24HoursWithPush() {
        assertTrue(service.remind(7L, NOW));
        ArgumentCaptor<Notifications> saved = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications).save(saved.capture());
        assertEquals("ana", saved.getValue().getUsername());
        assertEquals(NotificationType.SESSION_INVITE, saved.getValue().getType());
        assertEquals(50L, saved.getValue().getSessionId());
        assertTrue(saved.getValue().getMessage().startsWith("@org está à espera da tua resposta: Padel em Lisboa, "));
        verify(events).publishEvent(any(MobilePushDeliveryService.StoredNotification.class));
        when(invites.claimReminder(7L, NOW)).thenReturn(0);
        assertFalse(service.remind(7L, NOW));
    }

    @Test
    void waitsLongerForGamesFarAheadAndLessForGamesSoon() {
        invite.setCreatedAt(NOW.minus(Duration.ofHours(5)));
        assertFalse(SessionInviteReminderService.due(invite, NOW), "game in 3 days: wait 24h");
        invite.getSession().setGameDate(Date.from(NOW.plus(Duration.ofHours(10))));
        assertTrue(SessionInviteReminderService.due(invite, NOW), "game today: 3h is enough");
        invite.getSession().setGameDate(Date.from(NOW.plus(Duration.ofMinutes(90))));
        assertFalse(SessionInviteReminderService.due(invite, NOW), "too late to be useful");
    }

    @Test
    void neverRemindsAnsweredInvitesOrClosedGames() {
        invite.setStatus(SessionInviteStatus.ACCEPTED);
        assertFalse(service.remind(7L, NOW));
        invite.setStatus(SessionInviteStatus.PENDING);
        invite.getSession().setStatus(SessionStatus.FULL);
        assertFalse(service.remind(7L, NOW));
        verifyNoInteractions(notifications);
    }
}
