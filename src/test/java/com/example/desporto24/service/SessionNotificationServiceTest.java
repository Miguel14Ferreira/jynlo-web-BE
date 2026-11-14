package com.example.desporto24.service;

import com.example.desporto24.enumeration.AttendanceStatus;
import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.model.session.SessionWaitlist;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.session.SessionWaitlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionNotificationServiceTest {

    private NotificationsRepository notificationsRepository;
    private SessionParticipantRepository participantRepository;
    private SessionWaitlistRepository waitlistRepository;
    private SessionNotificationService service;

    @BeforeEach
    void setUp() {
        notificationsRepository = mock(NotificationsRepository.class);
        participantRepository = mock(SessionParticipantRepository.class);
        waitlistRepository = mock(SessionWaitlistRepository.class);
        service = new SessionNotificationService(
                notificationsRepository,
                participantRepository,
                waitlistRepository
        );
    }

    @Test
    void waitlistPromotionCreatesTypedUnreadNotification() {
        Profile profile = profile(2L, "miguel");
        Session session = session(10L, profile(1L, "creator"));

        service.notifyWaitlistPromoted(session, profile);

        ArgumentCaptor<Notifications> captor = ArgumentCaptor.forClass(Notifications.class);
        verify(notificationsRepository).save(captor.capture());
        Notifications saved = captor.getValue();

        assertEquals(NotificationType.WAITLIST_PROMOTED, saved.getType());
        assertEquals(10L, saved.getSessionId());
        assertEquals("miguel", saved.getUsername());
        assertFalse(saved.isRead());
    }

    @Test
    void removalCreatesSessionRemovedNotification() {
        Profile profile = profile(2L, "miguel");
        Session session = session(10L, profile(1L, "creator"));

        service.notifyParticipantRemoved(session, profile);

        ArgumentCaptor<Notifications> captor = ArgumentCaptor.forClass(Notifications.class);
        verify(notificationsRepository).save(captor.capture());
        assertEquals(NotificationType.SESSION_REMOVED, captor.getValue().getType());
    }

    @Test
    void meetingPointChangeNotifiesOnlyActivePlayersWithoutLeakingPrivateInstructions() {
        Profile creator = profile(1L, "creator");
        Profile active = profile(2L, "active");
        Profile waiting = profile(3L, "waitlisted");
        Session session = session(10L, creator);
        session.setMeetingPointInstructions("Entrada lateral do Pavilhão às 20h");

        when(participantRepository.findBySessionIdAndStateOrderByJoinedAtAsc(
                10L, ParticipantState.ACTIVE)).thenReturn(List.of(
                        participant(session, creator),
                        participant(session, active),
                        participant(session, active)));
        service.notifyMeetingPointUpdated(session);

        ArgumentCaptor<Notifications> captor = ArgumentCaptor.forClass(Notifications.class);
        verify(notificationsRepository, times(1)).save(captor.capture());
        Notifications saved = captor.getValue();
        assertEquals("active", saved.getUsername());
        assertEquals(10L, saved.getSessionId());
        assertEquals(NotificationType.SESSION_MEETING_POINT_UPDATED, saved.getType());
        assertFalse(saved.isRead());
        assertFalse(saved.getMessage().contains("Entrada lateral"));
        assertFalse(saved.getSubject().contains("Pavilhão"));
        verify(waitlistRepository, never()).findBySessionIdOrderByPriorityTierAscCreatedAtAsc(anyLong());
    }

    @Test
    void meetingPointChangeWithOnlyCreatorDoesNotNotifyAnyone() {
        Profile creator = profile(1L, "creator");
        Session session = session(10L, creator);
        when(participantRepository.findBySessionIdAndStateOrderByJoinedAtAsc(
                10L, ParticipantState.ACTIVE)).thenReturn(List.of(participant(session, creator)));
        service.notifyMeetingPointUpdated(session);
        verify(notificationsRepository, never()).save(any());
    }

    @Test
    void cancellationNotifiesActiveParticipantsAndWaitlistOnceButNotCreator() {
        Profile creator = profile(1L, "creator");
        Profile active = profile(2L, "active");
        Profile waiting = profile(3L, "waiting");
        Session session = session(10L, creator);

        when(participantRepository.findBySessionIdAndStateOrderByJoinedAtAsc(
                10L, ParticipantState.ACTIVE))
                .thenReturn(List.of(
                        participant(session, creator),
                        participant(session, active)
                ));
        when(waitlistRepository.findBySessionIdOrderByPriorityTierAscCreatedAtAsc(10L))
                .thenReturn(List.of(
                        waitlist(session, active),
                        waitlist(session, waiting)
                ));

        service.notifySessionCancelled(session);

        ArgumentCaptor<Notifications> captor = ArgumentCaptor.forClass(Notifications.class);
        verify(notificationsRepository, times(2)).save(captor.capture());

        List<Notifications> saved = captor.getAllValues();
        assertEquals(List.of("active", "waiting"),
                saved.stream().map(Notifications::getUsername).sorted().toList());
        assertTrue(saved.stream().allMatch(n -> n.getType() == NotificationType.SESSION_CANCELLED));
        assertTrue(saved.stream().allMatch(n -> n.getSessionId().equals(10L)));
        assertTrue(saved.stream().noneMatch(n -> n.getUsername().equals("creator")));
    }

    @Test
    void cancellationIncludesPublicSportDateAndCityWithoutPrivateAddress() {
        Profile creator = profile(1L, "creator");
        Profile player = profile(2L, "player");
        Session session = session(10L, creator);
        session.setSport("Futsal");
        session.setCity("Campo Grande");
        session.setAddress("MORADA_PRIVADA_773");
        session.setMeetingPointInstructions("CÓDIGO_PORTA_SECRETO");
        session.setGameDate(Date.from(java.time.LocalDateTime.of(2026,11,20,21,0)
                .atZone(ZoneId.of("Europe/Lisbon")).toInstant()));
        when(participantRepository.findBySessionIdAndStateOrderByJoinedAtAsc(
                10L, ParticipantState.ACTIVE))
                .thenReturn(List.of(participant(session, player)));
        when(waitlistRepository.findBySessionIdOrderByPriorityTierAscCreatedAtAsc(10L))
                .thenReturn(List.of());

        service.notifySessionCancelled(session);

        ArgumentCaptor<Notifications> captor = ArgumentCaptor.forClass(Notifications.class);
        verify(notificationsRepository).save(captor.capture());
        Notifications saved = captor.getValue();
        assertEquals("Sessão de Futsal cancelada",saved.getSubject());
        assertTrue(saved.getMessage().contains("20/11/2026 às 21:00"));
        assertTrue(saved.getMessage().contains("Campo Grande"));
        assertTrue(saved.getMessage().contains("não se vai realizar"));
        assertFalse(saved.getMessage().contains("MORADA_PRIVADA_773"));
        assertFalse(saved.getMessage().contains("CÓDIGO_PORTA_SECRETO"));
    }

    @Test
    void storedCancellationEmitsPushEventOnlyForPersistedAllowedNotification() {
        Profile creator=profile(1L,"creator");
        Profile player=profile(2L,"player");
        Session session=session(10L,creator);
        when(participantRepository.findBySessionIdAndStateOrderByJoinedAtAsc(
                10L,ParticipantState.ACTIVE))
                .thenReturn(List.of(participant(session,player)));
        when(waitlistRepository.findBySessionIdOrderByPriorityTierAscCreatedAtAsc(10L))
                .thenReturn(List.of());
        when(notificationsRepository.save(any(Notifications.class))).thenAnswer(invocation -> {
            Notifications notification=invocation.getArgument(0);
            notification.setId(99L);
            return notification;
        });
        ApplicationEventPublisher publisher=mock(ApplicationEventPublisher.class);
        ReflectionTestUtils.setField(service,"pushEvents",publisher);

        service.notifySessionCancelled(session);

        ArgumentCaptor<MobilePushDeliveryService.StoredNotification> captured=
                ArgumentCaptor.forClass(MobilePushDeliveryService.StoredNotification.class);
        verify(publisher).publishEvent(captured.capture());
        var event=captured.getValue();
        assertEquals("player",event.username());
        assertEquals(99L,event.notificationId());
        assertEquals("SESSION_CANCELLED",event.category());
        assertEquals(10L,event.sessionId());
        assertTrue(event.permitted());
    }

    @Test
    void disabledInAppCancellationPreferenceSuppressesStoredNotificationAndPush() {
        Profile creator=profile(1L,"creator");
        Profile player=profile(2L,"player");
        Session session=session(10L,creator);
        when(participantRepository.findBySessionIdAndStateOrderByJoinedAtAsc(
                10L,ParticipantState.ACTIVE))
                .thenReturn(List.of(participant(session,player)));
        when(waitlistRepository.findBySessionIdOrderByPriorityTierAscCreatedAtAsc(10L))
                .thenReturn(List.of());
        NotificationPreferencesService preferences=mock(NotificationPreferencesService.class);
        when(preferences.allowsInApp(eq("player"),any(Notifications.class))).thenReturn(false);
        ReflectionTestUtils.setField(service,"preferenceService",preferences);
        ApplicationEventPublisher publisher=mock(ApplicationEventPublisher.class);
        ReflectionTestUtils.setField(service,"pushEvents",publisher);

        service.notifySessionCancelled(session);

        verify(notificationsRepository,never()).save(any());
        verifyNoInteractions(publisher);
    }

    private Profile profile(Long id, String username) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setUsername(username);
        return profile;
    }

    private Session session(Long id, Profile creator) {
        Session session = new Session();
        session.setId(id);
        session.setProfile(creator);
        return session;
    }

    private SessionParticipant participant(Session session, Profile profile) {
        return SessionParticipant.builder()
                .session(session)
                .profile(profile)
                .state(ParticipantState.ACTIVE)
                .attendanceStatus(AttendanceStatus.PENDING)
                .joinedAt(Instant.now())
                .build();
    }

    private SessionWaitlist waitlist(Session session, Profile profile) {
        return SessionWaitlist.builder()
                .session(session)
                .profile(profile)
                .priorityTier(0)
                .createdAt(Instant.now())
                .build();
    }
}
