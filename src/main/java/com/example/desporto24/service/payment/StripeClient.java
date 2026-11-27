package com.example.desporto24.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The few Stripe calls Jynlo needs, over plain HTTPS. Standard connected accounts: each organiser has their
 * own Stripe account, pays Stripe's fees and receives the money directly; Jynlo never holds it.
 * Disabled (and every payment UI hidden) until STRIPE_SECRET_KEY and STRIPE_WEBHOOK_SECRET are set.
 */
@Component
public class StripeClient {
    static final String API = "https://api.stripe.com/v1";
    static final long WEBHOOK_TOLERANCE_SECONDS = 300;

    public record CheckoutLink(String id, String url) {}

    private final String secretKey;
    private final String webhookSecret;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper json = new ObjectMapper();

    public StripeClient(@Value("${desporto.stripe.secret-key:}") String secretKey,
                        @Value("${desporto.stripe.webhook-secret:}") String webhookSecret) {
        this.secretKey = secretKey == null ? "" : secretKey.trim();
        this.webhookSecret = webhookSecret == null ? "" : webhookSecret.trim();
    }

    public boolean enabled() { return !secretKey.isEmpty() && !webhookSecret.isEmpty(); }

    public String createAccount(String email) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("type", "standard");
        form.put("country", "PT");
        if (email != null && !email.isBlank()) form.put("email", email.strip());
        return post("/accounts", form, null).path("id").asText();
    }

    public String onboardingLink(String accountId, String refreshUrl, String returnUrl) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("account", accountId);
        form.put("type", "account_onboarding");
        form.put("refresh_url", refreshUrl);
        form.put("return_url", returnUrl);
        return post("/account_links", form, null).path("url").asText();
    }

    public boolean chargesEnabled(String accountId) {
        return get("/accounts/" + URLEncoder.encode(accountId, StandardCharsets.UTF_8)).path("charges_enabled").asBoolean(false);
    }

    /** Stripe-hosted page on the organiser's account, with MB WAY and card. */
    public CheckoutLink checkout(String accountId, int amountCents, String name, String successUrl, String cancelUrl,
                                 Map<String, String> metadata) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("mode", "payment");
        form.put("payment_method_types[0]", "mb_way");
        form.put("payment_method_types[1]", "card");
        form.put("line_items[0][quantity]", "1");
        form.put("line_items[0][price_data][currency]", "eur");
        form.put("line_items[0][price_data][unit_amount]", String.valueOf(amountCents));
        form.put("line_items[0][price_data][product_data][name]", name);
        form.put("success_url", successUrl);
        form.put("cancel_url", cancelUrl);
        form.put("locale", "pt");
        metadata.forEach((k, v) -> {
            form.put("metadata[" + k + "]", v);
            form.put("payment_intent_data[metadata][" + k + "]", v);
        });
        JsonNode node = post("/checkout/sessions", form, accountId);
        return new CheckoutLink(node.path("id").asText(), node.path("url").asText());
    }

    /** Checks the Stripe-Signature header and returns the parsed event, or throws 400. */
    public JsonNode verifiedEvent(String payload, String signatureHeader) {
        if (!enabled()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!validSignature(payload, signatureHeader, webhookSecret, Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assinatura inválida");
        }
        try {
            return json.readTree(payload);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento inválido");
        }
    }

    static boolean validSignature(String payload, String header, String secret, Instant now) {
        if (payload == null || header == null || secret == null || secret.isEmpty()) return false;
        Long timestamp = null;
        java.util.List<String> signatures = new java.util.ArrayList<>();
        for (String part : header.split(",")) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length != 2) continue;
            if (kv[0].equals("t")) {
                try { timestamp = Long.parseLong(kv[1]); } catch (NumberFormatException ignored) { return false; }
            } else if (kv[0].equals("v1")) {
                signatures.add(kv[1]);
            }
        }
        if (timestamp == null || signatures.isEmpty()) return false;
        if (Math.abs(now.getEpochSecond() - timestamp) > WEBHOOK_TOLERANCE_SECONDS) return false;
        String expected = hmac(secret, timestamp + "." + payload);
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        return signatures.stream().anyMatch(s -> MessageDigest.isEqual(expectedBytes, s.getBytes(StandardCharsets.UTF_8)));
    }

    static String hmac(String secret, String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private JsonNode post(String path, Map<String, String> form, String stripeAccount) {
        String body = form.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        HttpRequest.Builder request = base(path).header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (stripeAccount != null) request.header("Stripe-Account", stripeAccount);
        return send(request.build());
    }

    private JsonNode get(String path) {
        return send(base(path).GET().build());
    }

    private HttpRequest.Builder base(String path) {
        if (!enabled()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Os pagamentos na app ainda não estão ativos.");
        return HttpRequest.newBuilder(URI.create(API + path))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + secretKey);
    }

    private JsonNode send(HttpRequest request) {
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = json.readTree(response.body());
            if (response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "O serviço de pagamentos não respondeu como esperado. Tenta daqui a pouco.");
            }
            return node;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "O serviço de pagamentos não respondeu.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "O serviço de pagamentos não respondeu.");
        }
    }
}
