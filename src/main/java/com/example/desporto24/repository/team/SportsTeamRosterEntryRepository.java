package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamRosterEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SportsTeamRosterEntryRepository extends JpaRepository<SportsTeamRosterEntry, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "profile")
    List<SportsTeamRosterEntry> findByTeamIdOrderByShirtNumberAscNameAsc(Long teamId);
    Optional<SportsTeamRosterEntry> findByIdAndTeamId(Long id, Long teamId);
    long countByTeamId(Long teamId);
    Optional<SportsTeamRosterEntry> findByClaimTokenHash(String claimTokenHash);
    Optional<SportsTeamRosterEntry> findFirstByTeamIdAndProfileId(Long teamId, Long profileId);
}
