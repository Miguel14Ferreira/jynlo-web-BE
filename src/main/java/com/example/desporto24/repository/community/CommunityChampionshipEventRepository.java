package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityChampionshipEventRepository extends JpaRepository<CommunityChampionshipEvent, Long> {
    List<CommunityChampionshipEvent> findByChampionshipIdOrderByCreatedAtAscIdAsc(Long championshipId);
    java.util.Optional<CommunityChampionshipEvent> findByIdAndChampionshipId(Long id, Long championshipId);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query(
            "select event from CommunityChampionshipEvent event where event.id = :eventId and event.championship.id = :championshipId")
    java.util.Optional<CommunityChampionshipEvent> lockForScheduling(
            @org.springframework.data.repository.query.Param("eventId") Long eventId,
            @org.springframework.data.repository.query.Param("championshipId") Long championshipId);
}
