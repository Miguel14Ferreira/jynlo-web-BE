package com.example.desporto24.service.nearby;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.enumeration.SessionAudience;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.moderation.UserBlockRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.service.SessionCapacity;
import com.example.desporto24.service.SessionNotificationService;
import com.example.desporto24.service.SessionReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * "New game near you" alerts (docs/session-geographic-notifications.md in the app repository).
 *
 * Opt-in only: a person turns it on and picks a place, a radius (10/25/50/100 km)
 * and, optionally, sports. A scheduled pass looks at sessions created in the
 * last two hours, once each (nearby_alerts_processed_at is set under a row
 * lock), and alerts people whose place is within their radius. Rules:
 * - only public, not password-protected, enabled, open sessions with real
 *   coordinates, starting between 2 hours and 14 days from now;
 * - never the organiser, people already going, or people blocked either way;
 * - at most 2 alerts per person per 24 hours, at most one per session;
 * - the push text is generic; the in-app text has sport, city and time only,
 *   never the address.
 *
 * jynlo.nearby-alerts.mode: "on" (default), "dry-run" (counts only, logged) or "off".
 */
@Slf4j
@Service
public class NearbySessionAlertService {

    static final Duration LOOKBACK = Duration.ofHours(2);
    static final Duration TOO_SOON = Duration.ofHours(2);
    static final Duration TOO_FAR_AHEAD = Duration.ofDays(14);
    static final Duration DAILY_WINDOW = Duration.ofHours(24);
    static final int DAILY_LIMIT = 2;
    static final int MAX_SPORTS = 12;
    private static final double KM_PER_DEGREE = 111.32;
    private static final int MAX_RADIUS_KM = 100;

    private final NearbyAlertStore store;
    private final SessionRepository sessions;
    private final SessionParticipantRepository participants;
    private final ProfileRepository profiles;
    private final UserBlockRepository blocks;
    private final SessionNotificationService notifications;
    private final TransactionTemplate transactions;
    private final String mode;
    Clock clock = Clock.systemUTC();

    public NearbySessionAlertService(NearbyAlertStore store,
                                     SessionRepository sessions,
                                     SessionParticipantRepository participants,
                                     ProfileRepository profiles,
                                     UserBlockRepository blocks,
                                     SessionNotificationService notifications,
                                     PlatformTransactionManager transactionManager,
                                     @Value("${jynlo.nearby-alerts.mode:on}") String mode) {
        this.store = store;
        this.sessions = sessions;
        this.participants = participants;
        this.profiles = profiles;
        this.blocks = blocks;
        this.notifications = notifications;
        this.transactions = new TransactionTemplate(transactionManager);
        this.mode = mode == null ? "on" : mode.strip().toLowerCase(Locale.ROOT);
    }

    // ---- Preferences -------------------------------------------------------

    public NearbyAlertPreference get(String username) {
        return store.find(profileId(username)).orElse(NearbyAlertPreference.off());
    }

    public NearbyAlertPreference update(String username, NearbyAlertPreference request) {
        NearbyAlertPreference clean = validate(request);
        store.save(profileId(username), clean, clock.instant());
        return clean;
    }

    static NearbyAlertPreference validate(NearbyAlertPreference request) {
        if (request == null) throw bad("Pedido inválido.");
        int radius = request.radiusKm() == 0 ? NearbyAlertPreference.DEFAULT_RADIUS_KM : request.radiusKm();
        if (!NearbyAlertPreference.RADII_KM.contains(radius)) throw bad("Escolhe um raio de 10, 25, 50 ou 100 km.");
        Double lat = request.latitude(), lng = request.longitude();
        if ((lat == null) != (lng == null)) throw bad("Localização incompleta.");
        if (lat != null && !validPosition(lat, lng)) throw bad("Localização inválida.");
        if (request.enabled() && lat == null) throw bad("Escolhe um local para receberes avisos.");
        String label = request.placeLabel() == null ? null : request.placeLabel().strip();
        if (label != null && (label.length() > 80 || label.codePoints().anyMatch(Character::isISOControl))) {
            throw bad("Nome do local inválido.");
        }
        Map<String, String> sports = new LinkedHashMap<>();
        for (String sport : request.sports() == null ? List.<String>of() : request.sports()) {
            if (sport == null) continue;
            String value = sport.strip();
            if (value.isEmpty()) continue;
            if (value.length() > 40 || value.contains(",") || value.codePoints().anyMatch(Character::isISOControl)) {
                throw bad("Modalidade inválida.");
            }
            sports.putIfAbsent(normalize(value), value);
        }
        if (sports.size() > MAX_SPORTS) throw bad("Escolhe no máximo " + MAX_SPORTS + " modalidades.");
        return new NearbyAlertPreference(request.enabled(),
                lat == null ? null : round2(lat), lng == null ? null : round2(lng),
                label == null || label.isEmpty() ? null : label, radius, List.copyOf(sports.values()));
    }

    // ---- Delivery ----------------------------------------------------------

    @Scheduled(fixedDelayString = "${jynlo.nearby-alerts.check-ms:300000}",
               initialDelayString = "${jynlo.nearby-alerts.initial-delay-ms:180000}")
    public void processNewSessions() {
        if ("off".equals(mode)) return;
        Instant now = clock.instant();
        List<Long> due = sessions.findDueForNearbyAlerts(Date.from(now.minus(LOOKBACK)), PageRequest.of(0, 50));
        for (Long id : due) {
            try {
                process(id, now);
            } catch (RuntimeException failed) {
                log.warn("Nearby alerts failed for session {}", id, failed);
            }
        }
    }

