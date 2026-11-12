package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

/**
 * Story notifications are optional. They must never roll back the durable
 * private reply, and must not be sent if the original transaction rolls back.
 */
@Component
@RequiredArgsConstructor
public class SocialStoryNotificationDispatcher {
    private static final Logger log = LoggerFactory.getLogger(SocialStoryNotificationDispatcher.class);

    public record StoryNotice(Long storyId, String authorUsername, String senderUsername,
                              NotificationType type, String message) {}

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void afterCommit(StoryNotice event) {
        try {
            deliver(event);
        } catch (RuntimeException failure) {
            log.warn("Optional Story notification could not be delivered for Story {}",
                    event.storyId(), failure);
        }
    }

    private final NotificationPreferencesService preferences;
    private final NotificationService notifications;

    void deliver(StoryNotice event) {
        if (event.authorUsername().equalsIgnoreCase(event.senderUsername()) ||
                !preferences.allowsInApp(event.authorUsername(), event.type())) return;

        Notifications notice = new Notifications();
        notice.setUsername(event.authorUsername());
        notice.setRelatedUsername(event.senderUsername());
        notice.setStoryId(event.storyId());
        notice.setSubject(event.type() == NotificationType.SOCIAL_STORY_LIKED
                ? "Gosto na Story" : "Resposta à Story");
        notice.setMessage(event.message());
        notice.setGreeting("");
        notice.setSignature("");
        notice.setCreatedAt(Instant.now().toString());
        notice.setType(event.type());
        notice.setRead(false);
        notifications.save(notice);
    }
}
