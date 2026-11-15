package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class SportsTeamController {
    private final SportsTeamService service;

    @GetMapping
    public List<SportsTeamService.TeamView> discover(@RequestParam(defaultValue = "") String q) {
        return service.discover(q);
    }

    @GetMapping("/{id}")
    public SportsTeamService.TeamView get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public List<SportsTeamService.TeamView> mine(Authentication auth) {
        return service.mine(auth.getName());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SportsTeamService.TeamView> create(
            @RequestBody SportsTeamService.CreateTeam request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth.getName()));
    }

    @PatchMapping("/{id}/features")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamService.TeamView features(@PathVariable Long id,
            @RequestBody SportsTeamService.TeamFeatures features, Authentication auth) {
        return service.features(id, features, auth.getName());
    }
}
