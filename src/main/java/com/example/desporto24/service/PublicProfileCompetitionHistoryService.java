package com.example.desporto24.service;

import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityTournamentRepository;
import com.example.desporto24.repository.community.CommunityTournamentParticipantRepository;
import com.example.desporto24.repository.community.CommunityTournamentMatchRepository;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.enumeration.TournamentFormat;
import com.example.desporto24.enumeration.TournamentScoringMode;
import com.example.desporto24.service.interfaces.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;

/**
 * Public competitive honours. No live standings, roster links, disputed
 * champions, team titles, private community results or imported guesses.
 */
@Service
@RequiredArgsConstructor
public class PublicProfileCompetitionHistoryService {
    private final ProfileRepository profiles;
    private final ProfileService profileService;
    private final CommunityTournamentRepository tournaments;
    private final CommunityTournamentParticipantRepository participants;
    private final CommunityTournamentMatchRepository matches;

    public record IndividualTitle(Long tournamentId, String tournamentName,
                                  Long communityId, String communityName,
                                  String sport, Instant completedAt) {}

    /** Confirmed participation, not a claimed podium or reconstructed ranking. */
    public record CompletedParticipation(Long tournamentId, String tournamentName,
            Long communityId, String communityName, String sport,
            Instant completedAt, boolean individualChampion,
            Integer wins, Integer draws, Integer losses) {}

    @Transactional(readOnly = true)
    public List<CompletedParticipation> completedParticipations(String viewer, String username) {
        profileService.findPublicProfile(viewer, username);
        var target = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (target.isPrivateProfile()) return List.of();
        return participants.findPublicCompletedIndividualParticipations(
                target.getId(), PageRequest.of(0, 20)).stream()
                .map(p -> {
                    var t = p.getTournament();
                    var record = finalizedWinDrawLoss(t, target.getId());
                    return new CompletedParticipation(t.getId(), t.getName(),
                            t.getCommunity().getId(), t.getCommunity().getName(),
                            t.getSport(), t.getCompletedAt(),
                            !t.isChampionTied() && target.getId().equals(t.getChampionProfileId()),
                            record == null ? null : record[0],
                            record == null ? null : record[1],
                            record == null ? null : record[2]);
                })
                .toList();
    }

    private Integer[] finalizedWinDrawLoss(CommunityTournament tournament, Long profileId) {
        // Individual league scorecards are comparable; time, distance, racing,
        // golf and specialized modes cannot be converted into W/D/L.
        if (tournament.getFormat() != TournamentFormat.LEAGUE
                || (tournament.getScoringMode() != null
                    && tournament.getScoringMode() != TournamentScoringMode.CHESS_HALF_POINTS)) {
            return null;
        }
        var games = matches.findCompletedMatchesForParticipant(
                tournament.getId(), profileId, PageRequest.of(0, 33));
        if (games.isEmpty() || games.size() > 32) return null;
        int won = 0, drawn = 0, lost = 0;
        for (var game : games) {
            if (game.getHomeScore() == null || game.getAwayScore() == null
                    || game.getHomeScore() < 0 || game.getAwayScore() < 0) return null;
            boolean home = game.getHomeParticipant().getProfile().getId().equals(profileId);
            boolean away = game.getAwayParticipant().getProfile().getId().equals(profileId);
            if (home == away) return null;
            int scoreFor = home ? game.getHomeScore() : game.getAwayScore();
            int scoreAgainst = home ? game.getAwayScore() : game.getHomeScore();
            if (scoreFor > scoreAgainst) won++;
            else if (scoreFor == scoreAgainst) drawn++;
            else lost++;
        }
        return new Integer[] {won, drawn, lost};
    }

    @Transactional(readOnly = true)
    public List<IndividualTitle> individualTitles(String viewer, String username) {
        // Reuse the public profile block and visibility interaction policy.
        profileService.findPublicProfile(viewer, username);
        var target = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // Do not infer visibility from a follower relationship. A private
        // profile never exposes its competitive associations via this API.
        if (target.isPrivateProfile()) return List.of();

        return tournaments.findPublicIndividualTitlesForProfile(
                target.getId(), PageRequest.of(0, 20)).stream()
                .map(t -> new IndividualTitle(t.getId(), t.getName(),
                        t.getCommunity().getId(), t.getCommunity().getName(),
                        t.getSport(), t.getCompletedAt()))
                .toList();
    }
}
