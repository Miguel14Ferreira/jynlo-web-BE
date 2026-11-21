package com.example.desporto24.controllers;

import com.example.desporto24.service.CommunityPublicRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommunityPublicRankingController {
    private final CommunityPublicRankingService service;

    public record UpdateRequest(Boolean enabled) {}

    /** Anyone with the link, no login, only while an admin keeps it enabled. */
    @GetMapping("/api/public/communities/{communityId}/ranking")
    public CommunityPublicRankingService.Page page(@PathVariable Long communityId,
                                                   @RequestParam(required = false) Long seasonId) {
        return service.page(communityId, seasonId);
    }

    @GetMapping("/api/communities/{communityId}/public-ranking")
    public CommunityPublicRankingService.Setting setting(@PathVariable Long communityId, Authentication auth) {
        return service.setting(communityId, auth.getName());
    }

    @PutMapping("/api/communities/{communityId}/public-ranking")
    public CommunityPublicRankingService.Setting update(@PathVariable Long communityId,
                                                        @RequestBody UpdateRequest request, Authentication auth) {
        return service.update(communityId, auth.getName(), request != null && Boolean.TRUE.equals(request.enabled()));
    }
}
