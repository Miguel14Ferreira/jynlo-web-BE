package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** A season-long container for independent competitions; never replaces tournaments. */
@Entity
@Table(name = "community_championship")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionship {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 100)
    private String sport;

    @Column(name = "season_label", nullable = false, length = 50)
    private String seasonLabel;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToMany
    @JoinTable(name = "community_championship_tournament",
        joinColumns = @JoinColumn(name = "championship_id"),
        inverseJoinColumns = @JoinColumn(name = "tournament_id"),
        uniqueConstraints = @UniqueConstraint(name = "uk_championship_tournament", columnNames = "tournament_id"))
    @OrderColumn(name = "competition_order")
    @Builder.Default
    private List<CommunityTournament> competitions = new ArrayList<>();
}
