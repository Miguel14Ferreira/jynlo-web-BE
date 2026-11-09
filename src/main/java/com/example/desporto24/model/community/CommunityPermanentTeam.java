package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Persistent team identity within one community, independent from seasons. */
@Entity
@Table(name = "community_permanent_team", uniqueConstraints =
        @UniqueConstraint(name = "uk_permanent_team_community_name",
                columnNames = {"community_id", "normalized_name"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityPermanentTeam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(name = "normalized_name", nullable = false, length = 100)
    private String normalizedName;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
