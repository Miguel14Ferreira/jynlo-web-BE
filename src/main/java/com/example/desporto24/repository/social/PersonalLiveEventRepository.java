package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.PersonalLiveEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PersonalLiveEventRepository extends JpaRepository<PersonalLiveEvent, Long> {
    List<PersonalLiveEvent> findTop30ByStatusOrderByCreatedAtDesc(
            PersonalLiveEvent.Status status);
    Optional<PersonalLiveEvent> findByHostIdAndStatus(Long hostId, PersonalLiveEvent.Status status);
    List<PersonalLiveEvent> findTop100ByStatusAndCreatedAtBefore(
            PersonalLiveEvent.Status status, Instant cutoff);
    @Modifying
    @Query("UPDATE PersonalLiveEvent e SET e.hostHeartbeatAt=:now WHERE e.id=:id AND e.host.id=:host AND e.status=:status")
    int heartbeat(@Param("id") Long id,@Param("host") Long host,
      @Param("status") PersonalLiveEvent.Status status,@Param("now") Instant now);
}
