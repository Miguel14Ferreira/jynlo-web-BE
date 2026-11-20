package com.example.desporto24.controllers;

import com.example.desporto24.service.PublicProfileAffiliationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Explicitly authenticated and public-only; not a generic team/community
 * membership enumeration endpoint.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class PublicProfileAffiliationsController {
    private final PublicProfileAffiliationsService affiliations;

    @GetMapping("/{username}/public-affiliations")
    @PreAuthorize("isAuthenticated()")
    public PublicProfileAffiliationsService.Affiliations visible(
            @PathVariable String username, Authentication authentication) {
        return affiliations.visible(authentication.getName(), username);
    }
}
