package com.example.desporto24.model.dto.community;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LinkCommunityChampionshipTeamRequest(@NotNull @Positive Long championshipTeamId) {}
