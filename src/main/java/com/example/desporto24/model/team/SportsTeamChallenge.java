package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Direct, voluntary inter-team challenge. Acceptance never enrolls players. */
@Entity
@Table(schema="desporto24",name="sports_team_challenge",
    indexes={
        @Index(name="idx_team_challenge_challenger",columnList="challenger_team_id,status"),
        @Index(name="idx_team_challenge_challenged",columnList="challenged_team_id,status")
    })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SportsTeamChallenge {
    public enum Status { PENDING, ACCEPTED, DECLINED, CANCELLED }

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="challenger_team_id",nullable=false)
    private SportsTeam challenger;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="challenged_team_id",nullable=false)
    private SportsTeam challenged;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="created_by_profile_id",nullable=false)
    private Profile createdBy;
    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable=false,length=16)
    private Status status;
    @Column(name="message",length=280)
    private String message;
    @Column(name="session_id")
    private Long sessionId;
    @Column(name="created_at",nullable=false)
    private Instant createdAt;
    @Column(name="responded_at")
    private Instant respondedAt;
    @Column(name="linked_at")
    private Instant linkedAt;
}
