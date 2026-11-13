package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.SocialPostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface SocialPostCommentLikeRepository extends JpaRepository<SocialPostCommentLike,Long> {
    boolean existsByCommentIdAndProfileId(Long commentId, Long profileId);
    long countByCommentId(Long commentId);
    long deleteByCommentIdAndProfileId(Long commentId, Long profileId);
    long deleteByCommentId(Long commentId);
    @Query("SELECT l.comment.id, count(l.id) FROM SocialPostCommentLike l WHERE l.comment.id IN :ids GROUP BY l.comment.id")
    List<Object[]> countsByCommentIds(@Param("ids") Set<Long> ids);
    @Query("SELECT l.comment.id FROM SocialPostCommentLike l WHERE l.comment.id IN :ids AND l.profile.id=:viewer")
    Set<Long> likedCommentIds(@Param("ids") Set<Long> ids,@Param("viewer") Long viewer);
}
