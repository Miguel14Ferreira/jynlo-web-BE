package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityTournamentMatch;
import com.example.desporto24.enumeration.TournamentMatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CommunityTournamentMatchRepository extends JpaRepository<CommunityTournamentMatch, Long> {

    /**
     * Match-level results for one confirmed entrant. Only the server-calculated
     * completed profile history service calls this with a finalized tournament.
     */
    @org.springframework.data.jpa.repository.Query("""
        select match from CommunityTournamentMatch match
        join fetch match.homeParticipant home
        join fetch match.awayParticipant away
        where match.tournament.id = :tournamentId
          and match.status = com.example.desporto24.enumeration.TournamentMatchStatus.COMPLETED
          and (home.profile.id = :profileId or away.profile.id = :profileId)
        order by match.id asc
        """)
    List<CommunityTournamentMatch> findCompletedMatchesForParticipant(
        @org.springframework.data.repository.query.Param("tournamentId") Long tournamentId,
        @org.springframework.data.repository.query.Param("profileId") Long profileId,
        Pageable pageable);

    List<CommunityTournamentMatch> findByTournamentIdOrderByRoundNumberAscIdAsc(Long tournamentId);

    boolean existsByTournamentId(Long tournamentId);

    long countByTournamentId(Long tournamentId);

    @EntityGraph(attributePaths = {"homeParticipant", "awayParticipant"})
    Slice<CommunityTournamentMatch> findByTournamentIdAndStatus(
            Long tournamentId, TournamentMatchStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"homeParticipant.profile", "awayParticipant.profile"})
    Slice<CommunityTournamentMatch> findByTournamentId(Long tournamentId, Pageable pageable);
}
