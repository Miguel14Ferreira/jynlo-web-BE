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
    private final com.example.desporto24.service.SportsTeamChampionshipHonoursService championshipHonours;

    @GetMapping
    public List<SportsTeamService.TeamView> discover(@RequestParam(defaultValue = "") String q) {
        return service.discover(q);
    }

    @GetMapping("/{id}")
    public SportsTeamService.TeamView get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/{id}/honours")
    public List<com.example.desporto24.service.SportsTeamChampionshipHonoursService.Honour> honours(
            @PathVariable Long id) {
        return championshipHonours.honours(id);
    }

    @GetMapping("/{id}/history")
    public List<SportsTeamService.TeamCompetitionHistory> history(@PathVariable Long id) {
        return service.competitionHistory(id);
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

    @PutMapping("/{id}/kit")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamService.TeamView kit(@PathVariable Long id,
            @RequestBody SportsTeamService.KitSettings settings, Authentication auth) {
        return service.kit(id, settings, auth.getName());
    }

    @PutMapping(value = "/{id}/logo",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public SportsTeamService.TeamView uploadLogo(@PathVariable Long id,
            @RequestPart("logo") org.springframework.web.multipart.MultipartFile file,
            Authentication auth) {
        return service.uploadLogo(id, file, auth.getName());
    }

    @DeleteMapping("/{id}/logo")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamService.TeamView removeLogo(@PathVariable Long id, Authentication auth) {
        return service.removeLogo(id, auth.getName());
    }

    @PatchMapping("/{id}/features")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamService.TeamView features(@PathVariable Long id,
            @RequestBody SportsTeamService.TeamFeatures features, Authentication auth) {
        return service.features(id, features, auth.getName());
    }
}
