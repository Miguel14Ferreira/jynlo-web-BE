package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityTournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityTournamentTeamRepository extends JpaRepository<CommunityTournamentTeam, Long> {
    List<CommunityTournamentTeam> findByTournamentIdOrderByIdAsc(Long tournamentId);
    boolean existsByTournamentIdAndGlobalTeamId(Long tournamentId, Long globalTeamId);
    boolean existsByTournamentIdAndNormalizedName(Long tournamentId, String normalizedName);
    long countByTournamentId(Long tournamentId);
    /** Historical entries are retained even after a competition ends. */
    @org.springframework.data.jpa.repository.Query("""
        select entry from CommunityTournamentTeam entry
          join fetch entry.tournament tournament
          join fetch tournament.community
        where entry.globalTeam.id = :teamId
          and tournament.community.visibility = com.example.desporto24.enumeration.CommunityVisibility.PUBLIC
        order by entry.id desc
        """)
    List<CommunityTournamentTeam> historyForGlobalTeam(
            @org.springframework.data.repository.query.Param("teamId") Long teamId);

    boolean existsByGlobalTeamId(Long globalTeamId);
}
