package com.example.desporto24.model.social;
import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="personal_live_guest_request",
 uniqueConstraints=@UniqueConstraint(name="uk_live_guest_request",columnNames={"live_id","requester_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonalLiveGuestRequest {
 public enum Status {PENDING,APPROVED,DECLINED}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false)
 @JoinColumn(name="live_id",nullable=false) private PersonalLiveEvent live;
 @ManyToOne(fetch=FetchType.LAZY,optional=false)
 @JoinColumn(name="requester_id",nullable=false) private Profile requester;
 @Enumerated(EnumType.STRING)
 @Column(name="status",length=16,nullable=false) private Status status;
 @Column(name="created_at",nullable=false) private Instant createdAt;
}
