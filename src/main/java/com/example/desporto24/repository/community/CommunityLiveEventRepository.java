package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityLiveEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityLiveEventRepository extends JpaRepository<CommunityLiveEvent, Long> {
    List<CommunityLiveEvent> findTop30ByCommunityIdOrderByScheduledAtDescIdDesc(Long communityId);
    Optional<CommunityLiveEvent> findByIdAndCommunityId(Long eventId, Long communityId);
    boolean existsByCommunityIdAndStatus(Long communityId, CommunityLiveEvent.Status status);
}
