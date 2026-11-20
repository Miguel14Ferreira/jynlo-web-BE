package com.example.desporto24.controllers;

import com.example.desporto24.service.PublicProfileCompetitionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/** Only official public tournament wins. Membership and result APIs stay private. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class PublicProfileCompetitionHistoryController {
    private final PublicProfileCompetitionHistoryService history;

    @GetMapping("/{username}/individual-titles")
    @PreAuthorize("isAuthenticated()")
    public List<PublicProfileCompetitionHistoryService.IndividualTitle> individualTitles(
            @PathVariable String username, Authentication authentication) {
        return history.individualTitles(authentication.getName(), username);
    }
}
