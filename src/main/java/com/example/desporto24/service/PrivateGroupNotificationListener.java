package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.PrivateGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Locale;

/**
 * Alerts the other members when a private group message is committed.
 * One unread alert per group and member (a busy group does not flood the inbox or the phone),
 * and the usual account preferences and per-conversation mutes apply.
 */
@Component
@RequiredArgsConstructor
public class PrivateGroupNotificationListener {
    private final NotificationsRepository notifications;
    private final PrivateGroupRepository groups;
    private final NotificationPreferencesService preferences;
    private final ConversationNotificationMuteService mutes;
    private final ApplicationEventPublisher events;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onGroupEvent(PrivateGroupService.GroupEvent event) {
        if (event == null || event.message() == null || event.groupId() == null || event.usernames() == null) return;
        String sender = event.message().sender();
        if (sender == null) return;
        String title = groups.findById(event.groupId()).map(com.example.desporto24.model.PrivateGroup::getTitle).orElse(null);
        if (title == null) return;
        String preview = ChatMessagePreview.forText(event.message().text());
        for (String member : event.usernames()) {
            if (member == null || member.equalsIgnoreCase(sender)) continue;
            try {
                notify(member, sender, event.groupId(), title, preview);
            } catch (RuntimeException ignored) {
                // One member's alert must never block the others; the message itself is already saved.
            }
        }
    }

    /** Opening the group clears its alert, so the next message alerts again. Membership is checked by the caller. */
    @Transactional
    public void markRead(String username, Long groupId) {
        if (username == null || groupId == null || groupId <= 0) return;
        var unread = notifications.findByUsernameIgnoreCaseAndPrivateGroupIdAndReadFalse(username, groupId);
        if (unread == null || unread.isEmpty()) return;
        unread.forEach(n -> n.setRead(true));
        notifications.saveAll(unread);
    }

    private void notify(String member, String sender, Long groupId, String title, String preview) {
        if (mutes.muted(member.toLowerCase(Locale.ROOT), "private", groupId.toString())) return;
        // Every message alerts the phone (like WhatsApp); the inbox keeps one entry per chat with the latest text.
        var open = notifications.findByUsernameIgnoreCaseAndPrivateGroupIdAndReadFalse(member, groupId);
        Notifications n = open == null || open.isEmpty() ? new Notifications() : open.get(0);
        n.setSubject("Nova mensagem em " + title);
        n.setMessage(sender + ": " + preview);
        n.setGreeting("");
        n.setSignature("");
        n.setCreatedAt(Instant.now().toString());
        n.setNormalMessage(true);
        n.setUsername(member);
        n.setType(NotificationType.CHAT_MESSAGE);
        n.setPrivateGroupId(groupId);
        n.setRead(false);
        if (!preferences.allowsInApp(member, n)) return;
        Notifications saved = notifications.save(n);
        if (saved != null && saved.getId() != null) {
            events.publishEvent(new MobilePushDeliveryService.StoredNotification(
                    saved.getUsername(), saved.getId(), NotificationType.CHAT_MESSAGE.name(),
                    null, null, null, true, groupId));
        }
    }
}
