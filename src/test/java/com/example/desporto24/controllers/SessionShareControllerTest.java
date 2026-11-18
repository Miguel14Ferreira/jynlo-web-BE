package com.example.desporto24.controllers;

import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.exception.SessionNotFound;
import com.example.desporto24.model.dto.session.SessionPublicPreviewResponse;
import com.example.desporto24.service.interfaces.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionShareControllerTest {

    @Mock private SessionService sessionService;

    private SessionShareController controller;

    @BeforeEach
    void setUp() {
        controller = new SessionShareController(sessionService, "https://d24sports.com/");
    }

    private static SessionPublicPreviewResponse preview(String sport, String city, int max, int taken,
                                                        SessionStatus status) {
        return new SessionPublicPreviewResponse(7L, sport, city, Instant.parse("2026-11-18T20:30:00Z"),
                max, taken, BigDecimal.ZERO, status, null);
    }

    @Test
    void openSessionShowsSportCityTimeAndFreeSpots() {
        when(sessionService.publicPreview(7L)).thenReturn(preview("Padel", "Lisboa", 4, 3, SessionStatus.OPEN));

        ResponseEntity<String> response = controller.session(7L);
        String body = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(body.contains("og:title\" content=\"Padel em Lisboa · "));
        assertTrue(body.contains("20:30"));
        assertTrue(body.contains("1 vaga livre. Junta-te a este jogo na Jynlo."));
        assertTrue(body.contains("og:url\" content=\"https://d24sports.com/sessions/7\""));
        assertTrue(body.contains("og:image\" content=\"https://d24sports.com/assets/og-default.png\""));
        assertEquals("noindex", response.getHeaders().getFirst("X-Robots-Tag"));
    }

    @Test
    void severalFreeSpotsUsePlural() {
        when(sessionService.publicPreview(7L)).thenReturn(preview("Futebol 7", "Porto", 14, 10, SessionStatus.OPEN));

        assertTrue(controller.session(7L).getBody().contains("4 vagas livres."));
    }

    @Test
    void fullCancelledAndCompletedSessionsSayWhatHappened() {
        when(sessionService.publicPreview(1L)).thenReturn(preview("Padel", "Lisboa", 4, 4, SessionStatus.FULL));
        when(sessionService.publicPreview(2L)).thenReturn(preview("Padel", "Lisboa", 4, 2, SessionStatus.CANCELLED));
        when(sessionService.publicPreview(3L)).thenReturn(preview("Padel", "Lisboa", 4, 4, SessionStatus.COMPLETED));

        assertTrue(controller.session(1L).getBody().contains("Jogo completo."));
        assertTrue(controller.session(2L).getBody().contains("Este jogo foi cancelado."));
        assertTrue(controller.session(3L).getBody().contains("Este jogo já terminou."));
    }

    @Test
    void organiserTextIsEscapedSoItCannotBreakOutOfTheTags() {
        when(sessionService.publicPreview(7L)).thenReturn(
                preview("\"><script>alert(1)</script>", "Braga & arredores", 4, 1, SessionStatus.OPEN));

        String body = controller.session(7L).getBody();

        assertFalse(body.contains("<script>"));
        assertTrue(body.contains("&lt;script&gt;"));
        assertTrue(body.contains("Braga &amp; arredores"));
    }

    @Test
    void sessionThatCannotBePreviewedGetsTheGenericCardWithoutRevealingAnything() {
        when(sessionService.publicPreview(99L)).thenThrow(new SessionNotFound("Session not found"));

        ResponseEntity<String> response = controller.session(99L);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("og:title\" content=\"Jynlo — Join the game.\""));
        assertFalse(response.getBody().contains("vaga"));
    }

    @Test
    void missingFieldsStillProduceAUsableCard() {
        when(sessionService.publicPreview(5L)).thenReturn(new SessionPublicPreviewResponse(
                5L, " ", null, null, null, null, null, SessionStatus.OPEN, null));

        String body = controller.session(5L).getBody();

        assertTrue(body.contains("og:title\" content=\"Jogo\""));
        assertTrue(body.contains("Junta-te a este jogo na Jynlo."));
    }
}
