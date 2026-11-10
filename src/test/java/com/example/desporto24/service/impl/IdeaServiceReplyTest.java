package com.example.desporto24.service.impl;

import com.example.desporto24.model.Ideas;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.IdeasRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdeaServiceReplyTest {
    @Mock IdeasRepository ideas;
    @Mock ProfileRepository profiles;
    @Mock EmailService email;
    @InjectMocks IdeaServiceImpl service;

    private Profile actor(String role) {
        Profile actor = mock(Profile.class);
        when(actor.getRole()).thenReturn(role);
        lenient().when(actor.getEnabled()).thenReturn(true);
        lenient().when(actor.getNotLocked()).thenReturn(true);
        lenient().when(actor.getUsername()).thenReturn("moderador");
        return actor;
    }

    @Test
    void officialReplyEmailsSubmitterAndStoresSenderWithoutHtmlInjection() {
        when(profiles.findUserByUsernameIgnoreCase("moderador"))
                .thenReturn(Optional.of(actor("ROLE_ADMIN")));
        var idea = new Ideas("Emanuel", "emanuel@example.com", "Lisboa", "", "Outro", "Parabéns");
        idea.setId(8L);
        when(ideas.findForReply(8L)).thenReturn(Optional.of(idea));
        when(ideas.save(idea)).thenReturn(idea);

        var result = service.reply(8L, "Muito obrigado! <Continuamos>.", "moderador");

        verify(email).send(eq("emanuel@example.com"), contains("Jynlo"),
                argThat(html -> html.contains("&lt;Continuamos&gt;.")
                        && !html.contains("<Continuamos>")));
        assertEquals("Muito obrigado! <Continuamos>.", result.adminReply());
        assertEquals("moderador", result.adminRepliedBy());
        assertNotNull(result.adminRepliedAt());
    }

    @Test
    void aDemotedAdminCannotReplyUsingAnOldAuthenticatedIdentity() {
        when(profiles.findUserByUsernameIgnoreCase("moderador"))
                .thenReturn(Optional.of(actor("ROLE_USER")));
        assertEquals(403, assertThrows(ResponseStatusException.class,
                () -> service.reply(8L, "Obrigado!", "moderador")).getStatusCode().value());
        verifyNoInteractions(ideas, email);
    }

    @Test
    void cannotSendTwoRepliesToOneSuggestion() {
        when(profiles.findUserByUsernameIgnoreCase("moderador"))
                .thenReturn(Optional.of(actor("ROLE_ADMIN")));
        var idea = new Ideas("Emanuel", "emanuel@example.com", "", "", "Outro", "Parabéns");
        idea.setAdminRepliedAt(java.time.Instant.now());
        when(ideas.findForReply(8L)).thenReturn(Optional.of(idea));
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.reply(8L, "Obrigado!", "moderador")).getStatusCode().value());
        verifyNoInteractions(email);
    }

    @Test
    void failedEmailDoesNotMarkIdeaAsAnswered() {
        when(profiles.findUserByUsernameIgnoreCase("moderador"))
                .thenReturn(Optional.of(actor("ROLE_ADMIN")));
        var idea = new Ideas("Emanuel", "emanuel@example.com", "", "", "Outro", "Parabéns");
        when(ideas.findForReply(8L)).thenReturn(Optional.of(idea));
        doThrow(new IllegalStateException("email unavailable")).when(email)
                .send(eq("emanuel@example.com"), anyString(), anyString());

        assertThrows(IllegalStateException.class,
                () -> service.reply(8L, "Obrigado!", "moderador"));
        assertNull(idea.getAdminRepliedAt());
        verify(ideas, never()).save(any());
    }
}
