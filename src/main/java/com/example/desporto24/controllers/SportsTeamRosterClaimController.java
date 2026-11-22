package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamRosterImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Personal squad links: "És o nº 7 desta equipa? Entra aqui." Signed-in users only. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/roster-claims")
public class SportsTeamRosterClaimController {
    private final SportsTeamRosterImportService service;

    @GetMapping("/{token}")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamRosterImportService.ClaimPreview preview(@PathVariable String token) {
        return service.claimPreview(token);
    }

    @PostMapping("/{token}/accept")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamRosterImportService.ClaimPreview accept(@PathVariable String token, Authentication auth) {
        return service.acceptClaim(token, auth.getName());
    }
}
