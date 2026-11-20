package com.example.desporto24.service;

import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityTournamentRepository;
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

    public record IndividualTitle(Long tournamentId, String tournamentName,
                                  Long communityId, String communityName,
                                  String sport, Instant completedAt) {}

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
