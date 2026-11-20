package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityTournamentParticipant;
import com.example.desporto24.enumeration.TournamentParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.Optional;

import java.util.List;

public interface CommunityTournamentParticipantRepository
        extends JpaRepository<CommunityTournamentParticipant, Long> {

    /**
     * Only actual CONFIRMED entrants in completed individual tournaments
     * belonging to public communities. Never count pending invitations,
     * team rosters or private competition membership.
     */
    @org.springframework.data.jpa.repository.Query("""
        select participant from CommunityTournamentParticipant participant
        join fetch participant.tournament tournament
        join fetch tournament.community community
        where participant.profile.id = :profileId
          and participant.status = com.example.desporto24.enumeration.TournamentParticipantStatus.CONFIRMED
          and tournament.status = com.example.desporto24.enumeration.TournamentStatus.COMPLETED
          and tournament.entryType = com.example.desporto24.enumeration.TournamentEntryType.INDIVIDUAL
          and tournament.completedAt is not null
          and tournament.historicalArchiveId is null
          and community.visibility = com.example.desporto24.enumeration.CommunityVisibility.PUBLIC
        order by tournament.completedAt desc, tournament.id desc
        """)
    List<CommunityTournamentParticipant> findPublicCompletedIndividualParticipations(
            @org.springframework.data.repository.query.Param("profileId") Long profileId,
            Pageable pageable);

    List<CommunityTournamentParticipant> findByTournamentIdOrderByIdAsc(Long tournamentId);

    boolean existsByTournamentIdAndProfileId(Long tournamentId, Long profileId);

    Optional<CommunityTournamentParticipant> findByTournamentIdAndProfileId(Long tournamentId, Long profileId);

    @EntityGraph(attributePaths = "profile")
    Slice<CommunityTournamentParticipant> findByTournamentId(Long tournamentId, Pageable pageable);

    @EntityGraph(attributePaths = "profile")
    Slice<CommunityTournamentParticipant> findByTournamentIdAndStatus(
            Long tournamentId, TournamentParticipantStatus status, Pageable pageable);
}
