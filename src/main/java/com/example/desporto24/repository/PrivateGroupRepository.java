package com.example.desporto24.repository;
import com.example.desporto24.model.PrivateGroup;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PrivateGroupRepository extends JpaRepository<PrivateGroup, Long> {
    java.util.List<PrivateGroup> findByCreatorUsernameIgnoreCase(String creatorUsername);
}
