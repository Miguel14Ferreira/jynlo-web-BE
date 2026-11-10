package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.social.FollowProfileResponse;
import com.example.desporto24.service.SocialPostInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/feed/posts")
@RequiredArgsConstructor
public class SocialPostLikersController {
    private final SocialPostInteractionService interactions;

    @GetMapping("/{postId}/likers")
    public List<FollowProfileResponse> likers(@PathVariable Long postId, Authentication authentication) {
        return interactions.likers(postId, authentication.getName());
    }

    @PutMapping("/{postId}/likers/privacy")
    public boolean setLikersHidden(@PathVariable Long postId, @RequestBody HideLikersRequest request,
                                    Authentication authentication) {
        return interactions.setHideLikers(postId, authentication.getName(), request.hidden());
    }

    public record HideLikersRequest(boolean hidden) {}
}
