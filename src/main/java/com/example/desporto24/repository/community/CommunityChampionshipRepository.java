package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipRepository extends JpaRepository<CommunityChampionship, Long> {
    List<CommunityChampionship> findByCommunityIdOrderByCreatedAtDescIdDesc(Long communityId);
    Optional<CommunityChampionship> findByIdAndCommunityId(Long id, Long communityId);
}
