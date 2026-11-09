package com.example.desporto24.service;

import com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse;
import com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse.Standing;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Rejects ambiguous titles instead of treating a display sort as a sporting tie-break. */
public final class ChampionshipFinalizationPolicy {
    private ChampionshipFinalizationPolicy() {}

    public static Standing champion(CommunityChampionshipStandingsResponse result) {
        if (result.pendingCompetitions() != 0 || result.standings().isEmpty()
                || result.fullyScoredCompetitions() + result.winnerOnlyCompetitions() == 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Existem provas pendentes ou sem resultados");
        if (result.standings().stream().anyMatch(row -> "TEAM_UNLINKED".equals(row.entryType())))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Associa todas as equipas pontuadas antes de fechar o campeonato");
        // A final sporting decision cannot depend on the incoming display order.
        Standing first = result.standings().stream()
                .max(java.util.Comparator.comparingLong(Standing::points)
                        .thenComparingInt(Standing::victories))
                .orElseThrow();
        if (result.standings().stream().anyMatch(row -> !row.entryType().equals(first.entryType())))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível atribuir o mesmo título a equipas e atletas individuais");
        if (result.standings().stream().anyMatch(row -> row != first
                && row.points() == first.points() && row.victories() == first.victories()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Empate no primeiro lugar: é necessária uma decisão desportiva");
        return first;
    }
}
