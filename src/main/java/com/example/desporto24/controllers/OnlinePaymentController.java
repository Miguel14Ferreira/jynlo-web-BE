package com.example.desporto24.controllers;

import com.example.desporto24.service.payment.OnlinePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Paying games inside Jynlo, straight to the organiser's own Stripe account. */
@RestController
@RequiredArgsConstructor
public class OnlinePaymentController {
    private final OnlinePaymentService service;

    public record OnboardingRequest(String returnPath) {}

    @GetMapping("/api/payments/online/status")
    public OnlinePaymentService.Status status(Authentication auth) {
        return service.status(auth.getName());
    }

    @PostMapping("/api/payments/online/onboarding")
    public OnlinePaymentService.Link onboarding(@RequestBody(required = false) OnboardingRequest request, Authentication auth) {
        return service.onboarding(auth.getName(), request == null ? null : request.returnPath());
    }

    @PostMapping("/api/sessions/{id}/payments/checkout")
    public OnlinePaymentService.Link checkout(@PathVariable Long id, Authentication auth) {
        return service.checkout(id, auth.getName());
    }

    /** Called by Stripe; trusted only through the signature check. */
    @PostMapping("/api/payments/online/stripe-webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload,
                                        @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
        service.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
