package com.example.desporto24.model.dto.social;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Edits never accept media storage keys, authors, sessions or audience. */
public record EditSocialPostRequest(
        @NotNull @Size(max = 1000) String content,
        @Size(max = 160) String location
) {}
