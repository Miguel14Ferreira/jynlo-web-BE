package com.example.desporto24.websocket;

/** A former member must revalidate private chat access after their membership is removed. */
public record TeamMembershipRevokedEvent(Long teamId, String username) {}
