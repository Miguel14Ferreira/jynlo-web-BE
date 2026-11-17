package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamChallengeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/team-challenges")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class SportsTeamChallengeController {
    private final SportsTeamChallengeService service;
    public record CreateRequest(@NotNull @Positive Long challengedTeamId,@Size(max=280) String message) {}
    public record LinkRequest(@NotNull @Positive Long sessionId) {}

    @GetMapping("/teams/{teamId}")
    public List<SportsTeamChallengeService.ChallengeView> forTeam(
            @PathVariable Long teamId,Authentication auth) {
        return service.forTeam(teamId,auth.getName());
    }
    @PostMapping("/teams/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SportsTeamChallengeService.ChallengeView challenge(
            @PathVariable Long teamId,@Valid @RequestBody CreateRequest request,Authentication auth) {
        return service.create(teamId,request.challengedTeamId(),request.message(),auth.getName());
    }
    @PostMapping("/{id}/accept")
    public SportsTeamChallengeService.ChallengeView accept(@PathVariable Long id,Authentication auth) {
        return service.respond(id,true,auth.getName());
    }
    @PostMapping("/{id}/decline")
    public SportsTeamChallengeService.ChallengeView decline(@PathVariable Long id,Authentication auth) {
        return service.respond(id,false,auth.getName());
    }
    @PostMapping("/{id}/cancel")
    public SportsTeamChallengeService.ChallengeView cancel(@PathVariable Long id,Authentication auth) {
        return service.cancel(id,auth.getName());
    }
    @PostMapping("/{id}/session")
    public SportsTeamChallengeService.ChallengeView link(@PathVariable Long id,
            @Valid @RequestBody LinkRequest request,Authentication auth) {
        return service.linkSession(id,request.sessionId(),auth.getName());
    }
}
