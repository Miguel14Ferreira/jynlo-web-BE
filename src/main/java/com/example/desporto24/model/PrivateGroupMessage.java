package com.example.desporto24.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(schema="desporto24",name="private_group_message")
@Getter @Setter
public class PrivateGroupMessage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="group_id",nullable=false)
    private Long groupId;
    @Column(name="sender_username",nullable=false,length=128)
    private String senderUsername;
    @Column(nullable=false,length=2000)
    private String body;
    @Column(name="created_at",nullable=false)
    private Instant createdAt;
    @Column(name="reply_to_id")
    private Long replyToId;
    @Column(name="edited_at")
    private Instant editedAt;
    @Column(name="deleted_at")
    private Instant deletedAt;
    // Attachments are private: storage keys are never exposed to clients.
    @Column(name="attachment_key",length=240)
    private String attachmentKey;
    @Column(name="attachment_name",length=160)
    private String attachmentName;
    @Column(name="attachment_mime_type",length=60)
    private String attachmentMimeType;
    @Column(name="attachment_size")
    private Long attachmentSize;
    @jakarta.persistence.ElementCollection(fetch=jakarta.persistence.FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size=50)
    @jakarta.persistence.CollectionTable(name="private_group_message_extra_attachment",schema="desporto24",
        joinColumns=@jakarta.persistence.JoinColumn(name="message_id"))
    @jakarta.persistence.OrderColumn(name="position")
    private java.util.List<com.example.desporto24.model.chat.PrivateChatAttachment> extraAttachments=new java.util.ArrayList<>();
}
