package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentFormat;
import com.example.desporto24.enumeration.TournamentMatchStatus;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityTournamentMatchRepository;
import com.example.desporto24.repository.community.CommunityTournamentRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/**
 * Shareable tournament page ("vê a classificação do nosso torneio"), opt-in per
 * tournament by an Owner/Admin. Shows the table and the games only: display
 * names, never emails, usernames of non-players, member lists or private data.
 * A tournament without the page enabled answers 404, exactly like a missing one.
 */
@Service
@RequiredArgsConstructor
public class TournamentPublicPageService {
    static final int MAX_GAMES = 300;

    public record Setting(boolean enabled, String path) {}
    public record Row(int position, String name, int played, int won, int drawn, int lost,
                      int goalsFor, int goalsAgainst, int goalDifference, long points) {}
    public record Game(int round, Integer group, String home, String away,
                       TournamentMatchStatus status, Integer homeScore, Integer awayScore) {}
    public record Page(Long id, String name, String sport, String communityName, TournamentStatus status,
                       TournamentFormat format, TournamentEntryType entryType, Instant startedAt,
                       Instant completedAt, String champion, List<Row> table, List<Game> games) {}

    private final ProfileRepository profiles;
    private final CommunityMemberRepository members;
    private final CommunityTournamentRepository tournaments;
    private final CommunityTournamentMatchRepository matches;
    private final CommunityTournamentTeamMatchRepository teamMatches;
    private final CommunityTournamentStandingsService standings;
    private final CommunityTournamentTeamStandingsService teamStandings;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private EventRankingService eventRanking;

    @Transactional(readOnly = true)
    public Setting setting(Long communityId, Long tournamentId, String username) {
        CommunityTournament tournament = manageable(communityId, tournamentId, username, false);
        return new Setting(tournament.isPublicPageEnabled(), path(tournament));
    }

    @Transactional
    public Setting update(Long communityId, Long tournamentId, String username, boolean enabled) {
        CommunityTournament tournament = manageable(communityId, tournamentId, username, true);
        tournament.setPublicPageEnabled(enabled);
        tournaments.save(tournament);
        return new Setting(enabled, path(tournament));
    }

    @Transactional(readOnly = true)
    public Page page(Long tournamentId) {
        CommunityTournament tournament = tournaments.findById(tournamentId)
                .filter(CommunityTournament::isPublicPageEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneio não encontrado"));
        Long communityId = tournament.getCommunity().getId();
        boolean team = tournament.getEntryType() == TournamentEntryType.TEAM;
        List<Row> table = List.of();
        try {
            if (tournament.getScoringMode() == com.example.desporto24.enumeration.TournamentScoringMode.EVENT_RANKING
                    && eventRanking != null) {
                // Events, not games: show the points table; the events list stays in the app.
                table = eventRanking.publicTable(tournament).stream()
                        .map(r -> new Row(r.position(), clean(r.name()), r.events(), r.wins(), 0, 0, 0, 0, 0, r.points()))
                        .toList();
            } else if (team) {
                table = teamStandings.publicStandings(communityId, tournamentId).standings().stream()
                        .map(r -> new Row(r.position(), clean(r.teamName()), r.played(), r.won(), r.drawn(), r.lost(),
                                r.goalsFor(), r.goalsAgainst(), r.goalDifference(), r.points()))
                        .toList();
            } else {
                table = standings.publicStandings(communityId, tournamentId).standings().stream()
                        .map(r -> new Row(r.position(), publicName(r.profileId(), r.username()),
                                r.played(), r.won(), r.drawn(), r.lost(),
                                r.goalsFor(), r.goalsAgainst(), r.goalDifference(), r.points()))
                        .toList();
            }
        } catch (RuntimeException noLeagueTable) {
            // Knockouts, races and timed events have no points table here: games only.
        }
        var page = PageRequest.of(0, MAX_GAMES, Sort.by("roundNumber", "id"));
        List<Game> games = team
                ? teamMatches.findByTournamentId(tournamentId, page).getContent().stream()
                    .map(m -> new Game(m.getRoundNumber(), m.getGroupNumber(),
                            clean(m.getHomeTeam() == null ? null : m.getHomeTeam().getName()),
                            clean(m.getAwayTeam() == null ? null : m.getAwayTeam().getName()),
                            m.getStatus(), m.getHomeScore(), m.getAwayScore()))
                    .toList()
                : matches.findByTournamentId(tournamentId, page).getContent().stream()
                    .map(m -> new Game(m.getRoundNumber(), null,
                            display(m.getHomeParticipant() == null ? null : m.getHomeParticipant().getProfile()),
                            display(m.getAwayParticipant() == null ? null : m.getAwayParticipant().getProfile()),
                            m.getStatus(), m.getHomeScore(), m.getAwayScore()))
                    .toList();
        String champion = tournament.getStatus() == TournamentStatus.COMPLETED && !table.isEmpty()
                && (table.size() < 2 || table.get(0).points() != table.get(1).points())
                ? table.get(0).name() : null;
        return new Page(tournament.getId(), clean(tournament.getName()), clean(tournament.getSport()),
                clean(tournament.getCommunity().getName()), tournament.getStatus(), tournament.getFormat(),
                tournament.getEntryType(), tournament.getStartedAt(), tournament.getCompletedAt(),
                champion, table, games);
    }

    private CommunityTournament manageable(Long communityId, Long tournamentId, String username, boolean write) {
        Profile actor = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta não encontrada"));
        var membership = members.findByCommunityIdAndProfileId(communityId, actor.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Não pertences a esta comunidade"));
        if (write && membership.getRole() != CommunityRole.OWNER && membership.getRole() != CommunityRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só Owner/Admin pode partilhar a página do torneio");
        }
        CommunityTournament tournament = tournaments.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneio não encontrado"));
        if (!tournament.getCommunity().getId().equals(communityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneio não encontrado");
        }
        return tournament;
    }

    private static String path(CommunityTournament tournament) {
        return "/torneio/" + tournament.getId();
    }

    private String publicName(Long profileId, String username) {
        Profile profile = profileId == null ? null : profiles.findById(profileId).orElse(null);
        return profile != null ? display(profile) : clean(username);
    }

    private static String display(Profile profile) {
        if (profile == null) return "?";
        // Public pages show the nickname or the public handle, never the full real name.
        String nickname = profile.getNickname();
        return clean(nickname != null && !nickname.isBlank() ? nickname : profile.getUsername());
    }

    private static String clean(String value) {
        if (value == null) return null;
        String cleaned = value.strip().replaceAll("[\\p{Cntrl}]+", " ");
        return cleaned.length() > 80 ? cleaned.substring(0, 80).strip() : cleaned;
    }
}
