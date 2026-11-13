package com.example.desporto24.model.social;
import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity
@Table(name="profile_follow_request",uniqueConstraints=@UniqueConstraint(
  name="uk_profile_follow_request",columnNames={"requester_id","target_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileFollowRequest {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false)
  @JoinColumn(name="requester_id",nullable=false) private Profile requester;
  @ManyToOne(fetch=FetchType.LAZY,optional=false)
  @JoinColumn(name="target_id",nullable=false) private Profile target;
  @Column(name="created_at",nullable=false) private Instant createdAt;
}
