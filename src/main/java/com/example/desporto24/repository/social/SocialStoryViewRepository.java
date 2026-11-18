package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.SocialStoryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SocialStoryViewRepository extends JpaRepository<SocialStoryView,Long> {
    @Modifying
    @Query(value = """
      INSERT INTO social_story_view(story_id,profile_id,viewed_at)
      VALUES (:storyId,:profileId,CURRENT_TIMESTAMP)
      ON CONFLICT (story_id,profile_id) DO NOTHING
      """, nativeQuery=true)
    int recordUniqueView(@Param("storyId") Long storyId,@Param("profileId") Long profileId);

    long countByStoryId(Long storyId);
    @Query("""
       select v from SocialStoryView v
       join fetch v.profile
       where v.story.id = :storyId
       order by v.viewedAt desc,v.id desc
       """)
    List<SocialStoryView> recentViewers(@Param("storyId") Long storyId, Pageable page);
}
