package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.SocialStoryLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialStoryLikeRepository extends JpaRepository<SocialStoryLike, Long> {
    boolean existsByStoryIdAndProfileId(Long storyId, Long profileId);
    long countByStoryId(Long storyId);
    long deleteByStoryIdAndProfileId(Long storyId, Long profileId);
}
