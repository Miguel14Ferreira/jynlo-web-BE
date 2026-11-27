package com.example.desporto24.service.payment;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class StripeClientTest {
    private static final String SECRET = "whsec_test_secret";

    @Test
    void acceptsAFreshCorrectSignature() {
        Instant now = Instant.ofEpochSecond(1_800_000_000L);
        String payload = "{\"type\":\"checkout.session.completed\"}";
        String sig = StripeClient.hmac(SECRET, now.getEpochSecond() + "." + payload);
        assertTrue(StripeClient.validSignature(payload, "t=" + now.getEpochSecond() + ",v1=" + sig, SECRET, now));
    }

    @Test
    void rejectsWrongOldOrMissingSignatures() {
        Instant now = Instant.ofEpochSecond(1_800_000_000L);
        String payload = "{}";
        String sig = StripeClient.hmac(SECRET, now.getEpochSecond() + "." + payload);
        assertFalse(StripeClient.validSignature(payload, "t=" + now.getEpochSecond() + ",v1=" + sig, "other", now));
        assertFalse(StripeClient.validSignature("{\"x\":1}", "t=" + now.getEpochSecond() + ",v1=" + sig, SECRET, now));
        assertFalse(StripeClient.validSignature(payload, "t=" + now.getEpochSecond() + ",v1=" + sig, SECRET, now.plusSeconds(301)));
        assertFalse(StripeClient.validSignature(payload, null, SECRET, now));
        assertFalse(StripeClient.validSignature(payload, "v1=" + sig, SECRET, now));
    }

    @Test
    void isOffUntilBothKeysAreSet() {
        assertFalse(new StripeClient("", "").enabled());
        assertFalse(new StripeClient("sk_test_x", "").enabled());
        assertTrue(new StripeClient("sk_test_x", "whsec_x").enabled());
    }
}
