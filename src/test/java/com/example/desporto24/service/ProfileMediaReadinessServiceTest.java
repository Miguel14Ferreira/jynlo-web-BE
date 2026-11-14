package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.PublicPostMedia;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.SocialStory;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialStoryRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileMediaReadinessServiceTest {
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final SocialPostRepository posts=mock(SocialPostRepository.class);
    private final SocialStoryRepository stories=mock(SocialStoryRepository.class);
    private final ProfileMediaReadinessService service=new ProfileMediaReadinessService(profiles,posts,stories);
    @Test void legacyMainAlbumAndExpiredStoryReferencesBlockCutover() {
        Profile owner=new Profile();
        owner.setId(15L);
        SocialPost post=SocialPost.builder().author(owner)
                .imageStorageKey("posts/15/old.jpg").imageUrl("https://public.example/posts/15/old.jpg")
                .extraMedia(List.of(
                  new PublicPostMedia("https://public.example/posts/15/second.png","posts/15/second.png","image/png")))
                .build();
        SocialStory expired=SocialStory.builder().author(owner)
                .mediaStorageKey("stories/15/old.webm").build();
        when(posts.findByAuthorId(15L)).thenReturn(List.of(post));
        when(stories.findByAuthorId(15L)).thenReturn(List.of(expired));
        var status=service.count(owner);
        assertEquals(3,status.publicReferences());
        assertEquals(0,status.protectedReferences());
        assertFalse(status.databaseReferencesReady());
    }
    @Test void onlyFullyMigratedReferencesPassDatabaseAudit() {
        Profile owner=new Profile();
        owner.setId(16L);
        when(posts.findByAuthorId(16L)).thenReturn(List.of(SocialPost.builder()
            .imageStorageKey("private-profile-media/posts/16/photo.jpg").build()));
        when(stories.findByAuthorId(16L)).thenReturn(List.of(SocialStory.builder()
            .mediaStorageKey("private-profile-media/stories/16/video.mp4").build()));
        var status=service.count(owner);
        assertEquals(0,status.publicReferences());
        assertEquals(2,status.protectedReferences());
        assertTrue(status.databaseReferencesReady());
    }
}
