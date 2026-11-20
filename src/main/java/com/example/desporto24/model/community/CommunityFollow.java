package com.example.desporto24.model.community;

import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/** Followers are spectators, not community members. No private permissions are granted. */
@Entity
@Table(name="community_follow",schema="desporto24",
 uniqueConstraints=@UniqueConstraint(name="uk_community_follow",columnNames={"community_id","follower_profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityFollow {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="community_id",nullable=false)
    private Community community;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="follower_profile_id",nullable=false)
    private Profile follower;
    @Column(name="created_at",nullable=false)
    private Instant createdAt;
}
