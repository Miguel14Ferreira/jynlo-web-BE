package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamDeletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class SportsTeamDeletionController {
    private final SportsTeamDeletionService deletion;

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteEmpty(@PathVariable Long teamId, Authentication auth) {
        deletion.deleteEmpty(teamId, auth.getName());
    }
}
