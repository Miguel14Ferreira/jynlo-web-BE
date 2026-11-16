package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamMembershipService;
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
@RequestMapping("/api/teams")
public class SportsTeamMembershipController {
    private final SportsTeamMembershipService service;

    public record InvitePlayer(@NotNull Long profileId) {}

    @GetMapping("/players/search")
    @PreAuthorize("isAuthenticated()")
    public List<SportsTeamMembershipService.PlayerCandidate> search(
            @RequestParam(defaultValue = "") String q, Authentication auth) {
        return service.searchPlayers(q, auth.getName());
    }

    @GetMapping("/{teamId}/members")
    public List<SportsTeamMembershipService.RosterPlayer> roster(@PathVariable Long teamId) {
        return service.roster(teamId);
    }

    @GetMapping("/joined")
    @PreAuthorize("isAuthenticated()")
    public List<SportsTeamMembershipService.JoinedTeam> joined(Authentication auth) {
        return service.joined(auth.getName());
    }

    @GetMapping("/invitations/mine")
    @PreAuthorize("isAuthenticated()")
    public List<SportsTeamMembershipService.Invitation> mine(Authentication auth) {
        return service.mine(auth.getName());
    }

    @GetMapping("/{teamId}/invitations")
    @PreAuthorize("isAuthenticated()")
    public List<SportsTeamMembershipService.PendingInvitation> pendingInvitations(
            @PathVariable Long teamId, Authentication auth) {
        return service.pendingInvitations(teamId, auth.getName());
    }

    @PostMapping("/{teamId}/invitations")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void invite(@PathVariable Long teamId, @Valid @RequestBody InvitePlayer request,
                       Authentication auth) {
        service.invite(teamId, request.profileId(), auth.getName());
    }

    @PostMapping("/{teamId}/invitations/accept")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(@PathVariable Long teamId, Authentication auth) {
        service.respond(teamId, auth.getName(), true);
    }

    @PostMapping("/{teamId}/invitations/decline")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void decline(@PathVariable Long teamId, Authentication auth) {
        service.respond(teamId, auth.getName(), false);
    }

    @DeleteMapping("/{teamId}/members/{profileId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long teamId, @PathVariable Long profileId, Authentication auth) {
        service.remove(teamId, profileId, auth.getName());
    }
}
