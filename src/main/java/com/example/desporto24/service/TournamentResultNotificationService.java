package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.enumeration.TournamentMatchStatus;
import com.example.desporto24.enumeration.TournamentParticipantStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.community.CommunityTournamentTeamPlayer;
import com.example.desporto24.model.dto.community.CommunityTournamentStandingRowResponse;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.community.CommunityTournamentMatchRepository;
import com.example.desporto24.repository.community.CommunityTournamentParticipantRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamMatchRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamPlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Tournament alerts, sent only after the result is committed:
 * - both players (or every player of both teams) get the score, and in leagues
 *   their current position;
 * - when a result changes who is first in a league, every confirmed player is told.
 * The organiser who recorded the result is never notified about their own action.
 */
@Slf4j
@Service
public class TournamentResultNotificationService {

    /** Published by the result services after a score is stored (new or corrected). */
    public record ResultRecorded(Long communityId, Long tournamentId, Long matchId, boolean team,
                                 boolean correction, String actorUsername, String leaderBefore) {}

    private final NotificationsRepository notifications;
    private final CommunityTournamentMatchRepository matches;
    private final CommunityTournamentTeamMatchRepository teamMatches;
    private final CommunityTournamentTeamPlayerRepository teamPlayers;
    private final CommunityTournamentParticipantRepository participants;
    private final CommunityTournamentStandingsService standings;

    @Autowired(required = false)
    private NotificationPreferencesService preferences;

    @Autowired(required = false)
    private ApplicationEventPublisher pushEvents;

    public TournamentResultNotificationService(NotificationsRepository notifications,
                                               CommunityTournamentMatchRepository matches,
                                               CommunityTournamentTeamMatchRepository teamMatches,
                                               CommunityTournamentTeamPlayerRepository teamPlayers,
                                               CommunityTournamentParticipantRepository participants,
                                               CommunityTournamentStandingsService standings) {
        this.notifications = notifications;
        this.matches = matches;
        this.teamMatches = teamMatches;
        this.teamPlayers = teamPlayers;
        this.participants = participants;
        this.standings = standings;
    }

    /** League table for an individual tournament, or empty when it has none (knockout, races…). */
    public List<CommunityTournamentStandingRowResponse> table(Long communityId, Long tournamentId, String asUsername) {
        try {
            return standings.standings(communityId, tournamentId, asUsername).standings();
        } catch (RuntimeException noTable) {
            return List.of();
        }
    }

