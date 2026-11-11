package com.example.desporto24.model.community;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Invitation is not tournament membership; the invited player must explicitly accept. */
@Entity
@Table(name = "community_tournament_team_invite",
        uniqueConstraints = @UniqueConstraint(name = "uk_tournament_team_invitee",
                columnNames = {"team_id", "profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityTournamentTeamInvite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private CommunityTournament tournament;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private CommunityTournamentTeam team;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    @Column(name = "invited_at", nullable = false)
    private Instant invitedAt;
}
