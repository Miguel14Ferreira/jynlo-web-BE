package com.example.desporto24.controllers;

import com.example.desporto24.service.EventRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** "Classificação por provas" for /api/communities/{c}/tournaments/{t}/event-ranking. */
@RestController
@RequestMapping("/api/communities/{communityId}/tournaments/{tournamentId}/event-ranking")
@RequiredArgsConstructor
public class EventRankingController {
    private final EventRankingService service;

    public record KindRequest(String valueKind) {}
    public record PointsRequest(String pointsByPlace) {}

    @GetMapping
    public EventRankingService.Overview overview(@PathVariable Long communityId, @PathVariable Long tournamentId,
                                                 Authentication auth) {
        return service.overview(communityId, tournamentId, auth.getName());
    }

    @PutMapping("/value-kind")
    public EventRankingService.Overview valueKind(@PathVariable Long communityId, @PathVariable Long tournamentId,
                                                  @RequestBody KindRequest request, Authentication auth) {
        return service.setValueKind(communityId, tournamentId, auth.getName(), request == null ? null : request.valueKind());
    }

    @PutMapping("/points")
    public EventRankingService.Overview points(@PathVariable Long communityId, @PathVariable Long tournamentId,
                                               @RequestBody PointsRequest request, Authentication auth) {
        return service.setPoints(communityId, tournamentId, auth.getName(), request == null ? null : request.pointsByPlace());
    }

    @PostMapping("/events")
    public EventRankingService.Overview create(@PathVariable Long communityId, @PathVariable Long tournamentId,
                                               @RequestBody EventRankingService.EventInput input, Authentication auth) {
        return service.saveEvent(communityId, tournamentId, null, auth.getName(), input);
    }

    @PutMapping("/events/{eventId}")
    public EventRankingService.Overview update(@PathVariable Long communityId, @PathVariable Long tournamentId,
                                               @PathVariable Long eventId,
                                               @RequestBody EventRankingService.EventInput input, Authentication auth) {
        return service.saveEvent(communityId, tournamentId, eventId, auth.getName(), input);
    }

    @DeleteMapping("/events/{eventId}")
    public EventRankingService.Overview delete(@PathVariable Long communityId, @PathVariable Long tournamentId,
                                               @PathVariable Long eventId, Authentication auth) {
        return service.deleteEvent(communityId, tournamentId, eventId, auth.getName());
    }

    @PostMapping("/complete")
    public EventRankingService.Overview complete(@PathVariable Long communityId, @PathVariable Long tournamentId,
                                                 Authentication auth) {
        return service.complete(communityId, tournamentId, auth.getName());
    }
}
