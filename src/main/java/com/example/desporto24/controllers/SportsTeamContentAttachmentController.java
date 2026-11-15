package com.example.desporto24.controllers;

import com.example.desporto24.model.team.SportsTeamContent.Kind;
import com.example.desporto24.service.SportsTeamContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/** Files are never sent through public media URLs. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/{kind}/attachments")
@PreAuthorize("isAuthenticated()")
public class SportsTeamContentAttachmentController {
    private final SportsTeamContentService service;

    @GetMapping("/availability")
    public Map<String,Boolean> available(@PathVariable Long teamId,
            @PathVariable String kind, Authentication auth) {
        return Map.of("available", service.attachmentsAvailable(teamId, parse(kind),auth.getName()));
    }

    @PostMapping(value="/batch", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamContentService.Entry upload(@PathVariable Long teamId,
            @PathVariable String kind, @RequestPart("files") List<MultipartFile> files,
            @RequestParam(value="caption", required=false) String caption,
            @RequestParam(value="replyToMessageId", required=false) Long replyToMessageId,
            Authentication auth) {
        return service.upload(teamId, parse(kind), files, caption, replyToMessageId, auth.getName());
    }

    @GetMapping("/{messageId}/{index}")
    public ResponseEntity<byte[]> download(@PathVariable Long teamId, @PathVariable String kind,
            @PathVariable Long messageId, @PathVariable int index, Authentication auth) {
        var file = service.download(teamId, parse(kind), messageId, index, auth.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.mimeType()))
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(file.filename(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(file.content());
    }

    private Kind parse(String kind) {
        return switch (kind) {
            case "chat" -> Kind.CHAT;
            case "feed" -> Kind.FEED;
            default -> throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tipo inválido");
        };
    }
}
