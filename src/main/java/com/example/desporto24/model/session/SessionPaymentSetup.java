package com.example.desporto24.model.session;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/** Where players pay the organiser. Jynlo never holds or moves the money. */
@Entity
@Table(name = "session_payment_setup")
@Getter @Setter
public class SessionPaymentSetup {
    @Id
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "mbway_phone", nullable = false, length = 16)
    private String mbwayPhone;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
