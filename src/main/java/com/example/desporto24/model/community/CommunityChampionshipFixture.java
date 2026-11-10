package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.*;

/** An independently scheduled match inside a championship-owned league. */
@Entity
@Table(name = "community_championship_fixture",
        uniqueConstraints = @UniqueConstraint(name = "uk_championship_fixture_round",
                columnNames = {"event_id", "round_number", "match_number"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipFixture {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private CommunityChampionshipEvent event;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "match_number", nullable = false)
    private int matchNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_team_id", nullable = false)
    private CommunityChampionshipTeam homeTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "away_team_id", nullable = false)
    private CommunityChampionshipTeam awayTeam;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "result_recorded_at")
    private java.time.Instant resultRecordedAt;
}
