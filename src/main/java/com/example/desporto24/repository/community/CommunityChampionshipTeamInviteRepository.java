package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipTeamInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipTeamInviteRepository
        extends JpaRepository<CommunityChampionshipTeamInvite, Long> {
    Optional<CommunityChampionshipTeamInvite> findByTeamIdAndProfileId(Long teamId, Long profileId);
    List<CommunityChampionshipTeamInvite> findByTeamIdOrderByInvitedAtAscIdAsc(Long teamId);
    List<CommunityChampionshipTeamInvite> findByChampionshipIdAndProfileId(Long championshipId, Long profileId);
}
