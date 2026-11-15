package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamCommunityMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SportsTeamCommunityMembershipRepository extends JpaRepository<SportsTeamCommunityMembership,Long> {
    Optional<SportsTeamCommunityMembership> findByTeamIdAndCommunityId(Long teamId,Long communityId);
    List<SportsTeamCommunityMembership> findByCommunityIdOrderByIdAsc(Long communityId);
    List<SportsTeamCommunityMembership> findByTeamIdOrderByIdDesc(Long teamId);
}
