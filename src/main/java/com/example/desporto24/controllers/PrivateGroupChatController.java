package com.example.desporto24.controllers;

import com.example.desporto24.service.PrivateGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Private group chat actions beyond plain text: files, edit, delete and search.
 * Membership is rechecked by the service on every call; files are never served
 * through public media URLs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private-groups/{id}/messages")
@PreAuthorize("isAuthenticated()")
public class PrivateGroupChatController {
    private final PrivateGroupService service;

    @GetMapping("/attachments/availability")
    public Map<String, Boolean> available(@PathVariable Long id, Authentication auth) {
        return Map.of("available", service.attachmentsAvailable(auth.getName(), id));
    }

    @PostMapping(value = "/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PrivateGroupService.MessageView upload(@PathVariable Long id,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam(value = "replyToMessageId", required = false) Long replyToMessageId,
            Authentication auth) {
        return service.upload(auth.getName(), id, files, caption, replyToMessageId);
    }

    @GetMapping("/{messageId}/attachments/{index}")
    public ResponseEntity<byte[]> download(@PathVariable Long id, @PathVariable Long messageId,
            @PathVariable int index, Authentication auth) {
        var file = service.download(auth.getName(), id, messageId, index);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.mimeType()))
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(file.filename(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(file.content());
    }

    @PutMapping("/{messageId}")
    public PrivateGroupService.MessageView edit(@PathVariable Long id, @PathVariable Long messageId,
            @RequestBody PrivateGroupService.EditRequest request, Authentication auth) {
        return service.edit(auth.getName(), id, messageId, request);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @PathVariable Long messageId, Authentication auth) {
        service.delete(auth.getName(), id, messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<PrivateGroupService.MessageView> search(@PathVariable Long id, @RequestParam String q,
            Authentication auth) {
        return service.search(auth.getName(), id, q);
    }
}
