package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportsTeamMembershipService {
    private final SportsTeamRepository teams;
    private final SportsTeamMemberRepository members;
    private final ProfileRepository profiles;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private org.springframework.context.ApplicationEventPublisher realtimeEvents;

    public record RosterPlayer(Long profileId, String username, String role) {}
    public record Invitation(Long teamId, String teamName, String status) {}
    public record PendingInvitation(Long profileId, String username, String status) {}

    @Transactional(readOnly = true)
    public List<PendingInvitation> pendingInvitations(Long teamId, String username) {
        SportsTeam team = team(teamId);
        Profile caller = actor(username);
        if (!team.getOwner().getId().equals(caller.getId()))
            throw new AccessDeniedException("Apenas o responsável pode consultar os convites");
        requireEligible(caller);
        return members.findByTeamIdAndStatusOrderByIdAsc(teamId, SportsTeamMember.Status.INVITED)
                .stream().map(m -> new PendingInvitation(
                        m.getProfile().getId(), m.getProfile().getUsername(), "INVITED"))
                .toList();
    }

    public record PlayerCandidate(Long profileId, String username, String fullName) {}
    public record JoinedTeam(Long id, String slug, String name, String sport, String location,
                             String description, Long ownerProfileId, boolean feedEnabled,
                             boolean chatEnabled) {}

    /** Only ACTIVE memberships are exposed. Pending and declined invitations stay private. */
    @Transactional(readOnly = true)
    public List<JoinedTeam> joined(String username) {
        Profile profile = actor(username);
        return members.findByProfileIdAndStatusOrderByIdDesc(
                profile.getId(), SportsTeamMember.Status.ACTIVE).stream()
                .map(m -> m.getTeam())
                .filter(t -> !t.getOwner().getId().equals(profile.getId()))
                .map(t -> new JoinedTeam(t.getId(), t.getSlug(), t.getName(), t.getSport(),
                        t.getLocation(), t.getDescription(), t.getOwner().getId(),
                        t.isFeedEnabled(), t.isChatEnabled()))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<PlayerCandidate> searchPlayers(String query, String username) {
        actor(username);
        String q = query == null ? "" : query.trim().toLowerCase(java.util.Locale.ROOT);
        if (q.length() < 2 || q.length() > 80) return List.of();
        return profiles.searchPublicCandidates(username, q, "", "", "",
                org.springframework.data.domain.PageRequest.of(0, 12)).stream()
                .map(p -> new PlayerCandidate(p.getId(), p.getUsername(), p.getFullName()))
                .toList();
    }

    private SportsTeam team(Long id) {
        return teams.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada"));
    }

    private Profile actor(String username) {
        return profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
    }

    @Transactional(readOnly = true)
    public List<RosterPlayer> roster(Long id) {
        SportsTeam team = team(id);
        List<RosterPlayer> result = new ArrayList<>();
        result.add(new RosterPlayer(team.getOwner().getId(), team.getOwner().getUsername(), "OWNER"));
        members.findByTeamIdAndStatusOrderByIdAsc(id, SportsTeamMember.Status.ACTIVE)
                .stream()
                .filter(m -> !m.getProfile().getId().equals(team.getOwner().getId()))
                .forEach(m -> result.add(new RosterPlayer(
                        m.getProfile().getId(), m.getProfile().getUsername(), "PLAYER")));
        return result;
    }

    @Transactional(readOnly = true)
    public List<Invitation> mine(String username) {
        Profile profile = actor(username);
        return members.findByProfileIdAndStatusOrderByIdDesc(
                profile.getId(), SportsTeamMember.Status.INVITED).stream()
                .map(m -> new Invitation(m.getTeam().getId(), m.getTeam().getName(),
                        m.getStatus().name())).toList();
    }

    private void requireEligible(Profile actor) {
        if (Boolean.FALSE.equals(actor.getEnabled()) || actor.getModerationSuspendedAt() != null)
            throw new AccessDeniedException("Conta indisponível para gerir convites");
    }

    @Transactional
    public void invite(Long teamId, Long targetProfileId, String username) {
        SportsTeam team = team(teamId);
        Profile inviter = actor(username);
        if (!team.getOwner().getId().equals(inviter.getId()))
            throw new AccessDeniedException("Apenas o responsável pode convidar jogadores");
        requireEligible(inviter);
        Profile target = profiles.findById(targetProfileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado"));
        if (Boolean.FALSE.equals(target.getEnabled()) || target.getModerationSuspendedAt() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jogador indisponível");
        if (target.getId().equals(inviter.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O responsável já pertence à equipa");
        var existing = members.findByTeamIdAndProfileId(teamId, targetProfileId);
        if (existing.isPresent() && existing.get().getStatus() != SportsTeamMember.Status.DECLINED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O jogador já está convidado ou pertence à equipa");
        SportsTeamMember member = existing.orElseGet(() -> SportsTeamMember.builder()
                .team(team).profile(target).build());
        member.setStatus(SportsTeamMember.Status.INVITED);
        member.setInvitedAt(Instant.now());
        member.setRespondedAt(null);
        members.saveAndFlush(member);
    }

    @Transactional
    public void respond(Long teamId, String username, boolean accept) {
        Profile profile = actor(username);
        // Pessimistic row lock makes the second simultaneous response see the committed status.
        var member = members.findInvitationForUpdate(teamId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Não existe convite para esta equipa"));
        if (member.getStatus() != SportsTeamMember.Status.INVITED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Convite já respondido");
        if (accept && (Boolean.FALSE.equals(profile.getEnabled())
                || profile.getModerationSuspendedAt() != null))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Jogador indisponível");
        member.setStatus(accept ? SportsTeamMember.Status.ACTIVE : SportsTeamMember.Status.DECLINED);
        member.setRespondedAt(Instant.now());
        members.saveAndFlush(member);
    }

    @Transactional
    public void remove(Long teamId, Long profileId, String username) {
        SportsTeam team = team(teamId);
        Profile actor = actor(username);
        if (!team.getOwner().getId().equals(actor.getId()) && !profileId.equals(actor.getId()))
            throw new AccessDeniedException("Não podes remover outro jogador");
        // Even a suspended player may leave voluntarily; only roster management
        // of other players requires an eligible account.
        if (!profileId.equals(actor.getId())) requireEligible(actor);
        if (team.getOwner().getId().equals(profileId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O responsável não pode sair sem transferir a equipa");
        var member = members.findByTeamIdAndProfileId(teamId, profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Jogador não pertence à equipa"));
        boolean wasActive = member.getStatus() == SportsTeamMember.Status.ACTIVE;
        String removedUsername = member.getProfile().getUsername();
        members.delete(member);
        members.flush();
        if (wasActive && realtimeEvents != null)
            realtimeEvents.publishEvent(new com.example.desporto24.websocket.TeamMembershipRevokedEvent(
                    teamId, removedUsername));
    }
}
