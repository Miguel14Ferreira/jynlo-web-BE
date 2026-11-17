package com.example.desporto24.service;

import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.*;
import com.example.desporto24.repository.community.CommunityChampionshipTeamRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Retire only unreferenced, empty permanent teams.
 * Never destroy private chat, member decisions or historical trophies.
 */
@Service
@RequiredArgsConstructor
public class SportsTeamDeletionService {
    private final SportsTeamRepository teams;
    private final ProfileRepository profiles;
    private final SportsTeamMemberRepository members;
    private final SportsTeamContentRepository content;
    private final SportsTeamCommunityMembershipRepository communities;
    private final SportsTeamCompetitionApplicationRepository applications;
    private final SportsTeamCompetitionInvitationRepository invitations;
    private final CommunityTournamentTeamRepository tournamentTeams;
    private final CommunityChampionshipTeamRepository championshipTeams;

    @Transactional
    public void deleteEmpty(Long teamId, String username) {
        if (teamId == null || teamId <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipa inválida");
        var actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        var team = teams.findForDeletion(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada"));
        if (!team.getOwner().getId().equals(actor.getId()))
            throw new AccessDeniedException("Só o responsável pode eliminar a equipa");
        if (Boolean.FALSE.equals(actor.getEnabled()) || actor.getModerationSuspendedAt() != null)
            throw new AccessDeniedException("Conta indisponível para gerir equipas");
        // Refuse deletion if anything might be retained or referenced by another module.
        // All invitation statuses (including declined) are checked, not only ACTIVE.
        if (members.existsByTeamId(teamId)
                || content.existsByTeamId(teamId)
                || communities.existsByTeamId(teamId)
                || applications.existsByTeamId(teamId)
                || invitations.existsByTeamId(teamId)
                || tournamentTeams.existsByGlobalTeamId(teamId)
                || championshipTeams.existsByGlobalTeamId(teamId))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A equipa tem histórico ou ligações. Não pode ser eliminada.");
        teams.delete(team);
        teams.flush();
    }
}
