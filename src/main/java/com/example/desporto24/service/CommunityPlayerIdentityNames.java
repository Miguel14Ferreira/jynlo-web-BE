package com.example.desporto24.service;

import java.util.Locale;
import java.util.function.Predicate;

/**
 * Allocates a distinct community-player label without changing historical
 * records or attaching another person's profile to an imported identity.
 */
final class CommunityPlayerIdentityNames {
    private static final int MAX_NAME_LENGTH = 120;

    record Name(String display, String normalized) { }

    private CommunityPlayerIdentityNames() { }

    static Name allocate(String requestedDisplay, String username, Long profileId,
                         Predicate<String> occupiedNormalizedName) {
        if (profileId == null || username == null || username.isBlank()) {
            throw new IllegalArgumentException("A identidade do jogador não está completa");
        }
        String base = requestedDisplay == null || requestedDisplay.isBlank()
                ? username.trim() : requestedDisplay.trim();
        String handle = username.trim();
        String[] suffixes = {
                "",
                " (@" + handle + ")",
                " (#" + profileId + ")"
        };
        for (String suffix : suffixes) {
            // An overly long handle must not block the stable profile-ID fallback.
            if (suffix.length() >= MAX_NAME_LENGTH) {
                continue;
            }
            String candidate = shorten(base, suffix);
            String normalized = candidate.toLowerCase(Locale.ROOT);
            if (!occupiedNormalizedName.test(normalized)) {
                return new Name(candidate, normalized);
            }
        }
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.CONFLICT,
                "Não foi possível atribuir um nome único ao jogador nesta comunidade");
    }

    private static String shorten(String base, String suffix) {
        if (suffix.length() >= MAX_NAME_LENGTH) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT,
                    "Identificador demasiado longo para esta comunidade");
        }
        return base.substring(0, Math.min(base.length(), MAX_NAME_LENGTH - suffix.length())) + suffix;
    }
}
