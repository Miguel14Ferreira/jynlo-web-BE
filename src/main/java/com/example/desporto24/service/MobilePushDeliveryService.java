package com.example.desporto24.service;

import com.example.desporto24.repository.MobilePushDeviceRepository;
import com.example.desporto24.register.Notifications.Notifications;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Fail-closed, optional Expo transport. Push payloads must not include private message text. */
@Service
@RequiredArgsConstructor
public class MobilePushDeliveryService {
    private static final URI EXPO_SEND = URI.create("https://exp.host/--/api/v2/push/send");
    private final MobilePushDeviceRepository devices;
    private final com.example.desporto24.repository.MobilePushReceiptRepository receipts;
    private final NotificationPreferencesService preferences;
    private final ConversationNotificationMuteService mutes;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3)).build();

    @Value("${jynlo.push.enabled:false}")
    private boolean enabled;
    @Value("${jynlo.push.access-token:}")
    private String accessToken;

    public record StoredNotification(String username, Long notificationId,
            String category, Long sessionId, Long communityId, String relatedUsername,
            boolean permitted, Long privateGroupId) {
        /** Preserve callers that predate private group alerts. */
        public StoredNotification(String username, Long notificationId,
                String category, Long sessionId, Long communityId, String relatedUsername,
                boolean permitted) {
            this(username, notificationId, category, sessionId, communityId, relatedUsername, permitted, null);
        }
    }

    /**
     * No delivery on transaction rollback. Notifications are only a cue to reopen
     * the authenticated inbox, never a transport for message bodies or addresses.
     */
    @Async("jynloPushExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(StoredNotification event) {
        if (!enabled || accessToken == null || accessToken.isBlank() || !event.permitted()
                || event.username() == null || event.username().isBlank()
                || event.notificationId() == null || event.notificationId() <= 0
                // Mobile parses push IDs with JavaScript Number.isSafeInteger.
                || event.notificationId() > 9_007_199_254_740_991L) return;
        String account = event.username().toLowerCase(Locale.ROOT);
        // Re-evaluate account preferences at delivery time.
        var rules = preferences.get(account);
        if (!rules.inAppEnabled() || rules.disabledInAppTypes().contains(event.category())) return;
        if ("CHAT_MESSAGE".equals(event.category())) {
            // A chat push without a trustworthy conversation reference cannot
            // be checked against per-conversation mutes: fail closed.
            String kind;
            String key;
            if (event.privateGroupId()!=null && event.privateGroupId()>0) {
                kind="private"; key=event.privateGroupId().toString();
            } else if (event.sessionId()!=null && event.sessionId()>0) {
                kind="session"; key=event.sessionId().toString();
            } else if (event.communityId()!=null && event.communityId()>0) {
                kind="community"; key=event.communityId().toString();
            } else if (event.relatedUsername()!=null
                    && event.relatedUsername().toLowerCase(Locale.ROOT).matches("[a-z0-9._-]{1,128}")) {
                kind="direct"; key=event.relatedUsername().toLowerCase(Locale.ROOT);
            } else return;
            if (mutes.muted(account,kind,key)) return;
        }
        List<com.example.desporto24.model.MobilePushDevice> recipients =
                devices.findTop20ByUsernameOrderByUpdatedAtDesc(account);
        for (var device : recipients) {
            if (!account.equals(device.getUsername())) continue;
            deliver(device.getDeviceId(),device.getPushToken(), event.notificationId(), event.category());
        }
    }

    private void deliver(String deviceId,String token, Long notificationId, String category) {
        try {
            byte[] body = json.writeValueAsBytes(Map.of(
                    "to", token,
                    "title", text(category)[0],
                    "body", text(category)[1],
                    "sound", "default",
                    "channelId", "jynlo-messages",
                    "data", Map.of("kind", "notification", "id", notificationId)
            ));
            HttpRequest request = HttpRequest.newBuilder(EXPO_SEND)
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();
            var response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return;
            JsonNode tickets = json.readTree(response.body()).path("data");
            JsonNode ticket = tickets.isArray() && !tickets.isEmpty() ? tickets.get(0) : tickets;
            if ("ok".equals(ticket.path("status").asText())) {
                String ticketId=ticket.path("id").asText("");
                if(ticketId.matches("[A-Za-z0-9_-]{8,80}")) {
                    var pending=new com.example.desporto24.model.MobilePushReceipt();
                    pending.setTicketId(ticketId);
                    pending.setDeviceId(deviceId);
                    pending.setTokenHash(com.example.desporto24.service.MobilePushReceiptPoller.fingerprint(token));
                    pending.setCreatedAt(java.time.Instant.now());
                    pending.setNextCheckAt(pending.getCreatedAt().plus(java.time.Duration.ofMinutes(15)));
                    pending.setAttempts(0);
                    receipts.save(pending);
                }
            }
            if ("error".equals(ticket.path("status").asText())
                    && "DeviceNotRegistered".equals(ticket.path("details").path("error").asText())) {
                devices.findByPushToken(token).ifPresent(devices::delete);
            }
            // Expo receipt collection and permanent-error handling are a separate
            // required step before treating push delivery as production-ready.
        } catch (Exception ignored) {
            // Push delivery must never make a successful notification transaction fail.
            // Do not log tokens, external errors or private message metadata.
        }
    }

    /**
     * Lock-screen text per notification type. Deliberately generic: never names,
     * message bodies, places or times, which stay behind the authenticated inbox.
     */
    static String[] text(String category) {
        if (category == null) return new String[]{"Jynlo", "Tens uma nova notificação."};
        return switch (category) {
            case "PAYMENT_REMINDER" -> new String[]{"Pagamento em falta", "O organizador lembra que falta pagares um jogo."};
            case "TOURNAMENT_RESULT" -> new String[]{"Resultado do torneio", "Foi registado um resultado de um jogo teu."};
            case "TOURNAMENT_LEADER" -> new String[]{"Mudança na classificação", "Há um novo líder num torneio em que jogas."};
            case "AVAILABILITY_REQUEST" -> new String[]{"Quem pode?", "Alguém quer marcar um jogo. Abre a Jynlo e diz quando podes."};
            case "AVAILABILITY_REMINDER" -> new String[]{"Ainda não respondeste", "Estão à espera da tua disponibilidade para um jogo."};
            case "NEARBY_SESSION" -> new String[]{"Novo jogo perto de ti", "Há um novo jogo perto de ti. Abre a Jynlo para veres se te interessa."};
            case "SESSION_REMINDER" -> new String[]{"Lembrete de jogo", "Tens um jogo em breve. Abre a Jynlo para veres os detalhes."};
            case "SESSION_CANCELLED" -> new String[]{"Sessão cancelada", "Uma sessão em que estavas inscrito já não se vai realizar. Abre a Jynlo para saberes porquê."};
            case "SESSION_REMOVED" -> new String[]{"Sessão", "Deixaste de estar inscrito numa sessão."};
            case "WAITLIST_PROMOTED" -> new String[]{"Tens lugar!", "Saíste da lista de espera: já estás inscrito na sessão."};
            case "SESSION_MEETING_POINT_UPDATED" -> new String[]{"Ponto de encontro alterado", "O ponto de encontro de uma sessão tua mudou."};
            case "SESSION_DETAILS_UPDATED" -> new String[]{"Sessão atualizada", "Os detalhes de uma sessão tua mudaram."};
            case "SESSION_POST_GAME_REVIEW" -> new String[]{"Como correu o jogo?", "Confirma as presenças da última sessão."};
            case "SESSION_INVITE" -> new String[]{"Convite para jogar", "Convidaram-te para uma sessão."};
            case "ATTENDANCE_DISPUTE_ACCEPTED", "ATTENDANCE_DISPUTE_REJECTED" -> new String[]{"Presenças", "Há uma decisão sobre a tua contestação de presença."};
            case "COMMUNITY_INVITE" -> new String[]{"Convite para comunidade", "Convidaram-te para uma comunidade."};
            case "COMMUNITY_JOIN_REQUEST" -> new String[]{"Pedido de entrada", "Alguém quer entrar numa comunidade tua."};
            case "COMMUNITY_JOIN_ACCEPTED" -> new String[]{"Bem-vindo!", "O teu pedido de entrada na comunidade foi aceite."};
            case "COMMUNITY_JOIN_REJECTED" -> new String[]{"Comunidade", "Há uma resposta ao teu pedido de entrada."};
            case "CHAT_MESSAGE" -> new String[]{"Nova mensagem", "Tens uma nova mensagem na Jynlo."};
            case "SOCIAL_NEW_FOLLOWER" -> new String[]{"Novo seguidor", "Alguém começou a seguir-te."};
            case "SOCIAL_POST_LIKED", "SOCIAL_STORY_LIKED" -> new String[]{"Gostaram do teu conteúdo", "Alguém gostou de uma publicação tua."};
            case "SOCIAL_POST_COMMENTED" -> new String[]{"Novo comentário", "Comentaram uma publicação tua."};
            case "SOCIAL_STORY_REPLIED" -> new String[]{"Nova resposta", "Responderam a uma story tua."};
            case "TEAM_MEMBER_INVITE" -> new String[]{"Convite para equipa", "Convidaram-te para uma equipa."};
            case "TEAM_ADMIN_TRANSFER", "TEAM_ADMIN_ASSIGNED" -> new String[]{"Equipa", "O teu papel numa equipa mudou."};
            default -> new String[]{"Jynlo", "Tens uma nova notificação."};
        };
    }
}
