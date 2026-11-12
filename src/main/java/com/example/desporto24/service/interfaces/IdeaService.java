package com.example.desporto24.service.interfaces;

import com.example.desporto24.enumeration.IdeaStatus;
import com.example.desporto24.model.dto.idea.AdminIdeaResponse;
import com.example.desporto24.model.dto.idea.IdeaSubmissionRequest;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IdeaService {
    void submitNewIdea(IdeaSubmissionRequest request);
    void submitNewIdeaWithAttachment(IdeaSubmissionRequest request, MultipartFile image);
    AttachmentDownload attachment(Long ideaId);

    record AttachmentDownload(String filename, String mimeType, byte[] content) {}
    List<AdminIdeaResponse> getAllIdeas();
    AdminIdeaResponse updateStatus(Long id, IdeaStatus status);
    AdminIdeaResponse reply(Long id, String message, String actorUsername);
}
