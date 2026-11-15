package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamContentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SportsTeamContentReactionRepository
        extends JpaRepository<SportsTeamContentReaction,Long> {
    Optional<SportsTeamContentReaction> findByContentIdAndProfileId(Long contentId, Long profileId);
    List<SportsTeamContentReaction> findByContentIdIn(Collection<Long> ids);
}
