package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipDisciplinaryCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipDisciplinaryCardRepository extends JpaRepository<CommunityChampionshipDisciplinaryCard, Long> {
    List<CommunityChampionshipDisciplinaryCard> findByFixtureIdOrderByIdAsc(Long fixtureId);
    Optional<CommunityChampionshipDisciplinaryCard> findByIdAndFixtureId(Long id, Long fixtureId);
}
