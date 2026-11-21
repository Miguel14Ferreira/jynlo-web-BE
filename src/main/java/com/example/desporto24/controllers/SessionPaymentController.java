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

    public record MethodRequest(String method, String phone) {}
    public record PaidRequest(Boolean paid) {}

    @GetMapping
    public SessionPaymentService.Payments get(@PathVariable Long id, Authentication auth) {
        return service.get(id, auth.getName());
    }

    @PutMapping("/method")
    public SessionPaymentService.Payments method(@PathVariable Long id, @RequestBody MethodRequest request,
                                                 Authentication auth) {
        return service.setMethod(id, auth.getName(),
                request == null ? null : request.method(), request == null ? null : request.phone());
    }

    @PostMapping("/remind")
    public SessionPaymentService.Payments remind(@PathVariable Long id, Authentication auth) {
        return service.remindUnpaid(id, auth.getName());
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
