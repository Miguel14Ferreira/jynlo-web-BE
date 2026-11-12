package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.dto.community.CommunityChampionshipTeamsResponse;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityChampionshipTeamsService {
    private final CommunityChampionshipRepository championships;
    private final CommunityChampionshipTeamRepository seasonTeams;
    private final CommunityTournamentTeamRepository tournamentTeams;
    private final ProfileRepository profiles;
    private final CommunityMemberRepository members;
    private final CommunityChampionshipTeamPlayerRepository playerRegistrations;
    private final CommunityChampionshipTeamInviteRepository teamInvites;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private TeamInvitationNotificationService teamNotifications;
    private final CommunityChampionshipGoalRepository goals;
    private final CommunityChampionshipDisciplinaryCardRepository disciplinaryCards;
    private final CommunityChampionshipSubstitutionRepository substitutions;
    private final CommunityChampionshipLineupPlayerRepository lineups;

    @Transactional(readOnly = true)
    public CommunityChampionshipTeamsResponse list(Long communityId, Long championshipId, String username) {
        requireMember(communityId, username);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        return response(championship);
    }

    public CommunityChampionshipTeamsResponse create(Long communityId, Long championshipId,
                                                      String name, String username) {
        requireOrganizer(communityId, username);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        ensureOpen(championship);
        String trimmed = name == null ? "" : name.trim().replaceAll("\\s+", " ");
        if (trimmed.isEmpty() || trimmed.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da equipa inválido");
        String normalized = trimmed.toLowerCase(Locale.ROOT);
        if (seasonTeams.existsByChampionshipIdAndNormalizedName(championshipId, normalized))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Equipa já existente neste campeonato");
        seasonTeams.saveAndFlush(CommunityChampionshipTeam.builder()
                .championship(championship).name(trimmed).normalizedName(normalized)
                .createdAt(Instant.now()).build());
        return response(championship);
    }

    public CommunityChampionshipTeamsResponse link(Long communityId, Long championshipId,
                                                    Long tournamentTeamId, Long seasonTeamId,
                                                    String username) {
        requireOrganizer(communityId, username);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        ensureOpen(championship);
        CommunityChampionshipTeam seasonTeam = seasonTeams.findById(seasonTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa de campeonato inexistente"));
        if (!seasonTeam.getChampionship().getId().equals(championshipId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A equipa pertence a outro campeonato");
        CommunityTournamentTeam tournamentTeam = tournamentTeams.findById(tournamentTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa da prova inexistente"));
        Long tournamentId = tournamentTeam.getTournament().getId();
        if (championship.getCompetitions().stream().noneMatch(t -> t.getId().equals(tournamentId)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A prova não pertence ao campeonato");
        if (tournamentTeams.findByTournamentIdOrderByIdAsc(tournamentId).stream()
                .anyMatch(other -> !other.getId().equals(tournamentTeamId)
                        && other.getChampionshipTeam() != null
                        && seasonTeamId.equals(other.getChampionshipTeam().getId())))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta equipa já está associada a outra equipa da prova");
        tournamentTeam.setChampionshipTeam(seasonTeam);
        tournamentTeams.saveAndFlush(tournamentTeam);
        return response(championship);
    }

    public record TeamPlayer(Long profileId, String username, String displayName, String profilePicUrl) {}
    public record TeamAdminStatus(Long teamId, Long adminProfileId, Long pendingProfileId,
                                  boolean canManage, boolean canAccept, boolean canTransfer) {}

    @Transactional(readOnly = true)
    public TeamAdminStatus administrator(Long communityId, Long championshipId,
                                         Long teamId, String username) {
        requireMember(communityId, username);
        var team = getSeasonTeam(communityId, championshipId, teamId);
        var actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        boolean organizer = isOrganizer(communityId, actor.getId());
        Long adminId = team.getAdminProfile() == null ? null : team.getAdminProfile().getId();
        Long pendingId = team.getPendingAdminProfile() == null ? null : team.getPendingAdminProfile().getId();
        // Only the organizer and invited player see the pending identity.
        return new TeamAdminStatus(teamId, adminId,
                organizer || actor.getId().equals(pendingId) ? pendingId : null,
                organizer || actor.getId().equals(adminId), actor.getId().equals(pendingId),
                actor.getId().equals(adminId));
    }

    /** An invitation never adds a player or grants administration. */
    public void invitePlayer(Long communityId, Long championshipId, Long teamId,
                             Long profileId, String username) {
        requireOrganizerOrTeamAdmin(communityId, championshipId, teamId, username);
        var championship = championships.findForClosing(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ensureOpen(championship);
        var selected = getSeasonTeam(communityId, championshipId, teamId);
        if (profileId == null || !members.existsByCommunityIdAndProfileId(communityId, profileId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escolhe um membro da comunidade");
        var existing = playerRegistrations.findByChampionshipIdAndProfileId(championshipId, profileId);
        if (existing.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este jogador já está numa equipa nesta época");
        if (teamInvites.findByTeamIdAndProfileId(teamId, profileId).isPresent()) return;
        var player = profiles.findById(profileId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        teamInvites.saveAndFlush(CommunityChampionshipTeamInvite.builder()
                .championship(championship).team(selected).profile(player).invitedAt(Instant.now()).build());
        if (teamNotifications != null) {
            var sender = profiles.findUserByUsername(username).orElse(null);
            teamNotifications.memberInvitation(player, sender, communityId, selected.getName(),
                    TeamInvitationNotificationService.CompetitionTarget.championship(championshipId, teamId));
        }
    }

    public record PendingTeamInvite(Long profileId, String username, String displayName, Instant invitedAt) {}

    /** The roster is public to members, but outstanding invitations are not. */
    @Transactional(readOnly = true)
    public List<PendingTeamInvite> pendingPlayerInvitations(Long communityId, Long championshipId,
                                                             Long teamId, String username) {
        requireOrganizerOrTeamAdmin(communityId, championshipId, teamId, username);
        getSeasonTeam(communityId, championshipId, teamId);
        return teamInvites.findByTeamIdOrderByInvitedAtAscIdAsc(teamId).stream().map(invite -> {
            var profile = invite.getProfile();
            return new PendingTeamInvite(profile.getId(), profile.getUsername(),
                    profile.getFullName() == null || profile.getFullName().isBlank()
                            ? profile.getUsername() : profile.getFullName(), invite.getInvitedAt());
        }).toList();
    }

    /** Cancelling an invitation cannot remove a confirmed player or current admin. */
    public void revokePlayerInvitation(Long communityId, Long championshipId, Long teamId,
                                        Long profileId, String username) {
        requireOrganizerOrTeamAdmin(communityId, championshipId, teamId, username);
        ensureOpen(getChampionship(communityId, championshipId));
        getSeasonTeam(communityId, championshipId, teamId);
        if (profileId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jogador inválido");
        }
        var pending = teamInvites.findByTeamIdAndProfileId(teamId, profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "O convite já não está pendente"));
        teamInvites.delete(pending);
    }

    public List<TeamPlayer> respondPlayerInvite(Long communityId, Long championshipId, Long teamId,
                                                 boolean accept, String username) {
        var current = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireMember(communityId, username);
        // Serializes concurrent membership acceptances to preserve first-accepted ownership.
        var championship = championships.findForClosing(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ensureOpen(championship);
        var team = getSeasonTeam(communityId, championshipId, teamId);
        var invite = teamInvites.findByTeamIdAndProfileId(teamId, current.getId())
                .orElseThrow(() -> new AccessDeniedException("Não tens um convite para esta equipa"));
        if (accept) {
            var existing = playerRegistrations.findByChampionshipIdAndProfileId(championshipId, current.getId());
            if (existing.isPresent())
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já pertences a uma equipa nesta época");
            playerRegistrations.saveAndFlush(CommunityChampionshipTeamPlayer.builder()
                    .championship(championship).team(team).profile(current).joinedAt(Instant.now()).build());
            if (team.getAdminProfile() == null) {
                team.setAdminProfile(current);
                team.setPendingAdminProfile(null);
                seasonTeams.saveAndFlush(team);
                if (teamNotifications != null)
                    teamNotifications.assigned(current, communityId, team.getName(),
                            TeamInvitationNotificationService.CompetitionTarget.championship(championshipId, teamId));
            }
        }
        teamInvites.delete(invite);
        return players(communityId, championshipId, teamId, username);
    }

    @Transactional(readOnly = true)
    public boolean hasPlayerInvite(Long communityId, Long championshipId, Long teamId, String username) {
        requireMember(communityId, username);
        getSeasonTeam(communityId, championshipId, teamId);
        var current = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        return teamInvites.findByTeamIdAndProfileId(teamId, current.getId()).isPresent();
    }

    /** Only an existing administrator can propose another confirmed team member. */
    public TeamAdminStatus inviteAdministrator(Long communityId, Long championshipId,
                                                Long teamId, Long profileId, String username) {
        var actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireMember(communityId, username);
        var season = championships.findForClosing(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ensureOpen(season);
        var team = getSeasonTeam(communityId, championshipId, teamId);
        if (team.getAdminProfile() == null
                || !team.getAdminProfile().getId().equals(actor.getId()))
            throw new AccessDeniedException("Apenas o administrador atual pode transferir a equipa");
        var existing = profileId == null ? java.util.Optional.<CommunityChampionshipTeamPlayer>empty()
                : playerRegistrations.findByChampionshipIdAndProfileId(championshipId, profileId);
        if (existing.isEmpty() || !existing.get().getTeam().getId().equals(teamId)
                || profileId.equals(actor.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Escolhe outro jogador confirmado nesta equipa");
        team.setPendingAdminProfile(existing.get().getProfile());
        seasonTeams.saveAndFlush(team);
        if (teamNotifications != null)
            teamNotifications.adminTransfer(existing.get().getProfile(), actor, communityId, team.getName(),
                    TeamInvitationNotificationService.CompetitionTarget.championship(championshipId, teamId));
        return administrator(communityId, championshipId, teamId, username);
    }

    public TeamAdminStatus respondAdministrator(Long communityId, Long championshipId,
                                                 Long teamId, boolean accept, String username) {
        var actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireMember(communityId, username);
        var season = championships.findForClosing(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ensureOpen(season);
        var team = getSeasonTeam(communityId, championshipId, teamId);
        if (team.getPendingAdminProfile() == null
                || !actor.getId().equals(team.getPendingAdminProfile().getId()))
            throw new AccessDeniedException("Não tens uma transferência de administração pendente");
        if (accept) {
            var registered = playerRegistrations.findByChampionshipIdAndProfileId(championshipId, actor.getId());
            if (registered.isEmpty() || !registered.get().getTeam().getId().equals(teamId))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Já não pertences a esta equipa");
            team.setAdminProfile(actor);
            if (teamNotifications != null)
                teamNotifications.assigned(actor, communityId, team.getName(),
                        TeamInvitationNotificationService.CompetitionTarget.championship(championshipId, teamId));
        }
        team.setPendingAdminProfile(null);
        seasonTeams.saveAndFlush(team);
        return administrator(communityId, championshipId, teamId, username);
    }

    public TeamAdminStatus cancelAdministratorTransfer(Long communityId, Long championshipId,
                                                        Long teamId, String username) {
        var actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireMember(communityId, username);
        var season = championships.findForClosing(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ensureOpen(season);
        var team = getSeasonTeam(communityId, championshipId, teamId);
        if (team.getAdminProfile() == null
                || !team.getAdminProfile().getId().equals(actor.getId()))
            throw new AccessDeniedException("Não administras esta equipa");
        team.setPendingAdminProfile(null);
        seasonTeams.saveAndFlush(team);
        return administrator(communityId, championshipId, teamId, username);
    }

    private boolean isOrganizer(Long communityId, Long profileId) {
        return members.findByCommunityIdAndProfileId(communityId, profileId)
                .map(m -> m.getRole() == CommunityRole.OWNER || m.getRole() == CommunityRole.ADMIN)
                .orElse(false);
    }

    private void requireOrganizerOrTeamAdmin(Long communityId, Long championshipId, Long teamId,
                                             String username) {
        var actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        var membership = members.findByCommunityIdAndProfileId(communityId, actor.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences a esta comunidade"));
        if (membership.getRole() == CommunityRole.OWNER || membership.getRole() == CommunityRole.ADMIN) return;
        var team = getSeasonTeam(communityId, championshipId, teamId);
        if (team.getAdminProfile() == null || !actor.getId().equals(team.getAdminProfile().getId()))
            throw new AccessDeniedException("Não administras esta equipa");
    }

    @Transactional(readOnly = true)
    public List<TeamPlayer> players(Long communityId, Long championshipId, Long teamId, String username) {
        requireMember(communityId, username);
        getSeasonTeam(communityId, championshipId, teamId);
        return playerRegistrations.findByTeamIdOrderByJoinedAtAscIdAsc(teamId)
                .stream().map(reg -> {
                    var profile = reg.getProfile();
                    return new TeamPlayer(profile.getId(), profile.getUsername(),
                            profile.getFullName() == null || profile.getFullName().isBlank()
                                    ? profile.getUsername() : profile.getFullName(),
                            profile.getProfilePicUrl());
                }).toList();
    }

    public List<TeamPlayer> addPlayer(Long communityId, Long championshipId, Long teamId,
                                      Long profileId, String username) {
        requireOrganizerOrTeamAdmin(communityId, championshipId, teamId, username);
        var season = getChampionship(communityId, championshipId);
        ensureOpen(season);
        var team = getSeasonTeam(communityId, championshipId, teamId);
        if (profileId == null || !members.existsByCommunityIdAndProfileId(communityId, profileId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O jogador tem de pertencer à comunidade");
        if (playerRegistrations.findByChampionshipIdAndProfileId(championshipId, profileId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O jogador já pertence a uma equipa nesta época");
        var profile = profiles.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));
        try {
            playerRegistrations.saveAndFlush(CommunityChampionshipTeamPlayer.builder()
                    .championship(season).team(team).profile(profile).joinedAt(Instant.now()).build());
        } catch (org.springframework.dao.DataIntegrityViolationException conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O jogador já pertence a uma equipa nesta época", conflict);
        }
        return players(communityId, championshipId, teamId, username);
    }

    public void removePlayer(Long communityId, Long championshipId, Long teamId,
                             Long profileId, String username) {
        requireOrganizerOrTeamAdmin(communityId, championshipId, teamId, username);
        ensureOpen(getChampionship(communityId, championshipId));
        var managedTeam = getSeasonTeam(communityId, championshipId, teamId);
        if (managedTeam.getAdminProfile() != null &&
                managedTeam.getAdminProfile().getId().equals(profileId))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Substitui o administrador antes de o remover do plantel");
        var registration = playerRegistrations.findByChampionshipIdAndProfileId(championshipId, profileId)
                .filter(reg -> reg.getTeam().getId().equals(teamId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Jogador não inscrito nesta equipa"));
        // Preserve historical attribution: removing a player after a recorded
        // event would make match timelines inconsistent with the season roster.
        if (goals.existsByFixtureEventChampionshipIdAndScorerId(championshipId, profileId)
                || goals.existsByFixtureEventChampionshipIdAndAssisterId(championshipId, profileId)
                || disciplinaryCards.existsByFixtureEventChampionshipIdAndPlayerId(championshipId, profileId)
                || substitutions.existsByFixtureEventChampionshipIdAndPlayerOutId(championshipId, profileId)
                || substitutions.existsByFixtureEventChampionshipIdAndPlayerInId(championshipId, profileId)
                || lineups.existsByFixtureEventChampionshipIdAndPlayerId(championshipId, profileId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este jogador tem acontecimentos ou escalações registados. Corrige o histórico antes de o remover do plantel.");
        }
        playerRegistrations.delete(registration);
        if (managedTeam.getPendingAdminProfile() != null
                && managedTeam.getPendingAdminProfile().getId().equals(profileId)) {
            // Removed members must not retain a pending administrator invitation.
            managedTeam.setPendingAdminProfile(null);
            seasonTeams.saveAndFlush(managedTeam);
        }
    }

    private CommunityChampionshipTeam getSeasonTeam(Long communityId, Long championshipId, Long teamId) {
        getChampionship(communityId, championshipId);
        return seasonTeams.findById(teamId)
                .filter(team -> team.getChampionship().getId().equals(championshipId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Equipa não encontrada neste campeonato"));
    }

    private void ensureOpen(CommunityChampionship championship) {
        if (championship.getClosedAt() != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O campeonato está encerrado");
    }

    private CommunityChampionship getChampionship(Long communityId, Long championshipId) {
        return championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
    }

    private CommunityChampionshipTeamsResponse response(CommunityChampionship championship) {
        var teams = seasonTeams.findByChampionshipIdOrderByNameAscIdAsc(championship.getId())
                .stream().map(t -> new CommunityChampionshipTeamsResponse.Team(t.getId(), t.getName(),
                        t.getPermanentTeam() == null ? null : t.getPermanentTeam().getId())).toList();
        List<CommunityChampionshipTeamsResponse.CompetitionTeam> entries = new ArrayList<>();
        for (var tournament : championship.getCompetitions()) {
            for (var team : tournamentTeams.findByTournamentIdOrderByIdAsc(tournament.getId())) {
                entries.add(new CommunityChampionshipTeamsResponse.CompetitionTeam(
                        team.getId(), tournament.getId(), tournament.getName(), team.getName(),
                        team.getChampionshipTeam() == null ? null : team.getChampionshipTeam().getId()));
            }
        }
        return new CommunityChampionshipTeamsResponse(championship.getId(), teams, List.copyOf(entries));
    }

    private void requireMember(Long communityId, String username) {
        var profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        if (!members.existsByCommunityIdAndProfileId(communityId, profile.getId()))
            throw new AccessDeniedException("Não pertences a esta comunidade");
    }

    private void requireOrganizer(Long communityId, String username) {
        var profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        var member = members.findByCommunityIdAndProfileId(communityId, profile.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences a esta comunidade"));
        if (member.getRole() != CommunityRole.OWNER && member.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Apenas administradores podem associar equipas");
    }
}
