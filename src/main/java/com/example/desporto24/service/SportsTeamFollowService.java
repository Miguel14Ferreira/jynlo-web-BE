package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeamFollow;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamFollowRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;

/** Public follow state only; never derives a membership or access grant. */
@Service
@RequiredArgsConstructor
public class SportsTeamFollowService {
    private final SportsTeamRepository teams;
    private final SportsTeamFollowRepository followers;
    private final ProfileRepository profiles;

    public record FollowState(long count,boolean following) {}

    private Profile actor(String username) {
        return profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
    }
    private void requireTeam(Long teamId) {
        if(teamId==null || teamId<=0 || !teams.existsById(teamId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa não encontrada");
    }
    @Transactional(readOnly=true)
    public FollowState state(Long teamId,String username) {
        requireTeam(teamId);
        Profile profile=actor(username);
        return new FollowState(followers.countByTeamId(teamId),
                followers.existsByTeamIdAndFollowerId(teamId,profile.getId()));
    }

    @Transactional
    public FollowState follow(Long teamId,String username) {
        requireTeam(teamId);
        Profile profile=actor(username);
        if(Boolean.FALSE.equals(profile.getEnabled()) || profile.getModerationSuspendedAt()!=null)
            throw new AccessDeniedException("Conta indisponível");
        if(!followers.existsByTeamIdAndFollowerId(teamId,profile.getId())) {
            try {
                followers.saveAndFlush(SportsTeamFollow.builder()
                    .team(teams.getReferenceById(teamId)).follower(profile)
                    .createdAt(Instant.now()).build());
            } catch(DataIntegrityViolationException ex) {
                // Concurrent follow requests: unique index is authoritative.
                // Do not return success from a failed transaction.
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Segue a equipa novamente");
            }
        }
        return new FollowState(followers.countByTeamId(teamId),true);
    }

    @Transactional
    public FollowState unfollow(Long teamId,String username) {
        requireTeam(teamId);
        Profile profile=actor(username);
        followers.deleteByTeamIdAndFollowerId(teamId,profile.getId());
        return new FollowState(followers.countByTeamId(teamId),false);
    }
}
