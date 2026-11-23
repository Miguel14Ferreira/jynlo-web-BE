package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

/**
 * Phone alerts for 1:1 calls when the app is closed: "is calling you" while it
 * rings and "missed call" if nobody answered. The ringing alert is cleared once
 * the call is answered, declined or missed. Direct-chat mutes apply.
 */
@Service
@RequiredArgsConstructor
public class CallNotificationService {
    private final NotificationsRepository notifications;
    private final NotificationPreferencesService preferences;
    private final ConversationNotificationMuteService mutes;
    private final ApplicationEventPublisher events;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incoming(String caller, String callee) {
        safely(() -> create(callee, caller, NotificationType.CALL_INCOMING,
                "Chamada de " + caller, caller + " está a ligar-te."));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void answered(String caller, String callee) {
        safely(() -> clearRinging(callee, caller));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void missed(String caller, String callee) {
        safely(() -> {
            clearRinging(callee, caller);
            create(callee, caller, NotificationType.CALL_MISSED,
                    "Chamada perdida", "Chamada perdida de " + caller + ".");
        });
    }

    private void clearRinging(String callee, String caller) {
        var ringing = notifications.findByUsernameIgnoreCaseAndRelatedUsernameIgnoreCaseAndTypeAndReadFalse(
                callee, caller, NotificationType.CALL_INCOMING);
        if (ringing == null || ringing.isEmpty()) return;
        ringing.forEach(n -> n.setRead(true));
        notifications.saveAll(ringing);
    }

    private void create(String callee, String caller, NotificationType type, String subject, String message) {
        if (callee == null || caller == null) return;
        if (mutes.muted(callee.toLowerCase(Locale.ROOT), "direct", caller.toLowerCase(Locale.ROOT))) return;
        Notifications n = new Notifications();
        n.setSubject(subject);
        n.setMessage(message);
        n.setGreeting("");
        n.setSignature("");
        n.setCreatedAt(Instant.now().toString());
        n.setNormalMessage(true);
        n.setUsername(callee);
        n.setRelatedUsername(caller);
        n.setType(type);
        n.setRead(false);
        if (!preferences.allowsInApp(callee, n)) return;
        Notifications saved = notifications.save(n);
        if (saved != null && saved.getId() != null) {
            events.publishEvent(new MobilePushDeliveryService.StoredNotification(
                    saved.getUsername(), saved.getId(), type.name(), null, null, caller, true));
        }
    }

    private static void safely(Runnable action) {
        try { action.run(); } catch (RuntimeException ignored) {
            // An alert must never break call signalling.
        }
    }
}
