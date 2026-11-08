package com.example.desporto24.model.dto.community;

import java.util.List;

/** Confirmed placements for safe individual leagues; other formats use winner snapshots only. */
public record CommunityChampionshipStandingsResponse(
        Long championshipId, List<Standing> standings,
        int fullyScoredCompetitions, int winnerOnlyCompetitions,
        int pendingCompetitions) {
    public record Standing(String entryType, Long participantId, String name,
                           long points, int victories, int awardedCompetitions) {}
}
