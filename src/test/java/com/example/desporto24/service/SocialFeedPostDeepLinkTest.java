package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.dto.social.FeedItemResponse;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.social.ProfileFollowRepository;
import com.example.desporto24.repository.social.SocialPostCommentRepository;
import com.example.desporto24.repository.social.SocialPostLikeRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Regression coverage for opening the exact post referenced by a notification. */
class SocialFeedPostDeepLinkTest {

    private SocialPostRepository posts;
    private SocialPostLikeRepository likes;
    private SocialPostCommentRepository comments;
    private ProfileRepository profiles;
    private SocialFeedService service;

    @BeforeEach
    void setUp() {
        posts = mock(SocialPostRepository.class);
        likes = mock(SocialPostLikeRepository.class);
        comments = mock(SocialPostCommentRepository.class);
        profiles = mock(ProfileRepository.class);
        service = new SocialFeedService(
                posts, mock(ProfileFollowRepository.class), likes, comments,
                profiles, mock(SessionRepository.class), mock(SessionParticipantRepository.class),
                mock(UserBlockService.class));
        Profile viewer = new Profile();
        viewer.setId(7L);
        viewer.setUsername("viewer");
        when(profiles.findUserByUsernameIgnoreCase("viewer")).thenReturn(Optional.of(viewer));
    }

    private SocialPost samplePost() {
        Profile author = new Profile();
        author.setId(8L);
        author.setUsername("author");
        return SocialPost.builder()
                .id(55L)
                .author(author)
                .content("Treino de hoje")
                .createdAt(Instant.parse("2026-11-10T12:00:00Z"))
                .hideLikers(true)
                .build();
    }

    @Test
    void exactPostReturnsAuthorContentCountsAndViewerLikeWithoutExposingLikerIdentities() {
        when(posts.findById(55L)).thenReturn(Optional.of(samplePost()));
        when(likes.countByPostId(55L)).thenReturn(4L);
        when(comments.countByPostId(55L)).thenReturn(2L);
        when(likes.existsByPostIdAndProfileId(55L, 7L)).thenReturn(true);

        FeedItemResponse item = service.postById(55L, "viewer");

        assertEquals(55L, item.postId());
        assertEquals("author", item.authorUsername());
        assertEquals("Treino de hoje", item.content());
        assertEquals(4L, item.likeCount());
        assertEquals(2L, item.commentCount());
        assertTrue(item.likedByMe());
        assertTrue(item.hideLikers());
        verify(likes, never()).findLikers(anyLong(), any());
    }

    @Test
    void notificationDoesNotExposeInternalAttendanceBackedPost() {
        SocialPost internal = samplePost();
        internal.setActivityType("SESSION_ATTENDED");
        when(posts.findById(55L)).thenReturn(Optional.of(internal));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.postById(55L, "viewer"));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(likes, comments);
    }

    @Test
    void missingOrInvalidPostReturnsNotFoundWithoutLeakingOtherContent() {
        when(posts.findById(55L)).thenReturn(Optional.empty());

        ResponseStatusException missing = assertThrows(ResponseStatusException.class,
                () -> service.postById(55L, "viewer"));
        ResponseStatusException invalid = assertThrows(ResponseStatusException.class,
                () -> service.postById(-1L, "viewer"));

        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, invalid.getStatusCode());
        verifyNoInteractions(likes, comments);
    }
}
