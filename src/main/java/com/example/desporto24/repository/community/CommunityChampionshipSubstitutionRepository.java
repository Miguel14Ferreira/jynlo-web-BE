package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipSubstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipSubstitutionRepository extends JpaRepository<CommunityChampionshipSubstitution, Long> {
    List<CommunityChampionshipSubstitution> findByFixtureIdOrderByMatchMinuteAscIdAsc(Long fixtureId);
    Optional<CommunityChampionshipSubstitution> findByIdAndFixtureId(Long id, Long fixtureId);
    boolean existsByFixtureEventChampionshipIdAndPlayerOutId(Long championshipId, Long profileId);
    boolean existsByFixtureEventChampionshipIdAndPlayerInId(Long championshipId, Long profileId);
}
