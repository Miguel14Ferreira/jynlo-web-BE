package com.example.desporto24.model.dto.community;

import java.util.List;

public record CommunityChampionshipTeamsResponse(
        Long championshipId, List<Team> teams, List<CompetitionTeam> competitionTeams) {
    public record Team(Long id, String name, Long permanentTeamId) {}
    public record CompetitionTeam(Long id, Long tournamentId, String tournamentName,
                                  String name, Long championshipTeamId) {}
}
