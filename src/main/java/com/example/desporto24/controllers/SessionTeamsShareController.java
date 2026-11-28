package com.example.desporto24.controllers;

import com.example.desporto24.service.SessionTeamsShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SessionTeamsShareController {
    private final SessionTeamsShareService service;

    /** Organiser: the link to send on WhatsApp once both teams are confirmed. */
    @PostMapping("/api/sessions/{id}/teams/share")
    public SessionTeamsShareService.ShareLink link(@PathVariable Long id, Authentication auth) {
        return service.link(id, auth.getName());
    }

    /** Anyone with the link, no account: the game and its two teams. */
    @GetMapping("/api/public/sessions/{id}/teams")
    public SessionTeamsShareService.Page page(@PathVariable Long id, @RequestParam("t") String token) {
        return service.page(id, token);
    }
}
