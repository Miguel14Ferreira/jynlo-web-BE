package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamContent;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.repository.team.SportsTeamContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportsTeamContentService {
    private final SportsTeamRepository teams;
    private final SportsTeamMemberRepository members;
    private final SportsTeamContentRepository content;
    private final ProfileRepository profiles;

    public record Entry(Long id, Long teamId, String username, String body, Instant createdAt,
                        Long replyToId, Instant editedAt, boolean deleted, boolean canManage) {}
    public record Submit(String body, Long replyToId) {
        public Submit(String body) { this(body, null); }
    }

    private SportsTeam team(Long id) {
        return teams.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada"));
    }

    private Profile actor(String username) {
        return profiles.findUserByUsername(username).orElseThrow(() ->
                new AccessDeniedException("Utilizador desconhecido"));
    }

    private void requireActiveMember(SportsTeam team, Profile actor) {
        if (team.getOwner().getId().equals(actor.getId())) return;
        boolean active = members.findByTeamIdAndProfileId(team.getId(), actor.getId())
                .filter(member -> member.getStatus() == SportsTeamMember.Status.ACTIVE)
                .isPresent();
        if (!active) throw new AccessDeniedException("Apenas membros confirmados podem aceder à equipa");
    }

    private void requireFeature(SportsTeam team, SportsTeamContent.Kind kind) {
        boolean enabled = kind == SportsTeamContent.Kind.CHAT
                ? team.isChatEnabled() : team.isFeedEnabled();
        if (!enabled) throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Esta funcionalidade não está ativada");
    }

    private Entry view(SportsTeamContent entry, Profile viewer, SportsTeam team) {
        boolean canManage = viewer.getId().equals(entry.getAuthor().getId())
                || viewer.getId().equals(team.getOwner().getId());
        return new Entry(entry.getId(), entry.getTeam().getId(),
                entry.getAuthor().getUsername(),
                entry.getDeletedAt() == null ? entry.getBody() : "",
                entry.getCreatedAt(), entry.getReplyToId(), entry.getEditedAt(),
                entry.getDeletedAt() != null, canManage);
    }

    @Transactional(readOnly = true)
    public List<Entry> recent(Long teamId, SportsTeamContent.Kind kind, String username) {
        var team = team(teamId);
        var viewer = actor(username);
        requireActiveMember(team, viewer);
        requireFeature(team, kind);
        // Bounded responses; newest-first at database level, chronological for UI.
        return content.findByTeamIdAndKindOrderByIdDesc(teamId, kind, PageRequest.of(0, 100))
                .stream().map(item -> view(item, viewer, team))
                .sorted(Comparator.comparing(Entry::id)).toList();
    }

    @Transactional
    public Entry post(Long teamId, SportsTeamContent.Kind kind, Submit request, String username) {
        var team = team(teamId);
        var sender = actor(username);
        requireActiveMember(team, sender);
        requireFeature(team, kind);
        String body = request == null || request.body() == null ? "" : request.body().trim();
        if (body.isEmpty() || body.length() > 2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem inválida");
        Long replyToId = request.replyToId();
        if (replyToId != null) {
            var original = content.findById(replyToId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem original indisponível"));
            if (!original.getTeam().getId().equals(teamId) || original.getKind() != kind
                    || original.getDeletedAt() != null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resposta inválida");
        }
        return view(content.saveAndFlush(SportsTeamContent.builder()
                .team(team).author(sender).kind(kind).body(body).replyToId(replyToId)
                .createdAt(Instant.now()).build()), sender, team);
    }
    private SportsTeamContent writable(Long teamId, Long contentId, SportsTeamContent.Kind kind,
                                       Profile actor, SportsTeam team) {
        requireActiveMember(team, actor);
        requireFeature(team, kind);
        var entry = content.findById(contentId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Conteúdo não encontrado"));
        if (!entry.getTeam().getId().equals(teamId) || entry.getKind() != kind)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conteúdo não encontrado");
        if (entry.getDeletedAt() != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conteúdo eliminado");
        return entry;
    }

    @Transactional
    public Entry edit(Long teamId, SportsTeamContent.Kind kind, Long contentId,
                      Submit request, String username) {
        var team = team(teamId);
        var actor = actor(username);
        var entry = writable(teamId, contentId, kind, actor, team);
        if (!entry.getAuthor().getId().equals(actor.getId()))
            throw new AccessDeniedException("Só o autor pode editar");
        String body = request == null || request.body() == null ? "" : request.body().trim();
        if (body.isEmpty() || body.length() > 2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem inválida");
        entry.setBody(body);
        entry.setEditedAt(Instant.now());
        return view(content.saveAndFlush(entry), actor, team);
    }

    @Transactional
    public void delete(Long teamId, SportsTeamContent.Kind kind, Long contentId, String username) {
        var team = team(teamId);
        var actor = actor(username);
        var entry = writable(teamId, contentId, kind, actor, team);
        if (!entry.getAuthor().getId().equals(actor.getId())
                && !team.getOwner().getId().equals(actor.getId()))
            throw new AccessDeniedException("Só o autor ou o responsável podem eliminar");
        entry.setBody("");
        entry.setDeletedAt(Instant.now());
        content.saveAndFlush(entry);
    }

}