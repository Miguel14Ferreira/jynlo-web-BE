package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamCompetitionInvitationService;
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
@RequestMapping("/api/team-competition-invitations")
@PreAuthorize("isAuthenticated()")
public class SportsTeamCompetitionInvitationController {
    private final SportsTeamCompetitionInvitationService service;
    public record InviteRequest(@NotNull Long teamId) {}

    @GetMapping("/mine")
    public List<SportsTeamCompetitionInvitationService.InvitationView> mine(Authentication auth) {
        return service.inbox(auth.getName());
    }

    @GetMapping("/communities/{communityId}/{type}/{competitionId}")
    public List<SportsTeamCompetitionInvitationService.InvitationView> list(
            @PathVariable Long communityId, @PathVariable String type, @PathVariable Long competitionId,
            Authentication auth) {
        return service.forCompetition(communityId, type, competitionId, auth.getName());
    }

    @PostMapping("/communities/{communityId}/{type}/{competitionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamCompetitionInvitationService.InvitationView invite(
            @PathVariable Long communityId, @PathVariable String type, @PathVariable Long competitionId,
            @RequestBody @Valid InviteRequest request, Authentication auth) {
        return service.invite(communityId, type, competitionId, request.teamId(), auth.getName());
    }

    @PostMapping("/{id}/accept")
    public SportsTeamCompetitionInvitationService.InvitationView accept(
            @PathVariable Long id, Authentication auth) {
        return service.respond(id, auth.getName(), true);
    }

    @PostMapping("/{id}/decline")
    public SportsTeamCompetitionInvitationService.InvitationView decline(
            @PathVariable Long id, Authentication auth) {
        return service.respond(id, auth.getName(), false);
    }
}
