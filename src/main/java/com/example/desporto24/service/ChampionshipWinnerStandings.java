package com.example.desporto24.service;

import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.dto.community.CommunityChampionshipResponse.ChampionshipLeader;

import java.util.*;

/**
 * Provisional winner-only championship points. Never infers runners-up or
 * invents a winner for tied/incomplete competitions.
 * Individual athletes and teams have separate identity namespaces.
 */
public final class ChampionshipWinnerStandings {
    private ChampionshipWinnerStandings() {}

    public static List<ChampionshipLeader> calculate(List<CommunityTournament> competitions) {
        Map<String, MutableLeader> leaders = new HashMap<>();
        for (CommunityTournament t : competitions) {
            if (t.getStatus() != TournamentStatus.COMPLETED || t.isChampionTied()) continue;
            int points = t.getChampionshipWinnerPoints() == null ? 10 : t.getChampionshipWinnerPoints();
            if (points < 0 || points > 10000) continue;
            boolean team = t.getEntryType() == TournamentEntryType.TEAM;
            Long id = team ? t.getChampionTeamId() : t.getChampionProfileId();
            String name = team ? t.getChampionTeamName() : t.getChampionDisplayName();
            if (id == null || name == null || name.isBlank()) continue;
            String kind = team ? "TEAM" : "INDIVIDUAL";
            String key = kind + ":" + id;
            MutableLeader leader = leaders.computeIfAbsent(key, ignored -> new MutableLeader(kind, id, name));
            leader.victories++;
            leader.points += points;
        }
        return leaders.values().stream()
                .map(l -> new ChampionshipLeader(l.kind, l.id, l.name, l.victories, l.points))
                .sorted(Comparator.comparingLong(ChampionshipLeader::points).reversed()
                        .thenComparing(Comparator.comparingInt(ChampionshipLeader::victories).reversed())
                        .thenComparing(ChampionshipLeader::entryType)
                        .thenComparing(ChampionshipLeader::name)
                        .thenComparing(ChampionshipLeader::participantId))
                .toList();
    }

    private static final class MutableLeader {
        final String kind;
        final Long id;
        final String name;
        int victories;
        long points;
        MutableLeader(String kind, Long id, String name) {
            this.kind = kind; this.id = id; this.name = name;
        }
    }
}
