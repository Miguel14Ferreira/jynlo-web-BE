package com.example.desporto24.model.social;
import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="personal_live_mute",
 uniqueConstraints=@UniqueConstraint(name="uk_personal_live_mute",columnNames={"live_id","profile_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonalLiveMute {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false)
 @JoinColumn(name="live_id",nullable=false) private PersonalLiveEvent live;
 @ManyToOne(fetch=FetchType.LAZY,optional=false)
 @JoinColumn(name="profile_id",nullable=false) private Profile profile;
 @Column(name="created_at",nullable=false) private Instant createdAt;
}
