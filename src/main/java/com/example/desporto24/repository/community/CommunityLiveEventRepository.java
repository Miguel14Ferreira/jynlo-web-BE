package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityLiveEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityLiveEventRepository extends JpaRepository<CommunityLiveEvent, Long> {
    List<CommunityLiveEvent> findTop30ByCommunityIdOrderByScheduledAtDescIdDesc(Long communityId);
    Optional<CommunityLiveEvent> findByIdAndCommunityId(Long eventId, Long communityId);
    boolean existsByCommunityIdAndStatus(Long communityId, CommunityLiveEvent.Status status);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("""
            UPDATE CommunityLiveEvent e
            SET e.lastHostHeartbeatAt = :now
            WHERE e.id = :id AND e.community.id = :communityId AND e.status = :status
            """)
    int touchHostHeartbeat(@org.springframework.data.repository.query.Param("id") Long id,
            @org.springframework.data.repository.query.Param("communityId") Long communityId,
            @org.springframework.data.repository.query.Param("status") CommunityLiveEvent.Status status,
            @org.springframework.data.repository.query.Param("now") java.time.Instant now);

    java.util.List<CommunityLiveEvent> findTop100ByStatusAndStartedAtBefore(
            CommunityLiveEvent.Status status, java.time.Instant startedAt);
}
