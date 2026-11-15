package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Invitation only: a pending request never creates a competition entry or enrolls players. */
@Entity
@Table(name = "sports_team_competition_invitation", schema = "desporto24",
    uniqueConstraints = @UniqueConstraint(name = "uk_sports_team_competition_invite",
        columnNames = {"team_id", "competition_type", "competition_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SportsTeamCompetitionInvitation {
    public enum CompetitionType { TOURNAMENT, CHAMPIONSHIP }
    public enum Status { PENDING, ACCEPTED, DECLINED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private SportsTeam team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by_profile_id", nullable = false)
    private Profile invitedBy;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "competition_type", nullable = false, length = 20)
    private CompetitionType competitionType;

    @Column(name = "competition_id", nullable = false)
    private Long competitionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "responded_at")
    private Instant respondedAt;
}
