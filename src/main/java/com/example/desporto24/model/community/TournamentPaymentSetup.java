package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** Entry fee of a tournament and how players pay it: MB WAY to the organiser's number, or in person. */
@Entity
@Table(name = "tournament_payment_setup")
@Getter @Setter
public class TournamentPaymentSetup {
    @Id
    @Column(name = "tournament_id")
    private Long tournamentId;

    @Column(name = "entry_fee_cents")
    private Integer entryFeeCents;

    @Column(name = "method", nullable = false, length = 16)
    private String method;

    @Column(name = "mbway_phone", length = 16)
    private String mbwayPhone;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
