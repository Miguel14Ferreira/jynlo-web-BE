package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Independent private team timelines. No invitation grants read access. */
@Entity
@Table(name = "sports_team_content", schema = "desporto24",
       indexes = @Index(name = "idx_sports_team_content_feed", columnList = "team_id,kind,id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SportsTeamContent {
    public enum Kind { CHAT, FEED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private SportsTeam team;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_profile_id", nullable = false)
    private Profile author;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Kind kind;
    @Column(nullable = false, length = 2000)
    private String body;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}