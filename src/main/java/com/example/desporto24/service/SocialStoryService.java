package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.CommunityVisibility;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.model.dto.social.StoryResponse;
import com.example.desporto24.model.social.SocialStory;
import com.example.desporto24.model.social.SocialStoryLike;
import com.example.desporto24.model.social.SocialStoryReply;
import com.example.desporto24.repository.social.SocialStoryReplyRepository;
import com.example.desporto24.repository.social.SocialStoryLikeRepository;
import com.example.desporto24.enumeration.NotificationType;
import org.springframework.context.ApplicationEventPublisher;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.repository.social.ProfileFollowRepository;
import com.example.desporto24.repository.social.SocialStoryRepository;
import com.example.desporto24.storage.MediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialStoryService {
    private static final int FEED_LIMIT = 100;
    private final SocialStoryRepository stories;
    private final SocialStoryLikeRepository likes;
    private final SocialStoryReplyRepository replies;
    private final ApplicationEventPublisher events;
    private final UserBlockService userBlockService;
    private final ProfileRepository profiles;
    private final CommunityRepository communities;
    private final CommunityMemberRepository memberships;
    private final ProfileFollowRepository follows;
    private final MediaStorage storage;

    @Transactional(readOnly = true)
    public List<StoryResponse> feed(String username) {
        Profile actor = actor(username);
        Set<Long> authors = new LinkedHashSet<>();
        authors.add(actor.getId());
        follows.findByFollowerIdOrderByCreatedAtDesc(actor.getId())
                .forEach(follow -> authors.add(follow.getFollowing().getId()));

        Set<Long> groups = new LinkedHashSet<>();
        memberships.findByProfileIdOrderByJoinedAtDesc(actor.getId())
                .forEach(member -> {
                    if (member.getCommunity().getVisibility() == CommunityVisibility.PUBLIC) {
                        groups.add(member.getCommunity().getId());
                    }
                });
        communities.findByOwnerId(actor.getId()).stream()
                .filter(group -> group.getVisibility() == CommunityVisibility.PUBLIC)
                .forEach(group -> groups.add(group.getId()));
        // Avoid passing an empty collection into JPQL IN on different databases.
        if (groups.isEmpty()) groups.add(-1L);
        return responses(stories.findActiveFeed(Instant.now(), authors, groups, PageRequest.of(0, FEED_LIMIT)), actor);
    }

    @Transactional(readOnly = true)
    public List<StoryResponse> mine(String username) {
        Profile actor = actor(username);
        return responses(stories.findTop20ByAuthorIdAndExpiresAtAfterOrderByCreatedAtDesc(actor.getId(), Instant.now()), actor);
    }

    @Transactional(readOnly = true)
    public StoryResponse view(Long id, String username) {
        Profile actor = actor(username);
        SocialStory story = visibleStory(id, actor);
        return response(story, actor);
    }

    @Transactional
    public StoryResponse create(String username, Long communityId, String caption,
                                MultipartFile image, MultipartFile video) {
        Profile actor = actor(username);
        boolean hasImage = image != null && !image.isEmpty();
        boolean hasVideo = video != null && !video.isEmpty();
        if (hasImage == hasVideo) {
            throw new IllegalArgumentException("Choose exactly one photo or video");
        }
        String text = caption == null ? "" : caption.trim();
        if (text.length() > 180) {
            throw new IllegalArgumentException("Story text must not exceed 180 characters");
        }
        Community group = null;
        if (communityId != null) {
            group = communities.findById(communityId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
            if (!canManage(group, actor)) {
                throw new AccessDeniedException("Only community admins can publish group stories");
            }
            // Media storage serves public URLs; do not leak PRIVATE community stories.
            // Private group stories require an authenticated media delivery mechanism.
            if (group.getVisibility() != CommunityVisibility.PUBLIC) {
                throw new AccessDeniedException("Private community stories are not available yet");
            }
        }
        String key = "stories/" + actor.getId() + "/" + UUID.randomUUID();
        URI uri = hasVideo ? storage.storeVideo(key, video) : storage.store(key, image);
        String objectKey = storageKey(uri);
        Instant now = Instant.now();
        try {
            SocialStory story = stories.save(SocialStory.builder()
                    .author(actor).community(group).caption(text)
                    .mediaUrl(uri.toString()).mediaStorageKey(objectKey)
                    .video(hasVideo).createdAt(now).expiresAt(now.plus(24, ChronoUnit.HOURS))
                    .build());
            return response(story, actor);
        } catch (RuntimeException ex) {
            storage.delete(objectKey);
            throw ex;
        }
    }

    @Transactional
    public StoryResponse like(Long id, String username) {
        Profile viewer = actor(username);
        SocialStory story = visibleStory(id, viewer);
        userBlockService.ensureInteractionAllowed(viewer, story.getAuthor());
        if (!likes.existsByStoryIdAndProfileId(id, viewer.getId())) {
            likes.save(SocialStoryLike.builder().story(story).profile(viewer)
                    .createdAt(Instant.now()).build());
            notifyAuthor(story, viewer, NotificationType.SOCIAL_STORY_LIKED,
                    viewer.getUsername() + " gostou da tua Story.");
        }
        return response(story, viewer);
    }

    @Transactional
    public StoryResponse unlike(Long id, String username) {
        Profile viewer = actor(username);
        SocialStory story = visibleStory(id, viewer);
        likes.deleteByStoryIdAndProfileId(id, viewer.getId());
        return response(story, viewer);
    }

    /**
     * A story reply is private: notify the author only. Never publish it as
     * a public comment or attach it to the story response/feed.
     */
    @Transactional
    public void reply(Long id, String username, String content) {
        Profile viewer = actor(username);
        SocialStory story = visibleStory(id, viewer);
        if (content == null || content.trim().isEmpty() || content.trim().length() > 500) {
            throw new IllegalArgumentException("A resposta deve ter entre 1 e 500 caracteres.");
        }
        if (viewer.getId().equals(story.getAuthor().getId())) {
            throw new IllegalArgumentException("Não podes responder à tua própria Story.");
        }
        userBlockService.ensureInteractionAllowed(viewer, story.getAuthor());
        // The private reply is durable even if notifications are disabled.
        replies.save(SocialStoryReply.builder().storyId(story.getId()).sender(viewer)
                .recipient(story.getAuthor()).content(content.trim())
                .createdAt(Instant.now()).build());
        notifyAuthor(story, viewer, NotificationType.SOCIAL_STORY_REPLIED,
                viewer.getUsername() + " respondeu à tua Story: " + content.trim());
    }

    public record PrivateReply(Long id, Long storyId, String senderUsername,
                               String content, Instant createdAt) {}

    @Transactional(readOnly = true)
    public List<PrivateReply> myPrivateReplies(String username, int page) {
        if (page < 0 || page > 1000) throw new IllegalArgumentException("Página inválida");
        Profile recipient = actor(username);
        return replies.findByRecipientIdOrderByCreatedAtDesc(recipient.getId(), PageRequest.of(page, 20))
                .stream().map(reply -> new PrivateReply(reply.getId(), reply.getStoryId(),
                        reply.getSender().getUsername(), reply.getContent(),
                        reply.getCreatedAt())).toList();
    }

    private void notifyAuthor(SocialStory story, Profile viewer, NotificationType type, String message) {
        // The event is handled only AFTER a successful DB commit, in a separate
        // transaction; delivery failures cannot roll back a Story reply or like.
        events.publishEvent(new SocialStoryNotificationDispatcher.StoryNotice(
                story.getId(), story.getAuthor().getUsername(),
                viewer.getUsername(), type, message));
    }

    private SocialStory visibleStory(Long id, Profile viewer) {
        SocialStory story = activeStory(id);
        // Deep links must not expose Stories from profiles blocked in either direction.
        if (userBlockService.isBlockedBetween(viewer, story.getAuthor())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Story not found");
        }
        if (story.getCommunity() != null &&
                story.getCommunity().getVisibility() != CommunityVisibility.PUBLIC &&
                !isMemberOrOwner(story.getCommunity(), viewer)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Story not found");
        }
        return story;
    }

    @Transactional
    public void delete(Long id, String username) {
        Profile actor = actor(username);
        SocialStory story = activeStory(id);
        if (!story.getAuthor().getId().equals(actor.getId())
                && (story.getCommunity() == null || !canManage(story.getCommunity(), actor))) {
            throw new AccessDeniedException("Cannot delete another person's story");
        }
        stories.delete(story);
        stories.flush();
        storage.delete(story.getMediaStorageKey());
    }

    // Expiration is enforced on every read, even if scheduled cleanup is delayed.
    @Scheduled(cron = "0 17 * * * *")
    @Transactional
    public void removeExpired() {
        List<SocialStory> old = stories.findByExpiresAtLessThanEqualOrderByIdAsc(
                Instant.now(), PageRequest.of(0, 200));
        for (SocialStory story : old) {
            // Never remove the DB reference when media deletion fails: allow retry next hour.
            try {
                storage.delete(story.getMediaStorageKey());
                stories.delete(story);
            } catch (RuntimeException ex) {
                org.slf4j.LoggerFactory.getLogger(SocialStoryService.class)
                        .warn("Could not purge expired story {}", story.getId(), ex);
            }
        }
    }

    private SocialStory activeStory(Long id) {
        SocialStory story = stories.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Story not found"));
        if (!story.getExpiresAt().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Story expired");
        }
        return story;
    }

    private boolean isMemberOrOwner(Community group, Profile actor) {
        return group.getOwner().getId().equals(actor.getId())
                || memberships.existsByCommunityIdAndProfileId(group.getId(), actor.getId());
    }

    private boolean canManage(Community group, Profile actor) {
        if (group.getOwner().getId().equals(actor.getId())) return true;
        return memberships.findByCommunityIdAndProfileId(group.getId(), actor.getId())
                .map(CommunityMember::getRole).filter(role -> role == CommunityRole.ADMIN || role == CommunityRole.OWNER)
                .isPresent();
    }

    private List<StoryResponse> responses(List<SocialStory> page, Profile actor) {
        if (page.isEmpty()) return List.of();
        Set<Long> ids = page.stream().map(SocialStory::getId).collect(Collectors.toSet());
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : likes.countByStoryIds(ids)) {
            counts.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }
        Set<Long> liked = likes.likedStoryIds(ids, actor.getId());
        return page.stream().map(story -> response(story, actor,
                counts.getOrDefault(story.getId(), 0L), liked.contains(story.getId()))).toList();
    }

    private StoryResponse response(SocialStory story, Profile actor) {
        return response(story, actor, likes.countByStoryId(story.getId()),
                likes.existsByStoryIdAndProfileId(story.getId(), actor.getId()));
    }

    private StoryResponse response(SocialStory story, Profile actor, long likeCount, boolean likedByMe) {
        Profile author = story.getAuthor();
        Community group = story.getCommunity();
        String name = author.getNickname() != null && !author.getNickname().isBlank()
                ? author.getNickname()
                : author.getFullName() != null && !author.getFullName().isBlank()
                ? author.getFullName() : author.getUsername();
        return new StoryResponse(story.getId(), author.getUsername(), name,
                author.getProfilePicUrl(), group == null ? null : group.getId(),
                group == null ? null : group.getName(),
                group == null ? null : group.getPhotoUrl(),
                story.getCaption(), story.getMediaUrl(), story.isVideo(),
                story.getCreatedAt(), story.getExpiresAt(),
                author.getId().equals(actor.getId()) || (group != null && canManage(group, actor)),
                likeCount, likedByMe);
    }

    private Profile actor(String username) {
        return profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required"));
    }

    private String storageKey(URI uri) {
        String path = uri.getPath();
        int pos = path == null ? -1 : path.indexOf("stories/");
        if (pos < 0) throw new IllegalStateException("Unexpected media storage path");
        return path.substring(pos);
    }
}
