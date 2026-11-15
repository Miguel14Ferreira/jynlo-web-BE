package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityTournament;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

import java.util.List;

public interface CommunityTournamentRepository extends JpaRepository<CommunityTournament, Long> {
    @org.springframework.data.jpa.repository.Query("""
      select t from CommunityTournament t
      where t.entryType = com.example.desporto24.enumeration.TournamentEntryType.TEAM
        and t.status = com.example.desporto24.enumeration.TournamentStatus.DRAFT
        and t.teamApplicationsOpen = true
        and t.community.visibility = com.example.desporto24.enumeration.CommunityVisibility.PUBLIC
        and (:sport = '' or lower(t.sport) like concat('%', lower(:sport), '%'))
        and (:q = '' or lower(t.name) like concat('%', lower(:q), '%')
          or lower(t.community.name) like concat('%', lower(:q), '%'))
      order by t.createdAt desc
      """)
    java.util.List<CommunityTournament> discoverOpenTeams(@org.springframework.data.repository.query.Param("q") String q,
      @org.springframework.data.repository.query.Param("sport") String sport, Pageable pageable);

    long countByStatus(com.example.desporto24.enumeration.TournamentStatus status);


    List<CommunityTournament> findByCommunityIdOrderByCreatedAtDescIdDesc(Long communityId);

    /** Only living, individual, non-historical competitions qualify for standalone heat recording. */
    boolean existsByCommunityIdAndSportIgnoreCaseAndEntryTypeAndHistoricalArchiveIdIsNullAndStatusNot(
            Long communityId, String sport,
            com.example.desporto24.enumeration.TournamentEntryType entryType,
            com.example.desporto24.enumeration.TournamentStatus status);


    Slice<CommunityTournament> findByCommunityId(Long communityId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select tournament from CommunityTournament tournament where tournament.id = :id")
    Optional<CommunityTournament> findByIdForUpdate(@Param("id") Long id);
}
