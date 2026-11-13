package com.example.desporto24.model.dto.social;

import java.time.Instant;

public record StoryResponse(
        Long id, String authorUsername, String displayName, String avatarUrl,
        Long communityId, String communityName, String communityPhotoUrl,
        String caption, String mediaUrl, boolean video,
        Instant createdAt, Instant expiresAt, boolean canDelete,
        long likeCount, boolean likedByMe,
        String textPosition, String textScale, String textStyle,
        String altText, boolean showTime) {
    public StoryResponse(Long id, String authorUsername, String displayName, String avatarUrl,
            Long communityId, String communityName, String communityPhotoUrl,
            String caption, String mediaUrl, boolean video,
            Instant createdAt, Instant expiresAt, boolean canDelete,
            long likeCount, boolean likedByMe,
            String textPosition, String textScale, String textStyle) {
        this(id, authorUsername, displayName, avatarUrl, communityId, communityName,
             communityPhotoUrl, caption, mediaUrl, video, createdAt, expiresAt,
             canDelete, likeCount, likedByMe, textPosition, textScale, textStyle, null, false);
    }
    // Compatibility for older constructor call sites and mobile clients.
    public StoryResponse(Long id, String authorUsername, String displayName, String avatarUrl,
            Long communityId, String communityName, String communityPhotoUrl,
            String caption, String mediaUrl, boolean video,
            Instant createdAt, Instant expiresAt, boolean canDelete,
            long likeCount, boolean likedByMe) {
        this(id, authorUsername, displayName, avatarUrl, communityId, communityName,
             communityPhotoUrl, caption, mediaUrl, video, createdAt, expiresAt, canDelete,
             likeCount, likedByMe, "bottom", "medium", "pill");
    }
}
