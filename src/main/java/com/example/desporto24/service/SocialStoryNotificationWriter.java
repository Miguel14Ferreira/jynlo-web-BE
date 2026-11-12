package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/** Separate DB transaction for optional notices after Story data has committed. */
@Service
@RequiredArgsConstructor
public class SocialStoryNotificationWriter {
    private final NotificationPreferencesService preferences;
    private final NotificationService notifications;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deliver(SocialStoryNotificationDispatcher.StoryNotice event) {
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
