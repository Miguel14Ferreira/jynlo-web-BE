package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.enumeration.SessionInviteStatus;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.session.SessionInvite;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.session.SessionInviteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * One reminder to people who were invited to a game and have not answered:
 * - 24 hours after the invite, or
 * - 3 hours after it when the game starts within the next 24 hours,
 * and never when the game starts in less than 2 hours, is no longer open,
 * or the invite was answered. Sent at most once per invite (reminder_sent_at
 * is claimed atomically). Same preferences as invites (SESSION_INVITE).
 */
@Slf4j
@Component
public class SessionInviteReminderService {

    static final Duration NORMAL_WAIT = Duration.ofHours(24);
    static final Duration SHORT_WAIT = Duration.ofHours(3);
    static final Duration TOO_LATE = Duration.ofHours(2);
    static final Duration SOON = Duration.ofHours(24);

    private final SessionInviteRepository invites;
    private final NotificationsRepository notifications;
    private final ApplicationEventPublisher events;
    private final TransactionTemplate transactions;
    private final boolean enabled;
    Clock clock = Clock.systemUTC();

    @Autowired(required = false)
    private NotificationPreferencesService preferences;

    public SessionInviteReminderService(SessionInviteRepository invites, NotificationsRepository notifications,
                                        ApplicationEventPublisher events, PlatformTransactionManager transactionManager,
                                        @Value("${jynlo.sessions.invite-reminders-enabled:true}") boolean enabled) {
        this.invites = invites;
        this.notifications = notifications;
        this.events = events;
        this.transactions = new TransactionTemplate(transactionManager);
        this.enabled = enabled;
    }

    @Scheduled(fixedDelayString = "${jynlo.sessions.invite-reminder-check-ms:1800000}",
               initialDelayString = "${jynlo.sessions.invite-reminder-initial-delay-ms:300000}")
    public void remindPendingInvites() {
        if (!enabled) return;
        Instant now = clock.instant();
        for (Long id : invites.findDueForReminder(now.minus(SHORT_WAIT), Date.from(now.plus(TOO_LATE)), PageRequest.of(0, 100))) {
            try {
                remind(id, now);
            } catch (RuntimeException failed) {
                log.warn("Invite reminder failed for invite {}", id, failed);
            }
        }
    }

    /** @return true when a reminder was sent. */
    boolean remind(Long inviteId, Instant now) {
        Boolean sent = transactions.execute(status -> {
            SessionInvite invite = invites.findById(inviteId).orElse(null);
            if (invite == null || !due(invite, now)) return false;
            if (invites.claimReminder(inviteId, now) != 1) return false;
            var game = invite.getSession();
            var invitee = invite.getInvitee();
            String inviter = invite.getInviter() == null ? "o organizador" : "@" + invite.getInviter().getUsername();
            String what = label(game.getSport(), "Jogo") + (label(game.getCity(), "").isEmpty() ? "" : " em " + label(game.getCity(), ""));
            Notifications n = new Notifications();
            n.setSubject("Ainda não respondeste ao convite");
            n.setMessage(inviter + " está à espera da tua resposta: " + what + ", "
                    + SessionReminderService.whenLabel(game.getGameDate().toInstant(), now).toLowerCase(java.util.Locale.ROOT) + ".");
            n.setGreeting("");
            n.setSignature("");
            n.setCreatedAt(now.toString());
            n.setUsername(invitee.getUsername());
            n.setRelatedUsername(invite.getInviter() == null ? null : invite.getInviter().getUsername());
            n.setSessionInvite(true);
            n.setSessionId(game.getId());
            n.setType(NotificationType.SESSION_INVITE);
            n.setRead(false);
            if (preferences != null && !preferences.allowsInApp(n.getUsername(), n)) return false;
            Notifications saved = notifications.save(n);
            if (saved != null && saved.getId() != null) {
                events.publishEvent(new MobilePushDeliveryService.StoredNotification(
                        saved.getUsername(), saved.getId(), NotificationType.SESSION_INVITE.name(),
                        game.getId(), null, saved.getRelatedUsername(), true));
            }
            return true;
        });
        return Boolean.TRUE.equals(sent);
    }

    static boolean due(SessionInvite invite, Instant now) {
        if (invite.getStatus() != SessionInviteStatus.PENDING || invite.getReminderSentAt() != null) return false;
        var game = invite.getSession();
        if (game == null || game.getStatus() != SessionStatus.OPEN || game.getGameDate() == null
                || invite.getInvitee() == null || invite.getCreatedAt() == null) return false;
        Instant start = game.getGameDate().toInstant();
        if (!start.isAfter(now.plus(TOO_LATE))) return false;
        Duration age = Duration.between(invite.getCreatedAt(), now);
        boolean soon = !start.isAfter(now.plus(SOON));
        return age.compareTo(soon ? SHORT_WAIT : NORMAL_WAIT) >= 0;
    }

    private static String label(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        String cleaned = value.strip().replaceAll("[\\p{Cntrl}]+", " ");
        return cleaned.length() > 70 ? cleaned.substring(0, 70).strip() : cleaned;
    }
}
