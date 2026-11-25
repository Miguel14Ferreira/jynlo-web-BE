package com.example.desporto24.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** One vote per authenticated user and poll message (single choice). */
@Entity
@Table(name = "chat_poll_vote", schema = "desporto24",
        uniqueConstraints = @UniqueConstraint(name = "uq_chat_poll_vote_actor_message",
                columnNames = {"profile_id", "conversation_kind", "message_id"}),
        indexes = @Index(name = "idx_chat_poll_vote_lookup", columnList = "conversation_kind,message_id"))
@Getter @Setter
public class ChatPollVote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "conversation_kind", nullable = false, length = 16)
    private String conversationKind;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "option_index", nullable = false)
    private Short optionIndex;

    /** Shown to the chat (votes cast before 25 Nov 2026 were anonymous and stay hidden). */
    @Column(name = "visible", nullable = false)
    private boolean visible = false;
}
