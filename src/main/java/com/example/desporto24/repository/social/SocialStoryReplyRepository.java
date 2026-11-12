package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.SocialStoryReply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;

public interface SocialStoryReplyRepository extends JpaRepository<SocialStoryReply, Long> {
    // Filter both directions of user blocking in the database, before pagination.
    // Fetch the sender in the same query to avoid lazy N+1 requests.
    @Query("""
            select reply from SocialStoryReply reply
            join fetch reply.sender
            where reply.recipient.id = :recipientId
              and reply.sender.id not in :blockedIds
            order by reply.createdAt desc, reply.id desc
            """)
    List<SocialStoryReply> findVisibleReplies(
            @Param("recipientId") Long recipientId,
            @Param("blockedIds") Collection<Long> blockedIds,
            Pageable page);
}
