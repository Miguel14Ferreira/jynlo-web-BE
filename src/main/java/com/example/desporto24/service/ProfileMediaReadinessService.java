package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.SocialStory;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialStoryRepository;
import com.example.desporto24.storage.PrivateProfileMediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Data-side audit of the public-media cutover. It NEVER changes visibility,
 * copies content or deletes files. CDN cache invalidation remains a separate
 * deployment gate: a migrated DB URL alone does not revoke an old public URL.
 */
@Service
@RequiredArgsConstructor
public class ProfileMediaReadinessService {
    private final ProfileRepository profiles;
    private final SocialPostRepository posts;
    private final SocialStoryRepository stories;
    public record MediaReadiness(long publicReferences,long protectedReferences,
                                 boolean databaseReferencesReady) {}

    @Transactional(readOnly=true)
    public MediaReadiness mine(String username) {
        Profile profile = profiles.findUserByUsernameIgnoreCase(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile unavailable"));
        return count(profile);
    }

    private static boolean protectedReference(String key, String url, String expectedRoute) {
        // A copied object is not enough: the old public URL must also have been
        // replaced in the database before the owner may enable private mode.
        return PrivateProfileMediaStorage.isPrivateKey(key)
                && expectedRoute.equals(url);
    }

    @Transactional(readOnly=true)
    public MediaReadiness count(Profile profile) {
        long publicRefs=0,protectedRefs=0;
        for (SocialPost post : posts.findByAuthorId(profile.getId())) {
            if (post.getActivityType()!=null) continue;
            String[] keys={post.getImageStorageKey(),post.getVideoStorageKey()};
            String[] urls={post.getImageUrl(),post.getVideoUrl()};
            String[] routes={
                "/api/profile-media/posts/" + post.getId() + "/image",
                "/api/profile-media/posts/" + post.getId() + "/video"
            };
            for(int i=0;i<keys.length;i++) {
                if ((keys[i] == null || keys[i].isBlank()) && (urls[i] == null || urls[i].isBlank())) continue;
                if (protectedReference(keys[i], urls[i], routes[i])) protectedRefs++;
                else publicRefs++;
            }
            if (post.getExtraMedia()!=null) {
                for (int i=0;i<post.getExtraMedia().size();i++) {
                    var asset=post.getExtraMedia().get(i);
                    String expected="/api/profile-media/posts/" + post.getId() + "/extra-" + i;
                    if (protectedReference(asset.getStorageKey(), asset.getUrl(), expected)) protectedRefs++;
                    else publicRefs++;
                }
            }
        }
        // Include expired stories: their public objects may remain reachable.
        for (SocialStory story : stories.findByAuthorId(profile.getId())) {
            String route="/api/profile-media/stories/" + story.getId();
            if (protectedReference(story.getMediaStorageKey(),story.getMediaUrl(),route)) protectedRefs++;
            else publicRefs++;
        }
        return new MediaReadiness(publicRefs,protectedRefs,publicRefs==0);
    }
}
