package com.example.desporto24.controllers;

import com.example.desporto24.service.SessionGuestRequestService;
import com.example.desporto24.service.interfaces.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Invalid invitation and organizer decision bodies must never cause an
 * internal server error or invoke a privileged guest mutation.
 */
class SessionGuestRequestControllerValidationTest {

    private final SessionGuestRequestService guests = mock(SessionGuestRequestService.class);
    private final SessionService sessions = mock(SessionService.class);
    private final SessionGuestRequestController controller =
            new SessionGuestRequestController(guests, sessions);

    private static void badRequest(Runnable operation) {
        ResponseStatusException error =
                assertThrows(ResponseStatusException.class, operation::run);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void nullInvitationPreviewAndRosterAreRejectedBeforeServiceLookup() {
        badRequest(() -> controller.preview(12L, null));
        badRequest(() -> controller.roster(12L, null));
        verifyNoInteractions(guests, sessions);
    }

    @Test
    void missingOrNullOrganizerDecisionCannotSilentlyRejectGuest() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("organizer");

        badRequest(() -> controller.review(12L, 23L, null, auth));
        badRequest(() -> controller.review(12L, 23L, Map.of(), auth));
        badRequest(() -> controller.review(12L, 23L,
                new java.util.HashMap<>(Map.of("other", true)), auth));

        verifyNoInteractions(guests, sessions);
    }

    @Test
    void explicitOrganizerDecisionIsPassedThroughUnchanged() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("organizer");

        controller.review(12L, 23L, Map.of("approve", true), auth);
        controller.review(12L, 23L, Map.of("approve", false), auth);

        verify(guests).review(12L, 23L, true, "organizer");
        verify(guests).review(12L, 23L, false, "organizer");
        verifyNoMoreInteractions(guests);
    }
}
