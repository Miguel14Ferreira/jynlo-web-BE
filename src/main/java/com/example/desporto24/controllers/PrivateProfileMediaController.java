package com.example.desporto24.controllers;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.SocialStory;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialStoryRepository;
import com.example.desporto24.service.ProfilePrivacyService;
import com.example.desporto24.storage.PrivateProfileMediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.InputStream;
import java.time.Instant;

/**
 * Authenticated media delivery (no public redirects or public storage URLs).
 * Each request rechecks the user's current follow permission and blocks.
 * The existing public media is intentionally NOT served through this endpoint.
 */
@RestController
@RequestMapping("/api/profile-media")
@RequiredArgsConstructor
public class PrivateProfileMediaController {
    private final ProfileRepository profiles;
    private final SocialPostRepository posts;
    private final SocialStoryRepository stories;
    private final ProfilePrivacyService privacy;
    private final PrivateProfileMediaStorage storage;

    private Profile viewer(Authentication authentication) {
        if (authentication == null) throw unavailable();
        return profiles.findUserByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(PrivateProfileMediaController::unavailable);
    }
    private static ResponseStatusException unavailable() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Media unavailable");
    }
    private static void requireKey(String key, String type, Long authorId) {
        if (!PrivateProfileMediaStorage.isPrivateKey(key) || authorId == null
                || !key.startsWith("private-profile-media/" + type + "/" + authorId + "/"))
            throw unavailable();
    }
    private static MediaType type(String key) {
        if (key.endsWith(".jpg") || key.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (key.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (key.endsWith(".webp")) return MediaType.parseMediaType("image/webp");
        if (key.endsWith(".mp4")) return MediaType.parseMediaType("video/mp4");
        if (key.endsWith(".webm")) return MediaType.parseMediaType("video/webm");
        throw unavailable();
    }

    @GetMapping("/posts/{id}/{slot}")
    public ResponseEntity<StreamingResponseBody> post(@PathVariable long id,
            @PathVariable String slot, Authentication authentication) {
        Profile actor = viewer(authentication);
        SocialPost post = posts.findById(id).orElseThrow(PrivateProfileMediaController::unavailable);
        if (post.getActivityType() != null) throw unavailable();
        privacy.requireContentAccess(actor, post.getAuthor());
        String key;
        if ("image".equals(slot)) key = post.getImageStorageKey();
        else if ("video".equals(slot)) key = post.getVideoStorageKey();
        else if (slot.matches("extra-[0-5]")) {
            int index = slot.charAt(slot.length()-1)-'0';
            if (post.getExtraMedia() == null || index >= post.getExtraMedia().size()) throw unavailable();
            key = post.getExtraMedia().get(index).getStorageKey();
        } else throw unavailable();
        requireKey(key, "posts", post.getAuthor().getId());
        return deliver(key);
    }

    @GetMapping("/stories/{id}")
    public ResponseEntity<StreamingResponseBody> story(@PathVariable long id,
            Authentication authentication) {
        Profile actor = viewer(authentication);
        SocialStory story = stories.findById(id).orElseThrow(PrivateProfileMediaController::unavailable);
        // Expired stories and private communities are never accessible by this personal route.
        if (story.getCommunity() != null || !story.getExpiresAt().isAfter(Instant.now()))
            throw unavailable();
        privacy.requireContentAccess(actor, story.getAuthor());
        String key = story.getMediaStorageKey();
        requireKey(key, "stories", story.getAuthor().getId());
        return deliver(key);
    }

    private ResponseEntity<StreamingResponseBody> deliver(String key) {
        StreamingResponseBody body = output -> {
            try (InputStream in = storage.open(key)) {
                in.transferTo(output);
            }
        };
        return ResponseEntity.ok()
                .contentType(type(key))
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header("X-Content-Type-Options", "nosniff")
                .body(body);
    }
}
