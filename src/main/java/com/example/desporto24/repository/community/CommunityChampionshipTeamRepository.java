package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityChampionshipTeamRepository extends JpaRepository<CommunityChampionshipTeam, Long> {
    List<CommunityChampionshipTeam> findByChampionshipIdOrderByNameAscIdAsc(Long championshipId);
    boolean existsByChampionshipIdAndNormalizedName(Long championshipId, String normalizedName);
}
