package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SportsTeamFollowRepository extends JpaRepository<SportsTeamFollow,Long> {
    @Query("select follow.team.id from SportsTeamFollow follow where follow.follower.id = :profileId")
    List<Long> followedTeamIds(@Param("profileId") Long profileId);

    boolean existsByTeamIdAndFollowerId(Long teamId,Long followerId);
    long countByTeamId(Long teamId);
    long deleteByTeamIdAndFollowerId(Long teamId,Long followerId);
    boolean existsByTeamId(Long teamId);
}
