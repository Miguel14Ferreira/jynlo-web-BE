package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityTournamentTeamInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommunityTournamentTeamInviteRepository
        extends JpaRepository<CommunityTournamentTeamInvite, Long> {
    Optional<CommunityTournamentTeamInvite> findByTeamIdAndProfileId(Long teamId, Long profileId);
    void deleteByTeamId(Long teamId);
}
