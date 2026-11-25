package com.example.desporto24.model.dto.chat;

import java.time.Instant;

/**
 * One conversation in the inbox. {@code imageUrl} is the peer's profile photo,
 * the community or group photo, or the team logo (null when there is none).
 */
public record InboxSummaryResponse(
        String kind,
        String id,
        Long latestMessageId,
        String latestText,
        String latestSender,
        Instant latestAt,
        long unreadCount,
        String imageUrl) {

    public InboxSummaryResponse(String kind, String id, Long latestMessageId, String latestText,
                                String latestSender, Instant latestAt, long unreadCount) {
        this(kind, id, latestMessageId, latestText, latestSender, latestAt, unreadCount, null);
    }

    public InboxSummaryResponse withImageUrl(String url) {
        return new InboxSummaryResponse(kind, id, latestMessageId, latestText, latestSender, latestAt,
                unreadCount, url == null || url.isBlank() ? null : url);
    }
}
