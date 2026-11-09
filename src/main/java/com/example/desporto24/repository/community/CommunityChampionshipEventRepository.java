package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityChampionshipEventRepository extends JpaRepository<CommunityChampionshipEvent, Long> {
    List<CommunityChampionshipEvent> findByChampionshipIdOrderByCreatedAtAscIdAsc(Long championshipId);
}
