package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.dto.community.CommunityPermanentTeamsResponse;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityPermanentTeamsService {
    private final CommunityRepository communities;
    private final CommunityPermanentTeamRepository permanentTeams;
    private final CommunityChampionshipTeamRepository seasonTeams;
    private final CommunityChampionshipRepository championships;
    private final ProfileRepository profiles;
    private final CommunityMemberRepository members;

    @Transactional(readOnly = true)
    public CommunityPermanentTeamsResponse list(Long communityId, String username) {
        requireMember(communityId, username);
        return response(communityId);
    }

    public CommunityPermanentTeamsResponse create(Long communityId, String name, String username) {
        requireOrganizer(communityId, username);
        var community = communities.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidade não encontrada"));
        String clean = name == null ? "" : name.trim().replaceAll("\\s+", " ");
        if (clean.isEmpty() || clean.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome inválido");
        String normalized = clean.toLowerCase(Locale.ROOT);
        if (permanentTeams.existsByCommunityIdAndNormalizedName(communityId, normalized))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma equipa com este nome");
        permanentTeams.saveAndFlush(CommunityPermanentTeam.builder().community(community)
                .name(clean).normalizedName(normalized).createdAt(Instant.now()).build());
        return response(communityId);
    }

    /** Allows linking archived seasons without changing their frozen results or winner. */
    public CommunityPermanentTeamsResponse link(Long communityId, Long championshipId,
                                                Long seasonTeamId, Long permanentTeamId,
                                                String username) {
        requireOrganizer(communityId, username);
        var championship = championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
        var seasonTeam = seasonTeams.findById(seasonTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa da época não encontrada"));
        if (!seasonTeam.getChampionship().getId().equals(championship.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipa não pertence a esta época");
        var permanent = permanentTeams.findByIdAndCommunityId(permanentTeamId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa permanente não encontrada"));
        if (seasonTeams.findByChampionshipIdOrderByNameAscIdAsc(championshipId).stream()
                .anyMatch(other -> !other.getId().equals(seasonTeamId)
                        && other.getPermanentTeam() != null
                        && permanentTeamId.equals(other.getPermanentTeam().getId())))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A equipa permanente já corresponde a outra equipa desta época");
        seasonTeam.setPermanentTeam(permanent);
        seasonTeams.saveAndFlush(seasonTeam);
        return response(communityId);
    }

    private CommunityPermanentTeamsResponse response(Long communityId) {
        return new CommunityPermanentTeamsResponse(communityId,
                permanentTeams.findByCommunityIdOrderByNameAscIdAsc(communityId).stream()
                        .map(team -> new CommunityPermanentTeamsResponse.Team(team.getId(), team.getName()))
                        .toList());
    }

    private void requireMember(Long communityId, String username) {
        var profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        if (!members.existsByCommunityIdAndProfileId(communityId, profile.getId()))
            throw new AccessDeniedException("Não pertences a esta comunidade");
    }

    private void requireOrganizer(Long communityId, String username) {
        var profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        var member = members.findByCommunityIdAndProfileId(communityId, profile.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences a esta comunidade"));
        if (member.getRole() != CommunityRole.OWNER && member.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Apenas administradores podem associar equipas");
    }
}
