package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Alerts the other members when a team chat message is committed, like
 * private groups: one unread alert per team and member (a busy chat does not
 * flood the inbox or the phone); account preferences and the per-conversation
 * mute ("team") apply. Opening the team chat clears the alert.
 */
@Component
@RequiredArgsConstructor
public class TeamChatNotificationListener {

    /** Published by SportsTeamContentService after a chat message is saved. */
    public record TeamChatMessagePosted(Long teamId, Long messageId, String sender, String text) {}

    private final NotificationsRepository notifications;
    private final SportsTeamRepository teams;
    private final SportsTeamMemberRepository members;
    private final NotificationPreferencesService preferences;
    private final ConversationNotificationMuteService mutes;
    private final ApplicationEventPublisher events;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMessage(TeamChatMessagePosted event) {
        if (event == null || event.teamId() == null || event.sender() == null) return;
        SportsTeam team = teams.findById(event.teamId()).orElse(null);
        if (team == null || !team.isChatEnabled()) return;
        String name = team.getName() == null || team.getName().isBlank() ? "equipa" : team.getName().strip();
        String preview = ChatMessagePreview.forText(event.text());
        for (String member : recipients(team).values()) {
            if (member.equalsIgnoreCase(event.sender())) continue;
            try {
                notify(member, event.sender(), team.getId(), name, preview);
            } catch (RuntimeException ignored) {
                // One member's alert must never block the others; the message is already saved.
            }
        }
    }

    /** Opening the team chat clears its alert, so the next message alerts again. Access is checked by the caller. */
    @Transactional
    public void markRead(String username, Long teamId) {
        if (username == null || teamId == null || teamId <= 0) return;
        var unread = notifications.findByUsernameIgnoreCaseAndSportsTeamIdAndReadFalse(username, teamId);
        if (unread == null || unread.isEmpty()) return;
        unread.forEach(n -> n.setRead(true));
        notifications.saveAll(unread);
    }

    private Map<String, String> recipients(SportsTeam team) {
        Map<String, String> result = new LinkedHashMap<>();
        if (team.getOwner() != null && team.getOwner().getUsername() != null) {
            result.put(team.getOwner().getUsername().toLowerCase(Locale.ROOT), team.getOwner().getUsername());
        }
        for (SportsTeamMember m : members.findByTeamIdAndStatusOrderByIdAsc(team.getId(), SportsTeamMember.Status.ACTIVE)) {
            var p = m.getProfile();
            if (p == null || p.getUsername() == null || Boolean.FALSE.equals(p.getEnabled())
                    || p.getModerationSuspendedAt() != null) continue;
            result.putIfAbsent(p.getUsername().toLowerCase(Locale.ROOT), p.getUsername());
        }
        return result;
    }

    private void notify(String member, String sender, Long teamId, String teamName, String preview) {
        if (mutes.muted(member.toLowerCase(Locale.ROOT), "team", teamId.toString())) return;
        if (notifications.existsByUsernameAndSportsTeamIdAndReadFalse(member, teamId)) return;
        Notifications n = new Notifications();
        n.setSubject("Nova mensagem em " + teamName);
        n.setMessage(sender + ": " + preview);
        n.setGreeting("");
        n.setSignature("");
        n.setCreatedAt(Instant.now().toString());
        n.setNormalMessage(true);
        n.setUsername(member);
        n.setType(NotificationType.CHAT_MESSAGE);
        n.setSportsTeamId(teamId);
        // No relatedUsername: older clients would open the sender's direct chat instead of the team.
        n.setRead(false);
        if (!preferences.allowsInApp(member, n)) return;
        Notifications saved = notifications.save(n);
        if (saved != null && saved.getId() != null) {
            events.publishEvent(new MobilePushDeliveryService.StoredNotification(
                    saved.getUsername(), saved.getId(), NotificationType.CHAT_MESSAGE.name(),
                    null, null, null, true, null, teamId));
        }
    }
}
