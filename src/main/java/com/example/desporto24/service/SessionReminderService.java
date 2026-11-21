package com.example.desporto24.service;

import com.example.desporto24.email.EmailSender;
import com.example.desporto24.email.TransactionalEmailTemplate;
import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.util.HtmlUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pre-game reminder, about 24 hours before each open or full session:
 * - every active participant gets "Amanhã/Hoje às HH:mm: <desporto> em <cidade>";
 * - the organiser also learns how many places are still free.
 *
 * Sent at most once per session (pre_game_reminder_sent_at is set under a row
 * lock before anything is delivered). In-app notifications respect the user's
 * in-app preferences; emails respect the optional email preference for
 * SESSION_REMINDER. Sessions that start in less than two hours are skipped:
 * a reminder that late is noise.
 */
@Slf4j
@Component
public class SessionReminderService {

    static final Duration WINDOW = Duration.ofHours(24);
    static final Duration TOO_LATE = Duration.ofHours(2);
    private static final ZoneId LISBON = ZoneId.of("Europe/Lisbon");
    private static final DateTimeFormatter HOUR = DateTimeFormatter.ofPattern("HH:mm", Locale.forLanguageTag("pt-PT")).withZone(LISBON);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM", Locale.forLanguageTag("pt-PT")).withZone(LISBON);

    private final SessionRepository sessions;
    private final SessionParticipantRepository participants;
    private final SessionNotificationService notifications;
    private final EmailSender emailSender;
    private final TransactionTemplate transactions;
    private final String uiAppUrl;
    private final boolean enabled;
    Clock clock = Clock.systemUTC();

    @Autowired(required = false)
    private NotificationPreferencesService preferences;

    public SessionReminderService(SessionRepository sessions,
                                  SessionParticipantRepository participants,
                                  SessionNotificationService notifications,
                                  EmailSender emailSender,
                                  org.springframework.transaction.PlatformTransactionManager transactionManager,
                                  @Value("${ui.app.url}") String uiAppUrl,
                                  @Value("${jynlo.sessions.reminders-enabled:true}") boolean enabled) {
        this.sessions = sessions;
        this.participants = participants;
        this.notifications = notifications;
        this.emailSender = emailSender;
        this.transactions = new TransactionTemplate(transactionManager);
        this.uiAppUrl = uiAppUrl;
        this.enabled = enabled;
    }

    record Reminder(Profile profile, String subject, String message, boolean organiser) {}

    @Scheduled(fixedDelayString = "${jynlo.sessions.reminder-check-ms:600000}",
               initialDelayString = "${jynlo.sessions.reminder-initial-delay-ms:120000}")
    public void sendDueReminders() {
        if (!enabled) return;
        Instant now = clock.instant();
        List<Long> due = sessions.findDueForPreGameReminder(
                Date.from(now.plus(TOO_LATE)), Date.from(now.plus(WINDOW)), PageRequest.of(0, 50));
        for (Long id : due) {
            try {
                remind(id, now);
            } catch (RuntimeException failed) {
                // One bad session must not block the others; it is retried next pass.
                log.warn("Pre-game reminder failed for session {}", id, failed);
            }
        }
    }

    void remind(Long sessionId, Instant now) {
        // Claim the session and build the messages inside one short transaction.
        List<Reminder> reminders = transactions.execute(status -> {
            Session session = sessions.findByIdForUpdate(sessionId).orElse(null);
            if (session == null || session.getPreGameReminderSentAt() != null || !eligible(session, now)) {
                return List.<Reminder>of();
            }
            session.setPreGameReminderSentAt(now);
            sessions.save(session);
            List<Reminder> built = build(session, now);
            for (Reminder reminder : built) {
                notifications.notifySessionReminder(session, reminder.profile(), reminder.subject(), reminder.message());
            }
            return built;
        });
        if (reminders == null) return;
        // Emails after commit: a mail failure never re-sends in-app reminders.
        for (Reminder reminder : reminders) {
            sendEmail(sessionId, reminder);
        }
    }

