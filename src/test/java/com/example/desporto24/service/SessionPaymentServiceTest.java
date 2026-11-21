package com.example.desporto24.service;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.model.session.SessionParticipantPayment;
import com.example.desporto24.model.session.SessionPaymentSetup;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionParticipantPaymentRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.session.SessionPaymentSetupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SessionPaymentServiceTest {
    private final SessionRepository sessions = mock(SessionRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final SessionParticipantRepository participants = mock(SessionParticipantRepository.class);
    private final SessionPaymentSetupRepository setups = mock(SessionPaymentSetupRepository.class);
    private final SessionParticipantPaymentRepository payments = mock(SessionParticipantPaymentRepository.class);
    private final SessionPaymentService service = new SessionPaymentService(sessions, profiles, participants, setups, payments);
    private final Map<Long, SessionParticipantPayment> store = new HashMap<>();
    private SessionPaymentSetup setup;

    private Profile profile(long id, String username) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        when(profiles.findUserByUsername(username)).thenReturn(Optional.of(p));
        return p;
    }

    private SessionParticipant participant(long id, Profile profile) {
        SessionParticipant p = new SessionParticipant();
        p.setId(id);
        p.setProfile(profile);
        p.setState(ParticipantState.ACTIVE);
        return p;
    }

    @BeforeEach
    void setUp() {
        Profile ana = profile(1, "ana"), rui = profile(2, "rui"), bea = profile(3, "bea");
        profile(4, "intruso");
        Session session = new Session();
        session.setId(7L);
        session.setProfile(ana);
        session.setPriceAmount(new BigDecimal("4.00"));
        when(sessions.findById(7L)).thenReturn(Optional.of(session));
        List<SessionParticipant> active = List.of(participant(10, ana), participant(20, rui), participant(30, bea));
        when(participants.findBySessionIdAndStateOrderByJoinedAtAsc(7L, ParticipantState.ACTIVE)).thenReturn(active);
        when(participants.findBySessionIdAndProfileIdAndState(eq(7L), anyLong(), eq(ParticipantState.ACTIVE)))
                .thenAnswer(i -> active.stream().filter(p -> p.getProfile().getId().equals(i.getArgument(1))).findFirst());
        when(payments.findById(anyLong())).thenAnswer(i -> Optional.ofNullable(store.get((Long) i.getArgument(0))));
        when(payments.existsById(anyLong())).thenAnswer(i -> store.containsKey((Long) i.getArgument(0)));
        when(payments.save(any())).thenAnswer(i -> { SessionParticipantPayment r = i.getArgument(0); store.put(r.getParticipantId(), r); return r; });
        doAnswer(i -> store.remove((Long) i.getArgument(0))).when(payments).deleteById(anyLong());
        when(payments.findAllById(any())).thenAnswer(i -> {
            List<SessionParticipantPayment> out = new ArrayList<>();
            for (Object id : (Iterable<?>) i.getArgument(0)) if (store.containsKey((Long) id)) out.add(store.get((Long) id));
            return out;
        });
        when(setups.findById(7L)).thenAnswer(i -> Optional.ofNullable(setup));
        when(setups.save(any())).thenAnswer(i -> setup = i.getArgument(0));
    }

    @Test
    void organiserSetsMbwayAndOnlyPlayersSeeIt() {
        var view = service.setMethod(7L, "ana", "MBWAY", "+351 912 345 678");
        assertEquals("912345678", view.mbwayPhone());
        assertEquals("MBWAY", view.method());
        assertEquals(2, view.playerCount(), "organiser is not counted as a payer");
        assertEquals("912345678", service.get(7L, "rui").mbwayPhone());
        assertTrue(service.get(7L, "rui").players().isEmpty(), "players do not see each other's status");
        assertThrows(ResponseStatusException.class, () -> service.get(7L, "intruso"));
        assertThrows(ResponseStatusException.class, () -> service.setMethod(7L, "rui", "MBWAY", "912345678"));

        var inPerson = service.setMethod(7L, "ana", "IN_PERSON", "912345678");
        assertEquals("IN_PERSON", inPerson.method());
        assertNull(inPerson.mbwayPhone(), "no number is kept for payment on the day");
        assertThrows(ResponseStatusException.class, () -> service.setMethod(7L, "ana", "BITCOIN", null));
        assertThrows(ResponseStatusException.class, () -> service.setMethod(7L, "ana", "MBWAY", null));
    }

    @Test
    void reminderGoesOnlyToUnconfirmedPlayersAndNotTwiceIn12Hours() {
        var notifications = mock(com.example.desporto24.repository.NotificationsRepository.class);
        when(notifications.save(any())).thenAnswer(i -> i.getArgument(0));
        org.springframework.test.util.ReflectionTestUtils.setField(service, "notifications", notifications);
        assertThrows(ResponseStatusException.class, () -> service.remindUnpaid(7L, "ana"), "needs a payment method first");
        service.setMethod(7L, "ana", "MBWAY", "912345678");
        service.confirm(7L, "ana", "rui", true);

        service.remindUnpaid(7L, "ana");
        var captor = org.mockito.ArgumentCaptor.forClass(com.example.desporto24.register.Notifications.Notifications.class);
        verify(notifications, times(1)).save(captor.capture());
        assertEquals("bea", captor.getValue().getUsername());
        assertTrue(captor.getValue().getMessage().contains("4,00 € por MB WAY para 912345678"), captor.getValue().getMessage());
        assertEquals(com.example.desporto24.enumeration.NotificationType.PAYMENT_REMINDER, captor.getValue().getType());

        assertThrows(ResponseStatusException.class, () -> service.remindUnpaid(7L, "ana"));
        assertThrows(ResponseStatusException.class, () -> service.remindUnpaid(7L, "rui"));
    }

    @Test
    void invalidNumbersAreRejected() {
        for (String bad : List.of("212345678", "94123456", "+44 7911 123456", "abc")) {
            assertThrows(ResponseStatusException.class, () -> SessionPaymentService.normalisePhone(bad), bad);
        }
        assertEquals("961234567", SessionPaymentService.normalisePhone("00351 96 123 45 67"));
    }

    @Test
    void playerClaimsAndOrganiserConfirms() {
        var claimed = service.claim(7L, "rui", true);
        assertNotNull(claimed.me().claimedAt());
        assertNull(claimed.me().confirmedAt());
        assertEquals(0, claimed.confirmedCount(), "a claim is not a confirmation");

        var confirmed = service.confirm(7L, "ana", "RUI", true);
        assertEquals(1, confirmed.confirmedCount());
        var rui = confirmed.players().stream().filter(p -> p.username().equals("rui")).findFirst().orElseThrow();
        assertNotNull(rui.claimedAt());
        assertNotNull(rui.confirmedAt());

        assertThrows(ResponseStatusException.class, () -> service.confirm(7L, "rui", "bea", true));
        assertThrows(ResponseStatusException.class, () -> service.confirm(7L, "ana", "ana", true));
        assertThrows(ResponseStatusException.class, () -> service.claim(7L, "ana", true));

        service.confirm(7L, "ana", "rui", false);
        service.claim(7L, "rui", false);
        assertTrue(store.isEmpty(), "undoing both removes the row");
    }
}
