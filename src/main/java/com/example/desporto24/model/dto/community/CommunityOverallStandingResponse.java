package com.example.desporto24.model.dto.community;

/**
 * One row of the optional "Geral" table: attendance and wins across every
 * sport of a shared season. Raw sport points are never summed, because each
 * sport scores differently and plays at a different rhythm.
 */
public record CommunityOverallStandingResponse(
        int position,
        Long profileId,
        String username,
        String fullName,
        String profilePicUrl,
        int games,
        int wins,
        int sports,
        double winRate
) {
}