    static boolean eligible(Session session, Instant now) {
        if (session.getGameDate() == null) return false;
        Instant start = session.getGameDate().toInstant();
        boolean open = session.getStatus() == SessionStatus.OPEN || session.getStatus() == SessionStatus.FULL;
        return open && start.isAfter(now.plus(TOO_LATE)) && !start.isAfter(now.plus(WINDOW));
    }

    List<Reminder> build(Session session, Instant now) {
        Map<String, Profile> going = new LinkedHashMap<>();
        participants.findBySessionIdAndStateOrderByJoinedAtAsc(session.getId(), ParticipantState.ACTIVE)
                .stream().map(p -> p.getProfile())
                .filter(p -> p != null && p.getUsername() != null && !p.getUsername().isBlank())
                .forEach(p -> going.putIfAbsent(p.getUsername().toLowerCase(Locale.ROOT), p));
        Profile organiser = session.getProfile();
        String organiserKey = organiser == null || organiser.getUsername() == null ? null
                : organiser.getUsername().toLowerCase(Locale.ROOT);
        int confirmed = going.size();
        Integer max = session.getMaxParticipants();
        String what = label(session.getSport(), "Jogo") + (label(session.getCity(), "").isBlank() ? "" : " em " + label(session.getCity(), ""));
        String when = whenLabel(session.getGameDate().toInstant(), now);
        String count = max == null || max <= 0 ? confirmed + " confirmados" : confirmed + "/" + max + " confirmados";

        List<Reminder> result = new ArrayList<>();
        for (Map.Entry<String, Profile> entry : going.entrySet()) {
            if (entry.getKey().equals(organiserKey)) continue;
            result.add(new Reminder(entry.getValue(), when + ": " + what,
                    "Não te esqueças: " + what + ", " + when.toLowerCase(Locale.ROOT) + ". " + count + ".", false));
        }
        if (organiser != null && organiserKey != null) {
            String missing = max == null || max <= 0 ? "" : max - confirmed > 0
                    ? " Faltam " + (max - confirmed) + (max - confirmed == 1 ? " jogador." : " jogadores.")
                    : " A sessão está completa.";
            result.add(new Reminder(organiser, when + ": " + what + " (organizas tu)",
                    "A tua sessão é " + when.toLowerCase(Locale.ROOT) + ". " + count + "." + missing, true));
        }
        return result;
    }

    public static String whenLabel(Instant start, Instant now) {
        var startDay = start.atZone(LISBON).toLocalDate();
        var today = now.atZone(LISBON).toLocalDate();
        String day = startDay.equals(today) ? "Hoje" : startDay.equals(today.plusDays(1)) ? "Amanhã" : DAY.format(start);
        return day + " às " + HOUR.format(start);
    }

    private void sendEmail(Long sessionId, Reminder reminder) {
        Profile profile = reminder.profile();
        String email = profile.getEmail();
        if (email == null || email.isBlank()) return;
        try {
            if (preferences != null && !preferences.allowsOptionalEmail(profile.getUsername(), NotificationType.SESSION_REMINDER)) {
                return;
            }
            String link = uiAppUrl.replaceAll("/+$", "") + "/menu/events/" + sessionId;
            String html = TransactionalEmailTemplate.render(
                    HtmlUtils.htmlEscape(reminder.subject()),
                    HtmlUtils.htmlEscape(reminder.message()),
                    "Game reminder",
                    reminder.organiser()
                            ? "Your session is coming up. Open it to see who is going."
                            : "Your game is coming up. Open the session to see the details.",
                    "Ver sessão / View session",
                    link,
                    "Podes desligar estes emails em Notificações › Gerir notificações.",
                    "You can turn these emails off in Notifications › Manage notifications.");
            emailSender.send(email, reminder.subject() + " — Jynlo", html);
        } catch (RuntimeException failed) {
            log.warn("Pre-game reminder email skipped for session {}", sessionId, failed);
        }
    }

    private static String label(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        String cleaned = value.strip().replaceAll("[\\p{Cntrl}]+", " ");
        return cleaned.length() > 70 ? cleaned.substring(0, 70).strip() : cleaned;
    }
}
