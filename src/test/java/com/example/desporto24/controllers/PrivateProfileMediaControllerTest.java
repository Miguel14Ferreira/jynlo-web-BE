package com.example.desporto24.controllers;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.SocialStory;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialStoryRepository;
import com.example.desporto24.service.ProfilePrivacyService;
import com.example.desporto24.storage.PrivateProfileMediaStorage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrivateProfileMediaControllerTest {
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final SocialPostRepository posts = mock(SocialPostRepository.class);
    private final SocialStoryRepository stories = mock(SocialStoryRepository.class);
    private final ProfilePrivacyService privacy = mock(ProfilePrivacyService.class);
    private final PrivateProfileMediaStorage storage = mock(PrivateProfileMediaStorage.class);
    private final PrivateProfileMediaController controller =
            new PrivateProfileMediaController(profiles,posts,stories,privacy,storage);
    private final UsernamePasswordAuthenticationToken viewer =
            new UsernamePasswordAuthenticationToken("viewer","n/a");

    private Profile author() {
        Profile p = new Profile();
        p.setId(1L); p.setUsername("owner"); p.setPrivateProfile(true);
        Profile v = new Profile();
        v.setId(2L); v.setUsername("viewer");
        when(profiles.findUserByUsernameIgnoreCase("viewer")).thenReturn(Optional.of(v));
        return p;
    }
    @Test void publicStorageKeysAreNeverServedByThePrivateRoute() {
        Profile owner = author();
        SocialPost post = SocialPost.builder().id(7L).author(owner)
                .imageStorageKey("posts/1/image.jpg").build();
        when(posts.findById(7L)).thenReturn(Optional.of(post));
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class,
                        ()->controller.post(7L,"image",viewer)).getStatusCode());
        verifyNoInteractions(storage);
    }
    @Test void everyDownloadChecksTheCurrentFollowPermission() {
        Profile owner = author();
        SocialPost post = SocialPost.builder().id(7L).author(owner)
                .imageStorageKey("private-profile-media/posts/1/picture.jpg").build();
        when(posts.findById(7L)).thenReturn(Optional.of(post));
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(privacy).requireContentAccess(any(),eq(owner));
        assertThrows(ResponseStatusException.class,()->controller.post(7L,"image",viewer));
        verifyNoInteractions(storage);
    }
    @Test void expiredStoriesCannotBeFetchedEvenByApprovedFollowers() {
        Profile owner = author();
        SocialStory story = SocialStory.builder().id(8L).author(owner)
                .expiresAt(Instant.now().minusSeconds(60))
                .mediaStorageKey("private-profile-media/stories/1/photo.jpg").build();
        when(stories.findById(8L)).thenReturn(Optional.of(story));
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class,
                        ()->controller.story(8L,viewer)).getStatusCode());
        verifyNoInteractions(storage);
    }
    @Test void cannotReadAKeyFromAnotherAuthorsPrivateFolder() {
        Profile owner=author();
        SocialPost post=SocialPost.builder().id(7L).author(owner)
                .imageStorageKey("private-profile-media/posts/999/other.jpg").build();
        when(posts.findById(7L)).thenReturn(Optional.of(post));
        assertEquals(HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class,
                        ()->controller.post(7L,"image",viewer)).getStatusCode());
        verifyNoInteractions(storage);
    }
    @Test void authorizedProtectedPhotosHaveNoStoreCachePolicy() {
        Profile owner = author();
        SocialPost post = SocialPost.builder().id(7L).author(owner)
                .imageStorageKey("private-profile-media/posts/1/picture.jpg").build();
        when(posts.findById(7L)).thenReturn(Optional.of(post));
        var response = controller.post(7L,"image",viewer);
        assertTrue(response.getHeaders().getCacheControl().contains("no-store"));
        assertEquals("image/jpeg",response.getHeaders().getContentType().toString());
        verify(privacy).requireContentAccess(any(),eq(owner));
    }
}
