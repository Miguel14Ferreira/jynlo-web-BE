package com.example.desporto24.model.community;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.Instant;

@Entity
@Table(name = "community_tournament_team", uniqueConstraints =
        @UniqueConstraint(name = "uk_tournament_team_normalized_name",
                columnNames = {"tournament_id", "normalized_name"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityTournamentTeam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CommunityTournament tournament;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "normalized_name", nullable = false, length = 100)
    private String normalizedName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "championship_team_id")
    private CommunityChampionshipTeam championshipTeam;

    /** The first player accepting membership becomes administrator. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_profile_id")
    private com.example.desporto24.model.Profile adminProfile;

    /** Transfer is effective only after the proposed team member accepts. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pending_admin_profile_id")
    private com.example.desporto24.model.Profile pendingAdminProfile;

    // Assigned only when the organizer publishes the group-stage draw.
    @Column(name = "group_number")
    private Integer groupNumber;

    @Column(name = "group_draw_order")
    private Integer groupDrawOrder;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
