package com.example.desporto24.repository.social;
import com.example.desporto24.model.social.PersonalLiveMute;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PersonalLiveMuteRepository extends JpaRepository<PersonalLiveMute,Long> {
 java.util.List<PersonalLiveMute> findByLiveId(Long liveId);
 boolean existsByLiveIdAndProfileId(Long liveId,Long profileId);
 long deleteByLiveIdAndProfileId(Long liveId,Long profileId);
}
