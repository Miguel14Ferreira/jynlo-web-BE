package com.example.desporto24.service;

import com.example.desporto24.exception.UserNotFoundException;
import com.example.desporto24.enumeration.AttendanceStatus;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.model.Profile;
import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.model.dto.social.FollowProfileResponse;
import com.example.desporto24.model.dto.social.CreateSocialCommentRequest;
import com.example.desporto24.model.dto.moderation.CreateUserReportRequest;
import com.example.desporto24.model.dto.social.PostLikeResponse;
import com.example.desporto24.model.dto.social.SocialCommentResponse;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.SocialPostComment;
import com.example.desporto24.model.social.SocialPostCommentLike;
import com.example.desporto24.repository.social.SocialPostCommentLikeRepository;
import com.example.desporto24.model.social.SocialPostLike;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.SocialPostCommentRepository;
import com.example.desporto24.repository.social.SocialPostLikeRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.example.desporto24.websocket.ChatRealtimeGateway;

import java.time.Instant;
import java.util.List;

import static com.example.desporto24.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialPostInteractionService {

    private final SocialPostRepository postRepository;
    private final SocialPostLikeRepository likeRepository;
    private final SocialPostCommentRepository commentRepository;
    private final SocialPostCommentLikeRepository commentLikes;
    private final ProfileRepository profileRepository;
    private final UserReportService userReportService;
    private final SessionParticipantRepository participantRepository;
    private final NotificationsRepository notificationsRepository;
    private final NotificationPreferencesService notificationPreferencesService;
    private final UserBlockService userBlockService;
    private final ChatRealtimeGateway realtimeGateway;
    @Autowired(required=false)
    private ProfilePrivacyService privacy;

    private void requireVisiblePost(Profile viewer,SocialPost post) {
        if (post.getAuthor().isPrivateProfile() &&
                (privacy == null || !privacy.canView(viewer,post.getAuthor())))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Publicação indisponível");
    }


    /**
     * Send only after the database transaction commits. Otherwise another
     * browser could fetch the previous count or see a rolled-back like.
     */
    private void signalFeedAfterCommit() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    realtimeGateway.publishSocialFeedInvalidated();
                }
            });
        }
    }

    public PostLikeResponse like(Long postId, String username) {
        SocialPost post = post(postId);
        Profile actor = profile(username);
        userBlockService.ensureInteractionAllowed(actor, post.getAuthor());
        requireVisiblePost(actor,post);

        if (!likeRepository.existsByPostIdAndProfileId(postId, actor.getId())) {
            likeRepository.save(SocialPostLike.builder()
                    .post(post)
                    .profile(actor)
                    .createdAt(Instant.now())
                    .build());
            notifySocialAction(post, actor, NotificationType.SOCIAL_POST_LIKED);
            signalFeedAfterCommit();
        }

        return likeStatus(postId, actor.getId());
    }

    public PostLikeResponse unlike(Long postId, String username) {
        Profile actor = profile(username);
        SocialPost target = post(postId);

        // Removing an old like remains possible after blocking, without
        // leaking the blocked author's like totals through this endpoint.
        boolean blocked = userBlockService.isBlockedBetween(actor, target.getAuthor());
        requireVisiblePost(actor,target);
        likeRepository.deleteByPostIdAndProfileId(postId, actor.getId());
        signalFeedAfterCommit();
        return blocked ? new PostLikeResponse(false, 0) : likeStatus(postId, actor.getId());
    }

    @Transactional(readOnly = true)
    public List<SocialCommentResponse> comments(Long postId, String username) {
        SocialPost target = post(postId);
        Profile viewer = profile(username);
        userBlockService.ensureInteractionAllowed(viewer, target.getAuthor());
        requireVisiblePost(viewer,target);
        List<SocialPostComment> visible = commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .filter(comment -> !userBlockService.isBlockedBetween(viewer, comment.getAuthor()))
                .toList();
        java.util.Set<Long> ids=visible.stream().map(SocialPostComment::getId)
                .collect(java.util.stream.Collectors.toSet());
        if (ids.isEmpty()) return List.of();
        java.util.Map<Long,Long> counts = new java.util.HashMap<>();
        for (Object[] row:commentLikes.countsByCommentIds(ids)) {
            counts.put(((Number)row[0]).longValue(), ((Number)row[1]).longValue());
        }
        java.util.Set<Long> liked = commentLikes.likedCommentIds(ids,viewer.getId());
        return visible.stream().map(comment -> toResponse(comment,
                counts.getOrDefault(comment.getId(),0L),liked.contains(comment.getId()))).toList();
    }

    public SocialCommentResponse comment(
            Long postId,
            String username,
            CreateSocialCommentRequest request) {
        SocialPost post = post(postId);
        Profile actor = profile(username);
        userBlockService.ensureInteractionAllowed(actor, post.getAuthor());
        requireVisiblePost(actor,post);

        SocialPostComment comment = commentRepository.save(
                SocialPostComment.builder()
                        .post(post)
                        .author(actor)
                        .content(request.content().trim())
                        .createdAt(Instant.now())
                        .build()
        );

        notifySocialAction(post, actor, NotificationType.SOCIAL_POST_COMMENTED);
        signalFeedAfterCommit();
        return toResponse(comment, 0, false);
    }

    /** Only the author can alter an existing comment. Preserve its creation date. */
    public SocialCommentResponse editComment(Long postId,Long commentId,String username,String text) {
        Profile actor=profile(username);
        SocialPostComment comment=commentForPost(postId,commentId);
        if (!comment.getAuthor().getId().equals(actor.getId()))
            throw new AccessDeniedException("Só o autor pode editar o comentário");
        if (text==null || text.trim().isEmpty() || text.trim().length()>500)
            throw new IllegalArgumentException("O comentário deve ter entre 1 e 500 caracteres");
        userBlockService.ensureInteractionAllowed(actor,comment.getPost().getAuthor());
        requireVisiblePost(actor,comment.getPost());
        comment.setContent(text.trim());
        comment.setEditedAt(Instant.now());
        commentRepository.save(comment);
        signalFeedAfterCommit();
        return toResponse(comment, commentLikes.countByCommentId(commentId),
            commentLikes.existsByCommentIdAndProfileId(commentId,actor.getId()));
    }

    public SocialCommentResponse setCommentLike(Long postId,Long commentId,String username,boolean like) {
        Profile actor=profile(username);
        SocialPostComment comment=commentForPost(postId,commentId);
        if (like) {
            userBlockService.ensureInteractionAllowed(actor,comment.getPost().getAuthor());
            requireVisiblePost(actor,comment.getPost());
            userBlockService.ensureInteractionAllowed(actor,comment.getAuthor());
            if (!commentLikes.existsByCommentIdAndProfileId(commentId,actor.getId()))
                commentLikes.save(SocialPostCommentLike.builder().comment(comment)
                    .profile(actor).createdAt(Instant.now()).build());
        } else {
            commentLikes.deleteByCommentIdAndProfileId(commentId,actor.getId());
        }
        signalFeedAfterCommit();
        return toResponse(comment,commentLikes.countByCommentId(commentId),
            commentLikes.existsByCommentIdAndProfileId(commentId,actor.getId()));
    }

    private SocialPostComment commentForPost(Long postId,Long commentId) {
        SocialPostComment comment=commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado"));
        if (!comment.getPost().getId().equals(postId))
            throw new IllegalArgumentException("O comentário não pertence a esta publicação");
        post(postId);
        return comment;
    }

    @Transactional(readOnly = true)
    public List<FollowProfileResponse> likers(Long postId, String viewerUsername) {
        SocialPost target = post(postId);
        Profile viewer = profile(viewerUsername);
        userBlockService.ensureInteractionAllowed(viewer, target.getAuthor());
        requireVisiblePost(viewer,target);
        if (target.isHideLikers() && !target.getAuthor().getUsername().equalsIgnoreCase(viewerUsername)) {
            throw new AccessDeniedException("Lista de gostos oculta pelo autor");
        }
        return likeRepository.findLikers(postId, org.springframework.data.domain.PageRequest.of(0, 100))
                .stream()
                .filter(like -> !userBlockService.isBlockedBetween(viewer, like.getProfile()))
                .map(like -> {
                    Profile p = like.getProfile();
                    return new FollowProfileResponse(p.getUsername(), p.getFullName(), p.getNickname(), p.getProfilePicUrl());
                }).toList();
    }

    public boolean setHideLikers(Long postId, String username, boolean hidden) {
        SocialPost target = post(postId);
        if (!target.getAuthor().getUsername().equalsIgnoreCase(username)) {
            throw new AccessDeniedException("Só o autor pode alterar a privacidade dos gostos");
        }
        target.setHideLikers(hidden);
        postRepository.save(target);
        return hidden;
    }

    private void notifySocialAction(SocialPost post, Profile actor, NotificationType type) {
        Profile recipient = post.getAuthor();
        if (recipient.getId().equals(actor.getId())) return;
        if (!notificationPreferencesService.allowsInApp(recipient.getUsername(), type)) return;
        Notifications notification = new Notifications();
        notification.setUsername(recipient.getUsername());
        notification.setRelatedUsername(actor.getUsername());
        notification.setPostId(post.getId());
        notification.setSubject(type == NotificationType.SOCIAL_POST_LIKED ? "Novo gosto" : "Novo comentário");
        notification.setMessage(actor.getUsername() + (type == NotificationType.SOCIAL_POST_LIKED
                ? " gostou da tua publicação." : " comentou a tua publicação."));
        notification.setGreeting("");
        notification.setSignature("");
        notification.setCreatedAt(Instant.now().toString());
        notification.setType(type);
        notification.setRead(false);
        if (post.getSession() != null) notification.setSessionId(post.getSession().getId());
        notificationsRepository.save(notification);
    }

    public void reportPost(
            Long postId,
            String username,
            CreateUserReportRequest request) {
        SocialPost post = post(postId);
        userReportService.reportContent(
                username,
                post.getAuthor().getUsername(),
                "POST #" + postId,
                request.reason()
        );
    }

    public void reportComment(
            Long postId,
            Long commentId,
            String username,
            CreateUserReportRequest request) {
        SocialPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado"));
        post(comment.getPost().getId());

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("O comentário não pertence a esta publicação");
        }

        userReportService.reportContent(
                username,
                comment.getAuthor().getUsername(),
                "COMMENT #" + commentId + " (POST #" + postId + ")",
                request.reason()
        );
    }

    public void deleteComment(
            Long postId,
            Long commentId,
            String username) {
        Profile actor = profile(username);
        SocialPostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado"));

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("O comentário não pertence a esta publicação");
        }

        if (!comment.getAuthor().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Só podes apagar os teus comentários");
        }

        commentLikes.deleteByCommentId(commentId);
        commentRepository.delete(comment);
        signalFeedAfterCommit();
    }

    @Transactional(readOnly = true)
    public long likeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Transactional(readOnly = true)
    public boolean likedBy(Long postId, Long profileId) {
        return likeRepository.existsByPostIdAndProfileId(postId, profileId);
    }

    @Transactional(readOnly = true)
    public long commentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    private PostLikeResponse likeStatus(Long postId, Long profileId) {
        return new PostLikeResponse(
                likeRepository.existsByPostIdAndProfileId(postId, profileId),
                likeRepository.countByPostId(postId)
        );
    }

    private SocialCommentResponse toResponse(SocialPostComment comment,long likeCount,boolean likedByMe) {
        Profile author = comment.getAuthor();
        return new SocialCommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                author.getUsername(),
                displayName(author),
                author.getProfilePicUrl(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getEditedAt(),
                likeCount,
                likedByMe
        );
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

    private SocialPost post(Long id) {
        SocialPost post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publicação não encontrada"));
        if ("SESSION_ATTENDED".equals(post.getActivityType())) {
            if (!SocialFeedService.isPubliclyShareableSession(post.getSession())) {
                throw new AccessDeniedException("Atividade indisponível");
            }
            SessionParticipant participant = participantRepository
                    .findBySessionIdAndProfileId(post.getSession().getId(), post.getAuthor().getId())
                    .orElseThrow(() -> new AccessDeniedException("Atividade indisponível"));
            if (participant.isFeedHidden()
                    || participant.getAttendanceStatus() != AttendanceStatus.ATTENDED
                    || participant.getAttendanceRecordedAt() == null) {
                throw new AccessDeniedException("Atividade indisponível");
            }
        }
        return post;
    }

    private Profile profile(String username) {
        return profileRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USERNAME));
    }
}
