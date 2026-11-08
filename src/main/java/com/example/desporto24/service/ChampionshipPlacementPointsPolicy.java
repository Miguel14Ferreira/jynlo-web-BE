package com.example.desporto24.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates the championship award table independently from a tournament's
 * own results. No placements are inferred here.
 */
public final class ChampionshipPlacementPointsPolicy {
    private ChampionshipPlacementPointsPolicy() {}

    public static List<Integer> parse(String csv) {
        if (csv == null || csv.isBlank()) {
            throw new IllegalArgumentException("Define os pontos por posição");
        }
        String[] raw = csv.split(",", -1);
        if (raw.length < 2 || raw.length > 20) {
            throw new IllegalArgumentException("Define entre 2 e 20 posições");
        }
        List<Integer> points = new ArrayList<>(raw.length);
        int previous = 10001;
        for (String value : raw) {
            String token = value.trim();
            if (!token.matches("[0-9]{1,5}")) {
                throw new IllegalArgumentException("Pontos devem ser números inteiros não negativos");
            }
            int parsed = Integer.parseInt(token);
            if (parsed > 10000 || parsed > previous) {
                throw new IllegalArgumentException("Os pontos devem ser no máximo 10000 e decrescentes");
            }
            points.add(parsed);
            previous = parsed;
        }
        return List.copyOf(points);
    }

    public static String canonical(String csv) {
        return parse(csv).stream().map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
    }

    public static int pointsForPosition(List<Integer> points, int position) {
        if (position < 1) throw new IllegalArgumentException("Posição inválida");
        if (points == null || points.size() < 2 || points.size() > 20) {
            throw new IllegalArgumentException("Tabela de pontos inválida");
        }
        return position > points.size() ? 0 : points.get(position - 1);
    }
}
