package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** One participant's result in one event: a value (time, score, mark) or an entered position. */
@Entity
@Table(name = "community_tournament_ranking_result")
@Getter @Setter
public class CommunityTournamentRankingResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Column(name = "result_value", precision = 12, scale = 3)
    private BigDecimal value;

    @Column(name = "entered_position")
    private Integer position;

    @Column(name = "did_not_finish", nullable = false)
    private boolean didNotFinish;
}
