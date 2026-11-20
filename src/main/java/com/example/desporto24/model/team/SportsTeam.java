package com.example.desporto24.model.team;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Global team identity, independent of a tournament, season or community. */
@Entity
@Table(name = "sports_team", schema = "desporto24")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SportsTeam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_profile_id", nullable = false)
    private Profile owner;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String slug;

    @Column(length = 100)
    private String sport;

    @Column(length = 100)
    private String location;

    @Column(length = 500)
    private String description;

    @Column(name = "logo_url", length = 1000)
    private String logoUrl;

    /** Editable project for generated crests, never interpreted as HTML or remote SVG. */
    @Column(name = "crest_design", columnDefinition = "text")
    private String crestDesign;

    @Column(name = "kit_primary", length = 7)
    private String kitPrimary;

    @Column(name = "kit_secondary", length = 7)
    private String kitSecondary;

    @Column(name = "kit_pattern", length = 16)
    private String kitPattern;

    @Column(name = "kit_garment", length = 24)
    private String kitGarment;

    @Column(name = "kit_accent", length = 7)
    private String kitAccent;

    @Column(name = "kit_player_name", length = 14)
    private String kitPlayerName;

    @Column(name = "kit_player_number", length = 2)
    private String kitPlayerNumber;

    /** Optional alternate/training kits; canonical primary kit uses the legacy columns. */
    @Column(name = "kit_variants", columnDefinition = "text")
    private String kitVariants;

    @Column(name = "feed_enabled", nullable = false)
    private boolean feedEnabled;

    @Column(name = "chat_enabled", nullable = false)
    private boolean chatEnabled;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
