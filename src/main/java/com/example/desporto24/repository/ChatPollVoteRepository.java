package com.example.desporto24.repository;

import com.example.desporto24.model.ChatPollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChatPollVoteRepository extends JpaRepository<ChatPollVote, Long> {
    List<ChatPollVote> findByConversationKindAndMessageIdIn(String kind, Collection<Long> messageIds);

    @Modifying
    @Query(value = """
            INSERT INTO desporto24.chat_poll_vote
                (profile_id, conversation_kind, message_id, option_index)
            VALUES (:actorId, :kind, :messageId, :option)
            ON CONFLICT (profile_id, conversation_kind, message_id)
            DO UPDATE SET option_index = EXCLUDED.option_index
            """, nativeQuery = true)
    int upsert(@Param("actorId") Long actorId, @Param("kind") String kind,
               @Param("messageId") Long messageId, @Param("option") short option);

    long deleteByProfileIdAndConversationKindAndMessageId(Long actorId, String kind, Long messageId);
}
