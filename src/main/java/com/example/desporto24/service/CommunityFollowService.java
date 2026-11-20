package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityVisibility;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityFollow;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityFollowRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CommunityFollowService {
    private final CommunityRepository communities;
    private final CommunityFollowRepository follows;
    private final ProfileRepository profiles;
    public record FollowState(long count,boolean following) {}

    private Profile actor(String username) {
        return profiles.findUserByUsername(username)
          .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
    }
    private Community requirePublic(Long id) {
        if(id==null||id<=0) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Comunidade não encontrada");
        Community c=communities.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Comunidade não encontrada"));
        // Following a PRIVATE community must never reveal its membership, posts or existence.
        if(c.getVisibility()!=CommunityVisibility.PUBLIC)
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Comunidade indisponível para seguir");
        return c;
    }
    @Transactional(readOnly=true)
    public FollowState state(Long id,String username) {
        Community community=requirePublic(id);
        Profile user=actor(username);
        return new FollowState(follows.countByCommunityId(community.getId()),
            follows.existsByCommunityIdAndFollowerId(community.getId(),user.getId()));
    }
    @Transactional
    public FollowState follow(Long id,String username) {
        Community community=requirePublic(id);
        Profile user=actor(username);
        if(Boolean.FALSE.equals(user.getEnabled())||user.getModerationSuspendedAt()!=null)
          throw new AccessDeniedException("Conta indisponível");
        // Idempotent requests plus a unique DB constraint prevent duplicate subscriptions.
        if(!follows.existsByCommunityIdAndFollowerId(id,user.getId())) {
          try {
            follows.saveAndFlush(CommunityFollow.builder().community(community)
                .follower(user).createdAt(Instant.now()).build());
          } catch (DataIntegrityViolationException concurrentFollow) {
            // Database uniqueness wins races across Render instances; clients
            // can refresh authoritative follow state before trying again.
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "A subscrição foi alterada noutro pedido. Atualiza os seguidores.");
          }
        }
        return new FollowState(follows.countByCommunityId(id),true);
    }
    @Transactional
    public FollowState unfollow(Long id,String username) {
        requirePublic(id);
        Profile user=actor(username);
        follows.deleteByCommunityIdAndFollowerId(id,user.getId());
        return new FollowState(follows.countByCommunityId(id),false);
    }
}
