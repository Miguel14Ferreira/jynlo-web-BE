package com.example.desporto24.model.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** One payment attempt in the app (a Stripe Checkout Session on the organiser's account). */
@Entity
@Table(name = "session_online_payment")
@Getter @Setter
public class SessionOnlinePayment {
    public enum Status { PENDING, PAID }

    @Id
    @Column(name = "checkout_session_id", length = 128)
    private String checkoutSessionId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

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
