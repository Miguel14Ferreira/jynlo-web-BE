package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SportsTeamReactionService {
    private static final Set<String> ALLOWED = Set.of("👍", "❤️", "😂", "🎉", "🔥", "👏");
    private final SportsTeamRepository teams;
    private final SportsTeamMemberRepository members;
    private final SportsTeamContentRepository content;
    private final SportsTeamContentReactionRepository reactions;
    private final ProfileRepository profiles;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private org.springframework.context.ApplicationEventPublisher realtimeEvents;

    private void invalidate(Long teamId, SportsTeamContent.Kind kind) {
        if (realtimeEvents != null)
            realtimeEvents.publishEvent(new com.example.desporto24.websocket.TeamContentChangedEvent(
                    teamId, kind.name().toLowerCase(java.util.Locale.ROOT)));
    }

    public record ReactionState(Map<String, Long> counts, String mine) {}
    public record ReactionRequest(String emoji) {}

    private Profile actor(String username) {
        return profiles.findUserByUsername(username).orElseThrow(() ->
                new AccessDeniedException("Utilizador desconhecido"));
    }

    private SportsTeam requireTeam(Long teamId, SportsTeamContent.Kind kind, Profile actor) {
        if (Boolean.FALSE.equals(actor.getEnabled()) || actor.getModerationSuspendedAt() != null)
            throw new AccessDeniedException("Conta indisponível para conteúdos privados");
        var team = teams.findById(teamId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada"));
        boolean active = team.getOwner().getId().equals(actor.getId())
                || members.findByTeamIdAndProfileId(teamId, actor.getId())
                    .filter(member -> member.getStatus() == SportsTeamMember.Status.ACTIVE)
                    .isPresent();
        if (!active) throw new AccessDeniedException("Apenas membros confirmados");
        if (kind == SportsTeamContent.Kind.CHAT ? !team.isChatEnabled() : !team.isFeedEnabled())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Funcionalidade desativada");
        return team;
    }

    private SportsTeamContent requireContent(Long teamId, SportsTeamContent.Kind kind, Long id) {
        var entry = content.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensagem não encontrada"));
        if (!entry.getTeam().getId().equals(teamId) || entry.getKind() != kind
                || entry.getDeletedAt() != null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensagem não encontrada");
        return entry;
    }

    @Transactional(readOnly = true)
    public Map<Long, ReactionState> summaries(Long teamId, SportsTeamContent.Kind kind,
                                               List<Long> ids, String username) {
        var viewer = actor(username);
        requireTeam(teamId, kind, viewer);
        if (ids == null || ids.isEmpty() || ids.size() > 100 || ids.stream().anyMatch(
                id -> id == null || id <= 0))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identificadores inválidos");
        var requested = new HashSet<>(ids);
        var valid = content.findAllById(requested).stream()
                .filter(c -> c.getTeam().getId().equals(teamId)
                        && c.getKind() == kind && c.getDeletedAt() == null)
                .map(SportsTeamContent::getId).toList();
        if (valid.isEmpty()) return Map.of();
        Map<Long, Map<String, Long>> counts = new HashMap<>();
        Map<Long, String> myChoice = new HashMap<>();
        for (var reaction : reactions.findByContent_IdIn(valid)) {
            var id = reaction.getContent().getId();
            counts.computeIfAbsent(id, ignored -> new HashMap<>())
                    .merge(reaction.getEmoji(), 1L, Long::sum);
            if (reaction.getProfile().getId().equals(viewer.getId()))
                myChoice.put(id, reaction.getEmoji());
        }
        Map<Long, ReactionState> results = new HashMap<>();
        for (Long id : valid) results.put(id,
                new ReactionState(counts.getOrDefault(id, Map.of()), myChoice.get(id)));
        return results;
    }

    @Transactional
    public void set(Long teamId, SportsTeamContent.Kind kind, Long contentId,
                    String emoji, String username) {
        var viewer = actor(username);
        requireTeam(teamId, kind, viewer);
        if (emoji == null || !ALLOWED.contains(emoji))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reação inválida");
        var item = requireContent(teamId, kind, contentId);
        var existing = reactions.findByContent_IdAndProfile_Id(contentId, viewer.getId());
        if (existing.isPresent()) {
            var reaction = existing.get();
            if (!emoji.equals(reaction.getEmoji())) {
                reaction.setEmoji(emoji);
                reactions.saveAndFlush(reaction);
                invalidate(teamId, kind);
            }
        } else {
            reactions.saveAndFlush(SportsTeamContentReaction.builder()
                    .content(item).profile(viewer).emoji(emoji)
                    .createdAt(Instant.now()).build());
            invalidate(teamId, kind);
        }
    }

    @Transactional
    public void remove(Long teamId, SportsTeamContent.Kind kind, Long contentId, String username) {
        var viewer = actor(username);
        requireTeam(teamId, kind, viewer);
        requireContent(teamId, kind, contentId);
        reactions.findByContent_IdAndProfile_Id(contentId, viewer.getId())
                .ifPresent(reaction -> {
                    reactions.delete(reaction);
                    invalidate(teamId, kind);
                });
    }
}
