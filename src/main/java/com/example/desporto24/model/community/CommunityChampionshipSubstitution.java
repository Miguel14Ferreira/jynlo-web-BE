package com.example.desporto24.model.community;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** A recorded substitution; does not assert an official starting lineup or minutes played. */
@Entity
@Table(name = "community_championship_substitution")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipSubstitution {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private CommunityChampionshipFixture fixture;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private CommunityChampionshipTeam team;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "out_profile_id", nullable = false)
    private Profile playerOut;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "in_profile_id", nullable = false)
    private Profile playerIn;
    @Column(name = "match_minute", nullable = false)
    private int matchMinute;
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;
}
