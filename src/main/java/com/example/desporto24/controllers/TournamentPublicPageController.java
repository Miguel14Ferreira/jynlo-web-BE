package com.example.desporto24.controllers;

import com.example.desporto24.service.TournamentPublicPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TournamentPublicPageController {
    private final TournamentPublicPageService service;

    public record UpdateRequest(Boolean enabled) {}

    /** Anyone with the link, no login, only while an admin keeps the page enabled. */
    @GetMapping("/api/public/tournaments/{tournamentId}")
    public TournamentPublicPageService.Page page(@PathVariable Long tournamentId) {
        return service.page(tournamentId);
    }

    @GetMapping("/api/communities/{communityId}/tournaments/{tournamentId}/public-page")
    public TournamentPublicPageService.Setting setting(@PathVariable Long communityId,
                                                       @PathVariable Long tournamentId, Authentication auth) {
        return service.setting(communityId, tournamentId, auth.getName());
    }

    @PutMapping("/api/communities/{communityId}/tournaments/{tournamentId}/public-page")
    public TournamentPublicPageService.Setting update(@PathVariable Long communityId, @PathVariable Long tournamentId,
                                                      @RequestBody UpdateRequest request, Authentication auth) {
        return service.update(communityId, tournamentId, auth.getName(),
                request != null && Boolean.TRUE.equals(request.enabled()));
    }
}
