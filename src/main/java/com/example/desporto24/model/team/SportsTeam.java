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

    @Column(name = "feed_enabled", nullable = false)
    private boolean feedEnabled;

    @Column(name = "chat_enabled", nullable = false)
    private boolean chatEnabled;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
