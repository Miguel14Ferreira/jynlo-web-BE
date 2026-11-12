package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.dto.social.CreateSocialCommentRequest;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialPostLikeRepository;
import com.example.desporto24.repository.social.SocialPostCommentRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.NotificationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialPostInteractionBlockTest {
    @Mock private SocialPostRepository posts;
    @Mock private SocialPostLikeRepository likes;
    @Mock private SocialPostCommentRepository comments;
    @Mock private ProfileRepository profiles;
    @Mock private UserReportService reports;
    @Mock private SessionParticipantRepository participants;
    @Mock private NotificationsRepository notifications;
    @Mock private NotificationPreferencesService preferences;
    @Mock private UserBlockService blocks;
    @InjectMocks private SocialPostInteractionService service;

    private Profile viewer;
    private Profile author;
    private SocialPost post;

    @BeforeEach
    void setUp() {
        viewer = new Profile();
        viewer.setId(11L);
        viewer.setUsername("viewer");
        author = new Profile();
        author.setId(22L);
        author.setUsername("author");
        post = SocialPost.builder()
                .id(51L).author(author).content("Treino de hoje")
                .createdAt(Instant.now()).build();
    }

    @Test
    void blockedActorsCannotLikeOrComment() {
        when(posts.findById(51L)).thenReturn(Optional.of(post));
        when(profiles.findUserByUsernameIgnoreCase("viewer"))
                .thenReturn(Optional.of(viewer));
        doThrow(new AccessDeniedException("Blocked"))
                .when(blocks).ensureInteractionAllowed(viewer, author);

        assertThrows(AccessDeniedException.class, () -> service.like(51L, "viewer"));
        assertThrows(AccessDeniedException.class,
                () -> service.comment(51L, "viewer",
                        new CreateSocialCommentRequest("Olá")));
        verifyNoInteractions(likes, comments, notifications);
    }

    @Test
    void blockedActorsCannotReadCommentsOrLikers() {
        when(posts.findById(51L)).thenReturn(Optional.of(post));
        when(profiles.findUserByUsernameIgnoreCase("viewer"))
                .thenReturn(Optional.of(viewer));
        doThrow(new AccessDeniedException("Blocked"))
                .when(blocks).ensureInteractionAllowed(viewer, author);

        assertThrows(AccessDeniedException.class,
                () -> service.comments(51L, "viewer"));
        assertThrows(AccessDeniedException.class,
                () -> service.likers(51L, "viewer"));
        verifyNoInteractions(comments, likes);
    }

    @Test
    void aPreviouslyLikedPostCanStillBeUnlikedAfterBlocking() {
        when(posts.findById(51L)).thenReturn(Optional.of(post));
        when(profiles.findUserByUsernameIgnoreCase("viewer"))
                .thenReturn(Optional.of(viewer));
        when(likes.countByPostId(51L)).thenReturn(0L);

        service.unlike(51L, "viewer");

        verify(likes).deleteByPostIdAndProfileId(51L, 11L);
        verify(blocks, never()).ensureInteractionAllowed(any(Profile.class), any(Profile.class));
    }

    @Test
    void likerListExcludesProfilesBlockedByTheViewer() {
        // Identity filtering happens before profile information is mapped to responses.
        // The author relationship itself must be checked even when the list is empty.
        when(posts.findById(51L)).thenReturn(Optional.of(post));
        when(profiles.findUserByUsernameIgnoreCase("viewer"))
                .thenReturn(Optional.of(viewer));
        when(likes.findLikers(eq(51L), any())).thenReturn(java.util.List.of());

        assertTrue(service.likers(51L, "viewer").isEmpty());
        verify(blocks).ensureInteractionAllowed(viewer, author);
    }
}
