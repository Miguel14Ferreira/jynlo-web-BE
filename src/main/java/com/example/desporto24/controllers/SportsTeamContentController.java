package com.example.desporto24.controllers;

import com.example.desporto24.model.team.SportsTeamContent.Kind;
import com.example.desporto24.service.SportsTeamContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}")
@PreAuthorize("isAuthenticated()")
public class SportsTeamContentController {
    private final SportsTeamContentService service;

    @GetMapping("/chat")
    public List<SportsTeamContentService.Entry> chat(@PathVariable Long teamId, Authentication auth) {
        return service.recent(teamId, Kind.CHAT, auth.getName());
    }

    @GetMapping("/chat/search")
    public List<SportsTeamContentService.Entry> searchChat(@PathVariable Long teamId,
            @RequestParam("q") String term, Authentication auth) {
        return service.search(teamId, Kind.CHAT, term, auth.getName());
    }

    @GetMapping("/feed/search")
    public List<SportsTeamContentService.Entry> searchFeed(@PathVariable Long teamId,
            @RequestParam("q") String term, Authentication auth) {
        return service.search(teamId, Kind.FEED, term, auth.getName());
    }

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamContentService.Entry send(@PathVariable Long teamId,
            @RequestBody SportsTeamContentService.Submit request, Authentication auth) {
        return service.post(teamId, Kind.CHAT, request, auth.getName());
    }

    @GetMapping("/feed")
    public List<SportsTeamContentService.Entry> feed(@PathVariable Long teamId, Authentication auth) {
        return service.recent(teamId, Kind.FEED, auth.getName());
    }

    @PostMapping("/feed")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamContentService.Entry publish(@PathVariable Long teamId,
            @RequestBody SportsTeamContentService.Submit request, Authentication auth) {
        return service.post(teamId, Kind.FEED, request, auth.getName());
    }

    @PatchMapping("/chat/{contentId}")
    public SportsTeamContentService.Entry editChat(@PathVariable Long teamId,
            @PathVariable Long contentId, @RequestBody SportsTeamContentService.Submit request,
            Authentication auth) {
        return service.edit(teamId, Kind.CHAT, contentId, request, auth.getName());
    }

    @DeleteMapping("/chat/{contentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChat(@PathVariable Long teamId, @PathVariable Long contentId,
                           Authentication auth) {
        service.delete(teamId, Kind.CHAT, contentId, auth.getName());
    }

    @PatchMapping("/feed/{contentId}")
    public SportsTeamContentService.Entry editFeed(@PathVariable Long teamId,
            @PathVariable Long contentId, @RequestBody SportsTeamContentService.Submit request,
            Authentication auth) {
        return service.edit(teamId, Kind.FEED, contentId, request, auth.getName());
    }

    @DeleteMapping("/feed/{contentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFeed(@PathVariable Long teamId, @PathVariable Long contentId,
                           Authentication auth) {
        service.delete(teamId, Kind.FEED, contentId, auth.getName());
    }
}