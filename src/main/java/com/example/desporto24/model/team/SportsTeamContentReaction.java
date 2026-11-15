package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** One reaction selection per confirmed member and content item. */
@Entity
@Table(name = "sports_team_content_reaction", schema = "desporto24",
       uniqueConstraints = @UniqueConstraint(name = "uk_team_content_reaction_person",
           columnNames = {"content_id", "profile_id"}),
       indexes = @Index(name = "idx_team_content_reaction_content", columnList = "content_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SportsTeamContentReaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false)
    private SportsTeamContent content;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    @Column(nullable = false, length = 16)
    private String emoji;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
