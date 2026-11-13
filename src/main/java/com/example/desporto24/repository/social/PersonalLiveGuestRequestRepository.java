package com.example.desporto24.repository.social;
import com.example.desporto24.model.social.PersonalLiveGuestRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface PersonalLiveGuestRequestRepository extends JpaRepository<PersonalLiveGuestRequest,Long> {
 Optional<PersonalLiveGuestRequest> findByLiveIdAndRequesterId(Long liveId,Long requesterId);
 List<PersonalLiveGuestRequest> findByLiveIdAndStatusOrderByCreatedAtAsc(
  Long liveId,PersonalLiveGuestRequest.Status status);
}
