package com.example.desporto24.service;

import com.example.desporto24.model.MobilePushReceipt;
import com.example.desporto24.repository.MobilePushDeviceRepository;
import com.example.desporto24.repository.MobilePushReceiptRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

/** Durable, read-only receipt lookup; never resends the original push notification. */
@Service
@RequiredArgsConstructor
public class MobilePushReceiptPoller {
    private static final URI RECEIPTS = URI.create("https://exp.host/--/api/v2/push/getReceipts");
    private static final Duration MAX_AGE = Duration.ofHours(24);
    private final MobilePushReceiptRepository receipts;
    private final MobilePushDeviceRepository devices;
    // Jackson 2 is used for Expo's wire format; it is not auto-configured as a Spring bean.
    private final ObjectMapper json = new ObjectMapper();
    private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    @Value("${jynlo.push.enabled:false}")
    private boolean enabled;
    @Value("${jynlo.push.access-token:}")
    private String accessToken;

    public static String fingerprint(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    // Scheduled checks run only when configured; one bounded batch per invocation.
    @Scheduled(fixedDelayString = "${jynlo.push.receipts.poll-interval-ms:60000}")
    public void checkDueReceipts() {
        if (!enabled || accessToken == null || accessToken.isBlank()) return;
        Instant now=Instant.now();
        List<MobilePushReceipt> batch=receipts.findTop100ByNextCheckAtBeforeOrderByNextCheckAtAsc(now);
        if(batch.isEmpty())return;
        // Prune each expired ticket separately. A failed delete must not stop
        // receipt checks for still-valid devices in the same scheduled batch.
        List<MobilePushReceipt> due=pruneExpiredReceipts(batch,now);
        if(due.isEmpty())return;
        try {
            byte[] body=json.writeValueAsBytes(Map.of("ids",due.stream().map(MobilePushReceipt::getTicketId).toList()));
            HttpRequest request=HttpRequest.newBuilder(RECEIPTS).timeout(Duration.ofSeconds(7))
                    .header("Content-Type","application/json")
                    .header("Authorization","Bearer "+accessToken)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();
            HttpResponse<String> response=http.send(request,HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!=200){
                defer(due,now);
                return;
            }
            JsonNode payload=json.readTree(response.body());
            if(!payload.path("errors").isMissingNode() && !payload.path("errors").isEmpty()){
                defer(due,now);
                return;
            }
            JsonNode results=payload.path("data");
            if(!results.isObject()){
                defer(due,now);
                return;
            }
            processReceipts(due,results,now);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            defer(due,now);
        } catch (Exception ignored) {
            // No tokens, provider body, private metadata or secrets in logs.
            defer(due,now);
        }
    }

    List<MobilePushReceipt> pruneExpiredReceipts(List<MobilePushReceipt> batch,Instant now) {
        List<MobilePushReceipt> due=new java.util.ArrayList<>();
        for(var item:batch){
            if(item.getCreatedAt()!=null && item.getCreatedAt().plus(MAX_AGE).isAfter(now)){
                due.add(item);
            } else {
                try {receipts.delete(item);}
                catch (Exception ignored) {
                    // Retry deletion in the next scheduled run, without blocking
                    // still-valid Expo receipts or exposing device information.
                }
            }
        }
        return due;
    }

    /** An individual failed DB operation must never rewind already processed receipts. */
    void processReceipts(List<MobilePushReceipt> due,JsonNode results,Instant now) {
        for(var item:due){
            try {
                JsonNode receipt=results.path(item.getTicketId());
                switch (MobilePushReceiptPolicy.classify(receipt)) {
                    case CONFIRMED, FAILED -> receipts.delete(item);
                    case REVOKE_DEVICE -> {
                        // Never delete a device that has rotated its token after this ticket.
                        devices.findById(item.getDeviceId()).ifPresent(device -> {
                            if(device.getPushToken()!=null
                                    && fingerprint(device.getPushToken()).equals(item.getTokenHash()))
                                devices.delete(device);
                        });
                        receipts.delete(item);
                    }
                    case PENDING -> defer(List.of(item),now);
                }
            } catch (Exception ignored) {
                // Only this receipt may be retried; successful rows stay settled.
                try { defer(List.of(item),now); }
                catch (Exception ignoredAgain) {
                    // A database outage will be retried on a subsequent scheduled run.
                }
            }
        }
    }

    private void defer(List<MobilePushReceipt> due,Instant now) {
        for(var item:due){
            if(item.getAttempts()>=24 || !item.getCreatedAt().plus(MAX_AGE).isAfter(now)){
                receipts.delete(item);
                continue;
            }
            item.setAttempts(item.getAttempts()+1);
            item.setNextCheckAt(now.plus(Duration.ofMinutes(Math.min(60,5L * item.getAttempts()))));
            receipts.save(item);
        }
    }
}
