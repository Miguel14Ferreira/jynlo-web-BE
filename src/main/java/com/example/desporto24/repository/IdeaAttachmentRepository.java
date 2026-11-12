package com.example.desporto24.repository;

import com.example.desporto24.model.IdeaAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaAttachmentRepository extends JpaRepository<IdeaAttachment, Long> {}
