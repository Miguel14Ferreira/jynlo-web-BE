package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.ProfileFollow;
import com.example.desporto24.model.social.ProfileFollowRequest;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.ProfileFollowRepository;
import com.example.desporto24.repository.social.ProfileFollowRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfilePrivacyService {
  private final ProfileRepository profiles;
  private final ProfileFollowRepository follows;
  private final ProfileFollowRequestRepository requests;
  private final UserBlockService blocks;
  // Keep the setting disabled until private media delivery is access-controlled.
  @Value("${jynlo.profile.private-mode-enabled:false}")
  private boolean privateModeEnabled;
  // Deliberate cutover gate: public R2/CDN files must be migrated, purged and
  // all post/story uploads switched to protected storage before enabling this.
  @Value("${jynlo.profile.private-media-cutover-verified:false}")
  private boolean privateMediaCutoverVerified;
  @org.springframework.beans.factory.annotation.Autowired(required=false)
  private com.example.desporto24.storage.PrivateProfileMediaStorage protectedMedia;
  @org.springframework.beans.factory.annotation.Autowired(required=false)
  private ProfileMediaReadinessService mediaReadiness;

  private boolean privateModeAvailable(Profile profile) {
    return privateModeEnabled && privateMediaCutoverVerified
        && protectedMedia != null && protectedMedia.isConfigured()
        && mediaReadiness != null && mediaReadiness.count(profile).databaseReferencesReady();
  }

  private Profile profile(String username) {
    return profiles.findUserByUsernameIgnoreCase(username)
        .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Perfil não encontrado"));
  }
  public record PrivacyStatus(boolean privateProfile,boolean canViewContent,boolean privateModeAvailable) {}
  public record PendingRequest(long id,String username,String name,String avatarUrl,Instant createdAt) {}

  @Transactional(readOnly=true)
  public boolean canView(Profile viewer,Profile author) {
    if (viewer == null || author == null || blocks.isBlockedBetween(viewer,author)) return false;
    if (!author.isPrivateProfile() || viewer.getId().equals(author.getId())) return true;
    return follows.existsByFollowerIdAndFollowingId(viewer.getId(),author.getId());
  }
  public void requireContentAccess(Profile viewer,Profile author) {
    if (!canView(viewer,author)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Conteúdo indisponível");
  }
  public PrivacyStatus status(String viewerName,String targetName) {
    Profile viewer=profile(viewerName),target=profile(targetName);
    return new PrivacyStatus(target.isPrivateProfile(),canView(viewer,target),privateModeAvailable(target));
  }
  public PrivacyStatus update(String actorName,boolean privateProfile) {
    Profile actor=profile(actorName);
    if (privateProfile && !privateModeAvailable(actor))
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
        "A proteção de multimédia privado ainda está a ser preparada");
    actor.setPrivateProfile(privateProfile);
    profiles.save(actor);
    if (!privateProfile) requests.deleteByTargetId(actor.getId());
    return new PrivacyStatus(actor.isPrivateProfile(),true,privateModeAvailable(actor));
  }
  public boolean requested(String requesterName,String targetName) {
    Profile requester=profile(requesterName),target=profile(targetName);
    return requests.existsByRequesterIdAndTargetId(requester.getId(),target.getId());
  }
  public void request(String requesterName,String targetName) {
    Profile requester=profile(requesterName),target=profile(targetName);
    if (requester.getId().equals(target.getId())) throw new IllegalArgumentException("Não podes seguir-te");
    blocks.ensureInteractionAllowed(requester,target);
    if (!target.isPrivateProfile()) throw new ResponseStatusException(HttpStatus.CONFLICT,"O perfil é público");
    if (!follows.existsByFollowerIdAndFollowingId(requester.getId(),target.getId())
          && !requests.existsByRequesterIdAndTargetId(requester.getId(),target.getId())) {
      requests.save(ProfileFollowRequest.builder().requester(requester).target(target)
          .createdAt(Instant.now()).build());
    }
  }
  public void cancel(String requesterName,String targetName) {
    Profile requester=profile(requesterName),target=profile(targetName);
    requests.deleteByRequesterIdAndTargetId(requester.getId(),target.getId());
  }
  @Transactional(readOnly=true)
  public List<PendingRequest> pending(String username) {
    Profile actor=profile(username);
    return requests.findTop100ByTargetIdOrderByCreatedAtDesc(actor.getId()).stream().map(req->
      new PendingRequest(req.getId(),req.getRequester().getUsername(),
        req.getRequester().getFullName(),req.getRequester().getProfilePicUrl(),
        req.getCreatedAt())).toList();
  }
  public void decide(String targetName,long requestId,boolean accept) {
    Profile target=profile(targetName);
    ProfileFollowRequest request=requests.findById(requestId).orElseThrow(
      ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido indisponível"));
    if (!request.getTarget().getId().equals(target.getId()))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido indisponível");
    Profile requester=request.getRequester();
    if (accept) {
      blocks.ensureInteractionAllowed(requester,target);
      if (!follows.existsByFollowerIdAndFollowingId(requester.getId(),target.getId()))
        follows.save(ProfileFollow.builder().follower(requester).following(target)
          .createdAt(Instant.now()).build());
    }
    requests.delete(request);
  }
}
