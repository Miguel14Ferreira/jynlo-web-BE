package com.example.desporto24.repository.session;

import com.example.desporto24.enumeration.SessionInviteStatus;
import com.example.desporto24.model.session.SessionInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionInviteRepository extends JpaRepository<SessionInvite, Long> {
    Optional<SessionInvite> findBySessionIdAndInviteeIdAndStatus(
            Long sessionId, Long inviteeId, SessionInviteStatus status);

    List<SessionInvite> findByInviteeIdAndStatusOrderByCreatedAtDesc(
            Long inviteeId, SessionInviteStatus status);

    /** Pending invites old enough for a reminder, for games that are still ahead. */
    @org.springframework.data.jpa.repository.Query("""
            select i.id from SessionInvite i
            where i.status = com.example.desporto24.enumeration.SessionInviteStatus.PENDING
              and i.reminderSentAt is null and i.createdAt < :createdBefore
              and i.session.gameDate > :startsAfter
              and i.session.status = com.example.desporto24.enumeration.SessionStatus.OPEN
            order by i.id asc
            """)
    List<Long> findDueForReminder(
            @org.springframework.data.repository.query.Param("createdBefore") java.time.Instant createdBefore,
            @org.springframework.data.repository.query.Param("startsAfter") java.util.Date startsAfter,
            org.springframework.data.domain.Pageable pageable);

    /** Claims the reminder; true only for the caller that set it. */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("update SessionInvite i set i.reminderSentAt = :now where i.id = :id and i.reminderSentAt is null")
    int claimReminder(@org.springframework.data.repository.query.Param("id") Long id,
                      @org.springframework.data.repository.query.Param("now") java.time.Instant now);
}
