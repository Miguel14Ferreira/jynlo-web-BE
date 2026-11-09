package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunityChampionship;
import com.example.desporto24.model.dto.community.CommunityChampionshipHonoursResponse;
import java.util.*;

/**
 * Season honours only. Individual profile IDs are stable across seasons;
 * canonical team IDs exist only inside one championship and cannot be merged.
 */
public final class CommunityChampionshipHonours {
    private CommunityChampionshipHonours() {}

    public record PermanentTeam(Long id, String name) {}

    public static CommunityChampionshipHonoursResponse calculate(
            Long communityId, List<CommunityChampionship> seasons) {
        return calculate(communityId, seasons, Map.of());
    }

    public static CommunityChampionshipHonoursResponse calculate(
            Long communityId, List<CommunityChampionship> seasons,
            Map<Long, PermanentTeam> permanentChampionByChampionship) {
        List<CommunityChampionshipHonoursResponse.Title> titles = new ArrayList<>();
        Map<Long, IndividualBuilder> individuals = new HashMap<>();
        Map<Long, TeamBuilder> teams = new HashMap<>();
        for (CommunityChampionship season : seasons) {
            if (season.getClosedAt() == null || season.getChampionParticipantId() == null
                    || season.getChampionDisplayName() == null
                    || season.getChampionDisplayName().isBlank()) continue;
            String kind = season.getChampionEntryType();
            if (!"INDIVIDUAL".equals(kind) && !"TEAM".equals(kind)) continue;
            PermanentTeam permanent = "TEAM".equals(kind)
                    ? permanentChampionByChampionship.get(season.getId()) : null;
            titles.add(new CommunityChampionshipHonoursResponse.Title(
                    season.getId(), season.getName(), season.getSeasonLabel(),
                    season.getSport(), season.getClosedAt(), kind,
                    season.getChampionParticipantId(), season.getChampionDisplayName(),
                    permanent == null ? null : permanent.id()));
            if (permanent != null) {
                var holder = teams.computeIfAbsent(permanent.id(),
                        id -> new TeamBuilder(permanent.id(), permanent.name()));
                holder.ids.add(season.getId());
            }
            if ("INDIVIDUAL".equals(kind)) {
                var holder = individuals.computeIfAbsent(season.getChampionParticipantId(),
                        id -> new IndividualBuilder(id, season.getChampionDisplayName()));
                holder.ids.add(season.getId());
            }
        }
        titles.sort(Comparator.comparing(CommunityChampionshipHonoursResponse.Title::closedAt)
                .reversed().thenComparing(CommunityChampionshipHonoursResponse.Title::championshipId));
        var leaders = individuals.values().stream().map(x ->
                        new CommunityChampionshipHonoursResponse.IndividualHonour(
                                x.id, x.name, x.ids.size(), List.copyOf(x.ids)))
                .sorted(Comparator.comparingInt(
                        CommunityChampionshipHonoursResponse.IndividualHonour::titles).reversed()
                        .thenComparing(CommunityChampionshipHonoursResponse.IndividualHonour::name)
                        .thenComparing(CommunityChampionshipHonoursResponse.IndividualHonour::profileId))
                .toList();
        var teamLeaders = teams.values().stream()
                .map(x -> new CommunityChampionshipHonoursResponse.TeamHonour(
                        x.id, x.name, x.ids.size(), List.copyOf(x.ids)))
                .sorted(Comparator.comparingInt(
                        CommunityChampionshipHonoursResponse.TeamHonour::titles).reversed()
                        .thenComparing(CommunityChampionshipHonoursResponse.TeamHonour::name)
                        .thenComparing(CommunityChampionshipHonoursResponse.TeamHonour::permanentTeamId))
                .toList();
        return new CommunityChampionshipHonoursResponse(communityId, titles.size(),
                List.copyOf(titles), leaders, teamLeaders);
    }

    private static final class TeamBuilder {
        final Long id;
        final String name;
        final List<Long> ids = new ArrayList<>();
        TeamBuilder(Long id, String name) { this.id = id; this.name = name; }
    }

    private static final class IndividualBuilder {
        final Long id;
        final String name;
        final List<Long> ids = new ArrayList<>();
        IndividualBuilder(Long id, String name) { this.id = id; this.name = name; }
    }
}
