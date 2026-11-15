package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** A team's application; approval does not automatically enroll a roster. */
@Entity
@Table(name = "sports_team_competition_application", schema = "desporto24",
  uniqueConstraints = @UniqueConstraint(name = "uk_team_competition_application",
    columnNames = {"team_id", "competition_type", "competition_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SportsTeamCompetitionApplication {
    public enum CompetitionType { TOURNAMENT, CHAMPIONSHIP }
    public enum Status { PENDING, APPROVED, DECLINED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private SportsTeam team;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_profile_id", nullable = false)
    private Profile requestedBy;
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
