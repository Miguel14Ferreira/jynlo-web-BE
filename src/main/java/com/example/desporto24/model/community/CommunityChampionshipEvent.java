package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Competition owned by a championship, not an independent community tournament. */
@Entity
@Table(name = "community_championship_event")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "championship_id", nullable = false)
    private CommunityChampionship championship;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "format", nullable = false, length = 24)
    private String format;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
