package com.example.desporto24.model.dto.community;

import java.time.Instant;
import java.util.List;

public record CommunityChampionshipResponse(
        Long id, Long communityId, String name, String sport,
        String seasonLabel, Instant createdAt, List<ChampionshipCompetition> competitions, List<ChampionshipLeader> winnerStandings,
        Instant closedAt, String championEntryType, Long championParticipantId,
        String championDisplayName, String standingsMode) {
    /** Compatibility constructor for tests and historical snapshots. */
    public CommunityChampionshipResponse(Long id, Long communityId, String name, String sport,
            String seasonLabel, Instant createdAt, List<ChampionshipCompetition> competitions,
            List<ChampionshipLeader> winnerStandings, Instant closedAt, String championEntryType,
            Long championParticipantId, String championDisplayName) {
        this(id, communityId, name, sport, seasonLabel, createdAt, competitions,
                winnerStandings, closedAt, championEntryType, championParticipantId,
                championDisplayName, "AGGREGATE");
    }

    public record ChampionshipCompetition(Long id, String name, String format, String status,
            int winnerPoints, boolean winnerConfirmed, String pointsByPlace) {}
    public record ChampionshipLeader(String entryType, Long participantId, String name,
            int victories, long points) {}
}
