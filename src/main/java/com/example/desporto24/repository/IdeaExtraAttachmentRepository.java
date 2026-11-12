package com.example.desporto24.repository;

import com.example.desporto24.model.IdeaExtraAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IdeaExtraAttachmentRepository extends JpaRepository<IdeaExtraAttachment, IdeaExtraAttachment.Key> {
    List<IdeaExtraAttachment> findByIdeaIdOrderByImageIndexAsc(Long ideaId);
    Optional<IdeaExtraAttachment> findByIdeaIdAndImageIndex(Long ideaId, Integer imageIndex);
}
