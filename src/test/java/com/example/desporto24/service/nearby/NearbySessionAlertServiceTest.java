package com.example.desporto24.service.nearby;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.enumeration.SessionAudience;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.moderation.UserBlockRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.service.SessionNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NearbySessionAlertServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-21T18:00:00Z");
    // Lisbon (Marquês) and a point about 3 km away; Porto is ~275 km away.
    private static final double LIS_LAT = 38.725, LIS_LNG = -9.150;

    private final NearbyAlertStore store = mock(NearbyAlertStore.class);
    private final SessionRepository sessions = mock(SessionRepository.class);
    private final SessionParticipantRepository participants = mock(SessionParticipantRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final UserBlockRepository blocks = mock(UserBlockRepository.class);
    private final SessionNotificationService notifications = mock(SessionNotificationService.class);

    private NearbySessionAlertService service(String mode) {
        return new NearbySessionAlertService(store, sessions, participants, profiles, blocks, notifications,
                mock(PlatformTransactionManager.class), mode);
    }

    private Profile organiser;
    private Session session;

    @BeforeEach
    void setUp() {
        organiser = profile(1L, "org");
        session = new Session();
        session.setId(50L);
        session.setProfile(organiser);
        session.setSport("Padel");
        session.setCity("Lisboa");
        session.setAudience(SessionAudience.PUBLIC);
        session.setPrivateGame(false);
        session.setEnabled(true);
        session.setStatus(SessionStatus.OPEN);
        session.setLatitude(LIS_LAT);
        session.setLongitude(LIS_LNG);
        session.setMaxParticipants(4);
        session.setGameDate(Date.from(NOW.plus(Duration.ofDays(1))));
        when(sessions.findByIdForUpdate(50L)).thenReturn(Optional.of(session));
        when(participants.findBySessionIdAndStateOrderByJoinedAtAsc(50L, ParticipantState.ACTIVE))
                .thenReturn(List.of(participant(organiser)));
        when(participants.countBySessionIdAndState(50L, ParticipantState.ACTIVE)).thenReturn(1L);
        when(store.recordDelivery(anyLong(), anyLong(), any())).thenReturn(true);
    }

    private static Profile profile(long id, String username) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        p.setEnabled(true);
        p.setNotLocked(true);
        return p;
    }

    private static SessionParticipant participant(Profile profile) {
        return SessionParticipant.builder().profile(profile).state(ParticipantState.ACTIVE).build();
    }

    private void candidates(NearbyAlertStore.Candidate... list) {
        when(store.candidatesInBox(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(List.of(list));
        for (NearbyAlertStore.Candidate c : list) {
            when(profiles.findById(c.profileId())).thenReturn(Optional.of(profile(c.profileId(), "u" + c.profileId())));
        }
    }

    private static NearbyAlertStore.Candidate near(long id, int radius, String... sports) {
        return new NearbyAlertStore.Candidate(id, 38.75, -9.17, radius, List.of(sports));
    }

    @Test
    void alertsPeopleInsideTheirRadiusWhoPlayTheSport() {
        candidates(near(2, 10), near(3, 10, "padel"), near(4, 10, "Futebol"),
                new NearbyAlertStore.Candidate(5, 41.15, -8.61, 25, List.of()));
        assertEquals(2, service("on").process(50L, NOW));
        verify(notifications).notifyNearbySession(eq(session), argThat(p -> p.getId() == 2L), anyString(), anyString());
        verify(notifications).notifyNearbySession(eq(session), argThat(p -> p.getId() == 3L), anyString(), anyString());
        verify(notifications, never()).notifyNearbySession(any(), argThat(p -> p.getId() == 4L || p.getId() == 5L), any(), any());
        assertEquals(NOW, session.getNearbyAlertsProcessedAt());
    }

    @Test
    void neverAlertsTheOrganiserPeopleGoingBlockedOrOverTheDailyLimit() {
        Profile going = profile(6L, "going");
        when(participants.findBySessionIdAndStateOrderByJoinedAtAsc(50L, ParticipantState.ACTIVE))
                .thenReturn(List.of(participant(organiser), participant(going)));
        candidates(near(1, 25), near(6, 25), near(7, 25), near(8, 25), near(9, 25));
        when(blocks.existsBetween(7L, 1L)).thenReturn(true);
        when(store.sentSince(eq(8L), any())).thenReturn(NearbySessionAlertService.DAILY_LIMIT);
        assertEquals(1, service("on").process(50L, NOW));
        verify(notifications).notifyNearbySession(eq(session), argThat(p -> p.getId() == 9L), anyString(), anyString());
    }

    @Test
    void processesEachSessionOnlyOnce() {
        candidates(near(2, 25));
        session.setNearbyAlertsProcessedAt(NOW.minusSeconds(60));
        assertEquals(0, service("on").process(50L, NOW));
        verifyNoInteractions(notifications);
    }

    @Test
    void skipsADuplicateDelivery() {
        candidates(near(2, 25));
        when(store.recordDelivery(2L, 50L, NOW)).thenReturn(false);
        assertEquals(0, service("on").process(50L, NOW));
        verifyNoInteractions(notifications);
    }

    @Test
    void dryRunCountsWithoutSendingOrRecording() {
        candidates(near(2, 25));
        assertEquals(1, service("dry-run").process(50L, NOW));
        verifyNoInteractions(notifications);
        verify(store, never()).recordDelivery(anyLong(), anyLong(), any());
    }

    @Test
    void onlyPublicOpenSessionsWithCoordinatesAndASensibleStartAreEligible() {
        assertTrue(NearbySessionAlertService.eligible(session, NOW));
        session.setPrivateGame(true);
        assertFalse(NearbySessionAlertService.eligible(session, NOW));
        session.setPrivateGame(false);
        session.setAudience(SessionAudience.COMMUNITY_ONLY);
        assertFalse(NearbySessionAlertService.eligible(session, NOW));
        session.setAudience(SessionAudience.PUBLIC);
        session.setStatus(SessionStatus.FULL);
        assertFalse(NearbySessionAlertService.eligible(session, NOW));
        session.setStatus(SessionStatus.OPEN);
        session.setLatitude(null);
        assertFalse(NearbySessionAlertService.eligible(session, NOW));
        session.setLatitude(LIS_LAT);
        session.setGameDate(Date.from(NOW.plus(Duration.ofMinutes(90))));
        assertFalse(NearbySessionAlertService.eligible(session, NOW));
        session.setGameDate(Date.from(NOW.plus(Duration.ofDays(20))));
        assertFalse(NearbySessionAlertService.eligible(session, NOW));
    }

    @Test
    void textHasSportCityTimeAndFreePlacesButNoAddress() {
        session.setAddress("Rua Secreta 12");
        String[] text = service("on").text(session, NOW);
        assertEquals("Novo jogo perto de ti: Padel", text[0]);
        assertTrue(text[1].startsWith("Padel em Lisboa, amanhã às "), text[1]);
        assertTrue(text[1].endsWith("3 lugares livres."), text[1]);
        assertFalse(String.join(" ", text).contains("Rua"));
    }

    @Test
    void validatesAndRoundsPreferences() {
        var clean = NearbySessionAlertService.validate(new NearbyAlertPreference(
                true, 38.72534, -9.15012, " Lisboa ", 25, List.of("Padel", "padel", " Ténis ")));
        assertEquals(38.73, clean.latitude());
        assertEquals(-9.15, clean.longitude());
        assertEquals("Lisboa", clean.placeLabel());
        assertEquals(List.of("Padel", "Ténis"), clean.sports());

        assertThrows(ResponseStatusException.class, () -> NearbySessionAlertService.validate(
                new NearbyAlertPreference(true, null, null, null, 25, List.of())));
        assertThrows(ResponseStatusException.class, () -> NearbySessionAlertService.validate(
                new NearbyAlertPreference(false, 38.7, -9.1, null, 30, List.of())));
        assertThrows(ResponseStatusException.class, () -> NearbySessionAlertService.validate(
                new NearbyAlertPreference(false, 95.0, -9.1, null, 25, List.of())));
        assertThrows(ResponseStatusException.class, () -> NearbySessionAlertService.validate(
                new NearbyAlertPreference(false, 38.7, null, null, 25, List.of())));
        assertEquals(25, NearbySessionAlertService.validate(
                new NearbyAlertPreference(false, null, null, null, 0, null)).radiusKm());
    }

    @Test
    void distanceIsCorrectToWithinAKilometre() {
        assertEquals(274, NearbySessionAlertService.distanceKm(LIS_LAT, LIS_LNG, 41.15, -8.61), 5);
    }
}
