package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Optional Story notifications are delivered only AFTER the Story reply/like
 * commits. A failure in the independent notification transaction cannot undo it.
 */
@Component
@RequiredArgsConstructor
public class SocialStoryNotificationDispatcher {
    private static final Logger log = LoggerFactory.getLogger(SocialStoryNotificationDispatcher.class);

    public record StoryNotice(Long storyId, String authorUsername, String senderUsername,
                              NotificationType type, String message) {}

    private final SocialStoryNotificationWriter writer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(StoryNotice event) {
        try {
            writer.deliver(event);
        } catch (RuntimeException failure) {
            log.warn("Optional Story notification could not be delivered for Story {}",
                    event.storyId(), failure);
        }
    }
}
