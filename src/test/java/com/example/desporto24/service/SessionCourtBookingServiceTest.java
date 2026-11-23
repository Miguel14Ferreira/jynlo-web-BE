package com.example.desporto24.service;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionCourtBooking;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.model.venue.Venue;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionCourtBookingRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SessionCourtBookingServiceTest {
    private final SessionRepository sessions = mock(SessionRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final SessionParticipantRepository participants = mock(SessionParticipantRepository.class);
    private final SessionCourtBookingRepository bookings = mock(SessionCourtBookingRepository.class);
    private final SessionCourtBookingService service = new SessionCourtBookingService(sessions, profiles, participants, bookings);
    private Session session;
    private SessionCourtBooking stored;

    private Profile profile(long id, String username) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        when(profiles.findUserByUsername(username)).thenReturn(Optional.of(p));
        return p;
    }

    @BeforeEach
    void setUp() {
        Profile ana = profile(1, "ana");
        Profile rui = profile(2, "rui");
        profile(3, "intruso");
        session = new Session();
        session.setId(7L);
        session.setProfile(ana);
        session.setStatus(SessionStatus.OPEN);
        session.setGameDate(Date.from(Instant.now().plus(2, ChronoUnit.DAYS)));
        Venue venue = new Venue();
        venue.setName("Clube Padel Lisboa");
        venue.setWebsite("clubepadel.pt/reservas");
        session.setVenue(venue);
        when(sessions.findById(7L)).thenReturn(Optional.of(session));
        SessionParticipant ruiIn = new SessionParticipant();
        ruiIn.setProfile(rui);
        ruiIn.setState(ParticipantState.ACTIVE);
        when(participants.findBySessionIdAndProfileIdAndState(eq(7L), anyLong(), eq(ParticipantState.ACTIVE)))
                .thenAnswer(i -> ((Long) i.getArgument(1)) == 2L ? Optional.of(ruiIn) : Optional.empty());
        when(bookings.findById(7L)).thenAnswer(i -> Optional.ofNullable(stored));
        when(bookings.save(any())).thenAnswer(i -> stored = i.getArgument(0));
    }

    @Test
    void organiserSavesTheLinkAndMarksTheCourtBooked() {
        var view = service.update(7L, "ana", "playtomic.com/clubs/clube-padel-lisboa", true);
        assertEquals("https://playtomic.com/clubs/clube-padel-lisboa", view.bookingUrl());
        assertTrue(view.booked());
        assertNotNull(view.bookedAt());
        assertEquals("https://clubepadel.pt/reservas", view.suggestedUrl());
        assertEquals("Clube Padel Lisboa", view.venueName());
        assertTrue(view.editable());
    }

    @Test
    void playersSeeTheBookingButCannotChangeIt() {
        service.update(7L, "ana", null, true);
        var view = service.get(7L, "rui");
        assertTrue(view.booked());
        assertFalse(view.organiser());
        assertFalse(view.editable());
        assertThrows(ResponseStatusException.class, () -> service.update(7L, "rui", null, false));
    }

    @Test
    void outsidersCannotSeeIt() {
        assertThrows(ResponseStatusException.class, () -> service.get(7L, "intruso"));
    }

    @Test
    void rejectsUnsafeLinks() {
        for (String bad : new String[] {"javascript:alert(1)", "http://clube.pt", "https://localhost/x",
                "https://user:pass@clube.pt", "ftp://clube.pt"}) {
            assertThrows(ResponseStatusException.class, () -> service.update(7L, "ana", bad, false), bad);
        }
    }

    @Test
    void cannotChangeAfterKickOff() {
        session.setGameDate(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));
        assertThrows(ResponseStatusException.class, () -> service.update(7L, "ana", null, true));
        assertFalse(service.get(7L, "ana").editable());
    }
}
