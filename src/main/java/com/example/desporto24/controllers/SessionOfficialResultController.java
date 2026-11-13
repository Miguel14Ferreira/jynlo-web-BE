package com.example.desporto24.controllers;

import com.example.desporto24.service.SessionOfficialResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/** Exact session scoreboard; never public or accessible from guessed IDs. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sessions/{sessionId}/official-result")
public class SessionOfficialResultController {
    private final SessionOfficialResultService service;

    @GetMapping
    public SessionOfficialResultService.OfficialResult result(
            @PathVariable Long sessionId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return service.result(sessionId, authentication.getName());
    }
}
