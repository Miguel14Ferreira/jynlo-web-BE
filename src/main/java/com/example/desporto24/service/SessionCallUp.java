package com.example.desporto24.service;

import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Session;
import com.example.desporto24.repository.SessionRepository;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * "Convocatória": a chat message that points to a session so the group can
 * answer "Vou" from the chat. Stored as ordinary chat text with a structured
 * envelope (like GIFs), so no schema change is needed:
 *
 *   [JYNLO:SESSION:1]123
 *
 * Only the session's organiser may post it, and only while the session is
 * still upcoming and open or full (waitlist). Joining from the card goes
 * through the normal session join endpoint and its own authorisation.
 */
@Component
@RequiredArgsConstructor
public class SessionCallUp {

    public static final String PREFIX = "[JYNLO:SESSION:1]";
    private static final Pattern FORMAT = Pattern.compile("^\\[JYNLO:SESSION:1\\](\\d{1,18})$");

    private final SessionRepository sessions;

    public static boolean isCallUp(String text) {
        return text != null && text.trim().startsWith(PREFIX);
    }

    /** Validates a call-up envelope; ordinary messages pass through untouched. */
    public void validate(String text, String actor) {
        if (!isCallUp(text)) return;
        Matcher matcher = FORMAT.matcher(text.trim());
        if (!matcher.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid session call-up");
        }
        Long id = Long.valueOf(matcher.group(1));
        Session session = sessions.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session not found"));
        String organiser = session.getProfile() == null ? null : session.getProfile().getUsername();
        if (organiser == null || actor == null
                || !organiser.toLowerCase(Locale.ROOT).equals(actor.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the organiser can post a call-up");
        }
        boolean upcoming = session.getGameDate() != null && session.getGameDate().after(new Date());
        boolean open = session.getStatus() == SessionStatus.OPEN || session.getStatus() == SessionStatus.FULL;
        if (!upcoming || !open) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session is no longer open");
        }
    }
}
