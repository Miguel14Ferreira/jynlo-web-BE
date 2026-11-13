package com.example.desporto24.model.social;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "social_post")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Profile author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    // Internal interaction post for a public activity; excluded from ordinary posts.
    @Builder.Default
    @Column(name = "hide_likers", nullable = false)
    private boolean hideLikers = false;

    @Column(name = "activity_type", length = 32)
    private String activityType;

    @Column(nullable = false, length = 1000)
    private String content;

    /** Voluntary place label, never derived from the user's GPS. */
    @Column(name = "location_label", length = 160)
    private String locationLabel;

    @Column(name = "image_url", length = 1200)
    private String imageUrl;

    @Column(name = "image_storage_key", length = 800)
    private String imageStorageKey;

    @Column(name = "video_url", length = 1200)
    private String videoUrl;

    @Column(name = "video_storage_key", length = 800)
    private String videoStorageKey;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "social_post_extra_media", joinColumns = @JoinColumn(name = "post_id"))
    @OrderColumn(name = "position")
    @Builder.Default
    private java.util.List<PublicPostMedia> extraMedia = new java.util.ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
