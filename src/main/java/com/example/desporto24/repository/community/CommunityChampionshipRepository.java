package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipRepository extends JpaRepository<CommunityChampionship, Long> {
    List<CommunityChampionship> findByCommunityIdOrderByCreatedAtDescIdDesc(Long communityId);
    Optional<CommunityChampionship> findByIdAndCommunityId(Long id, Long communityId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query(
            "select c from CommunityChampionship c where c.id = :id and c.community.id = :communityId")
    Optional<CommunityChampionship> findForClosing(
            @org.springframework.data.repository.query.Param("id") Long id,
            @org.springframework.data.repository.query.Param("communityId") Long communityId);
}
