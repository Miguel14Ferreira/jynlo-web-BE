package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.HiddenFeedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HiddenFeedPostRepository extends JpaRepository<HiddenFeedPost, Long> {
    boolean existsByViewerIdAndPostId(Long viewerId, Long postId);
    List<HiddenFeedPost> findByViewerIdAndPostIdIn(Long viewerId, java.util.Collection<Long> postIds);
    long deleteByViewerIdAndPostId(Long viewerId, Long postId);
    long deleteByPostId(Long postId);
}
