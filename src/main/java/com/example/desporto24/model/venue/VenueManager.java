package com.example.desporto24.model.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** A person allowed to manage a venue's courts, hours and Jynlo bookings. */
@Entity
@Table(name = "venue_manager")
@Getter @Setter
public class VenueManager {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Column(name = "username", nullable = false, length = 120)
    private String username;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
