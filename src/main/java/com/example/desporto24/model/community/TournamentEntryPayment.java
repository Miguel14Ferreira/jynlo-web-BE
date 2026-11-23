package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** A player's entry-fee payment: they mark it paid, an organiser confirms it. */
@Entity
@Table(name = "tournament_entry_payment")
@Getter @Setter
public class TournamentEntryPayment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;
}
