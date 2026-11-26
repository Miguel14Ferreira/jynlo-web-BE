package com.example.desporto24.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HexFormat;

/**
 * Verifies Facebook Login user access tokens server-side: the token must be
 * valid and issued for OUR app (debug_token with the app token), and the
 * profile is then read with appsecret_proof. Nothing the client says about
 * the user is trusted. Disabled (and hidden in the clients) until both
 * FACEBOOK_APP_ID and FACEBOOK_APP_SECRET are configured.
 */
@Service
public class FacebookIdentityService {
    static final String GRAPH = "https://graph.facebook.com/v21.0";

    public interface Graph {
        JsonNode get(URI uri);
    }

    private final String appId;
    private final String appSecret;
    private final Graph graph;

    /** Show the button to everyone only once the Meta app is published (FACEBOOK_LOGIN_LIVE=true). */
    private final boolean live;

    @org.springframework.beans.factory.annotation.Autowired
    public FacebookIdentityService(@Value("${desporto.facebook.app-id:}") String appId,
                                   @Value("${desporto.facebook.app-secret:}") String appSecret,
                                   @Value("${desporto.facebook.live:false}") boolean live) {
        this(appId, appSecret, httpGraph(), live);
    }

    FacebookIdentityService(String appId, String appSecret, Graph graph) {
        this(appId, appSecret, graph, true);
    }

    FacebookIdentityService(String appId, String appSecret, Graph graph, boolean live) {
        this.appId = appId == null ? "" : appId.trim();
        this.appSecret = appSecret == null ? "" : appSecret.trim();
        this.graph = graph;
        this.live = live;
    }

    public record FacebookIdentity(String subject, String email, String name, String picture) {}

    public boolean enabled() {
        return !appId.isEmpty() && !appSecret.isEmpty();
    }

    /** Public: the app id is not a secret (it is shipped in the web page and the app). */
    public String appId() {
        return enabled() && live ? appId : "";
    }

    public FacebookIdentity verify(String accessToken) {
        if (!enabled()) throw new BadCredentialsException("O login com Facebook ainda não está disponível");
        if (accessToken == null || accessToken.isBlank() || accessToken.length() > 4096) {
            throw new BadCredentialsException("Credencial Facebook em falta");
        }
        String token = accessToken.trim();
        JsonNode debug = graph.get(URI.create(GRAPH + "/debug_token?input_token=" + enc(token)
                + "&access_token=" + enc(appId + "|" + appSecret)));
        JsonNode data = debug == null ? null : debug.path("data");
        if (data == null || !data.path("is_valid").asBoolean(false)
                || !appId.equals(data.path("app_id").asText(""))
                || data.path("user_id").asText("").isBlank()) {
            throw new BadCredentialsException("Sessão do Facebook inválida. Tenta outra vez.");
        }
        String userId = data.path("user_id").asText();
        JsonNode me = graph.get(URI.create(GRAPH + "/me?fields=id,name,email,picture.width(256).height(256)"
                + "&access_token=" + enc(token) + "&appsecret_proof=" + enc(proof(token))));
        if (me == null || !userId.equals(me.path("id").asText(""))) {
            throw new BadCredentialsException("Sessão do Facebook inválida. Tenta outra vez.");
        }
        String email = blankToNull(me.path("email").asText(null));
        String name = blankToNull(me.path("name").asText(null));
        JsonNode pic = me.path("picture").path("data");
        String picture = pic.path("is_silhouette").asBoolean(true) ? null : blankToNull(pic.path("url").asText(null));
        return new FacebookIdentity(userId, email == null ? null : email.toLowerCase(java.util.Locale.ROOT), name, picture);
    }

    String proof(String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Falha ao assinar o pedido ao Facebook", exception);
        }
    }

    private static String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }

    private static String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }

    private static Graph httpGraph() {
        HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        ObjectMapper json = new ObjectMapper();
        return uri -> {
            try {
                HttpResponse<String> response = http.send(HttpRequest.newBuilder(uri)
                        .timeout(Duration.ofSeconds(8)).GET().build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new BadCredentialsException("Sessão do Facebook inválida. Tenta outra vez.");
                }
                return json.readTree(response.body());
            } catch (BadCredentialsException exception) {
                throw exception;
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new BadCredentialsException("Não foi possível confirmar o login com o Facebook");
            } catch (Exception exception) {
                throw new BadCredentialsException("Não foi possível confirmar o login com o Facebook");
            }
        };
    }
}
