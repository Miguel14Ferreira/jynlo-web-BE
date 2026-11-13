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
import org.springframework.mock.web.MockMultipartFile;
import com.example.desporto24.storage.MediaStorage;
import org.springframework.test.util.ReflectionTestUtils;
import java.net.URI;
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
    void imageOnlyPostKeepsVoluntaryLocationWithoutNeedingCaption() {
        MediaStorage storage=mock(MediaStorage.class);
        ReflectionTestUtils.setField(service, "mediaStorage", storage);
        var photo=new MockMultipartFile("image","treino.jpg","image/jpeg",new byte[]{1,2,3});
        when(storage.store(anyString(),same(photo))).thenReturn(
                URI.create("/media/posts/7/treino.jpg"));
        when(posts.save(any())).thenAnswer(invocation -> {
            SocialPost post=invocation.getArgument(0);
            post.setId(123L);
            return post;
        });

        var item=service.createPostWithImage("viewer","",photo,null,"  Campo do Bairro, Lisboa  ");
        assertEquals("",item.content());
        assertEquals("Campo do Bairro, Lisboa",item.location());
        assertEquals("/media/posts/7/treino.jpg",item.imageUrl());
        verify(posts).save(argThat(post -> "".equals(post.getContent())
                && "Campo do Bairro, Lisboa".equals(post.getLocationLabel())));
    }

    @Test
    void failedPhotoPersistenceDeletesUploadedAsset() {
        MediaStorage storage = mock(MediaStorage.class);
        ReflectionTestUtils.setField(service, "mediaStorage", storage);
        var photo = new MockMultipartFile("image", "campo.jpg", "image/jpeg", new byte[]{1, 2});
        when(storage.store(anyString(), same(photo))).thenReturn(URI.create("/media/posts/7/campo.jpg"));
        when(posts.save(any())).thenThrow(new IllegalStateException("Database unavailable"));

        assertThrows(IllegalStateException.class,
                () -> service.createPostWithImage("viewer", "Jogo", photo, null, "Lisboa"));

        verify(storage).delete("posts/7/campo.jpg");
    }

    @Test
    void failedVideoPersistenceDeletesUploadedAsset() {
        MediaStorage storage = mock(MediaStorage.class);
        ReflectionTestUtils.setField(service, "mediaStorage", storage);
        var video = new MockMultipartFile("video", "jogo.mp4", "video/mp4", new byte[]{1, 2});
        when(storage.storeVideo(anyString(), same(video))).thenReturn(URI.create("/media/posts/7/jogo.mp4"));
        when(posts.save(any())).thenThrow(new IllegalStateException("Database unavailable"));

        assertThrows(IllegalStateException.class,
                () -> service.createPostWithMedia("viewer", "", null, video, null, "Lisboa"));

        verify(storage).delete("posts/7/jogo.mp4");
    }

    @Test
    void invalidAlbumLocationIsRejectedBeforeUploadingAnyFile() {
        MediaStorage storage = mock(MediaStorage.class);
        ReflectionTestUtils.setField(service, "mediaStorage", storage);
        var photo = new MockMultipartFile("files", "campo.jpg", "image/jpeg", new byte[]{1});
        assertThrows(IllegalArgumentException.class, () ->
                service.createPostWithAlbum("viewer", "", java.util.List.of(photo), null, "x".repeat(161)));
        verifyNoInteractions(storage);
        verify(posts, never()).save(any());
    }

    @Test
    void textOnlyPostsRejectEmptyContentEvenWhenALocationWasProvided() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createPostWithImage("viewer","",null,null,"Lisboa"));
        verify(posts,never()).save(any());
    }

    @Test
    void hidingAFeedPostIsViewerScopedAndDoesNotDeleteTheAuthorsContent() {
        var hidden = mock(com.example.desporto24.repository.social.HiddenFeedPostRepository.class);
        ReflectionTestUtils.setField(service, "hiddenFeedPosts", hidden);
        when(posts.findById(55L)).thenReturn(Optional.of(samplePost()));

        service.hidePost(55L, "viewer");
        verify(hidden).save(argThat(row -> row.getViewerId().equals(7L)
                && row.getPostId().equals(55L)));
        verify(posts, never()).delete(any());

        when(hidden.existsByViewerIdAndPostId(7L, 55L)).thenReturn(true);
        service.hidePost(55L, "viewer");
        verify(hidden, times(1)).save(any());

        service.unhidePost(55L, "viewer");
        verify(hidden).deleteByViewerIdAndPostId(7L, 55L);
        verify(posts, never()).delete(any());
    }

    @Test
    void hiddenPostsAreFilteredOnlyFromTheRequestingViewersFeed() {
        var follows = mock(ProfileFollowRepository.class);
        var sessions = mock(SessionRepository.class);
        var attendance = mock(SessionParticipantRepository.class);
        var hidden = mock(com.example.desporto24.repository.social.HiddenFeedPostRepository.class);
        var tested = new SocialFeedService(posts, follows, likes, comments, profiles,
                sessions, attendance, mock(UserBlockService.class));
        ReflectionTestUtils.setField(tested, "hiddenFeedPosts", hidden);

        SocialPost one = samplePost();
        SocialPost two = samplePost();
        two.setId(56L);
        when(follows.findByFollowerIdOrderByCreatedAtDesc(7L)).thenReturn(java.util.List.of());
        when(posts.findAllByOrderByCreatedAtDesc(any())).thenReturn(java.util.List.of(one, two));
        when(sessions.findRecentPublicFeedSessions(any(), any())).thenReturn(java.util.List.of());
        when(attendance.findPublicAttendanceFeed(any(), any())).thenReturn(java.util.List.of());

        var hiddenRow = new com.example.desporto24.model.social.HiddenFeedPost();
        hiddenRow.setViewerId(7L);
        hiddenRow.setPostId(55L);
        when(hidden.findByViewerIdAndPostIdIn(eq(7L), any())).thenReturn(java.util.List.of(hiddenRow));

        var results = tested.feed("viewer");
        assertEquals(java.util.List.of(56L),
                results.stream().map(FeedItemResponse::postId).toList());
        verify(hidden).findByViewerIdAndPostIdIn(eq(7L), eq(java.util.Set.of(55L, 56L)));
    }

    @Test
    void automaticActivityPostsCannotBeHiddenAsRegularPublications() {
        var hidden = mock(com.example.desporto24.repository.social.HiddenFeedPostRepository.class);
        ReflectionTestUtils.setField(service, "hiddenFeedPosts", hidden);
        SocialPost activity = samplePost();
        activity.setActivityType("SESSION_ATTENDED");
        when(posts.findById(55L)).thenReturn(Optional.of(activity));

        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class,
                    () -> service.hidePost(55L, "viewer")).getStatusCode());
        verifyNoInteractions(hidden);
    }

    @Test
    void onlyAuthorCanEditCaptionAndLocationWithoutChangingMedia() {
        SocialPost post = samplePost();
        post.setImageUrl("/media/photo.jpg");
        post.setLocationLabel("Lisboa");
        when(posts.findById(55L)).thenReturn(Optional.of(post));
        var request = new com.example.desporto24.model.dto.social.EditSocialPostRequest(" Novo texto ", " Benfica ");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.editPost(55L, "viewer", request));
        verify(posts, never()).saveAndFlush(any());

        Profile author = post.getAuthor();
        when(profiles.findUserByUsernameIgnoreCase("author")).thenReturn(Optional.of(author));
        var result = service.editPost(55L, "author", request);
        assertEquals("Novo texto", result.content());
        assertEquals("Benfica", result.location());
        assertEquals("/media/photo.jpg", result.imageUrl());
        verify(posts).saveAndFlush(post);
    }

    @Test
    void editingCannotEmptyTextOnlyPostsOrModifyAutomaticActivities() {
        SocialPost post = samplePost();
        when(posts.findById(55L)).thenReturn(Optional.of(post));
        when(profiles.findUserByUsernameIgnoreCase("author")).thenReturn(Optional.of(post.getAuthor()));
        var blank = new com.example.desporto24.model.dto.social.EditSocialPostRequest(" ", null);
        assertThrows(IllegalArgumentException.class, () -> service.editPost(55L, "author", blank));
        assertEquals("Treino de hoje", post.getContent());

        post.setActivityType("SESSION_ATTENDED");
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class,
                    () -> service.editPost(55L, "author",
                        new com.example.desporto24.model.dto.social.EditSocialPostRequest("Falso", null)))
                    .getStatusCode());
        verify(posts, never()).saveAndFlush(any());
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
