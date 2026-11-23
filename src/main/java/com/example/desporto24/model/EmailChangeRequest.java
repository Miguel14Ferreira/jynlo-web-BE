package com.example.desporto24.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * A pending change of a profile's email: applied only when the link sent to the
 * new address is opened. Only a SHA-256 of the link token is stored.
 */
@Entity
@Table(name = "email_change_request")
@Getter @Setter
public class EmailChangeRequest {
    @Id
    @Column(name = "token_hash", length = 64)
    private String tokenHash;
    @Column(name = "username", nullable = false, length = 255)
    private String username;
    @Column(name = "new_email", nullable = false, length = 254)
    private String newEmail;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "used_at")
    private Instant usedAt;
}
