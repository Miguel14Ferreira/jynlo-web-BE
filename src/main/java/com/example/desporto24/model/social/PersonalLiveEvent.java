package com.example.desporto24.model.social;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** A user's public broadcast, independent of any community membership. */
@Entity
@Table(name = "personal_live_event")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonalLiveEvent {
    public enum Status { LIVE, ENDED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "host_profile_id", nullable = false)
    private Profile host;
    @Column(nullable = false, length = 120)
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "ended_at")
    private Instant endedAt;
    @Column(name = "host_heartbeat_at")
    private Instant hostHeartbeatAt;
    @Column(name="chat_enabled",nullable=false)
    @Builder.Default private boolean chatEnabled=true;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="guest_profile_id")
    private Profile guest;
}
