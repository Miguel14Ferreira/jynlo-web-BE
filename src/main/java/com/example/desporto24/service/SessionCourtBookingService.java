package com.example.desporto24.service;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionCourtBooking;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionCourtBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.Locale;

/**
 * "Reservar campo": the organiser keeps the club's booking link with the game and
 * marks the court as booked; players see whether the court is guaranteed.
 */
@Service
@RequiredArgsConstructor
public class SessionCourtBookingService {
    static final int MAX_URL = 500;

    private final SessionRepository sessions;
    private final ProfileRepository profiles;
    private final SessionParticipantRepository participants;
    private final SessionCourtBookingRepository bookings;

    public record CourtBooking(boolean organiser, String bookingUrl, String suggestedUrl,
                               String venueName, boolean booked, Instant bookedAt, boolean editable) {}

    @Transactional(readOnly = true)
    public CourtBooking get(Long sessionId, String username) {
        Access access = access(sessionId, username);
        return view(access, bookings.findById(sessionId).orElse(null));
    }

    @Transactional
    public CourtBooking update(Long sessionId, String username, String bookingUrl, Boolean booked) {
        Access access = access(sessionId, username);
        if (!access.organiser()) throw forbidden("Só o organizador pode gerir a reserva do campo");
        if (!editable(access.session())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este jogo já não pode ser alterado");
        }
        String url = bookingUrl == null || bookingUrl.isBlank() ? null : safeUrl(bookingUrl.trim());
        SessionCourtBooking row = bookings.findById(sessionId).orElseGet(() -> {
            SessionCourtBooking fresh = new SessionCourtBooking();
            fresh.setSessionId(sessionId);
            return fresh;
        });
        boolean nowBooked = booked != null ? booked : row.isBooked();
        if (nowBooked && !row.isBooked()) row.setBookedAt(Instant.now());
        if (!nowBooked) row.setBookedAt(null);
        row.setBooked(nowBooked);
        row.setBookingUrl(url);
        row.setUpdatedAt(Instant.now());
        bookings.save(row);
        return view(access, row);
    }

    /** Only https links to real hosts; never javascript:, data: or local addresses. */
    static String safeUrl(String raw) {
        String candidate = raw.contains("://") ? raw : "https://" + raw;
        if (candidate.length() > MAX_URL) throw badRequest("O link é demasiado longo");
        try {
            URI uri = new URI(candidate);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
            if (!"https".equals(scheme) || host.isBlank() || !host.contains(".")
                    || host.equals("localhost") || host.endsWith(".local") || uri.getUserInfo() != null) {
                throw badRequest("Indica um link https válido (por exemplo, a página do clube)");
            }
            return uri.toString();
        } catch (java.net.URISyntaxException invalid) {
            throw badRequest("Indica um link https válido (por exemplo, a página do clube)");
        }
    }

    private record Access(Session session, boolean organiser) {}

    private Access access(Long sessionId, String username) {
        Session session = sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada"));
        Profile actor = profiles.findUserByUsername(username).orElseThrow(() -> forbidden("Conta não encontrada"));
        boolean organiser = session.getProfile() != null && session.getProfile().getId() != null
                && session.getProfile().getId().equals(actor.getId());
        if (!organiser && participants
                .findBySessionIdAndProfileIdAndState(sessionId, actor.getId(), ParticipantState.ACTIVE).isEmpty()) {
            throw forbidden("Só quem está inscrito vê a reserva do campo");
        }
        return new Access(session, organiser);
    }

    private CourtBooking view(Access access, SessionCourtBooking row) {
        Session session = access.session();
        String venueName = session.getVenue() == null ? null : session.getVenue().getName();
        String suggested = null;
        if (session.getVenue() != null) {
            for (String candidate : new String[] {session.getVenue().getBookingUrl(), session.getVenue().getWebsite()}) {
                if (suggested != null || candidate == null || candidate.isBlank()) continue;
                try { suggested = safeUrl(candidate.trim()); } catch (ResponseStatusException ignored) { }
            }
        }
        // Players only see whether the court is booked; the booking link stays with the organiser.
        boolean organiser = access.organiser();
        return new CourtBooking(organiser, !organiser || row == null ? null : row.getBookingUrl(),
                organiser ? suggested : null, venueName,
                row != null && row.isBooked(), row == null ? null : row.getBookedAt(),
                access.organiser() && editable(session));
    }

    private static boolean editable(Session session) {
        boolean open = session.getStatus() == SessionStatus.OPEN || session.getStatus() == SessionStatus.FULL;
        return open && session.getGameDate() != null && session.getGameDate().toInstant().isAfter(Instant.now());
    }

    private static ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
