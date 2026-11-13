package com.example.desporto24.model.social;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "social_post_comment_like",
    uniqueConstraints = @UniqueConstraint(name = "uk_social_post_comment_like",
        columnNames = {"comment_id", "profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SocialPostCommentLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private SocialPostComment comment;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