    /** @return how many people were (or, in dry-run, would be) alerted. */
    int process(Long sessionId, Instant now) {
        Integer sent = transactions.execute(status -> {
            Session session = sessions.findByIdForUpdate(sessionId).orElse(null);
            if (session == null || session.getNearbyAlertsProcessedAt() != null) return 0;
            session.setNearbyAlertsProcessedAt(now);
            sessions.save(session);
            if (!eligible(session, now)) return 0;
            List<Profile> recipients = recipients(session, now);
            boolean dryRun = "dry-run".equals(mode);
            int count = 0;
            for (Profile recipient : recipients) {
                if (dryRun) { count++; continue; }
                if (!store.recordDelivery(recipient.getId(), session.getId(), now)) continue;
                String[] text = text(session, now);
                notifications.notifyNearbySession(session, recipient, text[0], text[1]);
                count++;
            }
            log.info("nearby_alerts session={} sport={} recipients={} mode={}",
                    session.getId(), session.getSport(), count, mode);
            return count;
        });
        return sent == null ? 0 : sent;
    }

    static boolean eligible(Session session, Instant now) {
        boolean publicAudience = session.getAudience() == SessionAudience.PUBLIC
                || (session.getAudience() == null && session.getCommunity() == null);
        if (!publicAudience || Boolean.TRUE.equals(session.getPrivateGame())
                || Boolean.FALSE.equals(session.getEnabled())
                || session.getStatus() != SessionStatus.OPEN) return false;
        if (session.getLatitude() == null || session.getLongitude() == null
                || !validPosition(session.getLatitude(), session.getLongitude())) return false;
        if (session.getGameDate() == null) return false;
        Instant start = session.getGameDate().toInstant();
        return start.isAfter(now.plus(TOO_SOON)) && !start.isAfter(now.plus(TOO_FAR_AHEAD));
    }

    List<Profile> recipients(Session session, Instant now) {
        double lat = session.getLatitude(), lng = session.getLongitude();
        double dLat = MAX_RADIUS_KM / KM_PER_DEGREE;
        double dLng = MAX_RADIUS_KM / (KM_PER_DEGREE * Math.max(0.01, Math.cos(Math.toRadians(lat))));
        Profile organiser = session.getProfile();
        Long organiserId = organiser == null ? null : organiser.getId();
        Set<Long> going = participants.findBySessionIdAndStateOrderByJoinedAtAsc(session.getId(), ParticipantState.ACTIVE)
                .stream().map(p -> p.getProfile() == null ? null : p.getProfile().getId())
                .filter(Objects::nonNull).collect(Collectors.toSet());
        String sport = normalize(session.getSport());

        List<Profile> result = new ArrayList<>();
        for (NearbyAlertStore.Candidate candidate : store.candidatesInBox(lat - dLat, lat + dLat, lng - dLng, lng + dLng)) {
            if (Objects.equals(candidate.profileId(), organiserId) || going.contains(candidate.profileId())) continue;
            if (distanceKm(lat, lng, candidate.latitude(), candidate.longitude()) > candidate.radiusKm()) continue;
            if (!candidate.sports().isEmpty()
                    && candidate.sports().stream().map(NearbySessionAlertService::normalize).noneMatch(sport::equals)) continue;
            if (organiserId != null && blocks.existsBetween(candidate.profileId(), organiserId)) continue;
            if (store.sentSince(candidate.profileId(), now.minus(DAILY_WINDOW)) >= DAILY_LIMIT) continue;
            Profile profile = profiles.findById(candidate.profileId()).orElse(null);
            if (profile == null || profile.getUsername() == null || profile.getUsername().isBlank()
                    || !Boolean.TRUE.equals(profile.getEnabled())
                    || Boolean.FALSE.equals(profile.getNotLocked())
                    || profile.getModerationSuspendedAt() != null) continue;
            result.add(profile);
        }
        return result;
    }

    String[] text(Session session, Instant now) {
        String sport = label(session.getSport(), "Jogo");
        String city = label(session.getCity(), "");
        String when = SessionReminderService.whenLabel(session.getGameDate().toInstant(), now);
        long active = participants.countBySessionIdAndState(session.getId(), ParticipantState.ACTIVE);
        Integer max = session.getMaxParticipants();
        String places = "";
        if (max != null && max > 0) {
            long free = Math.max(0, max - SessionCapacity.totalConfirmed(session, active));
            places = " " + free + (free == 1 ? " lugar livre." : " lugares livres.");
        }
        String where = city.isEmpty() ? "" : " em " + city;
        return new String[]{
                "Novo jogo perto de ti: " + sport,
                sport + where + ", " + when.substring(0, 1).toLowerCase(Locale.ROOT) + when.substring(1) + "." + places};
    }

    // ---- Helpers -----------------------------------------------------------

    static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1), dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 6371.0 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    static boolean validPosition(double lat, double lng) {
        return Double.isFinite(lat) && Double.isFinite(lng) && lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180
                && !(lat == 0 && lng == 0);
    }

    static String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value.strip(), Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static String label(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        String cleaned = value.strip().replaceAll("[\\p{Cntrl}]+", " ");
        return cleaned.length() > 70 ? cleaned.substring(0, 70).strip() : cleaned;
    }

    private long profileId(String username) {
        return profiles.findUserByUsername(username)
                .map(Profile::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
