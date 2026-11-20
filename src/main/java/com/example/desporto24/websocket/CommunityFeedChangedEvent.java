package com.example.desporto24.websocket;

/** Content-free notification. The client fetches the authorized community feed. */
public record CommunityFeedChangedEvent(Long communityId) {}
