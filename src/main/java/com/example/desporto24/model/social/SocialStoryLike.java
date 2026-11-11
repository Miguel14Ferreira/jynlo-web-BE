package com.example.desporto24.model.social;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "social_story_like", uniqueConstraints = @UniqueConstraint(
    name = "uk_social_story_like", columnNames = {"story_id", "profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SocialStoryLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private SocialStory story;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
