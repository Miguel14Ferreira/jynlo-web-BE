package com.example.desporto24.service;

import com.example.desporto24.repository.session.SessionPaymentSetupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * "Falta pagar" after the game. A game usually lasts about an hour, so players who have not paid
 * are reminded 1h30 after the start. Never at night: between 23:00 and 09:00 (Lisbon) it waits
 * for the morning. Games older than three days are skipped so a late start never sends stale reminders.
 */
@Slf4j
// Production beans are lazy: without this the job was never created, so it never ran.
@Component
@org.springframework.context.annotation.Lazy(false)
public class PostGamePaymentReminderJob {
    static final Duration AFTER_START = Duration.ofMinutes(90);
    static final Duration TOO_OLD = Duration.ofDays(3);
    private static final ZoneId LISBON = ZoneId.of("Europe/Lisbon");

    private final SessionPaymentSetupRepository setups;
    private final SessionPaymentService payments;
    private final boolean enabled;
    Clock clock = Clock.systemUTC();

    public PostGamePaymentReminderJob(SessionPaymentSetupRepository setups, SessionPaymentService payments,
                                      @Value("${jynlo.payments.post-game-reminders-enabled:true}") boolean enabled) {
        this.setups = setups;
        this.payments = payments;
        this.enabled = enabled;
    }

    @Scheduled(fixedDelayString = "${jynlo.payments.post-game-check-ms:600000}",
               initialDelayString = "${jynlo.payments.post-game-initial-delay-ms:180000}")
    public void run() {
        if (!enabled) return;
        Instant now = clock.instant();
        if (!daytime(now)) return;
        List<Long> due = setups.findDueForPostGameReminder(
                Date.from(now.minus(TOO_OLD)), Date.from(now.minus(AFTER_START)), PageRequest.of(0, 50));
        for (Long id : due) {
            try {
                payments.sendPostGameReminder(id, now);
            } catch (RuntimeException failed) {
                log.warn("Post-game payment reminder failed for session {}", id, failed);
            }
        }
    }

    static boolean daytime(Instant now) {
        int hour = now.atZone(LISBON).getHour();
        return hour >= 9 && hour < 23;
    }
}
