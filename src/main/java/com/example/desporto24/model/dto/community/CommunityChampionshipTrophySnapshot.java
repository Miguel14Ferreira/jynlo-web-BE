package com.example.desporto24.model.dto.community;

/** Immutable winner recorded when the enclosing championship is closed. */
public record CommunityChampionshipTrophySnapshot(
        Long tournamentId, String tournamentName, String format,
        String entryType, Long winnerProfileId, Long winnerSeasonTeamId,
        String winnerName) {}
