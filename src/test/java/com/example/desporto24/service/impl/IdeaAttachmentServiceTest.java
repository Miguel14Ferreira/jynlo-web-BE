package com.example.desporto24.service.impl;

import com.example.desporto24.model.IdeaAttachment;
import com.example.desporto24.model.Ideas;
import com.example.desporto24.model.dto.idea.IdeaSubmissionRequest;
import com.example.desporto24.repository.IdeaAttachmentRepository;
import com.example.desporto24.repository.IdeaExtraAttachmentRepository;
import com.example.desporto24.repository.IdeasRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.Base64;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdeaAttachmentServiceTest {
    @Mock IdeasRepository ideas;
    @Mock IdeaAttachmentRepository attachments;
    @Mock IdeaExtraAttachmentRepository extraAttachments;
    @Mock ProfileRepository profiles;
    @Mock EmailService email;
    @InjectMocks IdeaServiceImpl service;

    private IdeaSubmissionRequest request() {
        return new IdeaSubmissionRequest("Ana", "", "ana@example.com", "",
                "Problema técnico", "Há uma falha no ecrã.");
    }

    private MockMultipartFile validPng() {
        byte[] png = Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+nm3sAAAAASUVORK5CYII=");
        return new MockMultipartFile("image", "screen.png", "image/png", png);
    }

    @Test
    void savesPrivateImageWithIdeaAndOnlyExposesMetadataInAdminResponse() {
        when(ideas.save(any(Ideas.class))).thenAnswer(call -> {
            Ideas idea = call.getArgument(0);
            idea.setId(8L);
            return idea;
        });
        service.submitNewIdeaWithAttachment(request(), validPng());
        ArgumentCaptor<IdeaAttachment> attachment = ArgumentCaptor.forClass(IdeaAttachment.class);
        verify(attachments).save(attachment.capture());
        assertEquals(8L, attachment.getValue().getIdeaId());
        assertTrue(attachment.getValue().getContent().length > 10);
        ArgumentCaptor<Ideas> saved = ArgumentCaptor.forClass(Ideas.class);
        verify(ideas).save(saved.capture());
        assertEquals("screen.png", saved.getValue().getAttachmentName());
        assertEquals("image/png", saved.getValue().getAttachmentMimeType());
    }

    @Test
    void rejectsSpoofedImageBeforeSavingAnything() {
        var fake = new MockMultipartFile("image", "fake.png", "image/png",
                "<script>alert(1)</script>".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.submitNewIdeaWithAttachment(request(), fake)).getStatusCode().value());
        verifyNoInteractions(ideas, attachments, email);
    }

    @Test
    void rejectsOversizedScreenshots() {
        var huge = new MockMultipartFile("image", "huge.png", "image/png",
                new byte[3 * 1024 * 1024 + 1]);
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.submitNewIdeaWithAttachment(request(), huge)).getStatusCode().value());
        verifyNoInteractions(ideas, attachments, email);
    }

    @Test
    void acceptsPdfEvidenceAndKeepsItPrivate() {
        when(ideas.save(any(Ideas.class))).thenAnswer(call -> {
            Ideas item = call.getArgument(0);
            item.setId(11L);
            return item;
        });
        var pdf = new MockMultipartFile("images", "evidence.pdf", "application/pdf",
                "%PDF-1.7\n1 0 obj\n<< /Type /Catalog >>\nendobj\n%%EOF"
                    .getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        service.submitNewIdeaWithAttachments(request(), java.util.List.of(pdf));
        verify(attachments).save(any(IdeaAttachment.class));
        verify(extraAttachments, never()).save(any());
    }

    @Test
    void rejectsSpoofedVideoAndExecutablePayloads() {
        var fake = new MockMultipartFile("images", "video.mp4", "video/mp4",
                "<html><script>bad()</script></html>".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.submitNewIdeaWithAttachments(request(), java.util.List.of(fake)))
                .getStatusCode().value());
        verifyNoInteractions(ideas, attachments, extraAttachments, email);
    }

    @Test
    void attachmentDownloadNeedsAnExistingIdeaWithEvidence() {
        Ideas idea = new Ideas("Ana", "ana@example.com", "", "", "Erro", "Texto");
        idea.setId(8L);
        idea.setAttachmentName("screen.png");
        idea.setAttachmentMimeType("image/png");
        when(ideas.findById(8L)).thenReturn(Optional.of(idea));
        IdeaAttachment stored = new IdeaAttachment(8L, new byte[]{1, 2, 3});
        when(attachments.findById(8L)).thenReturn(Optional.of(stored));
        var result = service.attachment(8L);
        assertEquals("screen.png", result.filename());
        assertEquals("image/png", result.mimeType());
        assertArrayEquals(new byte[]{1, 2, 3}, result.content());
    }
}
