package com.example.desporto24.model.dto.community;

/** Immutable winner recorded when the enclosing championship is closed. */
public record CommunityChampionshipTrophySnapshot(
        Long tournamentId, String tournamentName, String format,
        String entryType, Long winnerProfileId, Long winnerSeasonTeamId,
        String winnerName, Long ownedEventId) {
    /** Existing snapshots from linked standalone tournaments remain readable. */
    public CommunityChampionshipTrophySnapshot(Long tournamentId, String tournamentName,
            String format, String entryType, Long winnerProfileId,
            Long winnerSeasonTeamId, String winnerName) {
        this(tournamentId, tournamentName, format, entryType, winnerProfileId,
                winnerSeasonTeamId, winnerName, null);
    }
}
