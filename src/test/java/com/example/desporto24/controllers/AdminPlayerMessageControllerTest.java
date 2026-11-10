package com.example.desporto24.controllers;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.Profile;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.security.SensitiveActionRateLimiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPlayerMessageControllerTest {
    @Mock ProfileRepository profiles;
    @Mock NotificationsRepository notifications;
    @Mock SensitiveActionRateLimiter rateLimiter;
    @Mock Authentication authentication;
    @InjectMocks AdminPlayerMessageController controller;

    private Profile account(long id, String username, String role) {
        Profile profile = mock(Profile.class);
        lenient().when(profile.getId()).thenReturn(id);
        lenient().when(profile.getUsername()).thenReturn(username);
        lenient().when(profile.getRole()).thenReturn(role);
        lenient().when(profile.getEnabled()).thenReturn(true);
        lenient().when(profile.getNotLocked()).thenReturn(true);
        return profile;
    }

    @Test
    void sendsOfficialNoticeToSelectedPlayerWithAuditableActor() {
        var actor = account(1L, "admin", "ROLE_ADMIN");
        var target = account(3L, "jogador", "ROLE_USER");
        when(authentication.getName()).thenReturn("admin");
        when(profiles.findUserByUsernameIgnoreCase("admin")).thenReturn(Optional.of(actor));
        when(profiles.findById(3L)).thenReturn(Optional.of(target));
        when(notifications.save(any(Notifications.class))).thenAnswer(invocation -> {
            Notifications value = invocation.getArgument(0);
            value.setId(42L);
            return value;
        });

        var result = controller.send(3L,
                new AdminPlayerMessageController.MessageRequest("  A tua sessão foi atualizada.  "),
                authentication);

        assertEquals(42L, result.notificationId());
        var captured = org.mockito.ArgumentCaptor.forClass(Notifications.class);
        verify(notifications).save(captured.capture());
        assertEquals("jogador", captured.getValue().getUsername());
        assertEquals("admin", captured.getValue().getRelatedUsername());
        assertEquals("A tua sessão foi atualizada.", captured.getValue().getMessage());
        assertEquals(NotificationType.ADMIN_MESSAGE, captured.getValue().getType());
        assertFalse(captured.getValue().isRead());
        verify(rateLimiter).requireCooldown(eq("admin-message"), eq("1"), eq(java.time.Duration.ofSeconds(15)));
    }

    @Test
    void rejectsDemotedAdminEvenWithAuthenticatedPrincipal() {
        var actor = account(1L, "admin", "ROLE_USER");
        when(authentication.getName()).thenReturn("admin");
        when(profiles.findUserByUsernameIgnoreCase("admin")).thenReturn(Optional.of(actor));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> controller.send(3L,
                        new AdminPlayerMessageController.MessageRequest("Mensagem"),
                        authentication));

        assertEquals(403, error.getStatusCode().value());
        verifyNoInteractions(notifications);
        verify(profiles, never()).findById(3L);
    }

    @Test
    void rejectsMessagesToSelfAndInactiveRecipients() {
        var actor = account(1L, "admin", "ROLE_SUPER_ADMIN");
        var target = account(3L, "suspenso", "ROLE_USER");
        when(authentication.getName()).thenReturn("admin");
        when(profiles.findUserByUsernameIgnoreCase("admin")).thenReturn(Optional.of(actor));
        when(profiles.findById(3L)).thenReturn(Optional.of(target));
        when(target.getNotLocked()).thenReturn(false);

        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> controller.send(1L,
                        new AdminPlayerMessageController.MessageRequest("Mensagem"),
                        authentication)).getStatusCode().value());
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> controller.send(3L,
                        new AdminPlayerMessageController.MessageRequest("Mensagem"),
                        authentication)).getStatusCode().value());
        verifyNoInteractions(notifications);
    }

    @Test
    void rejectsControlCharactersInAdministrativeMessage() {
        var actor = account(1L, "admin", "ROLE_ADMIN");
        var target = account(3L, "jogador", "ROLE_USER");
        when(authentication.getName()).thenReturn("admin");
        when(profiles.findUserByUsernameIgnoreCase("admin")).thenReturn(Optional.of(actor));
        when(profiles.findById(3L)).thenReturn(Optional.of(target));

        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> controller.send(3L,
                        new AdminPlayerMessageController.MessageRequest("Mensagem\\nmaliciosa".replace("\\n", "\n")),
                        authentication)).getStatusCode().value());
        verifyNoInteractions(notifications);
    }
}
