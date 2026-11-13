package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.social.CreateSocialPostRequest;
import com.example.desporto24.model.dto.moderation.CreateUserReportRequest;
import com.example.desporto24.model.dto.social.CreateSocialCommentRequest;
import com.example.desporto24.model.dto.social.PostLikeResponse;
import com.example.desporto24.model.dto.social.SocialCommentResponse;
import com.example.desporto24.model.dto.social.FeedItemResponse;
import com.example.desporto24.service.SocialFeedService;
import com.example.desporto24.service.SocialPostInteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class SocialFeedController {

    private final SocialFeedService feedService;
    private final SocialPostInteractionService interactionService;

    @GetMapping
    public List<FeedItemResponse> feed(Authentication authentication) {
        return feedService.feed(authentication.getName());
    }

    @GetMapping("/users/{username}/posts")
    public List<FeedItemResponse> profilePosts(@PathVariable String username,
                                               @RequestParam(defaultValue = "0") int page,
                                               Authentication authentication) {
        return feedService.profilePosts(username, authentication.getName(), page);
    }

    @GetMapping("/posts/{postId}")
    public FeedItemResponse postById(@PathVariable Long postId, Authentication authentication) {
        return feedService.postById(postId, authentication.getName());
    }

    @PostMapping(
            value = "/posts",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public FeedItemResponse createPost(
            @RequestBody @Valid CreateSocialPostRequest request,
            Authentication authentication) {
        return feedService.createPost(authentication.getName(), request);
    }

    @PostMapping(
            value = "/posts",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FeedItemResponse createPostWithMedia(
            @RequestParam("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "sessionId", required = false) Long sessionId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "location", required = false) String location,
            Authentication authentication) {
        if (files != null && !files.isEmpty()) {
            if (image != null || video != null) {
                throw new IllegalArgumentException("Usa apenas um formato de envio");
            }
            return feedService.createPostWithAlbum(authentication.getName(), content, files, sessionId, location);
        }
        return feedService.createPostWithMedia(
                authentication.getName(), content, image, video, sessionId, location);
    }

    /** Source compatibility for callers exercising the old controller signature. */
    public FeedItemResponse createPostWithMedia(String content, MultipartFile image,
            MultipartFile video, Long sessionId, List<MultipartFile> files,
            Authentication authentication) {
        return createPostWithMedia(content, image, video, sessionId, files, null, authentication);
    }

    @PutMapping("/attendance/{sessionId}/hide")
    public ResponseEntity<Void> hideAttendanceActivity(
            @PathVariable Long sessionId,
            Authentication authentication) {
        feedService.hideAttendanceActivity(sessionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/likes")
    public PostLikeResponse likePost(
            @PathVariable Long postId,
            Authentication authentication) {
        return interactionService.like(postId, authentication.getName());
    }

    @DeleteMapping("/posts/{postId}/likes")
    public PostLikeResponse unlikePost(
            @PathVariable Long postId,
            Authentication authentication) {
        return interactionService.unlike(postId, authentication.getName());
    }

    @GetMapping("/posts/{postId}/comments")
    public List<SocialCommentResponse> comments(@PathVariable Long postId,
                                                Authentication authentication) {
        return interactionService.comments(postId, authentication.getName());
    }

    @PostMapping("/posts/{postId}/comments")
    public SocialCommentResponse comment(
            @PathVariable Long postId,
            @RequestBody @Valid CreateSocialCommentRequest request,
            Authentication authentication) {
        return interactionService.comment(
                postId,
                authentication.getName(),
                request
        );
    }

    public record EditCommentRequest(@jakarta.validation.constraints.NotBlank
        @jakarta.validation.constraints.Size(max=500) String content) {}

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public SocialCommentResponse editComment(@PathVariable Long postId,@PathVariable Long commentId,
            @RequestBody @Valid EditCommentRequest request,Authentication authentication) {
        return interactionService.editComment(postId,commentId,authentication.getName(),request.content());
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/likes")
    public SocialCommentResponse likeComment(@PathVariable Long postId,@PathVariable Long commentId,
            Authentication authentication) {
        return interactionService.setCommentLike(postId,commentId,authentication.getName(),true);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}/likes")
    public SocialCommentResponse unlikeComment(@PathVariable Long postId,@PathVariable Long commentId,
            Authentication authentication) {
        return interactionService.setCommentLike(postId,commentId,authentication.getName(),false);
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/report")
    public ResponseEntity<Void> reportComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody @Valid CreateUserReportRequest request,
            Authentication authentication) {
        interactionService.reportComment(
                postId,
                commentId,
                authentication.getName(),
                request
        );
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Authentication authentication) {
        interactionService.deleteComment(
                postId,
                commentId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<Void> reportPost(
            @PathVariable Long postId,
            @RequestBody @Valid CreateUserReportRequest request,
            Authentication authentication) {
        interactionService.reportPost(
                postId,
                authentication.getName(),
                request
        );
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/posts/{postId}")
    public FeedItemResponse editPost(@PathVariable Long postId,
            @RequestBody @Valid com.example.desporto24.model.dto.social.EditSocialPostRequest request,
            Authentication authentication) {
        return feedService.editPost(postId, authentication.getName(), request);
    }

    @PutMapping(value = "/posts/{postId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FeedItemResponse editPostWithMedia(@PathVariable Long postId,
            @RequestParam("content") String content,
            @RequestParam(value = "location", required = false) String location,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "replaceMedia", defaultValue = "false") boolean replaceMedia,
            Authentication authentication) {
        return feedService.editPostWithMedia(postId, authentication.getName(),
                content, location, files, replaceMedia);
    }

    @GetMapping("/posts/hidden")
    public List<FeedItemResponse> hiddenPosts(Authentication authentication) {
        return feedService.hiddenPosts(authentication.getName());
    }

    @PutMapping("/posts/{postId}/hide")
    public ResponseEntity<Void> hidePost(@PathVariable Long postId, Authentication authentication) {
        feedService.hidePost(postId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}/hide")
    public ResponseEntity<Void> unhidePost(@PathVariable Long postId, Authentication authentication) {
        feedService.unhidePost(postId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication) {
        feedService.deletePost(postId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
