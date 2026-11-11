package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.SocialStoryLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SocialStoryLikeRepository extends JpaRepository<SocialStoryLike, Long> {
    boolean existsByStoryIdAndProfileId(Long storyId, Long profileId);
    long countByStoryId(Long storyId);
    long deleteByStoryIdAndProfileId(Long storyId, Long profileId);

    @Query("select l.story.id, count(l.id) from SocialStoryLike l where l.story.id in :ids group by l.story.id")
    List<Object[]> countByStoryIds(@Param("ids") Collection<Long> ids);

    @Query("select l.story.id from SocialStoryLike l where l.story.id in :ids and l.profile.id = :profileId")
    Set<Long> likedStoryIds(@Param("ids") Collection<Long> ids, @Param("profileId") Long profileId);
}
