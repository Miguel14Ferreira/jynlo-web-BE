package com.example.desporto24.model.social;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** One recorded view per authenticated profile and Story (never anonymous). */
@Entity
@Table(name = "social_story_view", uniqueConstraints = @UniqueConstraint(
    name = "uk_social_story_view", columnNames = {"story_id", "profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SocialStoryView {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private SocialStory story;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    @Column(name = "viewed_at", nullable = false)
    private Instant viewedAt;
}
