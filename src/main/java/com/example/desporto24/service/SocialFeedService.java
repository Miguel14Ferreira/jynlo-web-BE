package com.example.desporto24.service;

import com.example.desporto24.enumeration.AttendanceStatus;
import com.example.desporto24.enumeration.SessionAudience;
import com.example.desporto24.exception.UserNotFoundException;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.dto.social.CreateSocialPostRequest;
import com.example.desporto24.model.dto.social.FeedItemResponse;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.social.ProfileFollowRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialPostLikeRepository;
import com.example.desporto24.repository.social.SocialPostCommentRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.storage.MediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.UUID;

import static com.example.desporto24.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialFeedService {

    private static final int FEED_LIMIT = 40;

    private final SocialPostRepository postRepository;
    private final ProfileFollowRepository followRepository;
    private final SocialPostLikeRepository likeRepository;
    private final SocialPostCommentRepository commentRepository;
    private final ProfileRepository profileRepository;
    private final SessionRepository sessionRepository;
    private final SessionParticipantRepository participantRepository;
    private final UserBlockService userBlockService;

    @Autowired(required = false)
    private MediaStorage mediaStorage;

    public FeedItemResponse createPost(
            String username,
            CreateSocialPostRequest request) {
        Profile author = profile(username);
        Session linkedSession = resolvePublicSession(request.sessionId());

        SocialPost post = postRepository.save(SocialPost.builder()
                .author(author)
                .session(linkedSession)
                .content(request.content().trim())
                .createdAt(Instant.now())
                .build());

        return postItem(post, author);
    }

    public FeedItemResponse createPostWithImage(
            String username,
            String content,
            MultipartFile image) {
        return createPostWithImage(username, content, image, null);
    }

    public FeedItemResponse createPostWithImage(
            String username,
            String content,
            MultipartFile image,
            Long sessionId) {
        String normalizedContent = content == null ? "" : content.trim();

        if (normalizedContent.length() > 1000) {
            throw new IllegalArgumentException("A publicação não pode ter mais de 1000 caracteres");
        }
        if (image == null || image.isEmpty()) {
            if (normalizedContent.isBlank())
                throw new IllegalArgumentException("A publicação não pode estar vazia");
            return createPost(
                    username,
                    new CreateSocialPostRequest(normalizedContent, sessionId)
            );
        }
        if (mediaStorage == null) {
            throw new IllegalStateException("Media storage is not configured");
        }

        Profile author = profile(username);
        Session linkedSession = resolvePublicSession(sessionId);
        String baseKey = "posts/" + author.getId() + "/" + UUID.randomUUID();
        URI uri = mediaStorage.store(baseKey, image);
        String storageKey = storageKey(uri);

        SocialPost post = postRepository.save(SocialPost.builder()
                .author(author)
                .session(linkedSession)
                .content(normalizedContent)
                .imageUrl(uri.toString())
                .imageStorageKey(storageKey)
                .createdAt(Instant.now())
                .build());

        return postItem(post, author);
    }

    public FeedItemResponse createPostWithMedia(
            String username, String content, MultipartFile image,
            MultipartFile video, Long sessionId) {
        if (image != null && !image.isEmpty() && video != null && !video.isEmpty()) {
            throw new IllegalArgumentException("Only one photo or video per post");
        }
        if (video == null || video.isEmpty()) {
            return createPostWithImage(username, content, image, sessionId);
        }
        String normalized = content == null ? "" : content.trim();
        if (normalized.length() > 1000) {
            throw new IllegalArgumentException("A publicação não pode ter mais de 1000 caracteres");
        }
        if (mediaStorage == null) {
            throw new IllegalStateException("Media storage is not configured");
        }
        Profile author = profile(username);
        Session linkedSession = resolvePublicSession(sessionId);
        String baseKey = "posts/" + author.getId() + "/" + UUID.randomUUID();
        URI uri = mediaStorage.storeVideo(baseKey, video);
        SocialPost post = postRepository.save(SocialPost.builder()
                .author(author)
                .session(linkedSession)
                .content(normalized)
                .videoUrl(uri.toString())
                .videoStorageKey(storageKey(uri))
                .createdAt(Instant.now())
                .build());
        return postItem(post, author);
    }

    /** One publication with an ordered gallery; storage is cleaned on upload or DB failure. */
    public FeedItemResponse createPostWithAlbum(String username, String content,
            java.util.List<MultipartFile> files, Long sessionId) {
        if (files == null || files.isEmpty() || files.size() > 6) {
            throw new IllegalArgumentException("Escolhe entre 1 e 6 fotografias ou vídeos");
        }
        long total = files.stream().mapToLong(file -> file == null ? 0 : file.getSize()).sum();
        if (total > 60L * 1024 * 1024 || files.stream().anyMatch(file ->
                file == null || file.isEmpty() || file.getSize() > 20L * 1024 * 1024)) {
            throw new IllegalArgumentException("Os ficheiros não podem exceder 60 MB no total");
        }
        String normalized = content == null ? "" : content.trim();
        if (normalized.length() > 1000) {
            throw new IllegalArgumentException("A publicação não pode ter mais de 1000 caracteres");
        }
        if (mediaStorage == null) throw new IllegalStateException("Media storage is not configured");
        Profile author = profile(username);
        Session linkedSession = resolvePublicSession(sessionId);
        java.util.List<com.example.desporto24.model.social.PublicPostMedia> stored = new java.util.ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String mime = file.getContentType() == null ? "" : file.getContentType().toLowerCase(java.util.Locale.ROOT);
                String base = "posts/" + author.getId() + "/" + UUID.randomUUID();
                URI uri = switch (mime) {
                    case "image/jpeg", "image/png", "image/webp" -> mediaStorage.store(base, file);
                    case "video/mp4", "video/webm" -> mediaStorage.storeVideo(base, file);
                    default -> throw new IllegalArgumentException("Formato de media não suportado");
                };
                stored.add(new com.example.desporto24.model.social.PublicPostMedia(
                        uri.toString(), storageKey(uri), mime));
            }
            var first = stored.get(0);
            SocialPost post = postRepository.save(SocialPost.builder()
                    .author(author)
                    .session(linkedSession)
                    .content(normalized)
                    .imageUrl(first.getMimeType().startsWith("image/") ? first.getUrl() : null)
                    .imageStorageKey(first.getMimeType().startsWith("image/") ? first.getStorageKey() : null)
                    .videoUrl(first.getMimeType().startsWith("video/") ? first.getUrl() : null)
                    .videoStorageKey(first.getMimeType().startsWith("video/") ? first.getStorageKey() : null)
                    .extraMedia(new java.util.ArrayList<>(stored.subList(1, stored.size())))
                    .createdAt(Instant.now())
                    .build());
            return postItem(post, author);
        } catch (RuntimeException failure) {
            for (var asset : stored) {
                try { if (asset.getStorageKey() != null) mediaStorage.delete(asset.getStorageKey()); }
                catch (RuntimeException ignored) { failure.addSuppressed(ignored); }
            }
            throw failure;
        }
    }

    public void deletePost(Long postId, String username) {
        Profile actor = profile(username);
        SocialPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Publicação não encontrada"));

        if (post.getActivityType() != null) {
            throw new AccessDeniedException("As atividades automáticas não podem ser apagadas como publicações");
        }
        if (!post.getAuthor().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Só podes apagar as tuas publicações");
        }

        likeRepository.deleteByPostId(postId);
        commentRepository.deleteByPostId(postId);
        postRepository.delete(post);
        postRepository.flush();

        if (
                mediaStorage != null
                && post.getImageStorageKey() != null
                && !post.getImageStorageKey().isBlank()
        ) {
            mediaStorage.delete(post.getImageStorageKey());
        }
        if (mediaStorage != null && post.getVideoStorageKey() != null
                && !post.getVideoStorageKey().isBlank()) {
            mediaStorage.delete(post.getVideoStorageKey());
        }
        if (mediaStorage != null && post.getExtraMedia() != null) {
            for (var asset : post.getExtraMedia()) {
                if (asset.getStorageKey() != null) mediaStorage.delete(asset.getStorageKey());
            }
        }
    }

    public void hideAttendanceActivity(Long sessionId, String username) {
        Profile actor = profile(username);
        SessionParticipant participant = participantRepository
                .findBySessionIdAndProfileId(sessionId, actor.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Atividade não encontrada"));

        if (participant.getAttendanceStatus() != AttendanceStatus.ATTENDED
                || participant.getAttendanceRecordedAt() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Atividade não encontrada");
        }

        if (!participant.isFeedHidden()) {
            participant.setFeedHidden(true);
            participantRepository.save(participant);
        }
    }

    @Transactional(readOnly = true)
    public FeedItemResponse postById(Long postId, String username) {
        Profile viewer = profile(username);
        if (postId == null || postId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada");
        }
        SocialPost target = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada"));
        // Activity interaction records can contain hidden attendance; they are
        // never individually exposed through a social notification deep link.
        if (target.getActivityType() != null
                || userBlockService.isBlockedBetween(viewer, target.getAuthor())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação indisponível");
        }
        long likes = likeRepository.countByPostId(postId);
        long comments = commentRepository.countByPostId(postId);
        boolean liked = likeRepository.existsByPostIdAndProfileId(postId, viewer.getId());
        return postItem(target, Map.of(postId, likes), Map.of(postId, comments),
                liked ? Set.of(postId) : Set.of());
    }

    /** Permanent authored publications, independent from expiring Stories and automatic activities. */
    @Transactional(readOnly = true)
    public List<FeedItemResponse> profilePosts(String authorUsername, String viewerUsername, int page) {
        if (page < 0 || page > 1000) throw new IllegalArgumentException("Página inválida");
        Profile author = profile(authorUsername);
        Profile viewer = profile(viewerUsername);
        // A direct profile-post URL must honour blocks in either direction,
        // just as story deep links do.
        if (userBlockService.isBlockedBetween(viewer, author)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicações não encontradas");
        }
        List<SocialPost> posts = postRepository.findByAuthorIdAndActivityTypeIsNullOrderByCreatedAtDesc(
                author.getId(), PageRequest.of(page, 20));
        if (posts.isEmpty()) return List.of();
        Set<Long> ids = posts.stream().map(SocialPost::getId).collect(Collectors.toSet());
        Map<Long, Long> likeCounts = countsByPostId(likeRepository.countByPostIds(ids));
        Map<Long, Long> commentCounts = countsByPostId(commentRepository.countByPostIds(ids));
        Set<Long> likedPostIds = likeRepository.findLikedPostIds(ids, viewer.getId());
        return posts.stream().map(post ->
                postItem(post, likeCounts, commentCounts, likedPostIds)).toList();
    }

    @Transactional(readOnly = true)
    public List<FeedItemResponse> feed(String username) {
        Profile actor = profile(username);

        Set<Long> authorIds = new LinkedHashSet<>();
        authorIds.add(actor.getId());
        followRepository.findByFollowerIdOrderByCreatedAtDesc(actor.getId())
                .stream()
                .map(follow -> follow.getFollowing().getId())
                .forEach(authorIds::add);

        boolean discoveryFallback = authorIds.size() == 1;

        List<SocialPost> posts = discoveryFallback
                ? postRepository.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(0, FEED_LIMIT))
                : postRepository.findFeedPosts(
                        authorIds,
                        PageRequest.of(0, FEED_LIMIT));

        // Filter expired games in SQL before pagination, without deleting feed history.
        Date now = Date.from(Instant.now());
        List<Session> sessions = discoveryFallback
                ? sessionRepository.findRecentPublicFeedSessions(
                        now, PageRequest.of(0, FEED_LIMIT))
                : sessionRepository.findPublicFeedSessions(
                        authorIds, now, PageRequest.of(0, FEED_LIMIT));

        List<SessionParticipant> attendedSessions =
                participantRepository.findPublicAttendanceFeed(
                        authorIds,
                        PageRequest.of(0, FEED_LIMIT)
                );

        // Blocking must apply across posts, created sessions and attendance entries,
        // including discovery fallback. Cache the decision once per author.
        Map<Long, Boolean> visibleAuthorIds = new HashMap<>();
        Predicate<Profile> visibleAuthor = author -> author != null
                && visibleAuthorIds.computeIfAbsent(author.getId(),
                        id -> !userBlockService.isBlockedBetween(actor, author));
        posts = posts.stream().filter(post -> visibleAuthor.test(post.getAuthor())).toList();
        sessions = sessions.stream().filter(session -> visibleAuthor.test(session.getProfile())).toList();
        attendedSessions = attendedSessions.stream()
                .filter(participant -> visibleAuthor.test(participant.getProfile())).toList();

        // Interaction backing posts never appear as separate feed entries.
        Set<Long> attendanceSessionIds = attendedSessions.stream()
                .map(participant -> participant.getSession().getId())
                .collect(Collectors.toSet());
        Map<String, SocialPost> activityPosts = attendanceSessionIds.isEmpty()
                ? Map.of()
                : postRepository.findByActivityTypeAndSessionIdIn(
                        "SESSION_ATTENDED", attendanceSessionIds).stream()
                  .collect(Collectors.toMap(
                        post -> post.getAuthor().getId() + ":" + post.getSession().getId(),
                        Function.identity()));

        Set<Long> postIds = posts.stream()
                .map(SocialPost::getId)
                .collect(Collectors.toSet());
        activityPosts.values().stream().map(SocialPost::getId).forEach(postIds::add);
        Map<Long, Long> likeCounts = countsByPostId(
                postIds.isEmpty() ? List.of() : likeRepository.countByPostIds(postIds));
        Map<Long, Long> commentCounts = countsByPostId(
                postIds.isEmpty() ? List.of() : commentRepository.countByPostIds(postIds));
        Set<Long> likedPostIds = postIds.isEmpty()
                ? Set.of()
                : likeRepository.findLikedPostIds(postIds, actor.getId());

        List<FeedItemResponse> items = new ArrayList<>();
        posts.stream()
                .map(post -> postItem(post, likeCounts, commentCounts, likedPostIds))
                .forEach(items::add);
        sessions.stream().map(this::sessionItem).forEach(items::add);
        attendedSessions.stream()
                .map(participant -> attendanceItem(participant, activityPosts, likeCounts, commentCounts, likedPostIds))
                .forEach(items::add);

        return items.stream()
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .limit(FEED_LIMIT)
                .toList();
    }

    private FeedItemResponse postItem(SocialPost post, Profile viewer) {
        return postItem(post, Map.of(), Map.of(), Set.of());
    }

    private FeedItemResponse postItem(
            SocialPost post,
            Map<Long, Long> likeCounts,
            Map<Long, Long> commentCounts,
            Set<Long> likedPostIds) {
        Profile author = post.getAuthor();
        Session linkedSession = post.getSession();
        if (!isPubliclyShareableSession(linkedSession)) {
            linkedSession = null;
        }

        return new FeedItemResponse(
                "POST",
                post.getId(),
                author.getUsername(),
                displayName(author),
                author.getProfilePicUrl(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt(),
                linkedSession == null ? null : linkedSession.getId(),
                linkedSession == null ? null : linkedSession.getSport(),
                linkedSession == null ? null : linkedSession.getCity(),
                linkedSession == null || linkedSession.getGameDate() == null
                        ? null : linkedSession.getGameDate().toInstant(),
                likeCounts.getOrDefault(post.getId(), 0L),
                likedPostIds.contains(post.getId()),
                commentCounts.getOrDefault(post.getId(), 0L),
                publicSessionImage(linkedSession),
                post.getVideoUrl(),
                post.isHideLikers(),
                post.getExtraMedia() == null ? java.util.List.of() : post.getExtraMedia().stream()
                    .map(asset -> new com.example.desporto24.model.dto.social.PublicPostMediaResponse(
                            asset.getUrl(), asset.getMimeType())).toList()
        );
    }

    private Map<Long, Long> countsByPostId(List<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Long) row[1]
        ));
    }

    private FeedItemResponse sessionItem(Session session) {
        Profile author = session.getProfile();
        Instant createdAt = session.getCreated_at() == null
                ? (session.getUpdatedAt() == null ? Instant.now() : session.getUpdatedAt())
                : session.getCreated_at().toInstant();

        return new FeedItemResponse(
                "SESSION_CREATED",
                null,
                author == null ? session.getUsername() : author.getUsername(),
                author == null ? session.getUsername() : displayName(author),
                author == null ? null : author.getProfilePicUrl(),
                null,
                null,
                createdAt,
                session.getId(),
                session.getSport(),
                session.getCity(),
                session.getGameDate() == null ? null : session.getGameDate().toInstant(),
                0,
                false,
                0,
                publicSessionImage(session),
                null,
                false
        );
    }

    private FeedItemResponse attendanceItem(
            SessionParticipant participant, Map<String, SocialPost> activityPosts,
            Map<Long, Long> likeCounts, Map<Long, Long> commentCounts, Set<Long> likedPostIds) {
        Profile author = participant.getProfile();
        Session session = participant.getSession();
        SocialPost activityPost = activityPosts.get(author.getId() + ":" + session.getId());
        Long activityPostId = activityPost == null ? null : activityPost.getId();

        return new FeedItemResponse(
                "SESSION_ATTENDED",
                activityPostId,
                author.getUsername(),
                displayName(author),
                author.getProfilePicUrl(),
                null,
                null,
                participant.getAttendanceRecordedAt(),
                session.getId(),
                session.getSport(),
                session.getCity(),
                session.getGameDate() == null
                        ? null
                        : session.getGameDate().toInstant(),
                activityPostId == null ? 0 : likeCounts.getOrDefault(activityPostId, 0L),
                activityPostId != null && likedPostIds.contains(activityPostId),
                activityPostId == null ? 0 : commentCounts.getOrDefault(activityPostId, 0L),
                publicSessionImage(session),
                null,
                activityPost != null && activityPost.isHideLikers()
        );
    }

    /** Materializes the interaction backing post only when someone interacts. */
    public Long ensureAttendancePost(Long sessionId, String attendeeUsername, String viewerUsername) {
        profile(viewerUsername);
        Profile attendee = profile(attendeeUsername);
        Session session = resolvePublicSession(sessionId);
        SessionParticipant participant = participantRepository
                .findBySessionIdAndProfileId(sessionId, attendee.getId())
                .orElseThrow(() -> new AccessDeniedException("Atividade não disponível"));
        if (participant.getAttendanceStatus() != AttendanceStatus.ATTENDED
                || participant.getAttendanceRecordedAt() == null
                || participant.isFeedHidden()) {
            throw new AccessDeniedException("Atividade não disponível");
        }
        return postRepository.findByActivityTypeAndAuthorIdAndSessionId(
                        "SESSION_ATTENDED", attendee.getId(), sessionId)
                .orElseGet(() -> postRepository.save(SocialPost.builder()
                        .activityType("SESSION_ATTENDED")
                        .author(attendee)
                        .session(session)
                        .content("Atividade realizada")
                        .createdAt(participant.getAttendanceRecordedAt())
                        .build()))
                .getId();
    }

    private Session resolvePublicSession(Long sessionId) {
        if (sessionId == null) {
            return null;
        }
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Sessão não encontrada"));
        if (!isPubliclyShareableSession(session)) {
            throw new AccessDeniedException("Não é possível partilhar esta sessão no feed");
        }
        return session;
    }

    static String publicSessionImage(Session session) {
        if (!isPubliclyShareableSession(session)
                || session.getGamePhoto() == null
                || session.getGamePhoto().isBlank()) {
            return null;
        }
        return session.getGamePhoto();
    }

    static boolean isPubliclyShareableSession(Session session) {
        if (session == null
                || Boolean.TRUE.equals(session.getPrivateGame())
                || Boolean.FALSE.equals(session.getEnabled())) {
            return false;
        }
        return session.getAudience() == SessionAudience.PUBLIC
                || (session.getAudience() == null && session.getCommunity() == null);
    }

    private String storageKey(URI uri) {
        String path = uri.getPath();
        if (path == null || path.isBlank()) {
            return null;
        }

        int index = path.indexOf("/posts/");
        if (index >= 0) {
            return path.substring(index + 1);
        }

        index = path.indexOf("posts/");
        if (index >= 0) {
            return path.substring(index);
        }

        return path.startsWith("/") ? path.substring(1) : path;
    }

    private String displayName(Profile profile) {
        if (profile.getNickname() != null && !profile.getNickname().isBlank()) {
            return profile.getNickname();
        }
        if (profile.getFullName() != null && !profile.getFullName().isBlank()) {
            return profile.getFullName();
        }
        return profile.getUsername();
    }

    private Profile profile(String username) {
        return profileRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USERNAME));
    }
}
