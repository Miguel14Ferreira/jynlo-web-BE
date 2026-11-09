package com.example.desporto24.model.dto.community;

import java.util.List;

public record CommunityPermanentTeamsResponse(Long communityId, List<Team> teams) {
    public record Team(Long id, String name) {}
}
