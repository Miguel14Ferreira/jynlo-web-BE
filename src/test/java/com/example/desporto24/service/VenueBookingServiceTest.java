package com.example.desporto24.service;

import com.example.desporto24.model.venue.*;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionCourtBookingRepository;
import com.example.desporto24.repository.venue.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VenueBookingServiceTest {
    private final VenueRepository venues = mock(VenueRepository.class);
    private final VenueOwnershipClaimRepository claims = mock(VenueOwnershipClaimRepository.class);
    private final VenueManagerRepository managers = mock(VenueManagerRepository.class);
    private final VenueCourtRepository courts = mock(VenueCourtRepository.class);
    private final VenueOpeningHoursRepository hours = mock(VenueOpeningHoursRepository.class);
    private final VenueBookingRequestRepository bookings = mock(VenueBookingRequestRepository.class);
    private final VenuePaymentSetupRepository paymentSetups = mock(VenuePaymentSetupRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final SessionRepository sessions = mock(SessionRepository.class);
    private final SessionCourtBookingRepository sessionCourts = mock(SessionCourtBookingRepository.class);
    private final NotificationsRepository notifications = mock(NotificationsRepository.class);
    private final NotificationPreferencesService preferences = mock(NotificationPreferencesService.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final VenueBookingService service = new VenueBookingService(venues, claims, managers, courts, hours,
            bookings, paymentSetups, profiles, sessions, sessionCourts, notifications, preferences, events);

    private VenueCourt court;
    private LocalDate tomorrow;

    @BeforeEach
    void setUp() {
        Venue venue = new Venue();
        venue.setId(5L);
        venue.setName("Clube Padel Lisboa");
        venue.setCity("Lisboa");
        when(venues.findById(5L)).thenReturn(Optional.of(venue));
        VenueManager ana = new VenueManager();
        ana.setVenueId(5L);
        ana.setUsername("ana");
        when(managers.findByVenueId(5L)).thenReturn(List.of(ana));
        when(managers.existsByVenueIdAndUsernameIgnoreCase(5L, "ana")).thenReturn(true);
        court = new VenueCourt();
        court.setId(9L);
        court.setVenueId(5L);
        court.setName("Campo 1");
        court.setSlotMinutes(60);
        court.setPriceCents(2400);
        court.setActive(true);
        when(courts.findByVenueIdAndActiveTrueOrderByIdAsc(5L)).thenReturn(List.of(court));
        when(courts.lockById(9L)).thenReturn(Optional.of(court));
        when(courts.findById(9L)).thenReturn(Optional.of(court));
        tomorrow = LocalDate.now(VenueBookingService.ZONE).plusDays(1);
        VenueOpeningHours h = new VenueOpeningHours();
        h.setVenueId(5L);
        h.setDayOfWeek((short) tomorrow.getDayOfWeek().getValue());
        h.setOpenTime("09:00");
        h.setCloseTime("11:00");
        when(hours.findByVenueIdOrderByDayOfWeekAscOpenTimeAsc(5L)).thenReturn(List.of(h));
        when(preferences.allowsInApp(anyString(), any(Notifications.class))).thenReturn(true);
        when(notifications.save(any(Notifications.class))).thenAnswer(i -> i.getArgument(0));
        when(bookings.save(any(VenueBookingRequest.class))).thenAnswer(i -> {
            VenueBookingRequest b = i.getArgument(0);
            if (b.getId() == null) b.setId(77L);
            return b;
        });
    }

    private Instant at(LocalDate day, int hour) {
        return day.atTime(hour, 0).atZone(VenueBookingService.ZONE).toInstant();
    }

    @Test
    void aClaimNeedsAnExplanationAndStaysPendingUntilAnAdminDecides() {
        when(claims.save(any(VenueOwnershipClaim.class))).thenAnswer(i -> i.getArgument(0));
        assertThrows(ResponseStatusException.class, () -> service.claim(5L, "rui", "dono", null));
        VenueBookingService.ClaimView view = service.claim(5L, "rui", "Sou o gerente do clube desde 2020", "912 345 678");
        assertEquals("PENDING", view.status());
        verify(managers, never()).save(any());
    }

    @Test
    void approvingAClaimMakesThePersonAManagerAndTellsThem() {
        VenueOwnershipClaim claim = new VenueOwnershipClaim();
        claim.setId(3L);
        claim.setVenueId(5L);
        claim.setUsername("rui");
        claim.setRoleDescription("Sou o gerente do clube");
        claim.setStatus(VenueOwnershipClaim.Status.PENDING);
        claim.setCreatedAt(Instant.now());
        when(claims.findById(3L)).thenReturn(Optional.of(claim));
        VenueBookingService.ClaimView view = service.decideClaim(3L, "admin", true, null);
        assertEquals("APPROVED", view.status());
        verify(managers).save(argThat(m -> "rui".equals(m.getUsername()) && m.getVenueId() == 5L));
        verify(notifications).save(argThat(n -> "rui".equals(n.getUsername())));
    }

    @Test
    void slotsFollowTheOpeningHoursAndShowTakenOnes() {
        VenueBookingRequest held = new VenueBookingRequest();
        held.setId(1L);
        held.setCourtId(9L);
        held.setStartAt(at(tomorrow, 9));
        held.setEndAt(at(tomorrow, 10));
        held.setStatus(VenueBookingRequest.Status.CONFIRMED);
        when(bookings.holdingInVenue(eq(5L), any(), any(), any())).thenReturn(List.of(held));
        VenueBookingService.DaySlots day = service.slots(5L, tomorrow.toString());
        List<VenueBookingService.Slot> slots = day.courts().get(0).slots();
        assertEquals(2, slots.size());
        assertFalse(slots.get(0).free());
        assertTrue(slots.get(1).free());
        assertEquals(at(tomorrow, 10), slots.get(1).start());
    }

    @Test
    void aVenueWithoutOpeningHoursDoesNotAcceptBookings() {
        when(hours.findByVenueIdOrderByDayOfWeekAscOpenTimeAsc(5L)).thenReturn(List.of());
        assertFalse(service.offer(5L).acceptsBookings());
        assertThrows(ResponseStatusException.class,
                () -> service.request(5L, "rui", 9L, at(tomorrow, 9).toString(), null, null));
    }

    @Test
    void requestsMustMatchAFreeSlotAndNotifyTheManager() {
        when(bookings.overlapping(eq(9L), any(), any(), any())).thenReturn(List.of());
        assertThrows(ResponseStatusException.class,
                () -> service.request(5L, "rui", 9L, at(tomorrow, 9).plusSeconds(1800).toString(), null, null));
        VenueBookingService.BookingView view = service.request(5L, "rui", 9L, at(tomorrow, 9).toString(), null, "Somos 4");
        assertEquals("PENDING", view.status());
        assertEquals(2400, view.priceCents());
        verify(notifications).save(argThat(n -> "ana".equals(n.getUsername())));
    }

    @Test
    void aTakenSlotCannotBeRequestedTwice() {
        when(bookings.overlapping(eq(9L), any(), any(), any())).thenReturn(List.of(new VenueBookingRequest()));
        assertThrows(ResponseStatusException.class,
                () -> service.request(5L, "rui", 9L, at(tomorrow, 9).toString(), null, null));
    }

    @Test
    void onlyAManagerAnswersAndConfirmingMarksTheGameCourtAsBooked() {
        VenueBookingRequest b = new VenueBookingRequest();
        b.setId(77L);
        b.setVenueId(5L);
        b.setCourtId(9L);
        b.setRequesterUsername("rui");
        b.setStartAt(at(tomorrow, 9));
        b.setEndAt(at(tomorrow, 10));
        b.setStatus(VenueBookingRequest.Status.PENDING);
        b.setSessionId(12L);
        b.setCreatedAt(Instant.now());
        when(bookings.findById(77L)).thenReturn(Optional.of(b));
        when(bookings.overlapping(eq(9L), any(), any(), any())).thenReturn(List.of(b));
        assertThrows(ResponseStatusException.class, () -> service.decide(77L, "rui", true, null));
        VenueBookingService.BookingView view = service.decide(77L, "ana", true, "Até amanhã");
        assertEquals("CONFIRMED", view.status());
        verify(sessionCourts).save(argThat(row -> row.getSessionId() == 12L && row.isBooked()));
        verify(notifications).save(argThat(n -> "rui".equals(n.getUsername())));
    }

    @Test
    void openingHoursRejectOverlapsAndBadTimes() {
        assertThrows(ResponseStatusException.class, () -> service.saveHours(5L, "ana",
                List.of(new VenueBookingService.HoursInput(1, "09:00", "12:00"),
                        new VenueBookingService.HoursInput(1, "11:00", "13:00"))));
        assertThrows(ResponseStatusException.class, () -> service.saveHours(5L, "ana",
                List.of(new VenueBookingService.HoursInput(1, "9h", "12:00"))));
        assertThrows(ResponseStatusException.class, () -> service.saveHours(5L, "rui",
                List.of(new VenueBookingService.HoursInput(1, "09:00", "12:00"))));
    }

    @Test
    void mbwayNeedsAPortugueseMobileAndIsShownOnceTheBookingIsConfirmed() {
        assertThrows(ResponseStatusException.class, () -> service.savePayment(5L, "ana", "MBWAY", "212345678"));
        service.savePayment(5L, "ana", "MBWAY", "+351 912 345 678");
        verify(paymentSetups).save(argThat(p -> "912345678".equals(p.getMbwayPhone()) && "MBWAY".equals(p.getMethod())));

        VenuePaymentSetup setup = new VenuePaymentSetup();
        setup.setVenueId(5L);
        setup.setMethod("MBWAY");
        setup.setMbwayPhone("912345678");
        when(paymentSetups.findById(5L)).thenReturn(Optional.of(setup));
        VenueBookingRequest b = new VenueBookingRequest();
        b.setId(78L);
        b.setVenueId(5L);
        b.setCourtId(9L);
        b.setRequesterUsername("rui");
        b.setStartAt(at(tomorrow, 9));
        b.setEndAt(at(tomorrow, 10));
        b.setStatus(VenueBookingRequest.Status.PENDING);
        b.setPriceCents(2400);
        b.setCreatedAt(Instant.now());
        when(bookings.findById(78L)).thenReturn(Optional.of(b));
        assertThrows(ResponseStatusException.class, () -> service.markPaid(78L, "rui", true));
        assertNull(service.mine("rui").stream().findFirst().map(VenueBookingService.BookingView::mbwayPhone).orElse(null));

        b.setStatus(VenueBookingRequest.Status.CONFIRMED);
        VenueBookingService.BookingView paid = service.markPaid(78L, "rui", true);
        assertEquals("912345678", paid.mbwayPhone());
        assertNotNull(paid.paidClaimedAt());
        assertThrows(ResponseStatusException.class, () -> service.confirmPaid(78L, "rui", true));
        assertNotNull(service.confirmPaid(78L, "ana", true).paidConfirmedAt());
    }

    @Test
    void searchListsOnlyFreeSlotsAndFiltersBySportPlaceAndTime() {
        court.setSport("Padel");
        when(courts.venuesAcceptingBookings()).thenReturn(List.of(5L));
        VenueBookingRequest held = new VenueBookingRequest();
        held.setId(1L);
        held.setCourtId(9L);
        held.setStartAt(at(tomorrow, 9));
        held.setEndAt(at(tomorrow, 10));
        held.setStatus(VenueBookingRequest.Status.CONFIRMED);
        when(bookings.holdingInVenue(eq(5L), any(), any(), any())).thenReturn(List.of(held));
        List<VenueBookingService.VenueAvailability> found = service.search(tomorrow.toString(), "pádel", "lisb", null, null);
        assertEquals(1, found.size());
        assertEquals(List.of(at(tomorrow, 10)), found.get(0).courts().get(0).slots().stream()
                .map(VenueBookingService.Slot::start).toList());
        assertTrue(service.search(tomorrow.toString(), "ténis", null, null, null).isEmpty());
        assertTrue(service.search(tomorrow.toString(), null, "Porto", null, null).isEmpty());
        assertTrue(service.search(tomorrow.toString(), null, null, "11:00", null).isEmpty());
    }

    @Test
    void onlyTheRequesterCanLinkABookingToAGame() {
        VenueBookingRequest b = new VenueBookingRequest();
        b.setId(40L);
        b.setVenueId(5L);
        b.setCourtId(9L);
        b.setRequesterUsername("rui");
        b.setStartAt(at(tomorrow, 9));
        b.setEndAt(at(tomorrow, 10));
        b.setStatus(VenueBookingRequest.Status.PENDING);
        when(bookings.findById(40L)).thenReturn(Optional.of(b));
        assertThrows(ResponseStatusException.class, () -> service.linkSession(40L, "maria", 3L));
        verify(bookings, never()).save(any());
    }
}
