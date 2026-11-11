package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.CommunityChampionshipGoal;
import com.example.desporto24.model.community.CommunityChampionshipDisciplinaryCard;
import java.util.*;

/** Counts only recorded events, never infers appearances from team registration.
 * This is a provisional league-event summary, not an official season leaderboard.
 */
public final class ChampionshipPlayerEventStatistics {
    private ChampionshipPlayerEventStatistics() {}

    public record Row(Long profileId, String playerName, Long teamId, String teamName,
                      int goals, int assists, int yellowCards, int redCards) {}

    private record Key(Long profileId, Long teamId) {}
    private static final class Count {
        Profile player;
        Long teamId;
        String teamName;
        int goals, assists, yellows, reds;
        Count(Profile player, Long teamId, String teamName) {
            this.player = player; this.teamId = teamId; this.teamName = teamName;
        }
        Row toRow() {
            String name = player.getFullName();
            if (name == null || name.isBlank()) name = player.getUsername();
            return new Row(player.getId(), name, teamId, teamName, goals, assists, yellows, reds);
        }
    }

    public static List<Row> calculate(List<CommunityChampionshipGoal> goals,
                                      List<CommunityChampionshipDisciplinaryCard> cards) {
        Map<Key, Count> counts = new HashMap<>();
        for (var goal : goals) {
            var team = goal.getTeam();
            var scorer = goal.getScorer();
            counts.computeIfAbsent(new Key(scorer.getId(), team.getId()),
                    k -> new Count(scorer, team.getId(), team.getName())).goals++;
            var assister = goal.getAssister();
            if (assister != null) {
                counts.computeIfAbsent(new Key(assister.getId(), team.getId()),
                        k -> new Count(assister, team.getId(), team.getName())).assists++;
            }
        }
        for (var card : cards) {
            var team = card.getTeam();
            var player = card.getPlayer();
            var count = counts.computeIfAbsent(new Key(player.getId(), team.getId()),
                    k -> new Count(player, team.getId(), team.getName()));
            if ("YELLOW".equals(card.getCardType())) count.yellows++;
            else if ("RED".equals(card.getCardType())) count.reds++;
        }
        return counts.values().stream().map(Count::toRow)
                .sorted(Comparator.comparingInt(Row::goals).reversed()
                        .thenComparing(Comparator.comparingInt(Row::assists).reversed())
                        .thenComparing(Row::playerName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Row::profileId))
                .toList();
    }
}
