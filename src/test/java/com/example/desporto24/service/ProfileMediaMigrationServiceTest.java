package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.PublicPostMedia;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.SocialStory;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialStoryRepository;
import com.example.desporto24.storage.MediaStorage;
import com.example.desporto24.storage.PrivateProfileMediaStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProfileMediaMigrationServiceTest {
    private final SocialPostRepository posts = mock(SocialPostRepository.class);
    private final SocialStoryRepository stories = mock(SocialStoryRepository.class);
    private final MediaStorage publicStorage = mock(MediaStorage.class);
    private final PrivateProfileMediaStorage privateStorage = mock(PrivateProfileMediaStorage.class);
    private final ProfileMediaDeletionOutbox outbox = mock(ProfileMediaDeletionOutbox.class);
    private final PlatformTransactionManager tx = mock(PlatformTransactionManager.class);
    private final Map<String, byte[]> privateObjects = new HashMap<>();
    private final Map<String, byte[]> publicObjects = new HashMap<>();
    private ProfileMediaMigrationService service;
    private Profile owner;

    @BeforeEach
    void setup() {
        when(tx.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        service = new ProfileMediaMigrationService(posts, stories, publicStorage, privateStorage, outbox, tx);
        owner = new Profile(); owner.setId(42L); owner.setUsername("ana");
        when(privateStorage.isConfigured()).thenReturn(true);
        publicObjects.put("posts/42/a.jpg", new byte[]{1, 2, 3});
        publicObjects.put("posts/42/b.mp4", new byte[]{4, 5});
        publicObjects.put("stories/42/s.jpg", new byte[]{6});
        when(publicStorage.open(anyString())).thenAnswer(inv -> new ByteArrayInputStream(publicObjects.get(inv.<String>getArgument(0))));
        doAnswer(inv -> { privateObjects.put(inv.getArgument(0), inv.getArgument(1)); return null; })
                .when(privateStorage).put(anyString(), any(byte[].class), anyString());
        when(privateStorage.open(anyString())).thenAnswer(inv -> new ByteArrayInputStream(privateObjects.get(inv.<String>getArgument(0))));
    }

    private SocialPost post() {
        SocialPost post = new SocialPost();
        post.setId(7L);
        post.setImageStorageKey("posts/42/a.jpg");
        post.setImageUrl("https://media.example/posts/42/a.jpg");
        post.setExtraMedia(new ArrayList<>(List.of(new PublicPostMedia("https://media.example/posts/42/b.mp4", "posts/42/b.mp4", "video/mp4"))));
        return post;
    }

    @Test
    void goingPrivateCopiesVerifiesThenSwitchesEveryReferenceAndQueuesTheOldFiles() {
        SocialPost post = post();
        SocialStory story = new SocialStory(); story.setId(9L);
        story.setMediaStorageKey("stories/42/s.jpg"); story.setMediaUrl("https://media.example/stories/42/s.jpg");
        SocialStory community = new SocialStory(); community.setId(10L);
        community.setCommunity(new com.example.desporto24.model.community.Community());
        community.setMediaStorageKey("stories/42/c.jpg"); community.setMediaUrl("https://media.example/stories/42/c.jpg");
        when(posts.findByAuthorId(42L)).thenReturn(List.of(post));
        when(stories.findByAuthorId(42L)).thenReturn(List.of(story, community));

        assertEquals(3, service.makePrivate(owner).moved());

        assertEquals("/api/profile-media/posts/7/image", post.getImageUrl());
        assertTrue(post.getImageStorageKey().startsWith("private-profile-media/posts/42/"));
        assertEquals("/api/profile-media/posts/7/extra-0", post.getExtraMedia().get(0).getUrl());
        assertEquals("video/mp4", post.getExtraMedia().get(0).getMimeType());
        assertEquals("/api/profile-media/stories/9", story.getMediaUrl());
        assertEquals("https://media.example/stories/42/c.jpg", community.getMediaUrl(), "community Stories never move");
        verify(outbox).enqueueAll(List.of("posts/42/a.jpg", "posts/42/b.mp4", "stories/42/s.jpg"));
        verify(posts).saveAll(List.of(post));
        verify(tx).commit(any());
    }

    @Test
    void aCorruptedCopyChangesNothingAndRemovesTheCopies() {
        SocialPost post = post();
        when(posts.findByAuthorId(42L)).thenReturn(List.of(post));
        when(stories.findByAuthorId(42L)).thenReturn(List.of());
        doReturn(new ByteArrayInputStream(new byte[]{9})).when(privateStorage).open(anyString());

        assertThrows(ResponseStatusException.class, () -> service.makePrivate(owner));

        assertEquals("https://media.example/posts/42/a.jpg", post.getImageUrl());
        verify(posts, never()).saveAll(any());
        verifyNoInteractions(outbox);
        verify(privateStorage, atLeastOnce()).delete(anyString());
    }

    @Test
    void goingPublicMovesProtectedMediaBack() {
        privateObjects.put("private-profile-media/posts/42/x.jpg", new byte[]{1, 1});
        SocialPost post = new SocialPost(); post.setId(7L);
        post.setImageStorageKey("private-profile-media/posts/42/x.jpg");
        post.setImageUrl("/api/profile-media/posts/7/image");
        when(posts.findByAuthorId(42L)).thenReturn(List.of(post));
        when(stories.findByAuthorId(42L)).thenReturn(List.of());
        when(publicStorage.putValidated(anyString(), any(byte[].class), anyString())).thenAnswer(inv -> {
            publicObjects.put(inv.getArgument(0), inv.getArgument(1));
            return URI.create("https://media.example/" + inv.getArgument(0));
        });

        assertEquals(1, service.makePublic(owner).moved());

        assertTrue(post.getImageStorageKey().matches("posts/42/[A-Za-z0-9_-]+\\.jpg"));
        assertEquals("https://media.example/" + post.getImageStorageKey(), post.getImageUrl());
        verify(outbox).enqueueAll(List.of("private-profile-media/posts/42/x.jpg"));
    }

    @Test
    void nothingToMoveIsANoOp() {
        when(posts.findByAuthorId(42L)).thenReturn(List.of());
        when(stories.findByAuthorId(42L)).thenReturn(List.of());
        assertEquals(0, service.makePrivate(owner).moved());
        verifyNoInteractions(outbox);
    }
}
