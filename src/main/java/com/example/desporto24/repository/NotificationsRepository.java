package com.example.desporto24.repository;

import com.example.desporto24.register.FriendRequest.FriendRequest;
import com.example.desporto24.register.Notifications.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

    List<Notifications> findByUsername(String username);
    long countByUsernameAndReadFalse(String username);
    /** Collapse a burst of group messages into one unread alert per group. */
    boolean existsByUsernameAndPrivateGroupIdAndReadFalse(String username, Long privateGroupId);
    List<Notifications> findByUsernameIgnoreCaseAndPrivateGroupIdAndReadFalse(String username, Long privateGroupId);
    boolean existsByUsernameAndSportsTeamIdAndReadFalse(String username, Long sportsTeamId);
    List<Notifications> findByUsernameIgnoreCaseAndSportsTeamIdAndReadFalse(String username, Long sportsTeamId);
    Optional<Notifications> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notifications n WHERE n.username = ?1")
    void deleteByUsername(String username);

    long deleteByUsernameOrRelatedUsername(String username, String relatedUsername);

    boolean existsByUsernameAndSessionIdAndTypeAndReadFalse(String username, Long sessionId,
            com.example.desporto24.enumeration.NotificationType type);
    boolean existsByUsernameAndCommunityIdAndTypeAndReadFalse(String username, Long communityId,
            com.example.desporto24.enumeration.NotificationType type);
    List<Notifications> findByUsernameIgnoreCaseAndSessionIdAndTypeAndReadFalse(String username, Long sessionId,
            com.example.desporto24.enumeration.NotificationType type);
    List<Notifications> findByUsernameIgnoreCaseAndCommunityIdAndTypeAndReadFalse(String username, Long communityId,
            com.example.desporto24.enumeration.NotificationType type);
    List<Notifications> findByUsernameIgnoreCaseAndRelatedUsernameIgnoreCaseAndTypeAndReadFalse(String username,
            String relatedUsername, com.example.desporto24.enumeration.NotificationType type);
}
