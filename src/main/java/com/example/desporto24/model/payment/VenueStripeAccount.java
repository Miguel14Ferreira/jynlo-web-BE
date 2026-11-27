package com.example.desporto24.model.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** A venue's own Stripe account (the company's, not a manager's). Bookings are paid straight into it. */
@Entity
@Table(name = "venue_stripe_account")
@Getter @Setter
public class VenueStripeAccount {
    @Id
    @Column(name = "venue_id")
    private Long venueId;

    @Column(name = "stripe_account_id", nullable = false, unique = true, length = 64)
    private String stripeAccountId;

    @Column(name = "charges_enabled", nullable = false)
    private boolean chargesEnabled;

    @Column(name = "created_by", length = 80)
    private String createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
