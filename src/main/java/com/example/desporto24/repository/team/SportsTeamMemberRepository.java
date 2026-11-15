package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SportsTeamMemberRepository extends JpaRepository<SportsTeamMember, Long> {
    Optional<SportsTeamMember> findByTeamIdAndProfileId(Long teamId, Long profileId);
    List<SportsTeamMember> findByTeamIdAndStatusOrderByIdAsc(Long teamId, SportsTeamMember.Status status);
    List<SportsTeamMember> findByProfileIdAndStatusOrderByIdDesc(Long profileId, SportsTeamMember.Status status);
}
