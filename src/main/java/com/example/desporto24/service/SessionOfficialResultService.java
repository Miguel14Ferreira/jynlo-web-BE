package com.example.desporto24.service;

import com.example.desporto24.repository.community.CommunityRoundRepository;
import com.example.desporto24.repository.community.CommunityRoundEntryRepository;
import java.util.List;
import com.example.desporto24.service.interfaces.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Read-only final score, without exposing a private roster or unreported scorers. */
@Service
@RequiredArgsConstructor
public class SessionOfficialResultService {
    private final SessionService sessions;
    private final CommunityRoundRepository rounds;
    private final CommunityRoundEntryRepository entries;

    public record PlayerContribution(String username, String team, int goals, int assists) {}
    public record OfficialResult(Long sessionId, Long communityId, Long roundId,
                                 Integer scoreA, Integer scoreB,
                                 List<PlayerContribution> contributions) {}

    @Transactional(readOnly = true)
    public OfficialResult result(Long sessionId, String username) {
        if (username == null || username.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        // Reuse the same identity and private-session authorization as the
        // session details API. Merely knowing the ID never grants score access.
        var detail = sessions.getById(sessionId, username);
        boolean creator = detail.creatorUsername() != null
                && detail.creatorUsername().equalsIgnoreCase(username);
        if (!creator && !detail.joined())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas o organizador e os participantes confirmados podem ver o resultado");
        var round = rounds.findBySourceSessionId(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Esta sessão ainda não tem resultado de classificação"));
        // Only explicitly stored nonzero contributions are displayed. Legacy
        // 0/0 may mean "unknown", so never label it an observed zero.
        List<PlayerContribution> contributions = entries.findByRoundIdOrderByIdAsc(round.getId())
                .stream()
                .filter(e -> (e.getGoals() > 0 || e.getAssists() > 0)
                        && e.getProfile() != null
                        && e.getProfile().getUsername() != null)
                .map(e -> new PlayerContribution(e.getProfile().getUsername(),
                        e.getTeam(), e.getGoals(), e.getAssists()))
                .toList();
        return new OfficialResult(sessionId, round.getCommunity().getId(), round.getId(),
                round.getTeamScoreA(), round.getTeamScoreB(), contributions);
    }
}
