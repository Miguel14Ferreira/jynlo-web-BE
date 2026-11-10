package com.example.desporto24.model.community;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** One real community member can represent one team in a season. */
@Entity
@Table(name = "community_championship_team_player",
        uniqueConstraints = @UniqueConstraint(name = "uk_championship_player_season",
                columnNames = {"championship_id", "profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipTeamPlayer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "championship_id", nullable = false)
    private CommunityChampionship championship;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private CommunityChampionshipTeam team;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;
}
