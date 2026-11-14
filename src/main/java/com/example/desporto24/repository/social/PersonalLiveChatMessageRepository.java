package com.example.desporto24.repository.social;
import com.example.desporto24.model.social.PersonalLiveChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PersonalLiveChatMessageRepository extends JpaRepository<PersonalLiveChatMessage,Long> {
 List<PersonalLiveChatMessage> findTop100ByLiveIdOrderByCreatedAtDesc(Long liveId);
 java.util.Optional<PersonalLiveChatMessage> findTopByLiveIdAndAuthorIdOrderByCreatedAtDesc(Long liveId,Long authorId);
}
