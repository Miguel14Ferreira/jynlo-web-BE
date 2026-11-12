package com.example.desporto24.model.dto.social;

import java.time.Instant;

public record FeedItemResponse(
        String type,
        Long postId,
        String authorUsername,
        String authorDisplayName,
        String authorProfilePicUrl,
        String content,
        String imageUrl,
        Instant createdAt,
        Long sessionId,
        String sport,
        String city,
        Instant gameDate,
        long likeCount,
        boolean likedByMe,
        long commentCount,
        String sessionImageUrl,
        String videoUrl,
        boolean hideLikers,
        java.util.List<PublicPostMediaResponse> extraMedia
) {
    public FeedItemResponse(String type, Long postId, String authorUsername,
            String authorDisplayName, String authorProfilePicUrl, String content, String imageUrl,
            java.time.Instant createdAt, Long sessionId, String sport, String city,
            java.time.Instant gameDate, long likeCount, boolean likedByMe, long commentCount,
            String sessionImageUrl, String videoUrl, boolean hideLikers) {
        this(type, postId, authorUsername, authorDisplayName, authorProfilePicUrl, content,
                imageUrl, createdAt, sessionId, sport, city, gameDate, likeCount, likedByMe,
                commentCount, sessionImageUrl, videoUrl, hideLikers, java.util.List.of());
    }
}
