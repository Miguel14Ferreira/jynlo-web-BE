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

    public record Entry(Long id, Long teamId, String username, String body, Instant createdAt) {}
    public record Submit(String body) {}

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

    private Entry view(SportsTeamContent entry) {
        return new Entry(entry.getId(), entry.getTeam().getId(),
                entry.getAuthor().getUsername(), entry.getBody(), entry.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<Entry> recent(Long teamId, SportsTeamContent.Kind kind, String username) {
        var team = team(teamId);
        requireActiveMember(team, actor(username));
        requireFeature(team, kind);
        // Bounded responses; newest-first at database level, chronological for UI.
        return content.findByTeamIdAndKindOrderByIdDesc(teamId, kind, PageRequest.of(0, 100))
                .stream().map(this::view).sorted(Comparator.comparing(Entry::id)).toList();
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
        return view(content.saveAndFlush(SportsTeamContent.builder()
                .team(team).author(sender).kind(kind).body(body)
                .createdAt(Instant.now()).build()));
    }
}