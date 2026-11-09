package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipFixture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityChampionshipFixtureRepository extends JpaRepository<CommunityChampionshipFixture, Long> {
    List<CommunityChampionshipFixture> findByEventIdOrderByRoundNumberAscMatchNumberAsc(Long eventId);
    boolean existsByEventId(Long eventId);
}
