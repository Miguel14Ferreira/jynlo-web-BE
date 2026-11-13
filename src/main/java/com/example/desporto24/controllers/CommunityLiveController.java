package com.example.desporto24.controllers;

import com.example.desporto24.service.CommunityLiveService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

/** Community-scoped scheduling and role-restricted broadcast connection. */
@RestController
@RequestMapping("/api/communities/{communityId}/live")
@RequiredArgsConstructor
public class CommunityLiveController {
    private final CommunityLiveService service;
    public record ScheduleRequest(@NotBlank @Size(max = 120) String title,
                                  @Size(max = 1000) String description,
                                  Instant scheduledAt) {}

    @GetMapping("/capabilities")
    public CommunityLiveService.Capabilities capabilities(@PathVariable Long communityId, Authentication auth) {
        return service.capabilities(communityId, auth.getName());
    }

    @GetMapping
    public List<CommunityLiveService.LiveEvent> list(@PathVariable Long communityId, Authentication auth) {
        return service.list(communityId, auth.getName());
    }

    @PostMapping
    public CommunityLiveService.LiveEvent schedule(@PathVariable Long communityId,
            @RequestBody @Valid ScheduleRequest request, Authentication auth) {
        return service.schedule(communityId, auth.getName(),
                request.title(), request.description(), request.scheduledAt());
    }

    @PostMapping("/{eventId}/start")
    public CommunityLiveService.LiveEvent start(@PathVariable Long communityId,
            @PathVariable Long eventId, Authentication auth) {
        return service.start(communityId, eventId, auth.getName());
    }

    @PostMapping("/{eventId}/heartbeat")
    public void heartbeat(@PathVariable Long communityId,
            @PathVariable Long eventId, Authentication auth) {
        service.heartbeat(communityId, eventId, auth.getName());
    }

    @PostMapping("/{eventId}/end")
    public CommunityLiveService.LiveEvent end(@PathVariable Long communityId,
            @PathVariable Long eventId, Authentication auth) {
        return service.end(communityId, eventId, auth.getName());
    }

    @PostMapping("/{eventId}/join")
    public CommunityLiveService.Connection join(@PathVariable Long communityId,
            @PathVariable Long eventId, Authentication auth) {
        return service.join(communityId, eventId, auth.getName());
    }
}
