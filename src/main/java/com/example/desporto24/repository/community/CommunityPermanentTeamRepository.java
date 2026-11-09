package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityPermanentTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityPermanentTeamRepository extends JpaRepository<CommunityPermanentTeam, Long> {
    List<CommunityPermanentTeam> findByCommunityIdOrderByNameAscIdAsc(Long communityId);
    Optional<CommunityPermanentTeam> findByIdAndCommunityId(Long id, Long communityId);
    boolean existsByCommunityIdAndNormalizedName(Long communityId, String normalizedName);
}
