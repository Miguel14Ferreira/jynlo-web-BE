package com.example.desporto24.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verifies "Sign in with Apple" identity tokens: RS256 signature with Apple's
 * published keys, issuer, audience (our bundle id) and expiry. The device's
 * claims are never trusted without this check.
 */
@Service
public class AppleIdentityService {
    static final String ISSUER = "https://appleid.apple.com";
    private static final URI KEYS = URI.create("https://appleid.apple.com/auth/keys");
    private static final Duration CACHE = Duration.ofHours(6);

    private final List<String> audiences;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper json = new ObjectMapper();
    private final Map<String, RSAPublicKey> keys = new ConcurrentHashMap<>();
    private volatile Instant keysLoadedAt = Instant.EPOCH;

    private final String webClientId;

    public AppleIdentityService(String clientIds) {
        this(clientIds, "");
    }

    /**
     * clientIds: the iPhone bundle id(s). webClientId: the Apple "Services ID" used by
     * "Sign in with Apple" on the website (empty = the web button stays hidden).
     */
    @org.springframework.beans.factory.annotation.Autowired
    public AppleIdentityService(@Value("${desporto.apple.client-ids:com.jynlo.app}") String clientIds,
                                @Value("${desporto.apple.web-client-id:}") String webClientId) {
        this.webClientId = webClientId == null ? "" : webClientId.trim();
        java.util.List<String> all = new java.util.ArrayList<>(
                Arrays.stream(clientIds.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        if (!this.webClientId.isEmpty() && !all.contains(this.webClientId)) all.add(this.webClientId);
        this.audiences = List.copyOf(all);
    }

    public String webClientId() {
        return webClientId;
    }

    public record AppleIdentity(String subject, String email, boolean privateEmail) {}

    public AppleIdentity verify(String identityToken) {
        if (identityToken == null || identityToken.isBlank()) throw new BadCredentialsException("Credencial Apple em falta");
        try {
            DecodedJWT unverified = JWT.decode(identityToken);
            return verifyWith(identityToken, key(unverified.getKeyId()));
        } catch (BadCredentialsException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BadCredentialsException("Não foi possível validar a credencial Apple", exception);
        }
    }

    AppleIdentity verifyWith(String identityToken, RSAPublicKey key) {
        DecodedJWT token = JWT.require(Algorithm.RSA256(provider(key)))
                .withIssuer(ISSUER)
                .withAnyOfAudience(audiences.toArray(String[]::new))
                .acceptLeeway(60)
                .build()
                .verify(identityToken);
        return identity(token);
    }

    static AppleIdentity identity(DecodedJWT token) {
        String subject = token.getSubject();
        if (subject == null || subject.isBlank()) throw new BadCredentialsException("Credencial Apple sem utilizador");
        String email = token.getClaim("email").asString();
        String verified = String.valueOf(token.getClaim("email_verified").as(Object.class));
        boolean emailVerified = "true".equalsIgnoreCase(verified);
        String privateEmail = String.valueOf(token.getClaim("is_private_email").as(Object.class));
        // Apple only lists verified addresses; ignore an unverified one rather than trust it.
        return new AppleIdentity(subject, emailVerified && email != null && !email.isBlank() ? email.trim() : null,
                "true".equalsIgnoreCase(privateEmail));
    }

    private RSAPublicKey key(String keyId) throws Exception {
        if (keyId == null) throw new BadCredentialsException("Credencial Apple sem chave");
        RSAPublicKey key = keys.get(keyId);
        if (key != null && Instant.now().isBefore(keysLoadedAt.plus(CACHE))) return key;
        load();
        key = keys.get(keyId);
        if (key == null) throw new BadCredentialsException("Chave Apple desconhecida");
        return key;
    }

    private synchronized void load() throws Exception {
        HttpResponse<String> response = http.send(HttpRequest.newBuilder(KEYS).timeout(Duration.ofSeconds(8)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IllegalStateException("Apple keys unavailable: " + response.statusCode());
        JsonNode root = json.readTree(response.body());
        KeyFactory factory = KeyFactory.getInstance("RSA");
        for (JsonNode k : root.path("keys")) {
            if (!"RSA".equals(k.path("kty").asText())) continue;
            BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(k.path("n").asText()));
            BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(k.path("e").asText()));
            keys.put(k.path("kid").asText(), (RSAPublicKey) factory.generatePublic(new RSAPublicKeySpec(n, e)));
        }
        keysLoadedAt = Instant.now();
    }

    private static RSAKeyProvider provider(RSAPublicKey key) {
        return new RSAKeyProvider() {
            public RSAPublicKey getPublicKeyById(String keyId) { return key; }
            public RSAPrivateKey getPrivateKey() { return null; }
            public String getPrivateKeyId() { return null; }
        };
    }
}
