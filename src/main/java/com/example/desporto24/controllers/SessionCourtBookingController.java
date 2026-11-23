package com.example.desporto24.controllers;

import com.example.desporto24.service.SessionCourtBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** "Reservar campo": the club's booking link and whether the court is booked. */
@RestController
@RequestMapping("/api/sessions/{id}/court-booking")
@RequiredArgsConstructor
public class SessionCourtBookingController {
    private final SessionCourtBookingService service;

    public record UpdateRequest(String bookingUrl, Boolean booked) {}

    @GetMapping
    public SessionCourtBookingService.CourtBooking get(@PathVariable Long id, Authentication auth) {
        return service.get(id, auth.getName());
    }

    @PutMapping
    public SessionCourtBookingService.CourtBooking update(@PathVariable Long id, @RequestBody UpdateRequest request,
                                                          Authentication auth) {
        return service.update(id, auth.getName(), request == null ? null : request.bookingUrl(),
                request == null ? null : request.booked());
    }
}
