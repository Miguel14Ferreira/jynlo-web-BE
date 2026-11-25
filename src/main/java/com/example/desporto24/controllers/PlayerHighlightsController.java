package com.example.desporto24.controllers;

import com.example.desporto24.service.PlayerHighlightsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PlayerHighlightsController {
    private final PlayerHighlightsService service;

    @GetMapping("/api/players/{username}/stats")
    public PlayerHighlightsService.ProfileStats stats(@PathVariable String username, Authentication auth) {
        return service.profileStats(username, auth == null ? null : auth.getName());
    }

    @GetMapping("/api/communities/{id}/month-leaders")
    public PlayerHighlightsService.MonthLeaders monthLeaders(@PathVariable Long id, @RequestParam(required = false) String month,
                                                             Authentication auth) {
        return service.monthLeaders(id, auth.getName(), month);
    }

    @PostMapping("/api/communities/{id}/player-of-month-poll")
    public void playerOfMonthPoll(@PathVariable Long id, @RequestParam(required = false) String month, Authentication auth) {
        service.playerOfMonthPoll(id, auth.getName(), month);
    }
}
