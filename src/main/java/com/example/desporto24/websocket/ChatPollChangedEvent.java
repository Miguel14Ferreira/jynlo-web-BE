package com.example.desporto24.websocket;

import java.util.List;
import java.util.Set;

/** Emitted after a committed vote. Counts only: never who voted for what. */
public record ChatPollChangedEvent(String kind, String conversationId, Long messageId,
                                   List<Long> counts, long total, Set<String> recipients, Object voters) {
    public ChatPollChangedEvent(String kind, String conversationId, Long messageId,
                                List<Long> counts, long total, Set<String> recipients) {
        this(kind, conversationId, messageId, counts, total, recipients, null);
    }
}
