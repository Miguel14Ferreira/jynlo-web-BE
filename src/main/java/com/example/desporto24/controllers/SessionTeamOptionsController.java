package com.example.desporto24.controllers;

import com.example.desporto24.exception.SessionNotFound;
import com.example.desporto24.exception.UserNotFoundException;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.community.CommunityRoundEntryRepository;
import com.example.desporto24.repository.community.CommunityStandingsArchiveRepository;
import com.example.desporto24.service.TeamBalancingSportPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/** Read-only list of team assignment sources for a session's organizer.
 * Reflects persisted ranking records, not merely whether community
 * rankings were enabled in settings. The suggestion/confirm endpoints
 * remain authoritative on roster completeness and ranking matches.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sessions")
public class SessionTeamOptionsController {
    private final SessionRepository sessions;
    private final ProfileRepository profiles;
    private final CommunityRoundEntryRepository rounds;
    private final CommunityStandingsArchiveRepository archives;
    /** Optional so tests can build the controller without it. */
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.desporto24.repository.session.SessionGuestRequestRepository guestRequests;

    public record TeamOptions(boolean randomAvailable,
                              boolean communityRankingAvailable,
                              boolean excelRankingAvailable) {}

    @GetMapping("/{id}/teams/options")
    @Transactional(readOnly = true)
    public TeamOptions options(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("É necessário iniciar sessão");
        }
        Session session = sessions.findById(id)
                .orElseThrow(() -> new SessionNotFound("Session not found"));
        Profile actor = profiles.findUserByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (session.getProfile() == null
                || !Objects.equals(session.getProfile().getId(), actor.getId())) {
            throw new AccessDeniedException("Só o organizador pode consultar as opções de equipas");
        }
        boolean random = TeamBalancingSportPolicy.isEligible(session, true);
        if (TeamBalancingSportPolicy.rankingCommunity(session) == null) {
            return new TeamOptions(random, false, false);
        }
        boolean ranked = TeamBalancingSportPolicy.isEligible(session, false)
                || (guestRequests != null && TeamBalancingSportPolicy.isEligibleWithGuests(session,
                (int) guestRequests.countBySessionIdAndStatus(session.getId(),
                        com.example.desporto24.model.session.SessionGuestRequest.State.APPROVED)));
        Long communityId = TeamBalancingSportPolicy.rankingCommunity(session).getId();
        if (!ranked) return new TeamOptions(random, false, false);

        boolean hasCommunityResults = rounds.existsByRoundCommunityId(communityId);
        boolean hasExcel = archives.findFirstByCommunityIdAndEntryTypeOrderByCreatedAtDescIdDesc(
                communityId, "INDIVIDUAL").isPresent();
        return new TeamOptions(random, hasCommunityResults, hasExcel);
    }
}
