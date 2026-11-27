package com.example.desporto24.model.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** One online payment attempt for a court booking (a Stripe Checkout Session on the venue's account). */
@Entity
@Table(name = "venue_online_payment")
@Getter @Setter
public class VenueOnlinePayment {
    public enum Status { PENDING, PAID }

    @Id
    @Column(name = "checkout_session_id", length = 128)
    private String checkoutSessionId;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "stripe_account_id", nullable = false, length = 64)
    private String stripeAccountId;

    @Column(name = "amount_cents", nullable = false)
    private int amountCents;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;
}
