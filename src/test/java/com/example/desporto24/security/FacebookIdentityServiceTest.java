package com.example.desporto24.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FacebookIdentityServiceTest {
    private final ObjectMapper json = new ObjectMapper();

    private FacebookIdentityService service(String debug, String me, List<URI> calls) {
        return new FacebookIdentityService("123", "shh", uri -> {
            calls.add(uri);
            try {
                return json.readTree(uri.getPath().endsWith("/debug_token") ? debug : me);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void disabledWithoutSecretHidesAppIdAndRefuses() {
        FacebookIdentityService off = new FacebookIdentityService("123", "", uri -> null);
        assertThat(off.enabled()).isFalse();
        assertThat(off.appId()).isEmpty();
        assertThatThrownBy(() -> off.verify("tok")).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void acceptsValidTokenForOurAppAndSendsAppSecretProof() {
        List<URI> calls = new ArrayList<>();
        var id = service("{\"data\":{\"is_valid\":true,\"app_id\":\"123\",\"user_id\":\"42\"}}",
                "{\"id\":\"42\",\"name\":\"Ana Silva\",\"email\":\"Ana@Mail.PT\","
                        + "\"picture\":{\"data\":{\"is_silhouette\":false,\"url\":\"https://x/p.jpg\"}}}", calls)
                .verify("tok");
        assertThat(id.subject()).isEqualTo("42");
        assertThat(id.email()).isEqualTo("ana@mail.pt");
        assertThat(id.name()).isEqualTo("Ana Silva");
        assertThat(id.picture()).isEqualTo("https://x/p.jpg");
        assertThat(calls.get(1).getQuery()).contains("appsecret_proof=");
    }

    @Test
    void rejectsTokenIssuedForAnotherApp() {
        assertThatThrownBy(() -> service("{\"data\":{\"is_valid\":true,\"app_id\":\"999\",\"user_id\":\"42\"}}",
                "{\"id\":\"42\"}", new ArrayList<>()).verify("tok")).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void rejectsInvalidTokenAndMismatchedProfile() {
        assertThatThrownBy(() -> service("{\"data\":{\"is_valid\":false,\"app_id\":\"123\",\"user_id\":\"42\"}}",
                "{\"id\":\"42\"}", new ArrayList<>()).verify("tok")).isInstanceOf(BadCredentialsException.class);
        assertThatThrownBy(() -> service("{\"data\":{\"is_valid\":true,\"app_id\":\"123\",\"user_id\":\"42\"}}",
                "{\"id\":\"7\"}", new ArrayList<>()).verify("tok")).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void silhouettePictureAndMissingEmailBecomeNull() {
        var id = service("{\"data\":{\"is_valid\":true,\"app_id\":\"123\",\"user_id\":\"42\"}}",
                "{\"id\":\"42\",\"name\":\"Rui\",\"picture\":{\"data\":{\"is_silhouette\":true,\"url\":\"https://x\"}}}",
                new ArrayList<>()).verify("tok");
        assertThat(id.email()).isNull();
        assertThat(id.picture()).isNull();
    }
}
