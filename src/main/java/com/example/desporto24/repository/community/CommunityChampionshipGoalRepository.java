package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipGoalRepository extends JpaRepository<CommunityChampionshipGoal, Long> {
    List<CommunityChampionshipGoal> findByFixtureIdOrderByIdAsc(Long fixtureId);
    Optional<CommunityChampionshipGoal> findByIdAndFixtureId(Long id, Long fixtureId);
    boolean existsByFixtureEventChampionshipIdAndScorerId(Long championshipId, Long profileId);
    boolean existsByFixtureEventChampionshipIdAndAssisterId(Long championshipId, Long profileId);
}
