package com.example.desporto24.model.dto.community;

public record CommunityCompetitionSettingsResponse(
        boolean rankingsEnabled,
        int winPoints,
        int drawPoints,
        int lossPoints,
        boolean overallStandingsEnabled
) {
    public CommunityCompetitionSettingsResponse(boolean rankingsEnabled, int winPoints,
            int drawPoints, int lossPoints) {
        this(rankingsEnabled, winPoints, drawPoints, lossPoints, false);
    }
}
