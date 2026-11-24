package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.websocket.CommunityChatMessageCreatedEvent;
import com.example.desporto24.websocket.CommunityChatRecipientResolver;
import com.example.desporto24.websocket.InboxOwnReadUpdatedEvent;
import com.example.desporto24.websocket.SessionChatMessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Game and community chat alerts, like team chats: at most one unread alert per
 * conversation and member (a busy chat sends one phone alert, not one per
 * message). Account preferences and the per-conversation mute apply; reading
 * the conversation clears the alert so the next message alerts again.
 */
@Component
@RequiredArgsConstructor
public class GroupChatNotificationListener {
    private final NotificationsRepository notifications;
    private final SessionRepository sessions;
    private final CommunityRepository communities;
    private final CommunityChatRecipientResolver communityRecipients;
    private final NotificationPreferencesService preferences;
    private final ConversationNotificationMuteService mutes;
    private final ApplicationEventPublisher events;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onSessionMessage(SessionChatMessageCreatedEvent event) {
        var m = event == null ? null : event.message();
        // Edits and deletions reuse the realtime event: only brand-new messages alert.
        if (m == null || m.sessionId() == null || m.senderUsername() == null
                || m.editedAt() != null || m.deletedAt() != null || event.recipients() == null) return;
        var session = sessions.findById(m.sessionId()).orElse(null);
        if (session == null) return;
        String title = "Nova mensagem no jogo" + (session.getSport() == null ? "" : " de " + session.getSport());
        String preview = m.senderUsername() + ": " + ChatMessagePreview.forText(m.body());
        for (String member : event.recipients()) {
            if (member == null || member.equalsIgnoreCase(m.senderUsername())) continue;
            try { notify(member, "session", m.sessionId(), title, preview); }
            catch (RuntimeException ignored) { /* one member must never block the others */ }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCommunityMessage(CommunityChatMessageCreatedEvent event) {
        var m = event == null ? null : event.message();
        if (m == null || m.communityId() == null || m.username() == null
                || m.editedAt() != null || m.deletedAt() != null || event.recipients() == null) return;
        Set<String> current;
        try {
            // Members removed meanwhile get nothing; never widen the original audience.
            current = new HashSet<>(communityRecipients.currentRecipients(m.communityId()));
        } catch (RuntimeException e) { return; }
        var community = communities.findById(m.communityId()).orElse(null);
        if (community == null) return;
        String name = community.getName() == null || community.getName().isBlank() ? "comunidade" : community.getName().strip();
        String preview = m.username() + ": " + ChatMessagePreview.forText(m.body());
        for (String member : event.recipients()) {
            if (member == null || member.equalsIgnoreCase(m.username()) || !current.contains(member)) continue;
            try { notify(member, "community", m.communityId(), "Novas mensagens em " + name, preview); }
            catch (RuntimeException ignored) { }
        }
    }

    /** Reading a game or community chat clears its alert. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRead(InboxOwnReadUpdatedEvent event) {
        if (event == null || event.username() == null || event.conversationId() == null) return;
        long id;
        try { id = Long.parseLong(event.conversationId()); } catch (NumberFormatException e) { return; }
        var unread = switch (event.kind() == null ? "" : event.kind()) {
            case "session" -> notifications.findByUsernameIgnoreCaseAndSessionIdAndTypeAndReadFalse(
                    event.username(), id, NotificationType.CHAT_MESSAGE);
            case "community" -> notifications.findByUsernameIgnoreCaseAndCommunityIdAndTypeAndReadFalse(
                    event.username(), id, NotificationType.CHAT_MESSAGE);
            default -> null;
        };
        if (unread == null || unread.isEmpty()) return;
        unread.forEach(n -> n.setRead(true));
        notifications.saveAll(unread);
    }

    private void notify(String member, String kind, Long id, String subject, String preview) {
        if (mutes.muted(member.toLowerCase(Locale.ROOT), kind, id.toString())) return;
        // Every message alerts the phone (like WhatsApp); the inbox keeps one entry per chat with the latest text.
        var open = "session".equals(kind)
                ? notifications.findByUsernameIgnoreCaseAndSessionIdAndTypeAndReadFalse(member, id, NotificationType.CHAT_MESSAGE)
                : notifications.findByUsernameIgnoreCaseAndCommunityIdAndTypeAndReadFalse(member, id, NotificationType.CHAT_MESSAGE);
        Notifications n = open == null || open.isEmpty() ? new Notifications() : open.get(0);
        n.setSubject(subject);
        n.setMessage(preview);
        n.setGreeting("");
        n.setSignature("");
        n.setCreatedAt(Instant.now().toString());
        n.setNormalMessage(true);
        n.setUsername(member);
        n.setType(NotificationType.CHAT_MESSAGE);
        if ("session".equals(kind)) n.setSessionId(id); else n.setCommunityId(id);
        n.setRead(false);
        if (!preferences.allowsInApp(member, n)) return;
        Notifications saved = notifications.save(n);
        if (saved != null && saved.getId() != null) {
            events.publishEvent(new MobilePushDeliveryService.StoredNotification(
                    saved.getUsername(), saved.getId(), NotificationType.CHAT_MESSAGE.name(),
                    "session".equals(kind) ? id : null, "community".equals(kind) ? id : null,
                    null, true, null, null));
        }
    }
}
