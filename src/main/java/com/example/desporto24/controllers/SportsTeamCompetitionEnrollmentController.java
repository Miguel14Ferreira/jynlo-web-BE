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
    public record InviteSelectedRequest(@NotNull Long globalTeamId, @NotNull List<Long> profileIds) {}

    @PostMapping("/communities/{communityId}/{type}/{competitionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamCompetitionEnrollmentService.EntryView finalizeEntry(
        @PathVariable Long communityId,@PathVariable String type,@PathVariable Long competitionId,
        @RequestBody @Valid FinalizeRequest request,Authentication auth) {
        return service.finalizeEntry(communityId,type,competitionId,request.globalTeamId(),auth.getName());
    }

    @GetMapping("/communities/{communityId}/{type}/{competitionId}/teams/{globalTeamId}/entry")
    public SportsTeamCompetitionEnrollmentService.EntryView existingEntry(
        @PathVariable Long communityId,@PathVariable String type,@PathVariable Long competitionId,
        @PathVariable Long globalTeamId,Authentication auth) {
        return service.existingEntry(communityId,type,competitionId,globalTeamId,auth.getName());
    }

    @GetMapping("/communities/{communityId}/{type}/{competitionId}/teams/{globalTeamId}/roster")
    public List<SportsTeamCompetitionEnrollmentService.RosterCandidate> roster(
        @PathVariable Long communityId,@PathVariable String type,@PathVariable Long competitionId,
        @PathVariable Long globalTeamId,Authentication auth) {
        return service.globalRoster(communityId,type,competitionId,globalTeamId,auth.getName());
    }

    @PostMapping("/communities/{communityId}/{type}/{competitionId}/invite-players")
    public java.util.Map<String,Integer> invitePlayers(
        @PathVariable Long communityId,@PathVariable String type,@PathVariable Long competitionId,
        @RequestBody @Valid InviteSelectedRequest request,Authentication auth) {
        int count=service.inviteSelected(communityId,type,competitionId,
            request.globalTeamId(),request.profileIds(),auth.getName());
        return java.util.Map.of("invitationsSent",count);
    }
}
