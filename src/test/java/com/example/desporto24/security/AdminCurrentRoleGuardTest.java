package com.example.desporto24.security;

import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminCurrentRoleGuardTest {
    private final ProfileRepository repository = mock(ProfileRepository.class);
    private final AdminCurrentRoleGuard guard = new AdminCurrentRoleGuard(repository);

    @Test
    void rejectsDemotedAdminDespiteOldJwtRole() {
        Profile actor = eligible("ROLE_USER");
        when(repository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(actor));
        var authentication = new UsernamePasswordAuthenticationToken(
                "ana", "jwt", org.springframework.security.core.authority.AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        assertFalse(guard.admin(authentication));
        assertFalse(guard.superAdmin(authentication));
    }

    @Test
    void deniesLockedSuspendedAndDisabledAccounts() {
        var auth = new UsernamePasswordAuthenticationToken("ana", "jwt", java.util.List.of());
        Profile actor = eligible("ROLE_ADMIN");
        when(repository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(actor));
        actor.setNotLocked(false);
        assertFalse(guard.admin(auth));
        actor.setNotLocked(true);
        actor.setEnabled(false);
        assertFalse(guard.admin(auth));
    }

    @Test
    void checksSuperAdminSeparatelyAndRejectsMissingAccount() {
        var auth = new UsernamePasswordAuthenticationToken("ana", "jwt", java.util.List.of());
        when(repository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.empty());
        assertFalse(guard.admin(auth));
        Profile actor = eligible("ROLE_ADMIN");
        when(repository.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(actor));
        assertTrue(guard.admin(auth));
        assertFalse(guard.superAdmin(auth));
        actor.setRole("ROLE_SUPER_ADMIN");
        assertTrue(guard.superAdmin(auth));
    }

    private static Profile eligible(String role) {
        var p = new Profile();
        p.setUsername("ana");
        p.setRole(role);
        p.setEnabled(true);
        p.setNotLocked(true);
        return p;
    }
}
