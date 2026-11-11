package com.example.desporto24.model.community;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Match card for a registered player in a football or futsal season. */
@Entity
@Table(name = "community_championship_disciplinary_card")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipDisciplinaryCard {
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

    @Column(name = "card_type", nullable = false, length = 10)
    private String cardType;

    @Column(name = "match_minute")
    private Integer matchMinute;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;
}
