package com.example.desporto24.model.dto.social;

import java.time.Instant;

public record SocialCommentResponse(
        Long id,
        Long postId,
        String authorUsername,
        String authorDisplayName,
        String authorProfilePicUrl,
        String content,
        Instant createdAt,
        Instant editedAt,
        long likeCount,
        boolean likedByMe
) {
    public SocialCommentResponse(Long id,Long postId,String authorUsername,
        String authorDisplayName,String authorProfilePicUrl,String content,Instant createdAt) {
        this(id,postId,authorUsername,authorDisplayName,authorProfilePicUrl,
            content,createdAt,null,0,false);
    }
}
