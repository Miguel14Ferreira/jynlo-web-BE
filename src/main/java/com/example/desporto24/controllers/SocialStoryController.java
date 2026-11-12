package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.social.StoryResponse;
import com.example.desporto24.service.SocialStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stories")
public class SocialStoryController {
    private final SocialStoryService stories;

    @GetMapping
    public List<StoryResponse> feed(Authentication auth) {
        return stories.feed(auth.getName());
    }

    // Only the signed-in author can inspect their own still-active publications.
    @GetMapping("/mine")
    public List<StoryResponse> mine(Authentication auth) {
        return stories.mine(auth.getName());
    }

    @GetMapping("/{id}")
    public StoryResponse view(@PathVariable Long id, Authentication auth) {
        return stories.view(id, auth.getName());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StoryResponse publish(@RequestPart(value = "caption", required = false) String caption,
                                 @RequestPart(value = "communityId", required = false) Long communityId,
                                 @RequestPart(value = "image", required = false) MultipartFile image,
                                 @RequestPart(value = "video", required = false) MultipartFile video,
                                 Authentication auth) {
        return stories.create(auth.getName(), communityId, caption, image, video);
    }

    @GetMapping("/me/replies")
    public List<SocialStoryService.PrivateReply> myReplies(
            @RequestParam(defaultValue = "0") int page, Authentication auth) {
        return stories.myPrivateReplies(auth.getName(), page);
    }

    @PostMapping("/{id}/likes")
    public StoryResponse like(@PathVariable Long id, Authentication auth) {
        return stories.like(id, auth.getName());
    }

    @DeleteMapping("/{id}/likes")
    public StoryResponse unlike(@PathVariable Long id, Authentication auth) {
        return stories.unlike(id, auth.getName());
    }

    public record StoryReplyRequest(String content) {}

    @PostMapping("/{id}/replies")
    public ResponseEntity<Void> reply(@PathVariable Long id,
                                      @RequestBody StoryReplyRequest request,
                                      Authentication auth) {
        stories.reply(id, auth.getName(), request == null ? null : request.content());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        stories.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
