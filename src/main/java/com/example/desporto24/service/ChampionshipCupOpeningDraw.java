package com.example.desporto24.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** First-round cup draw; future rounds require certified winners. */
public final class ChampionshipCupOpeningDraw {
    private ChampionshipCupOpeningDraw() {}

    public record Pairing(int matchNumber, long firstTeamId, long secondTeamId) {}
    public record Bye(int slotNumber, long teamId) {}
    public record Draw(List<Pairing> matches, List<Bye> byes) {}

    public static Draw generate(List<Long> teamIds, Random random) {
        if (teamIds == null || random == null || teamIds.size() < 2 || teamIds.size() > 64
                || teamIds.stream().anyMatch(id -> id == null || id <= 0)
                || teamIds.stream().distinct().count() != teamIds.size()) {
            throw new IllegalArgumentException("Uma taça requer entre 2 e 64 equipas distintas");
        }
        List<Long> shuffled = new ArrayList<>(teamIds.stream().sorted().toList());
        Collections.shuffle(shuffled, random);
        int bracketSize = 1;
        while (bracketSize < shuffled.size()) bracketSize *= 2;
        int byes = bracketSize - shuffled.size();
        List<Bye> qualifiers = new ArrayList<>(byes);
        for (int i = 0; i < byes; i++)
            qualifiers.add(new Bye(i + 1, shuffled.get(i)));
        List<Pairing> fixtures = new ArrayList<>();
        for (int i = byes; i < shuffled.size(); i += 2)
            fixtures.add(new Pairing(fixtures.size() + 1, shuffled.get(i), shuffled.get(i + 1)));
        return new Draw(List.copyOf(fixtures), List.copyOf(qualifiers));
    }
}
