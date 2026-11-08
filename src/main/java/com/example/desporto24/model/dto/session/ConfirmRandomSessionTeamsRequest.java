package com.example.desporto24.model.dto.session;

import java.util.List;

/** Explicit roster assignment selected in a RANDOM preview. */
public record ConfirmRandomSessionTeamsRequest(List<String> teamA, List<String> teamB) {
}
