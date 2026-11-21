package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityTournamentRankingResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CommunityTournamentRankingResultRepository extends JpaRepository<CommunityTournamentRankingResult, Long> {
    List<CommunityTournamentRankingResult> findByEventIdIn(Collection<Long> eventIds);
    void deleteByEventId(Long eventId);
}
