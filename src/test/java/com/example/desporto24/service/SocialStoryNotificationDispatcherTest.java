package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.service.interfaces.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialStoryNotificationDispatcherTest {
    @Mock private SocialStoryNotificationWriter writer;
    @Mock private NotificationPreferencesService preferences;
    @Mock private NotificationService notifications;

    @Test
    void failedOptionalNotificationNeverPropagatesToCommittedStoryAction() {
        SocialStoryNotificationDispatcher dispatcher = new SocialStoryNotificationDispatcher(writer);
        var event = new SocialStoryNotificationDispatcher.StoryNotice(
                15L, "ana", "joao", NotificationType.SOCIAL_STORY_REPLIED, "Resposta privada");
        doThrow(new IllegalStateException("notification DB unavailable")).when(writer).deliver(event);
        assertDoesNotThrow(() -> dispatcher.afterCommit(event));
        verify(writer).deliver(event);
    }

    @Test
    void writerRespectsOptOutWithoutSavingNotification() {
        SocialStoryNotificationWriter service = new SocialStoryNotificationWriter(preferences, notifications);
        var event = new SocialStoryNotificationDispatcher.StoryNotice(
                15L, "ana", "joao", NotificationType.SOCIAL_STORY_REPLIED, "Resposta privada");
        service.deliver(event);
        verify(preferences).allowsInApp("ana", NotificationType.SOCIAL_STORY_REPLIED);
        verifyNoInteractions(notifications);
    }

    @Test
    void writerStoresNotificationWithStoryTargetWhenAllowed() {
        SocialStoryNotificationWriter service = new SocialStoryNotificationWriter(preferences, notifications);
        var event = new SocialStoryNotificationDispatcher.StoryNotice(
                15L, "ana", "joao", NotificationType.SOCIAL_STORY_REPLIED, "Boa defesa!");
        when(preferences.allowsInApp("ana", NotificationType.SOCIAL_STORY_REPLIED)).thenReturn(true);
        service.deliver(event);
        verify(notifications).save(argThat(n -> n.getStoryId().equals(15L)
                && n.getUsername().equals("ana")
                && n.getRelatedUsername().equals("joao")
                && n.getMessage().equals("Boa defesa!")
                && n.getType() == NotificationType.SOCIAL_STORY_REPLIED));
    }

    @Test
    void selfLikesNeverNotifyTheAuthor() {
        SocialStoryNotificationWriter service = new SocialStoryNotificationWriter(preferences, notifications);
        service.deliver(new SocialStoryNotificationDispatcher.StoryNotice(
                15L, "ana", "ANA", NotificationType.SOCIAL_STORY_LIKED, "Gosto"));
        verifyNoInteractions(preferences, notifications);
    }
}
