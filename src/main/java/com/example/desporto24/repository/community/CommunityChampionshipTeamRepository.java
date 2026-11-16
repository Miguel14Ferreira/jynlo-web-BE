package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityChampionshipTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityChampionshipTeamRepository extends JpaRepository<CommunityChampionshipTeam, Long> {
    List<CommunityChampionshipTeam> findByChampionshipIdOrderByNameAscIdAsc(Long championshipId);
    List<CommunityChampionshipTeam> findByChampionshipIdIn(List<Long> championshipIds);
    boolean existsByChampionshipIdAndGlobalTeamId(Long championshipId, Long globalTeamId);
    boolean existsByChampionshipIdAndPermanentTeamId(Long championshipId, Long permanentTeamId);
    boolean existsByChampionshipIdAndNormalizedName(Long championshipId, String normalizedName);
    /** Only explicitly linked permanent identities can inherit season trophies. */
    @org.springframework.data.jpa.repository.Query("""
        select t from CommunityChampionshipTeam t
          join fetch t.championship season
          join fetch season.community
        where t.globalTeam.id = :globalTeamId
          and season.closedAt is not null
          and season.community.visibility = com.example.desporto24.enumeration.CommunityVisibility.PUBLIC
        order by season.closedAt desc, t.id desc
        """)
    List<CommunityChampionshipTeam> closedPublicSeasonsForGlobalTeam(
            @org.springframework.data.repository.query.Param("globalTeamId") Long globalTeamId);

    boolean existsByGlobalTeamId(Long globalTeamId);
}
