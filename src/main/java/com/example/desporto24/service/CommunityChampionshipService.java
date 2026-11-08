package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.dto.community.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityChampionshipService {
    private final CommunityRepository communities;
    private final CommunityMemberRepository members;
    private final CommunityChampionshipRepository championships;
    private final CommunityTournamentRepository tournaments;
    private final ProfileRepository profiles;

    public CommunityChampionshipResponse create(Long communityId, String username,
                                                  CreateCommunityChampionshipRequest request) {
        requireOrganizer(communityId, username);
        Community community = communities.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidade não encontrada"));
        String sport = request.sport().trim();
        String season = request.seasonLabel().trim();
        String name = request.name().trim();
        if (sport.isEmpty() || name.isEmpty() || season.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, época e modalidade obrigatórios");
        CommunityChampionship championship = CommunityChampionship.builder()
                .community(community).name(name).sport(sport)
                .seasonLabel(season).createdAt(Instant.now()).build();
        return toResponse(championships.save(championship));
    }

    @Transactional(readOnly = true)
    public List<CommunityChampionshipResponse> list(Long communityId, String username) {
        requireMember(communityId, username);
        return championships.findByCommunityIdOrderByCreatedAtDescIdDesc(communityId)
                .stream().map(this::toResponse).toList();
    }

    public CommunityChampionshipResponse addCompetition(Long communityId, Long championshipId,
                                                         Long tournamentId, String username) {
        requireOrganizer(communityId, username);
        CommunityChampionship championship = championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
        CommunityTournament tournament = tournaments.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada"));
        if (!tournament.getCommunity().getId().equals(communityId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A prova pertence a outra comunidade");
        if (tournament.getSport() == null || !championship.getSport().equalsIgnoreCase(tournament.getSport().trim()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A prova deve ter a modalidade do campeonato");
        if (championship.getCompetitions().stream().anyMatch(t -> t.getId().equals(tournamentId)))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prova já associada ao campeonato");
        // Unique database constraint prevents the same tournament being attached to two championships.
        championship.getCompetitions().add(tournament);
        return toResponse(championships.saveAndFlush(championship));
    }

    private void requireMember(Long communityId, String username) {
        Profile profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        if (!members.existsByCommunityIdAndProfileId(communityId, profile.getId()))
            throw new AccessDeniedException("Não pertences a esta comunidade");
    }

    private void requireOrganizer(Long communityId, String username) {
        Profile profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        CommunityMember member = members.findByCommunityIdAndProfileId(communityId, profile.getId())
                .orElseThrow(() -> new AccessDeniedException("Não pertences a esta comunidade"));
        if (member.getRole() != CommunityRole.OWNER && member.getRole() != CommunityRole.ADMIN)
            throw new AccessDeniedException("Apenas Owners e Admins podem gerir campeonatos");
    }

    private CommunityChampionshipResponse toResponse(CommunityChampionship championship) {
        return new CommunityChampionshipResponse(
                championship.getId(), championship.getCommunity().getId(), championship.getName(),
                championship.getSport(), championship.getSeasonLabel(), championship.getCreatedAt(),
                championship.getCompetitions().stream()
                        .map(t -> new CommunityChampionshipResponse.ChampionshipCompetition(
                                t.getId(), t.getName(), t.getFormat().name(), t.getStatus().name()))
                        .toList());
    }
}
