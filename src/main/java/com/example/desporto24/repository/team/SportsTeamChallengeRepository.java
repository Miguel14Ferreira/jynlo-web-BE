package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamChallenge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SportsTeamChallengeRepository extends JpaRepository<SportsTeamChallenge,Long> {
    @EntityGraph(attributePaths={"challenger","challenged"})
    @Query("select c from SportsTeamChallenge c where c.challenger.id = :teamId or c.challenged.id = :teamId order by c.createdAt desc, c.id desc")
    List<SportsTeamChallenge> forTeam(@Param("teamId") Long teamId, Pageable page);

    @EntityGraph(attributePaths={"challenger","challenged"})
    @Query("select c from SportsTeamChallenge c where c.id = :id")
    Optional<SportsTeamChallenge> findWithTeams(@Param("id") Long id);

    @Query("select count(c)>0 from SportsTeamChallenge c where c.status in :statuses and ((c.challenger.id = :first and c.challenged.id = :second) or (c.challenger.id = :second and c.challenged.id = :first))")
    boolean hasActivePair(@Param("first") Long first, @Param("second") Long second,
                          @Param("statuses") List<SportsTeamChallenge.Status> statuses);

    boolean existsByChallengerIdOrChallengedId(Long challengerId,Long challengedId);
}
