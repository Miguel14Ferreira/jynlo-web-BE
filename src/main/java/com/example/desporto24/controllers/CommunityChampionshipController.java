package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.community.CommunityChampionshipResponse;
import com.example.desporto24.model.dto.community.CreateCommunityChampionshipRequest;
import com.example.desporto24.service.CommunityChampionshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities/{communityId}/championships")
@PreAuthorize("isAuthenticated()")
public class CommunityChampionshipController {
    private final CommunityChampionshipService championships;

    @GetMapping
    public List<CommunityChampionshipResponse> list(@PathVariable Long communityId, Authentication auth) {
        return championships.list(communityId, auth.getName());
    }

    @PostMapping
    public ResponseEntity<CommunityChampionshipResponse> create(
            @PathVariable Long communityId,
            @RequestBody @Valid CreateCommunityChampionshipRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(championships.create(communityId, auth.getName(), request));
    }

    @PutMapping("/{championshipId}/competitions/{tournamentId}/placement-points")
    public CommunityChampionshipResponse setPlacementPoints(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long tournamentId,
            @RequestBody @Valid com.example.desporto24.model.dto.community.ChampionshipPlacementPointsRequest request,
            Authentication auth) {
        return championships.setPlacementPoints(communityId, championshipId,
                tournamentId, request.pointsByPlace(), auth.getName());
    }

    @PutMapping("/{championshipId}/competitions/{tournamentId}/winner-points")
    public CommunityChampionshipResponse setWinnerPoints(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long tournamentId, @RequestBody @Valid WinnerPointsRequest request,
            Authentication auth) {
        return championships.setWinnerPoints(communityId, championshipId,
                tournamentId, request.points(), auth.getName());
    }

    public record WinnerPointsRequest(@jakarta.validation.constraints.Min(0)
                                      @jakarta.validation.constraints.Max(10000) int points) {}

    @PostMapping("/{championshipId}/competitions/{tournamentId}")
    public CommunityChampionshipResponse addCompetition(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long tournamentId, Authentication auth) {
        return championships.addCompetition(communityId, championshipId, tournamentId, auth.getName());
    }
}
