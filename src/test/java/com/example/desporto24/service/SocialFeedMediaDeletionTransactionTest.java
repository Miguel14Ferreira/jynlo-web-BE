package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.PublicPostMedia;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.social.*;
import com.example.desporto24.storage.MediaStorage;
import com.example.desporto24.storage.PrivateProfileMediaStorage;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Storage deletion must never precede the successful database commit. */
class SocialFeedMediaDeletionTransactionTest {

    private final SocialPostRepository posts = mock(SocialPostRepository.class);
    private final MediaStorage publicStorage = mock(MediaStorage.class);
    private final PrivateProfileMediaStorage privateStorage = mock(PrivateProfileMediaStorage.class);
    private final ProfileMediaDeletionOutbox outbox = mock(ProfileMediaDeletionOutbox.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final SocialFeedService feed = new SocialFeedService(
            posts, mock(ProfileFollowRepository.class), mock(SocialPostLikeRepository.class),
            mock(SocialPostCommentRepository.class), mock(SocialPostCommentLikeRepository.class),
            profiles, mock(SessionRepository.class), mock(SessionParticipantRepository.class),
            mock(UserBlockService.class));

    private void prepare() {
        ReflectionTestUtils.setField(feed, "mediaStorage", publicStorage);
        ReflectionTestUtils.setField(feed, "privateMedia", privateStorage);
        ReflectionTestUtils.setField(feed, "mediaDeletionOutbox", outbox);
        Profile owner = new Profile();
        owner.setId(42L);
        owner.setUsername("owner");
        when(profiles.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));
        SocialPost post = SocialPost.builder().id(17L).author(owner)
                .imageStorageKey("private-profile-media/posts/42/photo.jpg")
                .videoStorageKey("posts/42/public-video.mp4")
                .extraMedia(new java.util.ArrayList<>(List.of(new PublicPostMedia("/api/profile-media/posts/17/extra-0",
                        "private-profile-media/posts/42/album.jpg", "image/jpeg"))))
                .build();
        when(posts.findById(17L)).thenReturn(Optional.of(post));
    }

    @Test void mediaReplacementEnqueuesOldKeysAndDoesNotDeleteNewOnCommit() {
        prepare();
        var image=new org.springframework.mock.web.MockMultipartFile(
                "files","new.jpg","image/jpeg",new byte[]{1,2});
        when(publicStorage.store(anyString(),same(image)))
                .thenReturn(java.net.URI.create("/media/posts/42/new.jpg"));
        TransactionSynchronizationManager.initSynchronization();
        try {
            feed.editPostWithMedia(17L,"owner","Alterado",null,List.of(image),true);
            verify(outbox).enqueue("private-profile-media/posts/42/photo.jpg");
            verify(outbox).enqueue("posts/42/public-video.mp4");
            verify(outbox).enqueue("private-profile-media/posts/42/album.jpg");
            verifyNoInteractions(privateStorage);
            for(var callback:TransactionSynchronizationManager.getSynchronizations())
                callback.afterCompletion(TransactionSynchronization.STATUS_COMMITTED);
            verify(publicStorage,never()).delete("posts/42/new.jpg");
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test void mediaReplacementRollbackCleansOnlyNewUpload() {
        prepare();
        var image=new org.springframework.mock.web.MockMultipartFile(
                "files","new.jpg","image/jpeg",new byte[]{1,2});
        when(publicStorage.store(anyString(),same(image)))
                .thenReturn(java.net.URI.create("/media/posts/42/new.jpg"));
        TransactionSynchronizationManager.initSynchronization();
        try {
            feed.editPostWithMedia(17L,"owner","Alterado",null,List.of(image),true);
            for(var callback:TransactionSynchronizationManager.getSynchronizations())
                callback.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
            verify(publicStorage).delete("posts/42/new.jpg");
            verify(privateStorage,never()).delete(anyString());
            verify(publicStorage,never()).delete("posts/42/public-video.mp4");
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test void postDeletionEnqueuesAllObjectsWithinTransaction() {
        prepare();
        TransactionSynchronizationManager.initSynchronization();
        try {
            feed.deletePost(17L,"owner");
            verify(posts).delete(any(SocialPost.class));
            verify(outbox).enqueue("private-profile-media/posts/42/photo.jpg");
            verify(outbox).enqueue("private-profile-media/posts/42/album.jpg");
            verify(outbox).enqueue("posts/42/public-video.mp4");
            verifyNoInteractions(privateStorage,publicStorage);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test void queueFailureAbortsPostDeletionInsteadOfLosingCleanup() {
        prepare();
        TransactionSynchronizationManager.initSynchronization();
        try {
            doThrow(new IllegalStateException("queue unavailable")).when(outbox)
                    .enqueue("private-profile-media/posts/42/photo.jpg");
            assertThrows(IllegalStateException.class,()->feed.deletePost(17L,"owner"));
            verifyNoInteractions(privateStorage,publicStorage);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
