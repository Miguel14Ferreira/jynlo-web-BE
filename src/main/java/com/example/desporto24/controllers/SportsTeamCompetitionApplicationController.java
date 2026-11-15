package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamCompetitionApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team-competition-applications")
public class SportsTeamCompetitionApplicationController {
    private final SportsTeamCompetitionApplicationService service;
    public record ApplyRequest(@NotNull Long teamId) {}
    public record OpenRequest(@NotNull Boolean open) {}

    @GetMapping("/discover")
    public List<SportsTeamCompetitionApplicationService.CompetitionView> discover(
        @RequestParam(defaultValue = "") String q,@RequestParam(defaultValue = "") String sport) {
        return service.discover(q,sport);
    }
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public List<SportsTeamCompetitionApplicationService.ApplicationView> mine(Authentication auth) {
        return service.mine(auth.getName());
    }
    @GetMapping("/communities/{communityId}/{type}/{competitionId}")
    @PreAuthorize("isAuthenticated()")
    public List<SportsTeamCompetitionApplicationService.ApplicationView> list(
        @PathVariable Long communityId,@PathVariable String type,
        @PathVariable Long competitionId,Authentication auth) {
        return service.forCompetition(communityId,type,competitionId,auth.getName());
    }
    @GetMapping("/communities/{communityId}/{type}/{competitionId}/settings")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamCompetitionApplicationService.CompetitionView settings(
        @PathVariable Long communityId,@PathVariable String type,@PathVariable Long competitionId,
        Authentication auth) {
        return service.settings(communityId,type,competitionId,auth.getName());
    }
    @PutMapping("/communities/{communityId}/{type}/{competitionId}/open")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamCompetitionApplicationService.CompetitionView configure(
        @PathVariable Long communityId,@PathVariable String type,@PathVariable Long competitionId,
        @Valid @RequestBody OpenRequest request,Authentication auth) {
        return service.configure(communityId,type,competitionId,request.open(),auth.getName());
    }
    @PostMapping("/communities/{communityId}/{type}/{competitionId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamCompetitionApplicationService.ApplicationView apply(
        @PathVariable Long communityId,@PathVariable String type,@PathVariable Long competitionId,
        @Valid @RequestBody ApplyRequest request,Authentication auth) {
        return service.apply(communityId,type,competitionId,request.teamId(),auth.getName());
    }
    @PostMapping("/{id}/approve")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamCompetitionApplicationService.ApplicationView approve(@PathVariable Long id,Authentication auth) {
        return service.decide(id,true,auth.getName());
    }
    @PostMapping("/{id}/decline")
    @PreAuthorize("isAuthenticated()")
    public SportsTeamCompetitionApplicationService.ApplicationView decline(@PathVariable Long id,Authentication auth) {
        return service.decide(id,false,auth.getName());
    }
}
