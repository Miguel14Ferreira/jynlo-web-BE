package com.example.desporto24.service;

import com.example.desporto24.model.EmailChangeRequest;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.EmailChangeRequestRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.security.PasswordHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EmailChangeServiceTest {
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final EmailChangeRequestRepository requests = mock(EmailChangeRequestRepository.class);
    private final PasswordHashService passwords = mock(PasswordHashService.class);
    private final EmailService email = mock(EmailService.class);
    private final EmailChangeService service = new EmailChangeService(profiles, requests, passwords, email);
    private final Profile profile = new Profile();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "uiAppUrl", "https://jynlo.test/");
        profile.setId(1L);
        profile.setUsername("miguel");
        profile.setEmail("old@example.com");
        when(profiles.findUserByUsername("miguel")).thenReturn(Optional.of(profile));
        when(profiles.findUserByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void wrongOrMissingPasswordChangesNothing() {
        when(passwords.matchesAndUpgrade(profile, "errada")).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> service.request("miguel", "new@example.com", "errada"));
        assertThrows(BadCredentialsException.class, () -> service.request("miguel", "new@example.com", ""));
        verifyNoInteractions(email);
        verify(requests, never()).save(any());
        assertEquals("old@example.com", profile.getEmail());
    }

    @Test
    void emailOnlyChangesAfterTheLinkSentToTheNewAddress() {
        when(passwords.matchesAndUpgrade(profile, "certa")).thenReturn(true);
        service.request("miguel", " New@Example.com ", "certa");
        assertEquals("old@example.com", profile.getEmail());

        ArgumentCaptor<String> html = ArgumentCaptor.forClass(String.class);
        verify(email).send(eq("new@example.com"), contains("Confirma"), html.capture());
        verify(email).send(eq("old@example.com"), contains("Pedido"), anyString());
        Matcher link = Pattern.compile("/login/confirm-email/([A-Za-z0-9_-]{40,})").matcher(html.getValue());
        assertTrue(link.find());
        String token = link.group(1);

        ArgumentCaptor<EmailChangeRequest> saved = ArgumentCaptor.forClass(EmailChangeRequest.class);
        verify(requests).save(saved.capture());
        assertNotEquals(token, saved.getValue().getTokenHash(), "only a hash is stored");
        when(requests.findById(EmailChangeService.hash(token))).thenReturn(Optional.of(saved.getValue()));

        assertEquals("new@example.com", service.confirm(token));
        assertEquals("new@example.com", profile.getEmail());
        assertNotNull(saved.getValue().getUsedAt());
        assertThrows(IllegalStateException.class, () -> service.confirm(token));
    }

    @Test
    void expiredLinksAreRejected() {
        EmailChangeRequest old = new EmailChangeRequest();
        old.setUsername("miguel"); old.setNewEmail("new@example.com");
        old.setCreatedAt(Instant.now().minusSeconds(100_000)); old.setExpiresAt(Instant.now().minusSeconds(10));
        String token = "x".repeat(43);
        when(requests.findById(EmailChangeService.hash(token))).thenReturn(Optional.of(old));
        assertThrows(IllegalStateException.class, () -> service.confirm(token));
        assertEquals("old@example.com", profile.getEmail());
    }

    @Test
    void googleAccountsConfirmByLinkWithoutPassword() {
        profile.setGoogleSubject("g-123");
        service.request("miguel", "new@example.com", null);
        verify(requests).save(any());
    }

    @Test
    void masksTheNewAddressInTheWarningToTheOldOne() {
        assertEquals("n***@e***", EmailChangeService.mask("new@example.com"));
    }
}
