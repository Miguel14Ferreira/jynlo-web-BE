package com.example.desporto24.model.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** A venue no longer takes booking requests from this player (e.g. they did not pay). Private to the venue. */
@Entity
@Table(name = "venue_player_block")
@Getter @Setter
public class VenuePlayerBlock {
    public enum Reason { NOT_PAID, NO_SHOW, BEHAVIOUR, OTHER }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Column(name = "username", nullable = false, length = 120)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 16)
    private Reason reason;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "created_by", nullable = false, length = 120)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