    public String leader(Long communityId, Long tournamentId, String asUsername) {
        List<CommunityTournamentStandingRowResponse> rows = table(communityId, tournamentId, asUsername);
        // Nobody leads before a single game is played.
        if (rows.isEmpty() || rows.getFirst().played() == 0) return null;
        return rows.getFirst().username();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onResultRecorded(ResultRecorded event) {
        try {
            if (event.team()) notifyTeamResult(event);
            else notifyIndividualResult(event);
        } catch (RuntimeException failed) {
            // Alerts are best effort: the result itself is already committed.
            log.warn("Tournament result notifications failed for match {}", event.matchId(), failed);
        }
    }

    void notifyIndividualResult(ResultRecorded event) {
        var match = matches.findById(event.matchId()).orElse(null);
        if (match == null || match.getStatus() != TournamentMatchStatus.COMPLETED) return;
        CommunityTournament tournament = match.getTournament();
        Profile home = match.getHomeParticipant().getProfile();
        Profile away = match.getAwayParticipant().getProfile();
        String score = name(home) + " " + match.getHomeScore() + "–" + match.getAwayScore() + " " + name(away);

        List<CommunityTournamentStandingRowResponse> rows = table(event.communityId(), event.tournamentId(), event.actorUsername());
        for (Profile player : List.of(home, away)) {
            if (same(player.getUsername(), event.actorUsername())) continue;
            String position = rows.stream().filter(row -> same(row.username(), player.getUsername())).findFirst()
                    .map(row -> " Estás em " + row.position() + ".º lugar com " + row.points()
                            + (row.points() == 1 ? " ponto." : " pontos."))
                    .orElse("");
            send(player, tournament, NotificationType.TOURNAMENT_RESULT,
                    (event.correction() ? "Resultado corrigido: " : "Resultado: ") + score,
                    torneio(tournament) + ": " + score + "." + position);
        }

        String leaderAfter = rows.isEmpty() || rows.getFirst().played() == 0 ? null : rows.getFirst().username();
        if (leaderAfter != null && !same(leaderAfter, event.leaderBefore())) {
            String leaderName = rows.getFirst().displayName() != null && !rows.getFirst().displayName().isBlank()
                    ? clean(rows.getFirst().displayName()) : clean(leaderAfter);
            var confirmed = participants.findByTournamentIdAndStatus(event.tournamentId(),
                    TournamentParticipantStatus.CONFIRMED, PageRequest.of(0, 64, Sort.by("id")));
            for (var participant : confirmed.getContent()) {
                Profile player = participant.getProfile();
                if (player == null || same(player.getUsername(), event.actorUsername())) continue;
                boolean isLeader = same(player.getUsername(), leaderAfter);
                send(player, tournament, NotificationType.TOURNAMENT_LEADER,
                        isLeader ? "Estás em 1.º lugar!" : "Novo líder: " + leaderName,
                        isLeader ? "Passaste para a liderança de " + torneio(tournament) + "."
                                 : leaderName + " passou para 1.º lugar em " + torneio(tournament) + ".");
            }
        }
    }

    void notifyTeamResult(ResultRecorded event) {
        var match = teamMatches.findById(event.matchId()).orElse(null);
        if (match == null || match.getStatus() != TournamentMatchStatus.COMPLETED) return;
        CommunityTournament tournament = match.getTournament();
        String score = clean(match.getHomeTeam().getName()) + " " + match.getHomeScore() + "–"
                + match.getAwayScore() + " " + clean(match.getAwayTeam().getName());
        Long homeId = match.getHomeTeam().getId(), awayId = match.getAwayTeam().getId();
        Map<String, Profile> recipients = new LinkedHashMap<>();
        for (CommunityTournamentTeamPlayer player : teamPlayers.findByTournamentIdOrderByTeamIdAscIdAsc(event.tournamentId())) {
            if (player.getTeam() == null || player.getProfile() == null || player.getProfile().getUsername() == null) continue;
            Long teamId = player.getTeam().getId();
            if (!Objects.equals(teamId, homeId) && !Objects.equals(teamId, awayId)) continue;
            recipients.putIfAbsent(player.getProfile().getUsername().toLowerCase(Locale.ROOT), player.getProfile());
        }
        for (Profile player : recipients.values()) {
            if (same(player.getUsername(), event.actorUsername())) continue;
            send(player, tournament, NotificationType.TOURNAMENT_RESULT,
                    (event.correction() ? "Resultado corrigido: " : "Resultado: ") + score,
                    torneio(tournament) + ": " + score + ".");
        }
    }

    private void send(Profile recipient, CommunityTournament tournament, NotificationType type,
                      String subject, String message) {
        if (recipient == null || recipient.getUsername() == null || recipient.getUsername().isBlank()) return;
        Notifications notification = new Notifications();
        notification.setUsername(recipient.getUsername());
        notification.setSubject(subject.length() > 120 ? subject.substring(0, 120) : subject);
        notification.setMessage(message);
        notification.setGreeting("");
        notification.setSignature("");
        notification.setCreatedAt(Instant.now().toString());
        notification.setFriendRequest(false);
        notification.setUpdateProfile(false);
        notification.setSessionInvite(false);
        notification.setNormalMessage(false);
        notification.setToken(null);
        notification.setCommunityId(tournament.getCommunity() == null ? null : tournament.getCommunity().getId());
        notification.setCompetitionTournamentId(tournament.getId());
        notification.setType(type);
        notification.setRead(false);
        if (preferences != null && !preferences.allowsInApp(notification.getUsername(), notification)) return;
        Notifications saved = notifications.save(notification);
        if (pushEvents != null && saved != null && saved.getId() != null) {
            pushEvents.publishEvent(new MobilePushDeliveryService.StoredNotification(
                    saved.getUsername(), saved.getId(), NotificationPreferencesService.category(saved),
                    null, saved.getCommunityId(), null, true));
        }
    }

    private static String torneio(CommunityTournament tournament) {
        return Optional.ofNullable(tournament.getName()).map(TournamentResultNotificationService::clean)
                .filter(name -> !name.isBlank()).orElse("O torneio");
    }

    private static String name(Profile profile) {
        return profile == null ? "?" : clean(profile.getUsername());
    }

    private static String clean(String value) {
        if (value == null) return "";
        String cleaned = value.strip().replaceAll("[\\p{Cntrl}]+", " ");
        return cleaned.length() > 60 ? cleaned.substring(0, 60).strip() : cleaned;
    }

    private static boolean same(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }
}
