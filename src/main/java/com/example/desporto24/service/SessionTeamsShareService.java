package com.example.desporto24.service;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionGuestRequest;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionGuestRequestRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * "Partilhar equipas no WhatsApp": a page with the game and its two confirmed teams, opened by a link
 * that carries a random secret. Shows only what the game's player list already shows: names and teams.
 */
@Service
@RequiredArgsConstructor
public class SessionTeamsShareService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SessionRepository sessions;
    private final SessionParticipantRepository participants;
    private final SessionGuestRequestRepository guests;

    public record ShareLink(Long sessionId, String token, String path) {}
    public record Player(String name, boolean hasAccount) {}
    public record Page(Long sessionId, String sport, Instant gameDate, String venueName, String city,
                       String communityName, List<Player> teamA, List<Player> teamB) {}

    /** Organiser only. The same link is reused so a message already sent keeps working. */
    @Transactional
    public ShareLink link(Long sessionId, String username) {
        Session session = sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo não encontrado"));
        if (session.getProfile() == null || username == null
                || !username.equalsIgnoreCase(session.getProfile().getUsername())) {
            throw new AccessDeniedException("Só o organizador pode partilhar as equipas");
        }
        Page page = build(session);
        if (page.teamA().isEmpty() || page.teamB().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Confirma primeiro as duas equipas");
        }
        if (session.getTeamsShareToken() == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            session.setTeamsShareToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
            sessions.save(session);
        }
        return new ShareLink(session.getId(), session.getTeamsShareToken(),
                "/equipas/" + session.getId() + "?t=" + session.getTeamsShareToken());
    }

    @Transactional(readOnly = true)
    public Page page(Long sessionId, String token) {
        Session session = sessions.findById(sessionId).orElse(null);
        if (session == null || token == null || session.getTeamsShareToken() == null
                || !MessageDigest.isEqual(session.getTeamsShareToken().getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipas indisponíveis");
        }
        return build(session);
    }

    private Page build(Session session) {
        List<Player> a = new ArrayList<>(), b = new ArrayList<>();
        for (var p : participants.findBySessionIdAndStateOrderByJoinedAtAsc(session.getId(), ParticipantState.ACTIVE)) {
            if (p.getProfile() == null || p.getProfile().getUsername() == null) continue;
            Player player = new Player("@" + p.getProfile().getUsername(), true);
            if ("A".equals(p.getTeamCode())) a.add(player); else if ("B".equals(p.getTeamCode())) b.add(player);
        }
        for (var g : guests.findBySessionIdAndStatusOrderByCreatedAtAsc(session.getId(), SessionGuestRequest.State.APPROVED)) {
            Player player = new Player(g.getDisplayName(), false);
            if ("A".equals(g.getTeamCode())) a.add(player); else if ("B".equals(g.getTeamCode())) b.add(player);
        }
        var community = TeamBalancingSportPolicy.rankingCommunity(session);
        return new Page(session.getId(), session.getSport(),
                session.getGameDate() == null ? null : session.getGameDate().toInstant(),
                session.getVenue() == null ? null : session.getVenue().getName(),
                session.getVenue() != null && session.getVenue().getCity() != null ? session.getVenue().getCity() : session.getCity(),
                community == null ? null : community.getName(), List.copyOf(a), List.copyOf(b));
    }
}
