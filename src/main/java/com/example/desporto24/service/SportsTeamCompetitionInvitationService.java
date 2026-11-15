package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import com.example.desporto24.repository.team.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportsTeamCompetitionInvitationService {
    private final SportsTeamRepository teams;
    private final SportsTeamCompetitionInvitationRepository invitations;
    private final ProfileRepository profiles;
    private final CommunityMemberRepository members;
    private final CommunityTournamentRepository tournaments;
    private final CommunityChampionshipRepository championships;

    public record InvitationView(Long id, Long teamId, String teamName, Long communityId,
                                 String competitionType, Long competitionId, String status) {}

    private InvitationView view(SportsTeamCompetitionInvitation invite) {
        return new InvitationView(invite.getId(), invite.getTeam().getId(),
            invite.getTeam().getName(), invite.getCommunityId(),
            invite.getCompetitionType().name(), invite.getCompetitionId(), invite.getStatus().name());
    }

    @Transactional(readOnly = true)
    public List<InvitationView> inbox(String username) {
        var user = profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        return invitations.findByTeamOwnerIdOrderByIdDesc(user.getId()).stream()
            .map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public List<InvitationView> forCompetition(Long communityId, String type, Long competitionId,
                                               String username) {
        var user = profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireOrganizer(communityId, user.getId());
        var competitionType = parseType(type);
        ensureCompetitionOpen(communityId, competitionType, competitionId);
        return invitations.findByCommunityIdAndCompetitionTypeAndCompetitionIdOrderByIdAsc(
            communityId, competitionType, competitionId).stream().map(this::view).toList();
    }

    @Transactional
    public InvitationView invite(Long communityId, String type, Long competitionId, Long teamId,
                                  String username) {
        var inviter = profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireOrganizer(communityId, inviter.getId());
        var competitionType = parseType(type);
        ensureCompetitionOpen(communityId, competitionType, competitionId);
        SportsTeam team = teams.findById(teamId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada"));
        var existing = invitations.findByTeamIdAndCompetitionTypeAndCompetitionId(
            teamId, competitionType, competitionId);
        if (existing.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta equipa já recebeu um convite para a competição");
        var invitation = SportsTeamCompetitionInvitation.builder()
            .team(team).invitedBy(inviter).communityId(communityId)
            .competitionType(competitionType).competitionId(competitionId)
            .status(SportsTeamCompetitionInvitation.Status.PENDING)
            .createdAt(Instant.now()).build();
        return view(invitations.saveAndFlush(invitation));
    }

    @Transactional
    public InvitationView respond(Long invitationId, String username, boolean accept) {
        var actor = profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        var invitation = invitations.findById(invitationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convite não encontrado"));
        if (!invitation.getTeam().getOwner().getId().equals(actor.getId()))
            throw new AccessDeniedException("Só o responsável pode responder pela equipa");
        if (invitation.getStatus() != SportsTeamCompetitionInvitation.Status.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este convite já foi respondido");
        if (accept) {
            ensureCompetitionOpen(invitation.getCommunityId(), invitation.getCompetitionType(),
                invitation.getCompetitionId());
        }
        invitation.setStatus(accept ? SportsTeamCompetitionInvitation.Status.ACCEPTED
                                    : SportsTeamCompetitionInvitation.Status.DECLINED);
        invitation.setRespondedAt(Instant.now());
        // Acceptance records the captain's authorization, not tournament enrollment.
        // The organizer must finalize the competition entry after eligibility checks.
        return view(invitations.saveAndFlush(invitation));
    }

    private SportsTeamCompetitionInvitation.CompetitionType parseType(String type) {
        try { return SportsTeamCompetitionInvitation.CompetitionType.valueOf(type.toUpperCase(java.util.Locale.ROOT)); }
        catch (Exception ignored) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de competição inválido");
        }
    }

    private void requireOrganizer(Long communityId, Long profileId) {
        var member = members.findByCommunityIdAndProfileId(communityId, profileId)
            .orElseThrow(() -> new AccessDeniedException("Não pertences à comunidade"));
        if (member.getRole() != CommunityRole.OWNER && member.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Só organizadores podem convidar equipas");
    }

    private void ensureCompetitionOpen(Long communityId,
            SportsTeamCompetitionInvitation.CompetitionType type, Long id) {
        if (type == SportsTeamCompetitionInvitation.CompetitionType.TOURNAMENT) {
            var competition = tournaments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneio não encontrado"));
            if (!competition.getCommunity().getId().equals(communityId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneio não encontrado");
            if (competition.getEntryType() != TournamentEntryType.TEAM)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este torneio é individual");
            if (competition.getStatus() != TournamentStatus.DRAFT)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "O torneio já começou");
        } else {
            var competition = championships.findByIdAndCommunityId(id, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
            if (competition.getClosedAt() != null)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "O campeonato está encerrado");
        }
    }
}
