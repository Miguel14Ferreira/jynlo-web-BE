package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface SportsTeamMemberRepository extends JpaRepository<SportsTeamMember, Long> {
    Optional<SportsTeamMember> findByTeamIdAndProfileId(Long teamId, Long profileId);

    // Serialize concurrent accept/decline requests for the same invitation.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from SportsTeamMember m where m.team.id = :teamId and m.profile.id = :profileId")
    Optional<SportsTeamMember> findInvitationForUpdate(@Param("teamId") Long teamId,
                                                       @Param("profileId") Long profileId);
    // The roster and notifications render each member's username. Fetch
    // profiles in one bounded query instead of lazily loading each player.
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "profile")
    List<SportsTeamMember> findByTeamIdAndStatusOrderByIdAsc(Long teamId, SportsTeamMember.Status status);
    List<SportsTeamMember> findByProfileIdAndStatusOrderByIdDesc(Long profileId, SportsTeamMember.Status status);
    boolean existsByTeamId(Long teamId);
    long countByTeamIdAndInvitedAtAfter(Long teamId, java.time.Instant since);
}
