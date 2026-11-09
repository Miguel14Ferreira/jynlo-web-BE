package com.example.desporto24.model.dto.community;

import java.time.Instant;
import java.util.List;

/** Titles from finished seasons only; no team identity is inferred from names. */
public record CommunityChampionshipHonoursResponse(
        Long communityId, int completedSeasons, List<Title> titles,
        List<IndividualHonour> individualHonours,
        List<TeamHonour> teamHonours) {
    public record Title(Long championshipId, String championshipName, String seasonLabel,
                        String sport, Instant closedAt, String entryType,
                        Long participantId, String championName, Long permanentTeamId) {}
    public record TeamHonour(Long permanentTeamId, String name, int titles,
                             List<Long> championshipIds) {}
    public record IndividualHonour(Long profileId, String name, int titles,
                                   List<Long> championshipIds) {}
}
