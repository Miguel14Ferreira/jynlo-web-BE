package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.*;

/** Direct qualification in the first knockout round of an owned cup. */
@Entity
@Table(name = "community_championship_cup_bye",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_champ_cup_bye_team", columnNames = {"event_id", "team_id"}),
        @UniqueConstraint(name = "uk_champ_cup_bye_slot", columnNames = {"event_id", "slot_number"})
    })
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipCupBye {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private CommunityChampionshipEvent event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private CommunityChampionshipTeam team;

    @Column(name = "slot_number", nullable = false)
    private int slotNumber;
}
