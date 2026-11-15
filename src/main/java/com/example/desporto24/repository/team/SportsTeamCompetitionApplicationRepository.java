package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamCompetitionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SportsTeamCompetitionApplicationRepository extends JpaRepository<SportsTeamCompetitionApplication, Long> {
    Optional<SportsTeamCompetitionApplication> findByTeamIdAndCompetitionTypeAndCompetitionId(
        Long teamId, SportsTeamCompetitionApplication.CompetitionType type, Long competitionId);
    List<SportsTeamCompetitionApplication> findByTeamOwnerIdOrderByIdDesc(Long ownerId);
    List<SportsTeamCompetitionApplication> findByCommunityIdAndCompetitionTypeAndCompetitionIdOrderByIdAsc(
        Long communityId, SportsTeamCompetitionApplication.CompetitionType type, Long competitionId);
}
