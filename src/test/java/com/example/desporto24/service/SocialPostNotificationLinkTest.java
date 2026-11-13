package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.social.SocialPostCommentRepository;
import com.example.desporto24.repository.social.SocialPostLikeRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Verify that social actions create notifications linking to exactly their source post. */
class SocialPostNotificationLinkTest {
    private SocialPostRepository posts;
    private SocialPostLikeRepository likes;
    private ProfileRepository profiles;
    private NotificationsRepository notifications;
    private NotificationPreferencesService preferences;
    private SocialPostInteractionService service;

    @BeforeEach
    void setup() {
        posts = mock(SocialPostRepository.class);
        likes = mock(SocialPostLikeRepository.class);
        profiles = mock(ProfileRepository.class);
        notifications = mock(NotificationsRepository.class);
        preferences = mock(NotificationPreferencesService.class);
        service = new SocialPostInteractionService(
                posts, likes, mock(SocialPostCommentRepository.class),
                mock(com.example.desporto24.repository.social.SocialPostCommentLikeRepository.class),
                profiles,
                mock(UserReportService.class), mock(SessionParticipantRepository.class),
                notifications, preferences, mock(UserBlockService.class),
                mock(com.example.desporto24.websocket.ChatRealtimeGateway.class));
    }

    private Profile profile(long id, String name) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(name);
        return p;
    }

    @Test
    void newLikeNotifiesAuthorWithExactPostIdAndActor() {
        Profile author = profile(1L, "owner");
        Profile liker = profile(2L, "liker");
        SocialPost post = SocialPost.builder().id(55L).author(author)
                .content("Treino").createdAt(Instant.now()).build();
        when(posts.findById(55L)).thenReturn(Optional.of(post));
        when(profiles.findUserByUsernameIgnoreCase("liker")).thenReturn(Optional.of(liker));
        when(preferences.allowsInApp("owner", NotificationType.SOCIAL_POST_LIKED)).thenReturn(true);

        service.like(55L, "liker");

        ArgumentCaptor<Notifications> sent = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications).save(sent.capture());
        assertEquals(55L, sent.getValue().getPostId());
        assertEquals("owner", sent.getValue().getUsername());
        assertEquals("liker", sent.getValue().getRelatedUsername());
        assertEquals(NotificationType.SOCIAL_POST_LIKED, sent.getValue().getType());
    }

    @Test
    void likingOwnPostNeverSendsNotification() {
        Profile owner = profile(1L, "owner");
        SocialPost post = SocialPost.builder().id(55L).author(owner)
                .content("Treino").createdAt(Instant.now()).build();
        when(posts.findById(55L)).thenReturn(Optional.of(post));
        when(profiles.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));

        service.like(55L, "owner");

        verify(notifications, never()).save(any());
    }
}
