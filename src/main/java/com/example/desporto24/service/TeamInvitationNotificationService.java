package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.Profile;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Optional in-app alerts for competition team invitations.
 * An alert is informational only: the team endpoint rechecks membership,
 * scope, status, and invite ownership when the user accepts.
 */
@Service
@RequiredArgsConstructor
public class TeamInvitationNotificationService {
    private final NotificationsRepository notificationsRepository;

    @Autowired(required = false)
    private NotificationPreferencesService preferences;

    public record CompetitionTarget(Long tournamentId, Long championshipId, Long teamId) {
        public static CompetitionTarget tournament(Long tournamentId, Long teamId) {
            return new CompetitionTarget(tournamentId, null, teamId);
        }
        public static CompetitionTarget championship(Long championshipId, Long teamId) {
            return new CompetitionTarget(null, championshipId, teamId);
        }
    }

    public void memberInvitation(Profile recipient, Profile sender, Long communityId, String teamName) {
        memberInvitation(recipient, sender, communityId, teamName, null);
    }

    public void memberInvitation(Profile recipient, Profile sender, Long communityId, String teamName,
                                 CompetitionTarget target) {
        send(recipient, sender, communityId, NotificationType.TEAM_MEMBER_INVITE,
                "Convite para equipa: " + teamName,
                "Foste convidado para a equipa " + teamName
                        + ". Abre a comunidade em Competições para aceitar ou recusar. "
                        + "O primeiro membro a aceitar torna-se administrador.", target);
    }

    public void adminTransfer(Profile recipient, Profile sender, Long communityId, String teamName) {
        adminTransfer(recipient, sender, communityId, teamName, null);
    }

    public void adminTransfer(Profile recipient, Profile sender, Long communityId, String teamName,
                              CompetitionTarget target) {
        send(recipient, sender, communityId, NotificationType.TEAM_ADMIN_TRANSFER,
                "Transferência da equipa: " + teamName,
                "O administrador propôs que assumas a equipa " + teamName
                        + ". Abre Competições para aceitar ou recusar. "
                        + "O administrador atual mantém-se até aceitares.", target);
    }

    public void assigned(Profile recipient, Long communityId, String teamName) {
        assigned(recipient, communityId, teamName, null);
    }

    public void assigned(Profile recipient, Long communityId, String teamName,
                         CompetitionTarget target) {
        send(recipient, null, communityId, NotificationType.TEAM_ADMIN_ASSIGNED,
                "Agora administras a equipa " + teamName,
                "Foste confirmado como administrador da equipa " + teamName
                        + ". Podes gerir os membros no separador Competições da comunidade.", target);
    }

    private void send(Profile recipient, Profile sender, Long communityId,
                      NotificationType type, String subject, String message, CompetitionTarget target) {
        if (recipient == null || recipient.getUsername() == null
                || recipient.getUsername().isBlank()) return;
        Notifications notification = new Notifications();
        notification.setUsername(recipient.getUsername());
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setGreeting("");
        notification.setSignature("");
        notification.setCreatedAt(Instant.now().toString());
        notification.setFriendRequest(false);
        notification.setUpdateProfile(false);
        notification.setSessionInvite(false);
        notification.setNormalMessage(false);
        notification.setToken(null);
        notification.setCommunityId(communityId);
        if (target != null) {
            notification.setCompetitionTournamentId(target.tournamentId());
            notification.setCompetitionChampionshipId(target.championshipId());
            notification.setCompetitionTeamId(target.teamId());
        }
        notification.setRelatedUsername(sender == null ? null : sender.getUsername());
        notification.setType(type);
        notification.setRead(false);
        if (preferences == null || preferences.allowsInApp(notification.getUsername(), notification)) {
            notificationsRepository.save(notification);
        }
    }
}
