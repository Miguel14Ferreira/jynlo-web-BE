package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

/** One event ("prova") of an EVENT_RANKING tournament. */
@Entity
@Table(name = "community_tournament_ranking_event")
@Getter @Setter
public class CommunityTournamentRankingEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "held_on")
    private LocalDate heldOn;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
