package com.example.desporto24.model.dto.community;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

/** Explicit post-game report; no final score is required or inferred. */
public record CommunitySessionResultRequest(
        @NotNull Outcome outcome,
        @NotEmpty List<@Valid Player> players
) {
    public enum Outcome { A, B, DRAW }
    public record Player(@NotBlank String username, @NotNull Team team,
                         @Min(0) @Max(99) Integer goals,
                         @Min(0) @Max(99) Integer assists) {
        // Keep existing callers (and older app payloads) fully compatible.
        public Player(String username, Team team) { this(username, team, null, null); }
    }
    public enum Team { A, B }
}
