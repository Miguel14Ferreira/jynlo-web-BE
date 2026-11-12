package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.idea.AdminIdeaResponse;
import com.example.desporto24.model.dto.idea.UpdateIdeaStatusRequest;
import com.example.desporto24.service.interfaces.IdeaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import java.nio.charset.StandardCharsets;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/ideas")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminIdeaController {

    private final IdeaService ideaService;

    @GetMapping
    public ResponseEntity<List<AdminIdeaResponse>> list() {
        return ResponseEntity.ok(ideaService.getAllIdeas());
    }

    /** Private, authenticated download only; never publish feedback images to /media. */
    @GetMapping("/{id}/attachment")
    public ResponseEntity<byte[]> attachment(@PathVariable Long id) {
        var file = ideaService.attachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.mimeType()))
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(file.filename(), StandardCharsets.UTF_8).build().toString())
                .body(file.content());
    }

    public record ReplyRequest(@jakarta.validation.constraints.NotBlank
                               @jakarta.validation.constraints.Size(min = 3, max = 2000) String message) {}

    @PostMapping("/{id}/reply")
    public ResponseEntity<AdminIdeaResponse> reply(
            @PathVariable Long id,
            @RequestBody @Valid ReplyRequest request,
            org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.ok(ideaService.reply(id, request.message(), authentication.getName()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AdminIdeaResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateIdeaStatusRequest request) {
        return ResponseEntity.ok(ideaService.updateStatus(id, request.status()));
    }
}
