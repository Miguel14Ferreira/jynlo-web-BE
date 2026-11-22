package com.example.desporto24.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AppleIdentityServiceTest {
    private static KeyPair keys() throws Exception {
        KeyPairGenerator g = KeyPairGenerator.getInstance("RSA");
        g.initialize(2048);
        return g.generateKeyPair();
    }

    private static String token(KeyPair kp, String issuer, String audience, Instant exp) {
        return JWT.create().withIssuer(issuer).withAudience(audience).withSubject("001234.abc")
                .withClaim("email", "ana@privaterelay.appleid.com").withClaim("email_verified", "true")
                .withClaim("is_private_email", true).withExpiresAt(exp).withKeyId("k1")
                .sign(Algorithm.RSA256((RSAPublicKey) kp.getPublic(), (RSAPrivateKey) kp.getPrivate()));
    }

    @Test
    void acceptsAValidAppleTokenForOurApp() throws Exception {
        KeyPair kp = keys();
        var service = new AppleIdentityService("com.jynlo.app");
        var id = service.verifyWith(token(kp, AppleIdentityService.ISSUER, "com.jynlo.app",
                Instant.now().plusSeconds(600)), (RSAPublicKey) kp.getPublic());
        assertEquals("001234.abc", id.subject());
        assertEquals("ana@privaterelay.appleid.com", id.email());
        assertTrue(id.privateEmail());
    }

    @Test
    void rejectsOtherAppsIssuersExpiredAndForgedTokens() throws Exception {
        KeyPair kp = keys(), other = keys();
        var service = new AppleIdentityService("com.jynlo.app");
        RSAPublicKey key = (RSAPublicKey) kp.getPublic();
        assertThrows(Exception.class, () -> service.verifyWith(
                token(kp, AppleIdentityService.ISSUER, "com.other.app", Instant.now().plusSeconds(600)), key));
        assertThrows(Exception.class, () -> service.verifyWith(
                token(kp, "https://evil.example", "com.jynlo.app", Instant.now().plusSeconds(600)), key));
        assertThrows(Exception.class, () -> service.verifyWith(
                token(kp, AppleIdentityService.ISSUER, "com.jynlo.app", Instant.now().minusSeconds(600)), key));
        assertThrows(Exception.class, () -> service.verifyWith(
                token(other, AppleIdentityService.ISSUER, "com.jynlo.app", Instant.now().plusSeconds(600)), key));
    }
}
