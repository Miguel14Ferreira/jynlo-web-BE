package com.example.desporto24.security;

import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Admin privileges are checked in the database on each request.
 * A JWT issued before role revocation must not retain administrative access.
 */
@Component("adminCurrentRoleGuard")
@RequiredArgsConstructor
public class AdminCurrentRoleGuard {
    private final ProfileRepository profiles;

    public boolean admin(Authentication authentication) {
        return hasRole(authentication, false);
    }

    public boolean superAdmin(Authentication authentication) {
        return hasRole(authentication, true);
    }

    private boolean hasRole(Authentication authentication, boolean superOnly) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) return false;
        return profiles.findUserByUsernameIgnoreCase(authentication.getName())
                .filter(profile -> Boolean.TRUE.equals(profile.getEnabled())
                        && Boolean.TRUE.equals(profile.getNotLocked())
                        && profile.getModerationSuspendedAt() == null
                        && (superOnly ? "ROLE_SUPER_ADMIN".equals(profile.getRole())
                        : ("ROLE_ADMIN".equals(profile.getRole())
                           || "ROLE_SUPER_ADMIN".equals(profile.getRole()))))
                .isPresent();
    }
}
