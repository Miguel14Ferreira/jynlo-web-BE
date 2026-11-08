package com.example.desporto24.model.dto.community;

import java.time.Instant;
import java.util.List;

public record CommunityChampionshipResponse(
        Long id, Long communityId, String name, String sport,
        String seasonLabel, Instant createdAt, List<ChampionshipCompetition> competitions) {
    public record ChampionshipCompetition(Long id, String name, String format, String status) {}
}
