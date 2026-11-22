package com.example.desporto24.service;

import com.example.desporto24.model.dto.community.CommunityOverallStandingResponse;
import com.example.desporto24.model.dto.community.CommunityRaceStandingGroupResponse;
import com.example.desporto24.model.dto.community.CommunityRacketStandingGroupResponse;
import com.example.desporto24.model.dto.community.CommunitySportStandingGroupResponse;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * "Geral": one table across every sport of a shared season. Sports score
 * differently (3 points a football win, sets in padel, places in a race), so
 * their points are never added together. The table counts what is comparable
 * across sports: games played and games won. Ranked by wins, then games.
 */
public final class CommunityOverallStandings {
    private CommunityOverallStandings() {}

    private static final class Row {
        Long profileId; String username; String fullName; String picture;
        int games; int wins; final Set<String> sports = new HashSet<>();
    }

    public static List<CommunityOverallStandingResponse> calculate(
            List<CommunitySportStandingGroupResponse> sportGroups,
            List<CommunityRacketStandingGroupResponse> racketGroups,
            List<CommunityRaceStandingGroupResponse> raceGroups) {
        Map<String, Row> rows = new LinkedHashMap<>();
        Set<String> sportsWithResults = new HashSet<>();
        if (sportGroups != null) for (var group : sportGroups) {
            if (group == null || group.standings() == null) continue;
            for (var s : group.standings()) {
                if (s.games() <= 0) continue;
                add(rows, sportsWithResults, group.sport(), s.profileId(), s.username(),
                        s.fullName(), s.profilePicUrl(), s.games(), s.wins());
            }
        }
        if (racketGroups != null) for (var group : racketGroups) {
            if (group == null || group.standings() == null) continue;
            for (var s : group.standings()) {
                if (s.matches() <= 0) continue;
                add(rows, sportsWithResults, group.sport(), s.profileId(), s.username(),
                        s.fullName(), s.profilePicUrl(), s.matches(), s.wins());
            }
        }
        if (raceGroups != null) for (var group : raceGroups) {
            if (group == null || group.standings() == null) continue;
            for (var s : group.standings()) {
                if (s.races() <= 0) continue;
                add(rows, sportsWithResults, group.sport(), s.profileId(), s.username(),
                        s.fullName(), s.profilePicUrl(), s.races(), s.wins());
            }
        }
        // A single sport already has its own table: "Geral" would only repeat it.
        if (sportsWithResults.size() < 2) return List.of();

        List<Row> sorted = new ArrayList<>(rows.values());
        sorted.sort(Comparator.comparingInt((Row r) -> r.wins).reversed()
                .thenComparing(Comparator.comparingInt((Row r) -> r.games).reversed())
                .thenComparing(r -> label(r).toLowerCase(Locale.ROOT)));
        List<CommunityOverallStandingResponse> out = new ArrayList<>();
        int position = 0;
        Row previous = null;
        for (int i = 0; i < sorted.size(); i++) {
            Row r = sorted.get(i);
            if (previous == null || previous.wins != r.wins || previous.games != r.games) position = i + 1;
            double winRate = r.games == 0 ? 0 : Math.round(r.wins * 1000.0 / r.games) / 10.0;
            out.add(new CommunityOverallStandingResponse(position, r.profileId, r.username,
                    r.fullName, r.picture, r.games, r.wins, r.sports.size(), winRate));
            previous = r;
        }
        return out;
    }

    private static void add(Map<String, Row> rows, Set<String> sportsWithResults, String sport,
                            Long profileId, String username, String fullName, String picture,
                            int games, int wins) {
        String key = profileId != null ? "p:" + profileId
                : username != null && !username.isBlank() ? "u:" + username.toLowerCase(Locale.ROOT)
                : fullName != null && !fullName.isBlank() ? "n:" + normalize(fullName) : null;
        if (key == null) return;
        Row row = rows.computeIfAbsent(key, k -> new Row());
        if (row.profileId == null) row.profileId = profileId;
        if (row.username == null) row.username = username;
        if (row.fullName == null) row.fullName = fullName;
        if (row.picture == null) row.picture = picture;
        row.games += games;
        row.wins += wins;
        String normalizedSport = sport == null ? "" : normalize(sport);
        row.sports.add(normalizedSport);
        sportsWithResults.add(normalizedSport);
    }

    private static String label(Row r) {
        if (r.fullName != null && !r.fullName.isBlank()) return r.fullName;
        return r.username == null ? "" : r.username;
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }
}
