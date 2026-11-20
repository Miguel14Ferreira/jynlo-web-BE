package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportsTeamFollowRepository extends JpaRepository<SportsTeamFollow,Long> {
    boolean existsByTeamIdAndFollowerId(Long teamId,Long followerId);
    long countByTeamId(Long teamId);
    long deleteByTeamIdAndFollowerId(Long teamId,Long followerId);
    boolean existsByTeamId(Long teamId);
}
