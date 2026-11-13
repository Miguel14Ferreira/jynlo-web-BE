package com.example.desporto24.repository.social;
import com.example.desporto24.model.social.ProfileFollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ProfileFollowRequestRepository extends JpaRepository<ProfileFollowRequest,Long> {
  Optional<ProfileFollowRequest> findByRequesterIdAndTargetId(Long requesterId,Long targetId);
  boolean existsByRequesterIdAndTargetId(Long requesterId,Long targetId);
  List<ProfileFollowRequest> findTop100ByTargetIdOrderByCreatedAtDesc(Long targetId);
  long deleteByRequesterIdAndTargetId(Long requesterId,Long targetId);
  long deleteByRequesterIdOrTargetId(Long requesterId,Long targetId);
  long deleteByTargetId(Long targetId);
}
