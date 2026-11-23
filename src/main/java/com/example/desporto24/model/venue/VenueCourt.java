package com.example.desporto24.model.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** A court the venue offers on Jynlo, with its price per slot (paid at the venue for now). */
@Entity
@Table(name = "venue_court")
@Getter @Setter
public class VenueCourt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "sport", length = 80)
    private String sport;

    /** Price of one slot in cents; null = ask at the venue. */
    @Column(name = "price_cents")
    private Integer priceCents;

    @Column(name = "slot_minutes", nullable = false)
    private int slotMinutes = 60;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
