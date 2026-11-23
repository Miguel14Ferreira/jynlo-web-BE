package com.example.desporto24.model.session;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Where the organiser books the court (the club's own page, Playtomic, WhatsApp…)
 * and whether it is already booked. Jynlo only links out; it never books or pays.
 */
@Entity
@Table(name = "session_court_booking")
@Getter @Setter
public class SessionCourtBooking {
    @Id
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "booking_url", length = 500)
    private String bookingUrl;

    @Column(name = "booked", nullable = false)
    private boolean booked;

    @Column(name = "booked_at")
    private Instant bookedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
