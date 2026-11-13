package com.example.desporto24.service;

import com.example.desporto24.enumeration.AttendanceStatus;
import com.example.desporto24.enumeration.CommunityMatchResult;
import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.CommunityRound;
import com.example.desporto24.model.dto.community.CommunityRoundEntryRequest;
import com.example.desporto24.model.dto.community.CommunityRoundResponse;
import com.example.desporto24.model.dto.community.CommunitySessionResultRequest;
import com.example.desporto24.model.dto.community.CreateCommunityRoundRequest;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.repository.community.CommunityRoundRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommunitySessionResultService {
    private final CommunityRepository communities;
    private final CommunityMemberRepository memberships;
    private final ProfileRepository profiles;
    private final SessionRepository sessions;
    private final SessionParticipantRepository participants;
    private final CommunityRoundRepository rounds;
    private final CommunityCompetitionService competition;

    /** Roster for admins: the generic session roster API intentionally requires
     * session participation, but community admins may not have joined this match.
     */
    @Transactional(readOnly = true)
    public List<com.example.desporto24.model.dto.session.SessionParticipantResponse> roster(
            Long communityId, Long sessionId, String actorUsername) {
        Profile actor = profiles.findUserByUsernameIgnoreCase(actorUsername)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        var membership = memberships.findByCommunityIdAndProfileId(communityId, actor.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences a esta comunidade"));
        if (membership.getRole() != CommunityRole.OWNER && membership.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Só a administração pode registar resultados");
        var community = communities.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidade inexistente"));
        if (!community.isRankingsEnabled())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A classificação não está ativa");
        if (sessions.findById(sessionId)
                .filter(s -> s.getCommunity() != null && communityId.equals(s.getCommunity().getId())
                        || s.getSeasonCompetition() != null
                        && communityId.equals(s.getSeasonCompetition().getCommunity().getId()))
                .isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão fora desta comunidade");
        return participants.findBySessionIdAndStateOrderByJoinedAtAsc(sessionId, ParticipantState.ACTIVE)
                .stream()
                .map(p -> new com.example.desporto24.model.dto.session.SessionParticipantResponse(
                        p.getId(), p.getProfile().getUsername(),
                        p.getAttendanceStatus(), p.getJoinedAt(), p.getTeamCode()))
                .toList();
    }

    /** Feature negotiation prevents older servers from silently dropping
     * optional player statistics while still accepting a W/D/L round.
     * Apply the same administrator and community checks as roster access.
     */
    public record ResultCapabilities(boolean footballGoalsAssists, boolean footballFinalScore, int version) {}

    @Transactional(readOnly = true)
    public ResultCapabilities capabilities(Long communityId, Long sessionId, String actorUsername) {
        roster(communityId, sessionId, actorUsername);
        var session = sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sessão não encontrada"));
        boolean football = "Futebol".equalsIgnoreCase(session.getSport());
        return new ResultCapabilities(football, football, 1);
    }

    @Transactional(readOnly = true)
    public List<Long> recordedSessionIds(Long communityId, String actorUsername) {
        Profile actor = profiles.findUserByUsernameIgnoreCase(actorUsername)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        var membership = memberships.findByCommunityIdAndProfileId(communityId, actor.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences a esta comunidade"));
        if (membership.getRole() != CommunityRole.OWNER && membership.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Só a administração pode consultar resultados por sessão");
        return rounds.findRecordedSessionIds(communityId);
    }

    /** A completed session has one independently editable, season-bound competition round.
     * Locking the session row serializes two simultaneous confirmations. The database
     * also enforces unique source_session_id in case another path is introduced later.
     */
    @Transactional
    public CommunityRoundResponse record(Long communityId, Long sessionId,
                                         String actorUsername, CommunitySessionResultRequest input) {
        var community = communities.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidade inexistente"));
        Profile actor = profiles.findUserByUsernameIgnoreCase(actorUsername)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        var membership = memberships.findByCommunityIdAndProfileId(communityId, actor.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences a esta comunidade"));
        if (membership.getRole() != CommunityRole.OWNER && membership.getRole() != CommunityRole.ADMIN) {
            throw new AccessDeniedException("Só a administração pode validar os resultados das sessões");
        }
        if (!community.isRankingsEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A classificação não está ativa nesta comunidade");
        }
        Session session = sessions.lockForCommunityResult(communityId, sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sessão indisponível nesta comunidade"));
        if (session.getSeasonCompetition() != null
                && (!session.getSeasonCompetition().isActive()
                    || !communityId.equals(session.getSeasonCompetition().getCommunity().getId()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A época deste jogo já não está ativa nesta comunidade");
        }
        if (session.getStatus() == SessionStatus.CANCELLED ||
                session.getGameDate() == null ||
                !session.getGameDate().toInstant().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Só é possível registar resultados de sessões realizadas e não canceladas");
        }
        if (rounds.existsByCommunityIdAndSourceSessionId(communityId, sessionId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esta sessão já tem uma jornada registada");
        }
        List<SessionParticipant> eligible = participants.findBySessionIdAndStateOrderByJoinedAtAsc(
                sessionId, ParticipantState.ACTIVE);
        // Only sessions created with the new policy enforce an automatic minimum.
        // Historical sessions have no persisted minimum and retain their prior rules.
        if (session.getMinParticipants() != null) {
            int totalConfirmed = com.example.desporto24.service.SessionCapacity.totalConfirmed(
                    session, participants.countBySessionIdAndState(sessionId, ParticipantState.ACTIVE));
            int minRequired = com.example.desporto24.service.SessionMinimumPlayers.required(session);
            if (totalConfirmed < minRequired) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Esta sessão não atingiu o mínimo de " + minRequired +
                        " jogadores para validar resultados");
            }
        }
        Map<String, SessionParticipant> eligibleByName = new HashMap<>();
        for (var participant : eligible) {
            if (participant.getAttendanceStatus() != AttendanceStatus.NO_SHOW &&
                    participant.getAttendanceStatus() != AttendanceStatus.LATE_CANCELLED) {
                eligibleByName.put(participant.getProfile().getUsername().toLowerCase(Locale.ROOT),
                        participant);
            }
        }
        if (input == null || input.outcome() == null || input.players() == null ||
                input.players().size() < 2 || input.players().size() > 100) {
            throw new IllegalArgumentException("Seleciona pelo menos dois participantes e indica o resultado");
        }
        boolean hasScoreA = input.scoreA() != null, hasScoreB = input.scoreB() != null;
        if (hasScoreA != hasScoreB)
            throw new IllegalArgumentException("Preenche o marcador das duas equipas ou deixa ambos vazios");
        if (hasScoreA) {
            if (!"Futebol".equalsIgnoreCase(session.getSport()))
                throw new IllegalArgumentException("Este marcador oficial só está disponível para Futebol");
            if (input.scoreA() < 0 || input.scoreA() > 99
                    || input.scoreB() < 0 || input.scoreB() > 99)
                throw new IllegalArgumentException("Marcador oficial deve estar entre 0 e 99");
            CommunitySessionResultRequest.Outcome derived = input.scoreA().equals(input.scoreB())
                    ? CommunitySessionResultRequest.Outcome.DRAW
                    : input.scoreA() > input.scoreB()
                        ? CommunitySessionResultRequest.Outcome.A
                        : CommunitySessionResultRequest.Outcome.B;
            if (derived != input.outcome())
                throw new IllegalArgumentException("O vencedor selecionado não corresponde ao marcador");
        }
        Set<String> unique = new HashSet<>();
        int teamA = 0;
        int teamB = 0;
        var entries = new java.util.ArrayList<CommunityRoundEntryRequest>();
        for (var player : input.players()) {
            if (player == null || player.username() == null || player.team() == null)
                throw new IllegalArgumentException("Cada participante precisa de uma equipa");
            String key = player.username().trim().toLowerCase(Locale.ROOT);
            SessionParticipant registered = eligibleByName.get(key);
            if (registered == null || !unique.add(key)) {
                throw new IllegalArgumentException(
                        "Só os inscritos ativos e não repetidos podem contar para esta jornada");
            }
            if (player.team() == CommunitySessionResultRequest.Team.A) teamA++; else teamB++;
            // Individual scoring is explicitly reported; it must never be invented
            // from a team victory. Other sports retain the existing W/D/L contract.
            if ((player.goals() != null || player.assists() != null)
                    && !"Futebol".equalsIgnoreCase(session.getSport())) {
                throw new IllegalArgumentException(
                        "Golos e assistências individuais só estão disponíveis para Futebol");
            }
            if (player.goals() != null && (player.goals() < 0 || player.goals() > 99)
                    || player.assists() != null && (player.assists() < 0 || player.assists() > 99)) {
                throw new IllegalArgumentException("Cada estatística deve estar entre 0 e 99");
            }
            CommunityMatchResult result = input.outcome() == CommunitySessionResultRequest.Outcome.DRAW
                    ? CommunityMatchResult.DRAW
                    : player.team().name().equals(input.outcome().name())
                        ? CommunityMatchResult.WIN : CommunityMatchResult.LOSS;
            // Null means unreported, preserved as the historical zero in
            // standings. Explicit numeric values are trusted only after validation.
            entries.add(new CommunityRoundEntryRequest(
                    registered.getProfile().getUsername(), player.team().name(),
                    result, player.goals() == null ? 0 : player.goals(),
                    player.assists() == null ? 0 : player.assists(), false, false));
        }
        if (teamA == 0 || teamB == 0)
            throw new IllegalArgumentException("Seleciona jogadores para as duas equipas");

        var response = competition.createRound(communityId, actorUsername,
                hasScoreA ? new CreateCommunityRoundRequest(
                        "Sessão #" + sessionId, session.getGameDate().toInstant(),
                        entries, session.getSport(), null, null, "FOOTBALL_FINAL", null,
                        null, null,
                        new com.example.desporto24.model.dto.community.CommunityTeamFinalScore(
                                input.scoreA(), input.scoreB()))
                        : new CreateCommunityRoundRequest(
                        "Sessão #" + sessionId, session.getGameDate().toInstant(),
                        entries, session.getSport()));
        CommunityRound round = rounds.findById(response.id())
                .orElseThrow(() -> new IllegalStateException("Jornada não guardada"));
        round.setSourceSessionId(sessionId);
        rounds.saveAndFlush(round);
        return response;
    }
}
