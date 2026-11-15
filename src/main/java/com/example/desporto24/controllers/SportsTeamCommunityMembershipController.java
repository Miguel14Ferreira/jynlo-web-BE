package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamCommunityMembershipService;
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
@RequestMapping("/api/team-community-memberships")
@PreAuthorize("isAuthenticated()")
public class SportsTeamCommunityMembershipController {
    private final SportsTeamCommunityMembershipService service;
    public record TeamId(@NotNull Long teamId) {}
    public record CommunityId(@NotNull Long communityId) {}
    @GetMapping("/communities/{communityId}")
    public List<SportsTeamCommunityMembershipService.MembershipView> byCommunity(
        @PathVariable Long communityId,Authentication auth) {
        return service.communityTeams(communityId,auth.getName());
    }
    @GetMapping("/teams/{teamId}")
    public List<SportsTeamCommunityMembershipService.MembershipView> byTeam(
        @PathVariable Long teamId,Authentication auth) {
        return service.teamCommunities(teamId,auth.getName());
    }
    @PostMapping("/communities/{communityId}/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamCommunityMembershipService.MembershipView invite(@PathVariable Long communityId,
        @RequestBody @Valid TeamId request,Authentication auth) {
        return service.invite(communityId,request.teamId(),auth.getName());
    }
    @PostMapping("/teams/{teamId}/request")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamCommunityMembershipService.MembershipView request(@PathVariable Long teamId,
        @RequestBody @Valid CommunityId request,Authentication auth) {
        return service.request(teamId,request.communityId(),auth.getName());
    }
    @PostMapping("/{id}/accept")
    public SportsTeamCommunityMembershipService.MembershipView accept(@PathVariable Long id,Authentication auth) {
        return service.respond(id,true,auth.getName());
    }
    @PostMapping("/{id}/decline")
    public SportsTeamCommunityMembershipService.MembershipView decline(@PathVariable Long id,Authentication auth) {
        return service.respond(id,false,auth.getName());
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(@PathVariable Long id,Authentication auth) {
        service.leave(id,auth.getName());
    }
}
