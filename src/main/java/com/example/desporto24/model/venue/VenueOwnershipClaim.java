package com.example.desporto24.model.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** "Sou o responsável por este recinto": reviewed by an admin before the person can manage it. */
@Entity
@Table(name = "venue_ownership_claim")
@Getter @Setter
public class VenueOwnershipClaim {
    public enum Status { PENDING, APPROVED, REJECTED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Column(name = "username", nullable = false, length = 120)
    private String username;

    @Column(name = "role_description", nullable = false, length = 300)
    private String roleDescription;

    @Column(name = "contact_phone", length = 40)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private Status status = Status.PENDING;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Column(name = "decided_by", length = 120)
    private String decidedBy;

    @Column(name = "decision_note", length = 500)
    private String decisionNote;
}
