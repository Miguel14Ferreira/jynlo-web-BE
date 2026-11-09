package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunityChampionship;
import com.example.desporto24.model.dto.community.CommunityChampionshipHonoursResponse;
import com.example.desporto24.model.dto.community.CommunityChampionshipTrophySnapshot;
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
        return calculate(communityId, seasons, Map.of(), Map.of(), Map.of());
    }

    public static CommunityChampionshipHonoursResponse calculate(
            Long communityId, List<CommunityChampionship> seasons,
            Map<Long, PermanentTeam> permanentChampionByChampionship) {
        return calculate(communityId, seasons, permanentChampionByChampionship, Map.of(), Map.of());
    }

    public static CommunityChampionshipHonoursResponse calculate(
            Long communityId, List<CommunityChampionship> seasons,
            Map<Long, PermanentTeam> permanentChampionByChampionship,
            Map<Long, PermanentTeam> permanentTeamBySeasonTeam,
            Map<Long, List<CommunityChampionshipTrophySnapshot>> trophiesByChampionship) {
        List<CommunityChampionshipHonoursResponse.CompetitionTitle> competitionTitles = new ArrayList<>();
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
            for (CommunityChampionshipTrophySnapshot trophy :
                    trophiesByChampionship.getOrDefault(season.getId(), List.of())) {
                if (!"LEAGUE".equals(trophy.format())
                        && !"KNOCKOUT".equals(trophy.format())
                        && !"GROUP_KNOCKOUT".equals(trophy.format())) continue;
                PermanentTeam linked = trophy.winnerSeasonTeamId() == null
                        ? null : permanentTeamBySeasonTeam.get(trophy.winnerSeasonTeamId());
                competitionTitles.add(new CommunityChampionshipHonoursResponse.CompetitionTitle(
                        season.getId(), trophy.tournamentId(), trophy.tournamentName(),
                        season.getSeasonLabel(), season.getSport(), trophy.format(),
                        trophy.entryType(), trophy.winnerName(),
                        linked == null ? null : linked.id()));
                if ("TEAM".equals(trophy.entryType()) && linked != null) {
                    var holder = teams.computeIfAbsent(linked.id(),
                            id -> new TeamBuilder(linked.id(), linked.name()));
                    if ("LEAGUE".equals(trophy.format())) holder.leagues++;
                    else holder.cups++;
                }
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
                                x.id, x.name, x.ids.size(), List.copyOf(x.ids), x.leagues, x.cups))
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
                List.copyOf(titles), leaders, teamLeaders, List.copyOf(competitionTitles));
    }

    private static final class TeamBuilder {
        final Long id;
        final String name;
        final List<Long> ids = new ArrayList<>();
        int leagues;
        int cups;
        TeamBuilder(Long id, String name) { this.id = id; this.name = name; }
    }

    private static final class IndividualBuilder {
        final Long id;
        final String name;
        final List<Long> ids = new ArrayList<>();
        IndividualBuilder(Long id, String name) { this.id = id; this.name = name; }
    }
}
