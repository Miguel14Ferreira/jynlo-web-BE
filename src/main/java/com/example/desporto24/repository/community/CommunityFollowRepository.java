package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityFollowRepository extends JpaRepository<CommunityFollow,Long> {
    boolean existsByCommunityIdAndFollowerId(Long communityId,Long profileId);
    long countByCommunityId(Long communityId);
    long deleteByCommunityIdAndFollowerId(Long communityId,Long profileId);
}
