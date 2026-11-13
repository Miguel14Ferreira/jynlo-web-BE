package com.example.desporto24.service;

import com.example.desporto24.repository.community.CommunityRoundRepository;
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

    public record OfficialResult(Long sessionId, Long communityId, Long roundId,
                                 Integer scoreA, Integer scoreB) {}

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
        return new OfficialResult(sessionId, round.getCommunity().getId(), round.getId(),
                round.getTeamScoreA(), round.getTeamScoreB());
    }
}
