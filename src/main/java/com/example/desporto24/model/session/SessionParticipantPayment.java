package com.example.desporto24.model.session;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Payment bookkeeping for one place: the player says "já paguei" (claimed) and the
 * organiser confirms it arrived (confirmed). Either can exist on its own.
 */
@Entity
@Table(name = "session_participant_payment")
@Getter @Setter
public class SessionParticipantPayment {
    @Id
    @Column(name = "participant_id")
    private Long participantId;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;
}
