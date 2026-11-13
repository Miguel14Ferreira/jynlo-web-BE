package com.example.desporto24.model.social;
import com.example.desporto24.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="personal_live_chat_message")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonalLiveChatMessage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false)
 @JoinColumn(name="live_id",nullable=false) private PersonalLiveEvent live;
 @ManyToOne(fetch=FetchType.LAZY,optional=false)
 @JoinColumn(name="author_id",nullable=false) private Profile author;
 @Column(name="message",length=500,nullable=false) private String message;
 @Column(name="created_at",nullable=false) private Instant createdAt;
}
