package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommunityFollowRepository extends JpaRepository<CommunityFollow,Long> {
    @Query("""
      select f.community.id from CommunityFollow f
      where f.follower.id = :profileId
        and f.community.visibility = com.example.desporto24.enumeration.CommunityVisibility.PUBLIC
    """)
    List<Long> followedPublicCommunityIds(@Param("profileId") Long profileId);

    boolean existsByCommunityIdAndFollowerId(Long communityId,Long profileId);
    long countByCommunityId(Long communityId);
    long deleteByCommunityIdAndFollowerId(Long communityId,Long profileId);
}
