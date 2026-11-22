package com.example.desporto24.model.dto.community;

import jakarta.validation.constraints.Min;

public record UpdateCommunityCompetitionSettingsRequest(
        Boolean rankingsEnabled,
        @Min(0) Integer winPoints,
        @Min(0) Integer drawPoints,
        @Min(0) Integer lossPoints,
        Boolean overallStandingsEnabled
) {
    public UpdateCommunityCompetitionSettingsRequest(Boolean rankingsEnabled,
            Integer winPoints, Integer drawPoints, Integer lossPoints) {
        this(rankingsEnabled, winPoints, drawPoints, lossPoints, null);
    }
}
