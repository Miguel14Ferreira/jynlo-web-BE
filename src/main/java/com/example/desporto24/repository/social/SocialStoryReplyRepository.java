package com.example.desporto24.repository.social;

import com.example.desporto24.model.social.SocialStoryReply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SocialStoryReplyRepository extends JpaRepository<SocialStoryReply, Long> {
    List<SocialStoryReply> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable page);
}
