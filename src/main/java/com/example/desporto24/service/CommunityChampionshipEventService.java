package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.community.CommunityChampionshipEvent;
import com.example.desporto24.model.community.CommunityChampionship;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityChampionshipRepository;
import com.example.desporto24.repository.community.CommunityChampionshipEventRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Separate competition identities owned by the championship.
 * These draft competitions do not reuse community_tournament records or
 * count towards standings until their own fixtures/results engine is implemented.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityChampionshipEventService {
    private static final Set<String> FORMATS = Set.of("LEAGUE", "CUP", "SUPERCUP", "OTHER");

    private final CommunityChampionshipRepository championships;
    private final CommunityChampionshipEventRepository events;
    private final CommunityMemberRepository members;
    private final ProfileRepository profiles;

    public record CreateEvent(String name, String format) {}
    public record Event(Long id, Long championshipId, String name, String format, String status, Instant createdAt) {}

    @Transactional(readOnly = true)
    public List<Event> list(Long communityId, Long championshipId, String username) {
        requireMember(communityId, username, false);
        getChampionship(communityId, championshipId);
        return events.findByChampionshipIdOrderByCreatedAtAscIdAsc(championshipId)
                .stream().map(this::response).toList();
    }

    public Event create(Long communityId, Long championshipId, String username, CreateEvent request) {
        requireMember(communityId, username, true);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        if (championship.getClosedAt() != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Campeonato encerrado");
        if (request == null || request.name() == null || request.name().isBlank()
                || request.name().trim().length() > 120
                || request.format() == null || !FORMATS.contains(request.format()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome ou formato de prova inválido");

        return response(events.save(CommunityChampionshipEvent.builder()
                .championship(championship)
                .name(request.name().trim())
                .format(request.format())
                .status("DRAFT")
                .createdAt(Instant.now())
                .build()));
    }

    private CommunityChampionship getChampionship(Long communityId, Long championshipId) {
        return championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
    }

    private Event response(CommunityChampionshipEvent event) {
        return new Event(event.getId(), event.getChampionship().getId(), event.getName(),
                event.getFormat(), event.getStatus(), event.getCreatedAt());
    }

    private void requireMember(Long communityId, String username, boolean organizer) {
        var profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        CommunityMember member = members.findByCommunityIdAndProfileId(communityId, profile.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences à comunidade"));
        if (organizer && member.getRole() != CommunityRole.OWNER && member.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Apenas Owners e Admins podem gerir campeonatos");
    }
}
