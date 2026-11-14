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
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final SocialFeedService feed = new SocialFeedService(
            posts, mock(ProfileFollowRepository.class), mock(SocialPostLikeRepository.class),
            mock(SocialPostCommentRepository.class), mock(SocialPostCommentLikeRepository.class),
            profiles, mock(SessionRepository.class), mock(SessionParticipantRepository.class),
            mock(UserBlockService.class));

    private void prepare() {
        ReflectionTestUtils.setField(feed, "mediaStorage", publicStorage);
        ReflectionTestUtils.setField(feed, "privateMedia", privateStorage);
        Profile owner = new Profile();
        owner.setId(42L);
        owner.setUsername("owner");
        when(profiles.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));
        SocialPost post = SocialPost.builder().id(17L).author(owner)
                .imageStorageKey("private-profile-media/posts/42/photo.jpg")
                .videoStorageKey("posts/42/public-video.mp4")
                .extraMedia(List.of(new PublicPostMedia("/api/profile-media/posts/17/extra-0",
                        "private-profile-media/posts/42/album.jpg", "image/jpeg")))
                .build();
        when(posts.findById(17L)).thenReturn(Optional.of(post));
    }

    @Test void rollbackDoesNotRemoveReferencedPostMedia() {
        prepare();
        TransactionSynchronizationManager.initSynchronization();
        try {
            feed.deletePost(17L,"owner");
            verify(posts).delete(any(SocialPost.class));
            verifyNoInteractions(privateStorage,publicStorage);
            var callbacks=TransactionSynchronizationManager.getSynchronizations();
            assertEquals(1,callbacks.size());
            for(var callback: callbacks)
                callback.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
            verifyNoInteractions(privateStorage,publicStorage);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test void commitDeletesPrivateAndPublicObjectsFromTheirOwnStorage() {
        prepare();
        TransactionSynchronizationManager.initSynchronization();
        try {
            feed.deletePost(17L,"owner");
            verifyNoInteractions(privateStorage,publicStorage);
            var callbacks=TransactionSynchronizationManager.getSynchronizations();
            assertEquals(1,callbacks.size());
            for(var callback:callbacks) callback.afterCommit();
            verify(privateStorage).delete("private-profile-media/posts/42/photo.jpg");
            verify(privateStorage).delete("private-profile-media/posts/42/album.jpg");
            verify(publicStorage).delete("posts/42/public-video.mp4");
            verifyNoMoreInteractions(privateStorage,publicStorage);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
