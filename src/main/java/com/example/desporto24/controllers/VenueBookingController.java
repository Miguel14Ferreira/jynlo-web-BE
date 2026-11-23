package com.example.desporto24.controllers;

import com.example.desporto24.service.VenueBookingService;
import com.example.desporto24.service.VenueBookingService.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Jynlo court bookings: claim a venue, publish courts and hours, request and answer bookings. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VenueBookingController {
    private final VenueBookingService service;

    public record ClaimRequest(String roleDescription, String contactPhone) {}
    public record BookingRequestBody(Long courtId, String startAt, Long sessionId, String note) {}
    public record DecisionBody(String note) {}

    // Public reads (listed in SecurityConstant.PUBLIC_GET_URLS).
    @GetMapping("/venues/{id}/booking-offer")
    public Offer offer(@PathVariable Long id) {
        return service.offer(id);
    }

    @GetMapping("/venues/{id}/booking-slots")
    public DaySlots slots(@PathVariable Long id, @RequestParam(required = false) String date) {
        return service.slots(id, date);
    }

    @GetMapping("/venue-bookings/accepting")
    public List<Long> accepting() {
        return service.venuesAcceptingBookings();
    }

    // Signed-in.
    @GetMapping("/venues/{id}/management")
    public ManagementStatus status(@PathVariable Long id, Authentication auth) {
        return service.status(id, auth.getName());
    }

    @PostMapping("/venues/{id}/claims")
    public ClaimView claim(@PathVariable Long id, @RequestBody ClaimRequest body, Authentication auth) {
        return service.claim(id, auth.getName(), body == null ? null : body.roleDescription(),
                body == null ? null : body.contactPhone());
    }

    @GetMapping("/venues/{id}/management/offer")
    public Offer managerOffer(@PathVariable Long id, Authentication auth) {
        return service.managerOffer(id, auth.getName());
    }

    @PutMapping("/venues/{id}/management/courts")
    public Offer saveCourts(@PathVariable Long id, @RequestBody List<CourtInput> body, Authentication auth) {
        return service.saveCourts(id, auth.getName(), body);
    }

    @PutMapping("/venues/{id}/management/hours")
    public Offer saveHours(@PathVariable Long id, @RequestBody List<HoursInput> body, Authentication auth) {
        return service.saveHours(id, auth.getName(), body);
    }

    @GetMapping("/venues/{id}/management/requests")
    public List<BookingView> venueRequests(@PathVariable Long id, Authentication auth) {
        return service.venueRequests(id, auth.getName());
    }

    @PostMapping("/venues/{id}/booking-requests")
    public BookingView request(@PathVariable Long id, @RequestBody BookingRequestBody body, Authentication auth) {
        if (body == null) return service.request(id, auth.getName(), null, null, null, null);
        return service.request(id, auth.getName(), body.courtId(), body.startAt(), body.sessionId(), body.note());
    }

    @GetMapping("/venue-bookings/mine")
    public List<BookingView> mine(Authentication auth) {
        return service.mine(auth.getName());
    }

    @GetMapping("/venue-bookings/managed")
    public List<ManagedVenue> managed(Authentication auth) {
        return service.managedVenues(auth.getName());
    }

    @PostMapping("/venue-bookings/{id}/cancel")
    public BookingView cancel(@PathVariable Long id, Authentication auth) {
        return service.cancel(id, auth.getName());
    }

    @PostMapping("/venue-bookings/{id}/confirm")
    public BookingView confirm(@PathVariable Long id, @RequestBody(required = false) DecisionBody body, Authentication auth) {
        return service.decide(id, auth.getName(), true, body == null ? null : body.note());
    }

    @PostMapping("/venue-bookings/{id}/decline")
    public BookingView decline(@PathVariable Long id, @RequestBody(required = false) DecisionBody body, Authentication auth) {
        return service.decide(id, auth.getName(), false, body == null ? null : body.note());
    }
}
