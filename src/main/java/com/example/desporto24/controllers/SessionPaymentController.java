package com.example.desporto24.controllers;

import com.example.desporto24.service.SessionPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** "Quem já pagou?" — MB WAY directly to the organiser, bookkeeping in Jynlo. */
@RestController
@RequestMapping("/api/sessions/{id}/payments")
@RequiredArgsConstructor
public class SessionPaymentController {
    private final SessionPaymentService service;

    public record MbwayRequest(String phone) {}
    public record PaidRequest(Boolean paid) {}

    @GetMapping
    public SessionPaymentService.Payments get(@PathVariable Long id, Authentication auth) {
        return service.get(id, auth.getName());
    }

    @PutMapping("/mbway")
    public SessionPaymentService.Payments mbway(@PathVariable Long id, @RequestBody MbwayRequest request,
                                                Authentication auth) {
        return service.setMbway(id, auth.getName(), request == null ? null : request.phone());
    }

    @PutMapping("/me")
    public SessionPaymentService.Payments claim(@PathVariable Long id, @RequestBody PaidRequest request,
                                                Authentication auth) {
        return service.claim(id, auth.getName(), request != null && Boolean.TRUE.equals(request.paid()));
    }

    @PutMapping("/players/{username}")
    public SessionPaymentService.Payments confirm(@PathVariable Long id, @PathVariable String username,
                                                  @RequestBody PaidRequest request, Authentication auth) {
        return service.confirm(id, auth.getName(), username, request != null && Boolean.TRUE.equals(request.paid()));
    }
}
