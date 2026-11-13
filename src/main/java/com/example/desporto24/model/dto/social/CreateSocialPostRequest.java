package com.example.desporto24.model.dto.social;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSocialPostRequest(
        @NotBlank
        @Size(max = 1000)
        String content,
        Long sessionId,
        @Size(max = 160)
        String location
) {
    public CreateSocialPostRequest(String content, Long sessionId) {
        this(content, sessionId, null);
    }
    public CreateSocialPostRequest(String content) {
        this(content, null, null);
    }
}
