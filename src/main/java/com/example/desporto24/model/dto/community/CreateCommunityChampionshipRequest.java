package com.example.desporto24.model.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommunityChampionshipRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 100) String sport,
        @NotBlank @Size(max = 50) String seasonLabel) {}
