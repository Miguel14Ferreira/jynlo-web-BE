package com.example.desporto24.model.dto.community;

import java.time.Instant;

/**
 * Attachment metadata is deliberately URL-free: authorized members download
 * through /api/communities/{communityId}/messages/attachments/{messageId}.
 */
public record CommunityTimelineEntryResponse(
        Long id, Long communityId, String kind, String username, String body, Instant createdAt,
        Long replyToMessageId, String replyToSender, String replyToText,
        String imageUrl, String videoUrl,
        String attachmentName, String attachmentMimeType, Long attachmentSize,
        Instant editedAt, Instant deletedAt,
        java.util.List<com.example.desporto24.model.dto.social.PublicPostMediaResponse> extraMedia,
        java.util.List<com.example.desporto24.service.PrivateChatAttachmentBatch.PublicMetadata> attachments) {
    public CommunityTimelineEntryResponse(
            Long id, Long communityId, String kind, String username, String body, Instant createdAt,
            Long replyToMessageId, String replyToSender, String replyToText,
            String imageUrl, String videoUrl, String attachmentName, String attachmentMimeType,
            Long attachmentSize, Instant editedAt, Instant deletedAt,
            java.util.List<com.example.desporto24.model.dto.social.PublicPostMediaResponse> extraMedia) {
        this(id, communityId, kind, username, body, createdAt, replyToMessageId, replyToSender,
                replyToText, imageUrl, videoUrl, attachmentName, attachmentMimeType,
                attachmentSize, editedAt, deletedAt, extraMedia, java.util.List.of());
    }
    public CommunityTimelineEntryResponse(
            Long id, Long communityId, String kind, String username, String body, Instant createdAt,
            Long replyToMessageId, String replyToSender, String replyToText,
            String imageUrl, String videoUrl, String attachmentName,
            String attachmentMimeType, Long attachmentSize, Instant editedAt, Instant deletedAt) {
        this(id, communityId, kind, username, body, createdAt, replyToMessageId, replyToSender,
                replyToText, imageUrl, videoUrl, attachmentName, attachmentMimeType,
                attachmentSize, editedAt, deletedAt, java.util.List.of());
    }
    public CommunityTimelineEntryResponse(
            Long id, Long communityId, String kind, String username, String body, Instant createdAt,
            Long replyToMessageId, String replyToSender, String replyToText,
            String imageUrl, String videoUrl,
            String attachmentName, String attachmentMimeType, Long attachmentSize) {
        this(id, communityId, kind, username, body, createdAt,
                replyToMessageId, replyToSender, replyToText,
                imageUrl, videoUrl, attachmentName, attachmentMimeType, attachmentSize, null, null);
    }
    public CommunityTimelineEntryResponse(Long id, Long communityId, String kind,
                                          String username, String body, Instant createdAt,
                                          Long replyToMessageId, String replyToSender, String replyToText,
                                          String imageUrl, String videoUrl) {
        this(id, communityId, kind, username, body, createdAt,
                replyToMessageId, replyToSender, replyToText, imageUrl, videoUrl,
                null, null, null);
    }

    public CommunityTimelineEntryResponse(Long id, Long communityId, String kind,
                                          String username, String body, Instant createdAt,
                                          Long replyToMessageId, String replyToSender, String replyToText) {
        this(id, communityId, kind, username, body, createdAt,
                replyToMessageId, replyToSender, replyToText, null, null, null, null, null);
    }

    public CommunityTimelineEntryResponse(Long id, Long communityId, String kind,
                                          String username, String body, Instant createdAt) {
        this(id, communityId, kind, username, body, createdAt,
                null, null, null, null, null, null, null, null);
    }
}
