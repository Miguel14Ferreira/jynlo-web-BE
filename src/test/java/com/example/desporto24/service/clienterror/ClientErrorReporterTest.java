package com.example.desporto24.service.clienterror;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class ClientErrorReporterTest {

    private final ClientErrorReporter reporter =
            new ClientErrorReporter(Clock.fixed(Instant.parse("2026-11-21T20:00:00Z"), ZoneOffset.UTC));

    private static ClientErrorReporter.Report report(String platform, String message) {
        return new ClientErrorReporter.Report(platform, "1.0.0", "/sessions/12", message, "at a\nat b", false);
    }

    @Test
    void acceptsKnownPlatformsOnly() {
        assertTrue(reporter.report(report("android", "boom"), false, null));
        assertTrue(reporter.report(report("WEB", "boom web"), false, null));
        assertFalse(reporter.report(report("windows", "boom"), false, null));
        assertFalse(reporter.report(report(null, "boom"), false, null));
        assertFalse(reporter.report(null, false, null));
    }

    @Test
    void ignoresEmptyMessages() {
        assertFalse(reporter.report(report("ios", "   "), false, null));
        assertFalse(reporter.report(report("ios", null), false, null));
    }

    @Test
    void masksPersonalDataAndSecrets() {
        String cleaned = ClientErrorReporter.clean(
                "user ana.silva@mail.pt Bearer abc.def token eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4In0.c2lnbmF0dXJlMTIz "
                        + "phone +351 912 345 678 url https://api.d24sports.com/api/x?code=123456&u=ana "
                        + "key AbCdEfGhIjKlMnOpQrStUvWxYz0123456789", 1000, false);
        assertFalse(cleaned.contains("ana.silva"));
        assertFalse(cleaned.contains("abc.def"));
        assertFalse(cleaned.contains("eyJhbGci"));
        assertFalse(cleaned.contains("912 345"));
        assertFalse(cleaned.contains("code=123456"));
        assertFalse(cleaned.contains("AbCdEfGhIjKlMnOp"));
        assertTrue(cleaned.contains("[email]"));
        assertTrue(cleaned.contains("https://api.d24sports.com/api/x?[...]"));
    }

    @Test
    void removesControlCharactersSoReportsCannotForgeLogLines() {
        String cleaned = ClientErrorReporter.clean("line one\nFAKE client_error\r\u001b[31m\"x\"", 300, false);
        assertFalse(cleaned.contains("\n"));
        assertFalse(cleaned.contains("\r"));
        assertFalse(cleaned.contains("\u001b"));
        assertFalse(cleaned.contains("\""));
    }

    @Test
    void cutsLongFields() {
        String cleaned = ClientErrorReporter.clean("a b ".repeat(5000), ClientErrorReporter.MAX_MESSAGE, false);
        assertTrue(cleaned.length() <= ClientErrorReporter.MAX_MESSAGE);
    }

    @Test
    void limitsTheSameErrorPerMinute() {
        int logged = 0;
        for (int i = 0; i < 20; i++) if (reporter.report(report("android", "same crash"), false, null)) logged++;
        assertEquals(ClientErrorReporter.PER_ERROR_PER_MINUTE, logged);
    }

    @Test
    void limitsEachSignedInPersonPerMinute() {
        int logged = 0;
        for (int i = 0; i < 50; i++) if (reporter.report(report("android", "crash " + i), true, "Ana")) logged++;
        assertEquals(ClientErrorReporter.PER_USER_PER_MINUTE, logged);
        assertTrue(reporter.report(report("android", "other person"), true, "rui"));
    }

    @Test
    void limitsAllReportsPerMinute() {
        int logged = 0;
        for (int i = 0; i < 300; i++) if (reporter.report(report("web", "distinct " + i), false, null)) logged++;
        assertEquals(ClientErrorReporter.GLOBAL_PER_MINUTE, logged);
    }
}
