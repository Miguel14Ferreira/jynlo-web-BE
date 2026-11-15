package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamCompetitionEnrollmentService;
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
@RequestMapping("/api/team-competition-enrollments")
@PreAuthorize("isAuthenticated()")
public class SportsTeamCompetitionEnrollmentController {
    private final SportsTeamCompetitionEnrollmentService service;

    public record FinalizeRequest(@NotNull Long globalTeamId) {}

    @PostMapping("/communities/{communityId}/{type}/{competitionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamCompetitionEnrollmentService.EntryView finalizeEntry(
        @PathVariable Long communityId,@PathVariable String type,@PathVariable Long competitionId,
        @RequestBody @Valid FinalizeRequest request,Authentication auth) {
        return service.finalizeEntry(communityId,type,competitionId,request.globalTeamId(),auth.getName());
    }

    @GetMapping("/teams/{globalTeamId}/roster")
    public List<SportsTeamCompetitionEnrollmentService.RosterCandidate> roster(
        @PathVariable Long globalTeamId,Authentication auth) {
        return service.globalRoster(globalTeamId,auth.getName());
    }
}
