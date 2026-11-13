package com.example.desporto24.model.social;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** A viewer's personal feed preference; never changes the author's post. */
@Entity
@Table(name = "social_post_hidden", uniqueConstraints =
        @UniqueConstraint(name = "uq_social_post_hidden_viewer_post", columnNames = {"viewer_id", "post_id"}))
@Getter @Setter
public class HiddenFeedPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "viewer_id", nullable = false)
    private Long viewerId;

    @Column(name = "post_id", nullable = false)
    private Long postId;
}
