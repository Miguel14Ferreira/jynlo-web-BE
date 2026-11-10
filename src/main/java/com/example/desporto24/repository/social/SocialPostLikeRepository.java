package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.SocialPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface SocialPostLikeRepository extends JpaRepository<SocialPostLike, Long> {

    boolean existsByPostIdAndProfileId(Long postId, Long profileId);

    long countByPostId(Long postId);

    long deleteByPostIdAndProfileId(Long postId, Long profileId);

    long deleteByPostId(Long postId);

    @Query("select l.post.id, count(l.id) from SocialPostLike l where l.post.id in :postIds group by l.post.id")
    List<Object[]> countByPostIds(@Param("postIds") Set<Long> postIds);

    @Query("select l.post.id from SocialPostLike l where l.post.id in :postIds and l.profile.id = :profileId")
    Set<Long> findLikedPostIds(@Param("postIds") Set<Long> postIds, @Param("profileId") Long profileId);

    @Query("select l from SocialPostLike l join fetch l.profile where l.post.id = :postId order by l.createdAt desc")
    List<com.example.desporto24.model.social.SocialPostLike> findLikers(@Param("postId") Long postId, org.springframework.data.domain.Pageable pageable);

    long deleteByProfileId(Long profileId);
}
