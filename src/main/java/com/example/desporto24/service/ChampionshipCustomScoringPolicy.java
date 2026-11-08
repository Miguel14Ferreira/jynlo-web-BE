package com.example.desporto24.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure validation for future organiser-defined championship scoring.
 * Not yet connected to existing tournament result endpoints.
 */
public final class ChampionshipCustomScoringPolicy {
    public enum RankingDirection { HIGHER_IS_BETTER, LOWER_IS_BETTER }
    public enum TieBreak { SHARED_POSITION, MOST_WINS, BEST_LAST_RESULT }

    public record Rules(
            String unit,
            RankingDirection direction,
            List<Integer> pointsByPlace,
            TieBreak tieBreak
    ) {}

    private ChampionshipCustomScoringPolicy() {}

    public static Rules validate(Rules rules) {
        if (rules == null) throw new IllegalArgumentException("Regras obrigatórias");
        String unit = rules.unit() == null ? "" : rules.unit().trim();
        if (unit.isEmpty() || unit.length() > 40 || !unit.matches("[\\p{L}\\p{N} ./%_-]+")) {
            throw new IllegalArgumentException("Unidade de classificação inválida");
        }
        if (rules.direction() == null || rules.tieBreak() == null) {
            throw new IllegalArgumentException("Direção e desempate obrigatórios");
        }
        List<Integer> points = rules.pointsByPlace();
        if (points == null || points.size() < 2 || points.size() > 100) {
            throw new IllegalArgumentException("Definir entre 2 e 100 posições");
        }
        int previous = Integer.MAX_VALUE;
        for (Integer value : points) {
            if (value == null || value < 0 || value > 10000 || value > previous) {
                throw new IllegalArgumentException("Pontos devem ser válidos e não crescentes");
            }
            previous = value;
        }
        return new Rules(unit, rules.direction(), List.copyOf(points), rules.tieBreak());
    }

    public static int pointsForPlace(Rules rules, int place) {
        Rules validated = validate(rules);
        if (place < 1) throw new IllegalArgumentException("Posição inválida");
        return place <= validated.pointsByPlace().size()
                ? validated.pointsByPlace().get(place - 1) : 0;
    }
}
