package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipEventTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipEventTeamRepository
        extends JpaRepository<CommunityChampionshipEventTeam, Long> {
    List<CommunityChampionshipEventTeam> findByEventIdOrderByIdAsc(Long eventId);
    Optional<CommunityChampionshipEventTeam> findByEventIdAndTeamId(Long eventId, Long teamId);
    boolean existsByEventIdAndTeamId(Long eventId, Long teamId);
}
