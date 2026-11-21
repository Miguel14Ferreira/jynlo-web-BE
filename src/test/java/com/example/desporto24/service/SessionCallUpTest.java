package com.example.desporto24.service;

import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionCallUpTest {
    private final SessionRepository sessions = mock(SessionRepository.class);
    private final SessionCallUp callUp = new SessionCallUp(sessions);

    private Session session(String organiser, SessionStatus status, long offsetMs) {
        Session session = new Session();
        Profile profile = new Profile();
        profile.setUsername(organiser);
        session.setProfile(profile);
        session.setStatus(status);
        session.setGameDate(new Date(System.currentTimeMillis() + offsetMs));
        return session;
    }

    @Test
    void ordinaryMessagesAreIgnored() {
        callUp.validate("Vens quinta?", "ana");
        callUp.validate(null, "ana");
        verifyNoInteractions(sessions);
    }

    @Test
    void organiserCanPostCallUpForUpcomingOpenSession() {
        when(sessions.findById(7L)).thenReturn(Optional.of(session("Ana", SessionStatus.OPEN, 3_600_000)));
        assertDoesNotThrow(() -> callUp.validate("[JYNLO:SESSION:1]7", "ana"));
    }

    @Test
    void fullSessionsStillAcceptCallUpsForTheWaitlist() {
        when(sessions.findById(7L)).thenReturn(Optional.of(session("ana", SessionStatus.FULL, 3_600_000)));
        assertDoesNotThrow(() -> callUp.validate("[JYNLO:SESSION:1]7", "ana"));
    }

    @Test
    void onlyTheOrganiserCanPostIt() {
        when(sessions.findById(7L)).thenReturn(Optional.of(session("ana", SessionStatus.OPEN, 3_600_000)));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> callUp.validate("[JYNLO:SESSION:1]7", "rui"));
        assertEquals(HttpStatus.FORBIDDEN, error.getStatusCode());
    }

    @Test
    void pastCancelledOrUnknownSessionsAreRejected() {
        when(sessions.findById(1L)).thenReturn(Optional.of(session("ana", SessionStatus.OPEN, -3_600_000)));
        when(sessions.findById(2L)).thenReturn(Optional.of(session("ana", SessionStatus.CANCELLED, 3_600_000)));
        when(sessions.findById(3L)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> callUp.validate("[JYNLO:SESSION:1]1", "ana")).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> callUp.validate("[JYNLO:SESSION:1]2", "ana")).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> callUp.validate("[JYNLO:SESSION:1]3", "ana")).getStatusCode());
    }

    @Test
    void malformedEnvelopesAreRejected() {
        for (String text : new String[]{"[JYNLO:SESSION:1]", "[JYNLO:SESSION:1]abc", "[JYNLO:SESSION:1]7 extra"}) {
            assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                    () -> callUp.validate(text, "ana")).getStatusCode());
        }
    }

    @Test
    void previewsHideTheEnvelope() {
        assertEquals("📣 Convocatória", ChatMessagePreview.forText("[JYNLO:SESSION:1]7"));
        assertEquals("olá", ChatMessagePreview.forText("olá"));
    }
}
