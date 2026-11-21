package com.example.desporto24.websocket;

import java.util.List;
import java.util.Set;

/** Emitted after a committed vote. Counts only: never who voted for what. */
public record ChatPollChangedEvent(String kind, String conversationId, Long messageId,
                                   List<Long> counts, long total, Set<String> recipients) {}
