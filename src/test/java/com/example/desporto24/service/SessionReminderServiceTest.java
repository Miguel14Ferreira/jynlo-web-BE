package com.example.desporto24.service;

import com.example.desporto24.email.EmailSender;
import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SessionReminderServiceTest {
    private final SessionRepository sessions = mock(SessionRepository.class);
    private final SessionParticipantRepository participants = mock(SessionParticipantRepository.class);
    private final SessionNotificationService notifications = mock(SessionNotificationService.class);
    private final EmailSender email = mock(EmailSender.class);
    private final SessionReminderService service = new SessionReminderService(
            sessions, participants, notifications, email, mock(PlatformTransactionManager.class),
            "https://jynlo.test/", true);
    private final Instant now = Instant.parse("2026-11-21T12:00:00Z");

    private Profile profile(String username, String mail) {
        Profile p = new Profile();
        p.setUsername(username);
        p.setEmail(mail);
        return p;
    }

    private Session session(SessionStatus status, Duration fromNow) {
        Session s = new Session();
        s.setId(7L);
        s.setSport("Futsal");
        s.setCity("Lisboa");
        s.setStatus(status);
        s.setMaxParticipants(10);
        s.setGameDate(Date.from(now.plus(fromNow)));
        s.setProfile(profile("ana", "ana@jynlo.test"));
        return s;
    }

    private void participants(String... usernames) {
        List<SessionParticipant> rows = new java.util.ArrayList<>();
        for (String u : usernames) {
            SessionParticipant p = new SessionParticipant();
            p.setProfile(profile(u, u + "@jynlo.test"));
            rows.add(p);
        }
        when(participants.findBySessionIdAndStateOrderByJoinedAtAsc(7L, ParticipantState.ACTIVE)).thenReturn(rows);
    }

    @Test
    void onlyUpcomingOpenSessionsInsideTheWindowAreEligible() {
        assertTrue(SessionReminderService.eligible(session(SessionStatus.OPEN, Duration.ofHours(20)), now));
        assertTrue(SessionReminderService.eligible(session(SessionStatus.FULL, Duration.ofHours(24)), now));
        assertFalse(SessionReminderService.eligible(session(SessionStatus.OPEN, Duration.ofHours(1)), now));
        assertFalse(SessionReminderService.eligible(session(SessionStatus.OPEN, Duration.ofHours(30)), now));
        assertFalse(SessionReminderService.eligible(session(SessionStatus.CANCELLED, Duration.ofHours(20)), now));
    }

    @Test
    void playersGetTheGameAndTheOrganiserGetsMissingPlaces() {
        participants("ana", "rui", "bea");
        List<SessionReminderService.Reminder> reminders = service.build(session(SessionStatus.OPEN, Duration.ofHours(20)), now);
        assertEquals(3, reminders.size(), "two players + organiser, organiser not duplicated");
        SessionReminderService.Reminder player = reminders.get(0);
        assertEquals("rui", player.profile().getUsername());
        assertTrue(player.subject().startsWith("Amanhã às "), player.subject());
        assertTrue(player.subject().endsWith("Futsal em Lisboa"));
        assertTrue(player.message().contains("3/10 confirmados"));
        SessionReminderService.Reminder organiser = reminders.get(2);
        assertTrue(organiser.organiser());
        assertTrue(organiser.message().contains("Faltam 7 jogadores."), organiser.message());
    }

    @Test
    void sessionIsClaimedOnceThenRemindersAndEmailsAreSent() {
        participants("ana", "rui");
        Session s = session(SessionStatus.OPEN, Duration.ofHours(20));
        when(sessions.findByIdForUpdate(7L)).thenReturn(Optional.of(s));
        service.remind(7L, now);
        assertEquals(now, s.getPreGameReminderSentAt());
        verify(sessions).save(s);
        verify(notifications, times(2)).notifySessionReminder(eq(s), any(), anyString(), anyString());
        verify(email).send(eq("rui@jynlo.test"), contains("Futsal em Lisboa"), contains("/menu/events/7"));
        verify(email).send(eq("ana@jynlo.test"), anyString(), anyString());

        // Already reminded: nothing is sent again.
        reset(notifications, email);
        service.remind(7L, now);
        verifyNoInteractions(notifications, email);
    }

    @Test
    void emailRespectsTheOptOutPreference() throws Exception {
        participants("rui");
        Session s = session(SessionStatus.OPEN, Duration.ofHours(20));
        when(sessions.findByIdForUpdate(7L)).thenReturn(Optional.of(s));
        NotificationPreferencesService prefs = mock(NotificationPreferencesService.class);
        when(prefs.allowsOptionalEmail(anyString(), eq(NotificationType.SESSION_REMINDER))).thenReturn(false);
        var field = SessionReminderService.class.getDeclaredField("preferences");
        field.setAccessible(true);
        field.set(service, prefs);
        service.remind(7L, now);
        verify(notifications, times(2)).notifySessionReminder(eq(s), any(), anyString(), anyString());
        verifyNoInteractions(email);
    }

    @Test
    void dayLabelsUseLisbonTime() {
        assertEquals("Hoje às 21:00", SessionReminderService.whenLabel(Instant.parse("2026-11-21T21:00:00Z"), now));
        assertEquals("Amanhã às 09:30", SessionReminderService.whenLabel(Instant.parse("2026-11-22T09:30:00Z"), now));
    }
}
