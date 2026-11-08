package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** A stable team identity within one championship season, shared across its tournaments. */
@Entity
@Table(name = "community_championship_team", uniqueConstraints =
        @UniqueConstraint(name = "uk_championship_team_name",
                columnNames = {"championship_id", "normalized_name"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipTeam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "championship_id", nullable = false)
    private CommunityChampionship championship;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "normalized_name", nullable = false, length = 100)
    private String normalizedName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
