package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipLineupPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipLineupPlayerRepository extends JpaRepository<CommunityChampionshipLineupPlayer, Long> {
    List<CommunityChampionshipLineupPlayer> findByFixtureIdOrderByTeamIdAscIdAsc(Long fixtureId);
    Optional<CommunityChampionshipLineupPlayer> findByFixtureIdAndPlayerId(Long fixtureId, Long playerId);
    boolean existsByFixtureEventChampionshipIdAndPlayerId(Long championshipId, Long playerId);
}
