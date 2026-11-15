package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommunityChampionshipRepository extends JpaRepository<CommunityChampionship, Long> {
    @org.springframework.data.jpa.repository.Query("""
      select c from CommunityChampionship c
      where c.teamApplicationsOpen = true and c.closedAt is null
        and c.community.visibility = com.example.desporto24.enumeration.CommunityVisibility.PUBLIC
        and (:sport = '' or lower(c.sport) like concat('%', lower(:sport), '%'))
        and (:q = '' or lower(c.name) like concat('%', lower(:q), '%')
          or lower(c.community.name) like concat('%', lower(:q), '%'))
      order by c.createdAt desc
      """)
    java.util.List<CommunityChampionship> discoverOpenTeams(
       @org.springframework.data.repository.query.Param("q") String q,
       @org.springframework.data.repository.query.Param("sport") String sport,
       org.springframework.data.domain.Pageable pageable);

    List<CommunityChampionship> findByCommunityIdOrderByCreatedAtDescIdDesc(Long communityId);
    Optional<CommunityChampionship> findByIdAndCommunityId(Long id, Long communityId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query(
            "select c from CommunityChampionship c where c.id = :id and c.community.id = :communityId")
    Optional<CommunityChampionship> findForClosing(
            @org.springframework.data.repository.query.Param("id") Long id,
            @org.springframework.data.repository.query.Param("communityId") Long communityId);
}
