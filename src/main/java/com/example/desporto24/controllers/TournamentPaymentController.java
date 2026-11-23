package com.example.desporto24.controllers;

import com.example.desporto24.service.TournamentPaymentService;
import com.example.desporto24.service.TournamentPaymentService.Payments;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Tournament entry fee paid by MB WAY to the organiser or in person; players mark it paid, organisers confirm. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities/{communityId}/tournaments/{tournamentId}/payments")
public class TournamentPaymentController {
    private final TournamentPaymentService service;

    public record SetupBody(Integer entryFeeCents, String method, String phone) {}
    public record ClaimBody(Boolean paid) {}
    public record ConfirmBody(String username, Boolean paid) {}

    @GetMapping
    public Payments get(@PathVariable Long communityId, @PathVariable Long tournamentId, Authentication auth) {
        return service.get(communityId, tournamentId, auth.getName());
    }

    @PutMapping
    public Payments setup(@PathVariable Long communityId, @PathVariable Long tournamentId,
                          @RequestBody(required = false) SetupBody body, Authentication auth) {
        return service.setup(communityId, tournamentId, auth.getName(),
                body == null ? null : body.entryFeeCents(), body == null ? null : body.method(), body == null ? null : body.phone());
    }

    @PostMapping("/claim")
    public Payments claim(@PathVariable Long communityId, @PathVariable Long tournamentId,
                          @RequestBody(required = false) ClaimBody body, Authentication auth) {
        return service.claim(communityId, tournamentId, auth.getName(), body == null || body.paid() == null || body.paid());
    }

    @PostMapping("/confirm")
    public Payments confirm(@PathVariable Long communityId, @PathVariable Long tournamentId,
                            @RequestBody ConfirmBody body, Authentication auth) {
        return service.confirm(communityId, tournamentId, auth.getName(), body == null ? null : body.username(),
                body == null || body.paid() == null || body.paid());
    }
}
