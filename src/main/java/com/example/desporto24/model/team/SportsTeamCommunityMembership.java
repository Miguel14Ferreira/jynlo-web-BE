package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Global team membership in a community. Never implies individual player membership. */
@Entity
@Table(name = "sports_team_community_membership", schema = "desporto24",
    uniqueConstraints = @UniqueConstraint(name = "uk_team_community_membership",
       columnNames = {"team_id", "community_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SportsTeamCommunityMembership {
    public enum Status { PENDING_TEAM, PENDING_COMMUNITY, ACTIVE, DECLINED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private SportsTeam team;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_profile_id", nullable = false)
    private Profile requestedBy;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private Status status;
    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;
    @Column(name = "decided_at")
    private Instant decidedAt;
}
