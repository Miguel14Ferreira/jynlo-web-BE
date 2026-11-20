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
    @Column(name = "reply_to_id")
    private Long replyToId;
    @Column(name = "edited_at")
    private Instant editedAt;
    @Column(name = "deleted_at")
    private Instant deletedAt;
    // Team attachments are private. Storage keys are never exposed to clients.
    @Column(name = "attachment_key", length = 240)
    private String attachmentKey;
    @Column(name = "attachment_name", length = 160)
    private String attachmentName;
    @Column(name = "attachment_mime_type", length = 60)
    private String attachmentMimeType;
    @Column(name = "attachment_size")
    private Long attachmentSize;

    @ElementCollection(fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 50)
    @CollectionTable(name = "sports_team_content_extra_attachment",
            schema = "desporto24", joinColumns = @JoinColumn(name = "message_id"))
    @OrderColumn(name = "position")
    @Builder.Default
    private java.util.List<com.example.desporto24.model.chat.PrivateChatAttachment> extraAttachments =
            new java.util.ArrayList<>();

}