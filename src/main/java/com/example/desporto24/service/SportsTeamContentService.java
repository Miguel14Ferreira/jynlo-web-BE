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

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.desporto24.storage.PrivateChatFileStorage privateFiles;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private org.springframework.context.ApplicationEventPublisher realtimeEvents;

    private void invalidate(Long teamId, SportsTeamContent.Kind kind) {
        if (realtimeEvents != null)
            realtimeEvents.publishEvent(new com.example.desporto24.websocket.TeamContentChangedEvent(
                    teamId, kind.name().toLowerCase(java.util.Locale.ROOT)));
    }

    /** Team chat messages alert the other members after commit (TeamChatNotificationListener). */
    private void announceChat(SportsTeamContent saved, Profile sender) {
        if (realtimeEvents == null || saved == null || saved.getKind() != SportsTeamContent.Kind.CHAT) return;
        realtimeEvents.publishEvent(new TeamChatNotificationListener.TeamChatMessagePosted(
                saved.getTeam().getId(), saved.getId(), sender.getUsername(), saved.getBody()));
    }

    /** Active member or owner of a team whose chat is on; used by the inbox and read cursors. */
    @Transactional(readOnly = true)
    public void requireChatAccess(Long teamId, Profile actor) {
        var team = team(teamId);
        requireActiveMember(team, actor);
        requireFeature(team, SportsTeamContent.Kind.CHAT);
    }

    public record Entry(Long id, Long teamId, String username, String body, Instant createdAt,
                        Long replyToId, Instant editedAt, boolean deleted, boolean canManage, boolean canEdit,
                        List<PrivateChatAttachmentBatch.PublicMetadata> attachments,
                        String profilePicUrl) {}
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
        // Membership alone must not keep a disabled or suspended account
        // inside private chat/feed sessions after moderation has taken effect.
        if (Boolean.FALSE.equals(actor.getEnabled()) || actor.getModerationSuspendedAt() != null)
            throw new AccessDeniedException("Conta indisponível para conteúdos privados");
        if (team.getOwner().getId().equals(actor.getId())) return;
        boolean active = members.findByTeamIdAndProfileId(team.getId(), actor.getId())
                .filter(member -> member.getStatus() == SportsTeamMember.Status.ACTIVE)
                .isPresent();
        if (!active) throw new AccessDeniedException("Apenas membros confirmados podem aceder à equipa");
    }

    /** The owner may keep the team feed for themselves; the chat stays open to all members. */
    private void requireFeedPoster(SportsTeam team, SportsTeamContent.Kind kind, Profile actor) {
        if (kind != SportsTeamContent.Kind.FEED || team.isMembersCanPost()) return;
        if (!team.getOwner().getId().equals(actor.getId()))
            throw new AccessDeniedException("Só o responsável pode publicar no feed desta equipa");
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
                entry.getDeletedAt() != null, canManage,
                viewer.getId().equals(entry.getAuthor().getId()),
                entry.getDeletedAt() == null ? PrivateChatAttachmentBatch.metadata(
                        entry.getAttachmentName(), entry.getAttachmentMimeType(),
                        entry.getAttachmentSize(), entry.getExtraAttachments()) : List.of(),
                entry.getAuthor().getProfilePicUrl());
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

    /** Older private feed items; authorization is rechecked for every page. */
    @Transactional(readOnly = true)
    public List<Entry> olderFeed(Long teamId, Long beforeId, int limit, String username) {
        var team = team(teamId);
        var viewer = actor(username);
        requireActiveMember(team, viewer);
        requireFeature(team, SportsTeamContent.Kind.FEED);
        if (beforeId == null || beforeId <= 0 || limit < 1 || limit > 50)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Página inválida");
        return content.findByTeamIdAndKindAndIdLessThanOrderByIdDesc(
                teamId, SportsTeamContent.Kind.FEED, beforeId, PageRequest.of(0, limit))
                .stream().map(item -> view(item, viewer, team))
                .sorted(Comparator.comparing(Entry::id)).toList();
    }

    /** Reconcile already-loaded older posts, including tombstones for deleted ones. */
    @Transactional(readOnly = true)
    public List<Entry> feedEntries(Long teamId, List<Long> ids, String username) {
        var team = team(teamId);
        var viewer = actor(username);
        requireActiveMember(team, viewer);
        requireFeature(team, SportsTeamContent.Kind.FEED);
        if (ids == null || ids.isEmpty() || ids.size() > 100
                || ids.stream().anyMatch(id -> id == null || id <= 0))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identificadores inválidos");
        return content.findAllById(new java.util.HashSet<>(ids)).stream()
                .filter(item -> item.getTeam().getId().equals(teamId)
                        && item.getKind() == SportsTeamContent.Kind.FEED)
                .map(item -> view(item, viewer, team))
                .sorted(Comparator.comparing(Entry::id)).toList();
    }

    /** Search uses a literal bounded query and never returns deleted content. */
    @Transactional(readOnly = true)
    public List<Entry> search(Long teamId, SportsTeamContent.Kind kind, String term, String username) {
        var team = team(teamId);
        var viewer = actor(username);
        requireActiveMember(team, viewer);
        requireFeature(team, kind);
        String normalized = term == null ? "" : term.strip();
        if (normalized.length() < 2 || normalized.length() > 80
                || normalized.codePoints().anyMatch(Character::isISOControl))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pesquisa deve ter entre 2 e 80 caracteres");
        return content.searchVisible(teamId, kind, normalized, PageRequest.of(0, 100))
                .stream().map(item -> view(item, viewer, team))
                .sorted(Comparator.comparing(Entry::id)).toList();
    }

    @Transactional
    public Entry post(Long teamId, SportsTeamContent.Kind kind, Submit request, String username) {
        var team = team(teamId);
        var sender = actor(username);
        requireActiveMember(team, sender);
        requireFeature(team, kind);
        requireFeedPoster(team, kind, sender);
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
        var saved = content.saveAndFlush(SportsTeamContent.builder()
                .team(team).author(sender).kind(kind).body(body).replyToId(replyToId)
                .createdAt(Instant.now()).build());
        invalidate(teamId, kind);
        announceChat(saved, sender);
        return view(saved, sender, team);
    }
    /** Probe is member-only, matching the other private chat surfaces. */
    @Transactional(readOnly = true)
    public boolean attachmentsAvailable(Long teamId, SportsTeamContent.Kind kind, String username) {
        var team = team(teamId);
        requireActiveMember(team, actor(username));
        requireFeature(team, kind);
        return privateFiles != null && privateFiles.isConfigured();
    }

    @Transactional
    public Entry upload(Long teamId, SportsTeamContent.Kind kind,
            List<org.springframework.web.multipart.MultipartFile> uploads,
            String caption, Long replyToId, String username) {
        var team = team(teamId);
        var sender = actor(username);
        requireActiveMember(team, sender);
        requireFeature(team, kind);
        requireFeedPoster(team, kind, sender);
        if (privateFiles == null || !privateFiles.isConfigured())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Armazenamento privado indisponível");
        String body = caption == null ? "" : caption.trim();
        if (body.length() > 2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Legenda demasiado longa");
        if (replyToId != null) {
            var original = content.findById(replyToId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resposta inválida"));
            if (!original.getTeam().getId().equals(teamId)
                    || original.getKind() != kind || original.getDeletedAt() != null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resposta inválida");
        }
        var batch = PrivateChatAttachmentBatch.store(privateFiles, uploads);
        var first = batch.first();
        try {
            var saved = content.saveAndFlush(SportsTeamContent.builder()
                    .team(team).author(sender).kind(kind)
                    .body(body.isBlank() ? "📎 " + first.name() : body)
                    .replyToId(replyToId).createdAt(Instant.now())
                    .attachmentKey(first.key()).attachmentName(first.name())
                    .attachmentMimeType(first.mime()).attachmentSize(first.size())
                    .extraAttachments(new java.util.ArrayList<>(batch.extras())).build());
            invalidate(teamId, kind);
            announceChat(saved, sender);
            return view(saved, sender, team);
        } catch (RuntimeException failure) {
            PrivateChatAttachmentBatch.cleanup(privateFiles, batch.files(), failure);
            throw failure;
        }
    }

    public record AttachmentContent(byte[] content, String mimeType, String filename) {}

    @Transactional(readOnly = true)
    public AttachmentContent download(Long teamId, SportsTeamContent.Kind kind,
                                      Long messageId, int index, String username) {
        var team = team(teamId);
        requireActiveMember(team, actor(username));
        requireFeature(team, kind);
        if (privateFiles == null || !privateFiles.isConfigured())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Armazenamento privado indisponível");
        if (messageId == null || messageId <= 0 || index < 0
                || index >= PrivateChatAttachmentBatch.MAX_FILES)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ficheiro não encontrado");
        var entry = content.findById(messageId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Ficheiro não encontrado"));
        if (!entry.getTeam().getId().equals(teamId)
                || entry.getKind() != kind || entry.getDeletedAt() != null
                || entry.getAttachmentKey() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ficheiro não encontrado");
        if (index == 0)
            return new AttachmentContent(privateFiles.read(entry.getAttachmentKey()),
                    entry.getAttachmentMimeType(), entry.getAttachmentName());
        var extras = entry.getExtraAttachments();
        if (extras == null || extras.size() < index)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ficheiro não encontrado");
        var extra = extras.get(index - 1);
        if (extra == null || extra.getStorageKey() == null || extra.getStorageKey().isBlank())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ficheiro não encontrado");
        return new AttachmentContent(privateFiles.read(extra.getStorageKey()),
                extra.getMimeType(), extra.getFilename());
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
        var saved = content.saveAndFlush(entry);
        invalidate(teamId, kind);
        return view(saved, actor, team);
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
        invalidate(teamId, kind);
    }

}