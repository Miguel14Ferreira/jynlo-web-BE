package com.example.desporto24.model.community;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Invitation is not membership; only the invited profile can accept. */
@Entity
@Table(name = "community_championship_team_invite",
       uniqueConstraints = @UniqueConstraint(name = "uk_championship_team_invitee",
               columnNames = {"team_id", "profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityChampionshipTeamInvite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "championship_id", nullable = false)
    private CommunityChampionship championship;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private CommunityChampionshipTeam team;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    @Column(name = "invited_at", nullable = false)
    private Instant invitedAt;
}
