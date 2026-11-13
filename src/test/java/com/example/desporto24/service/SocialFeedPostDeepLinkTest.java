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
