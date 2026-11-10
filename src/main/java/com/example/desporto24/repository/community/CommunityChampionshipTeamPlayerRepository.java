package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipTeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipTeamPlayerRepository extends JpaRepository<CommunityChampionshipTeamPlayer, Long> {
    List<CommunityChampionshipTeamPlayer> findByTeamIdOrderByJoinedAtAscIdAsc(Long teamId);
    Optional<CommunityChampionshipTeamPlayer> findByChampionshipIdAndProfileId(Long championshipId, Long profileId);
}
