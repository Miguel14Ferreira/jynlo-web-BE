package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * A player of the team's squad, imported from a spreadsheet. Players without
 * a Jynlo account stay visible to the team owner only, so names from a file
 * are never published on the public team page.
 */
@Entity
@Table(name = "sports_team_roster_entry", schema = "desporto24")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SportsTeamRosterEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private SportsTeam team;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "shirt_number")
    private Integer shirtNumber;

    @Column(length = 40)
    private String position;

    /** Set when the row matched a Jynlo account (an invitation was sent). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
