package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.social.FollowProfileResponse;
import com.example.desporto24.service.SocialPostInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/feed/posts")
@RequiredArgsConstructor
public class SocialPostLikersController {
    private final SocialPostInteractionService interactions;

    @GetMapping("/{postId}/likers")
    public List<FollowProfileResponse> likers(@PathVariable Long postId) {
        return interactions.likers(postId);
    }
}
