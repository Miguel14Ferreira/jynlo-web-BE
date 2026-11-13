package com.example.desporto24.service;

import com.example.desporto24.exception.UserNotFoundException;
import com.example.desporto24.model.Profile;
import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.model.dto.social.FollowProfileResponse;
import com.example.desporto24.model.dto.social.FollowStatusResponse;
import com.example.desporto24.model.social.ProfileFollow;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.ProfileFollowRepository;
import com.example.desporto24.repository.social.ProfileFollowRequestRepository;
import com.example.desporto24.model.social.ProfileFollowRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.example.desporto24.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileFollowService {

    private final ProfileFollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final UserBlockService userBlockService;
    private final NotificationsRepository notificationsRepository;
    private final NotificationPreferencesService notificationPreferencesService;
    @Autowired(required=false)
    private ProfileFollowRequestRepository requests;

    public FollowStatusResponse follow(String actorUsername, String targetUsername) {
        Profile actor = profile(actorUsername);
        Profile target = profile(targetUsername);

        if (actor.getId().equals(target.getId())) {
            throw new IllegalArgumentException("Não podes seguir o teu próprio perfil");
        }

        userBlockService.ensureInteractionAllowed(actor, target);

        if (target.isPrivateProfile() && !followRepository.existsByFollowerIdAndFollowingId(
                actor.getId(),target.getId())) {
            if (requests == null)
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Pedidos indisponíveis");
            if (!requests.existsByRequesterIdAndTargetId(actor.getId(),target.getId()))
                requests.save(ProfileFollowRequest.builder().requester(actor).target(target)
                    .createdAt(Instant.now()).build());
            return status(actor,target);
        }
        if (!followRepository.existsByFollowerIdAndFollowingId(
                actor.getId(), target.getId())) {
            followRepository.save(ProfileFollow.builder()
                    .follower(actor)
                    .following(target)
                    .createdAt(Instant.now())
                    .build());
            if (notificationPreferencesService.allowsInApp(target.getUsername(), NotificationType.SOCIAL_NEW_FOLLOWER)) {
                Notifications notification = new Notifications();
                notification.setUsername(target.getUsername());
                notification.setRelatedUsername(actor.getUsername());
                notification.setSubject("Novo seguidor");
                notification.setMessage(actor.getUsername() + " começou a seguir-te.");
                notification.setGreeting("");
                notification.setSignature("");
                notification.setCreatedAt(Instant.now().toString());
                notification.setType(NotificationType.SOCIAL_NEW_FOLLOWER);
                notification.setRead(false);
                notificationsRepository.save(notification);
            }
        }

        return status(actor, target);
    }

    public FollowStatusResponse unfollow(String actorUsername, String targetUsername) {
        Profile actor = profile(actorUsername);
        Profile target = profile(targetUsername);

        followRepository.deleteByFollowerIdAndFollowingId(
                actor.getId(), target.getId());
        if (requests != null) requests.deleteByRequesterIdAndTargetId(actor.getId(),target.getId());

        return status(actor, target);
    }

    @Transactional(readOnly = true)
    public FollowStatusResponse status(String actorUsername, String targetUsername) {
        Profile actor = profile(actorUsername);
        Profile target = profile(targetUsername);
        userBlockService.ensureInteractionAllowed(actor, target);
        return status(actor, target);
    }

    @Transactional(readOnly = true)
    public List<FollowProfileResponse> following(String username,String viewerUsername) {
        ensureFollowListVisible(profile(viewerUsername),profile(username));
        return following(username);
    }

    @Transactional(readOnly = true)
    public List<FollowProfileResponse> followers(String username,String viewerUsername) {
        ensureFollowListVisible(profile(viewerUsername),profile(username));
        return followers(username);
    }

    private void ensureFollowListVisible(Profile viewer,Profile target) {
        if (userBlockService.isBlockedBetween(viewer,target) ||
            (target.isPrivateProfile() && !viewer.getId().equals(target.getId()) &&
            !followRepository.existsByFollowerIdAndFollowingId(viewer.getId(),target.getId()))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Lista de seguidores indisponível");
        }
    }

    @Transactional(readOnly = true)
    public List<FollowProfileResponse> following(String username) {
        Profile profile = profile(username);

        return followRepository.findByFollowerIdOrderByCreatedAtDesc(profile.getId())
                .stream()
                .map(ProfileFollow::getFollowing)
                .map(this::toProfile)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FollowProfileResponse> followers(String username) {
        Profile profile = profile(username);

        return followRepository.findByFollowingIdOrderByCreatedAtDesc(profile.getId())
                .stream()
                .map(ProfileFollow::getFollower)
                .map(this::toProfile)
                .toList();
    }

    private FollowStatusResponse status(Profile actor, Profile target) {
        return new FollowStatusResponse(
                !actor.getId().equals(target.getId())
                        && followRepository.existsByFollowerIdAndFollowingId(
                        actor.getId(), target.getId()),
                !actor.getId().equals(target.getId())
                        && followRepository.existsByFollowerIdAndFollowingId(
                        target.getId(), actor.getId()),
                followRepository.countByFollowingId(target.getId()),
                followRepository.countByFollowerId(target.getId()),
                requests != null && requests.existsByRequesterIdAndTargetId(actor.getId(),target.getId()),
                target.isPrivateProfile()
        );
    }

    private FollowProfileResponse toProfile(Profile profile) {
        return new FollowProfileResponse(
                profile.getUsername(),
                profile.getFullName(),
                profile.getNickname(),
                profile.getProfilePicUrl()
        );
    }

    private Profile profile(String username) {
        return profileRepository.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USERNAME));
    }
}
