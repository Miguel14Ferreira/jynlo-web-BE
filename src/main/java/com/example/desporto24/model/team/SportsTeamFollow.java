package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Following a team grants no membership or private content permissions. */
@Entity
@Table(name="sports_team_follow",schema="desporto24",
       uniqueConstraints=@UniqueConstraint(name="uk_sports_team_follow",
           columnNames={"team_id","follower_profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SportsTeamFollow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="team_id",nullable=false)
    private SportsTeam team;

    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="follower_profile_id",nullable=false)
    private Profile follower;

    @Column(name="created_at",nullable=false)
    private Instant createdAt;
}
