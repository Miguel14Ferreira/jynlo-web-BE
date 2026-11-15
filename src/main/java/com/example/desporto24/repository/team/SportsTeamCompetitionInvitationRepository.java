package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamCompetitionInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SportsTeamCompetitionInvitationRepository
        extends JpaRepository<SportsTeamCompetitionInvitation, Long> {
    Optional<SportsTeamCompetitionInvitation> findByTeamIdAndCompetitionTypeAndCompetitionId(
        Long teamId, SportsTeamCompetitionInvitation.CompetitionType type, Long competitionId);
    List<SportsTeamCompetitionInvitation> findByTeamOwnerIdOrderByIdDesc(Long ownerId);
    List<SportsTeamCompetitionInvitation> findByCommunityIdAndCompetitionTypeAndCompetitionIdOrderByIdAsc(
        Long communityId, SportsTeamCompetitionInvitation.CompetitionType type, Long competitionId);
}
