package com.example.desporto24.model.social;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Private story response kept after the 24-hour story media expires. */
@Entity
@Table(name = "social_story_reply")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SocialStoryReply {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Story reference is a snapshot, not a foreign key: expiry must not erase replies.
    @Column(name = "story_id", nullable = false)
    private Long storyId;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Profile sender;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Profile recipient;
    @Column(name = "content", nullable = false, length = 500)
    private String content;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
