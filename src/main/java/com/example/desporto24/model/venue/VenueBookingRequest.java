package com.example.desporto24.model.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** A player's request for a court slot; the venue confirms or declines it. Paid at the venue for now. */
@Entity
@Table(name = "venue_booking_request")
@Getter @Setter
public class VenueBookingRequest {
    public enum Status { PENDING, CONFIRMED, DECLINED, CANCELLED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Column(name = "court_id", nullable = false)
    private Long courtId;

    @Column(name = "requester_username", nullable = false, length = 120)
    private String requesterUsername;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private Status status = Status.PENDING;

    @Column(name = "price_cents")
    private Integer priceCents;

    @Column(name = "player_note", length = 300)
    private String playerNote;

    @Column(name = "owner_note", length = 300)
    private String ownerNote;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "decided_at")
    private Instant decidedAt;

    /** The player says they paid (MB WAY or at the venue). */
    @Column(name = "paid_claimed_at")
    private Instant paidClaimedAt;

    /** The venue confirms it received the payment. */
    @Column(name = "paid_confirmed_at")
    private Instant paidConfirmedAt;
}
