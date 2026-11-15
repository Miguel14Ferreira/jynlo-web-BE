package com.example.desporto24.controllers;

import com.example.desporto24.model.team.SportsTeamContent.Kind;
import com.example.desporto24.service.SportsTeamReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}")
@PreAuthorize("isAuthenticated()")
public class SportsTeamReactionController {
    private final SportsTeamReactionService service;

    @GetMapping("/{kind}/reactions")
    public Map<Long, SportsTeamReactionService.ReactionState> summaries(
            @PathVariable Long teamId, @PathVariable String kind,
            @RequestParam List<Long> ids, Authentication auth) {
        return service.summaries(teamId, parseKind(kind), ids, auth.getName());
    }

    @PutMapping("/{kind}/{contentId}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void set(@PathVariable Long teamId, @PathVariable String kind,
            @PathVariable Long contentId,
            @RequestBody SportsTeamReactionService.ReactionRequest request, Authentication auth) {
        service.set(teamId, parseKind(kind), contentId,
                request == null ? null : request.emoji(), auth.getName());
    }

    @DeleteMapping("/{kind}/{contentId}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long teamId, @PathVariable String kind,
            @PathVariable Long contentId, Authentication auth) {
        service.remove(teamId, parseKind(kind), contentId, auth.getName());
    }

    private Kind parseKind(String kind) {
        return switch (kind) {
            case "chat" -> Kind.CHAT;
            case "feed" -> Kind.FEED;
            default -> throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tipo de conteúdo inválido");
        };
    }
}
