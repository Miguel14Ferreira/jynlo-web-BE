package com.example.desporto24.controllers;

import com.example.desporto24.service.VenueBookingService;
import com.example.desporto24.service.VenueBookingService.ClaimView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin review of "Sou o responsável por este recinto" requests. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/venue-claims")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class AdminVenueClaimController {
    private final VenueBookingService service;

    public record DecisionBody(String note) {}

    @GetMapping
    public List<ClaimView> pending() {
        return service.pendingClaims();
    }

    @GetMapping("/history")
    public List<ClaimView> history() {
        return service.decidedClaims();
    }

    @PostMapping("/{id}/approve")
    public ClaimView approve(@PathVariable Long id, @RequestBody(required = false) DecisionBody body, Authentication auth) {
        return service.decideClaim(id, auth.getName(), true, body == null ? null : body.note());
    }

    @PostMapping("/{id}/reject")
    public ClaimView reject(@PathVariable Long id, @RequestBody(required = false) DecisionBody body, Authentication auth) {
        return service.decideClaim(id, auth.getName(), false, body == null ? null : body.note());
    }
}
