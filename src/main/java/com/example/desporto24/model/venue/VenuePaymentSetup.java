package com.example.desporto24.model.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** How players pay a venue for Jynlo bookings: MB WAY to its number, or at the venue. Jynlo never holds the money. */
@Entity
@Table(name = "venue_payment_setup")
@Getter @Setter
public class VenuePaymentSetup {
    @Id
    @Column(name = "venue_id")
    private Long venueId;

    @Column(name = "method", nullable = false, length = 16)
    private String method;

    @Column(name = "mbway_phone", length = 16)
    private String mbwayPhone;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
