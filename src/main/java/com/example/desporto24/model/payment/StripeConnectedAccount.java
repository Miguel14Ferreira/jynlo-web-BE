package com.example.desporto24.model.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** An organiser's own Stripe account. Players pay straight into it; Jynlo never holds the money. */
@Entity
@Table(name = "stripe_connected_account")
@Getter @Setter
public class StripeConnectedAccount {
    @Id
    @Column(name = "username", length = 80)
    private String username;

    @Column(name = "stripe_account_id", nullable = false, unique = true, length = 64)
    private String stripeAccountId;

    @Column(name = "charges_enabled", nullable = false)
    private boolean chargesEnabled;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
