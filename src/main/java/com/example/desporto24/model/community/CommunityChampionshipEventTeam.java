package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.*;

/**
 * Registration of an existing championship-season team in one owned competition.
 * Never references standalone community tournaments.
 */
@Entity
@Table(name = "community_championship_event_team",
       uniqueConstraints = @UniqueConstraint(name = "uk_championship_event_team",
           columnNames = {"event_id", "championship_team_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipEventTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private CommunityChampionshipEvent event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "championship_team_id", nullable = false)
    private CommunityChampionshipTeam team;
}
