package com.example.desporto24.model.social;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "social_story")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SocialStory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Profile author;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;
    @Column(length = 180)
    private String caption;
    // Visual metadata is saved with the Story, so the editor preview matches
    // how every viewer sees it on web/mobile after publication.
    @Column(name = "text_position", length = 10)
    private String textPosition;
    @Column(name = "text_scale", length = 10)
    private String textScale;
    @Column(name = "text_style", length = 10)
    private String textStyle;
    @Column(name = "text_x")
    private Double textX;
    @Column(name = "text_y")
    private Double textY;
    @Column(name = "text_rotation")
    private Double textRotation;
    @Column(name = "text_zoom")
    private Double textZoom;
    @Column(name = "text_color", length = 16)
    private String textColor;
    @Column(name = "text_background", length = 16)
    private String textBackground;
    @Column(name = "text_font", length = 16)
    private String textFont;
    @Column(name = "alt_text", length = 350)
    private String altText;
    @Column(name = "location_label", length = 160)
    private String locationLabel;
    @Column(name = "tagged_usernames", length = 500)
    private String taggedUsernames;
    @Column(name = "show_time", nullable = false)
    private boolean showTime;
    @Column(name = "media_url", nullable = false, length = 1200)
    private String mediaUrl;
    @Column(name = "media_storage_key", nullable = false, length = 800)
    private String mediaStorageKey;
    @Column(name = "is_video", nullable = false)
    private boolean video;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
