package com.example.desporto24.model.community;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Explicit initial match selection; no inferred appearances or minutes played. */
@Entity
@Table(name = "community_championship_lineup_player",
       uniqueConstraints = @UniqueConstraint(name = "uk_championship_lineup_fixture_profile",
               columnNames = {"fixture_id", "profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipLineupPlayer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private CommunityChampionshipFixture fixture;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private CommunityChampionshipTeam team;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile player;
    @Column(name = "selection", length = 10, nullable = false)
    private String selection;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
