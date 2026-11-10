package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.dto.community.CommunityChampionshipTeamsResponse;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityChampionshipTeamsService {
    private final CommunityChampionshipRepository championships;
    private final CommunityChampionshipTeamRepository seasonTeams;
    private final CommunityTournamentTeamRepository tournamentTeams;
    private final ProfileRepository profiles;
    private final CommunityMemberRepository members;
    private final CommunityChampionshipTeamPlayerRepository playerRegistrations;

    @Transactional(readOnly = true)
    public CommunityChampionshipTeamsResponse list(Long communityId, Long championshipId, String username) {
        requireMember(communityId, username);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        return response(championship);
    }

    public CommunityChampionshipTeamsResponse create(Long communityId, Long championshipId,
                                                      String name, String username) {
        requireOrganizer(communityId, username);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        ensureOpen(championship);
        String trimmed = name == null ? "" : name.trim().replaceAll("\\s+", " ");
        if (trimmed.isEmpty() || trimmed.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da equipa inválido");
        String normalized = trimmed.toLowerCase(Locale.ROOT);
        if (seasonTeams.existsByChampionshipIdAndNormalizedName(championshipId, normalized))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Equipa já existente neste campeonato");
        seasonTeams.saveAndFlush(CommunityChampionshipTeam.builder()
                .championship(championship).name(trimmed).normalizedName(normalized)
                .createdAt(Instant.now()).build());
        return response(championship);
    }

    public CommunityChampionshipTeamsResponse link(Long communityId, Long championshipId,
                                                    Long tournamentTeamId, Long seasonTeamId,
                                                    String username) {
        requireOrganizer(communityId, username);
        CommunityChampionship championship = getChampionship(communityId, championshipId);
        ensureOpen(championship);
        CommunityChampionshipTeam seasonTeam = seasonTeams.findById(seasonTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa de campeonato inexistente"));
        if (!seasonTeam.getChampionship().getId().equals(championshipId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A equipa pertence a outro campeonato");
        CommunityTournamentTeam tournamentTeam = tournamentTeams.findById(tournamentTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa da prova inexistente"));
        Long tournamentId = tournamentTeam.getTournament().getId();
        if (championship.getCompetitions().stream().noneMatch(t -> t.getId().equals(tournamentId)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A prova não pertence ao campeonato");
        if (tournamentTeams.findByTournamentIdOrderByIdAsc(tournamentId).stream()
                .anyMatch(other -> !other.getId().equals(tournamentTeamId)
                        && other.getChampionshipTeam() != null
                        && seasonTeamId.equals(other.getChampionshipTeam().getId())))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta equipa já está associada a outra equipa da prova");
        tournamentTeam.setChampionshipTeam(seasonTeam);
        tournamentTeams.saveAndFlush(tournamentTeam);
        return response(championship);
    }

    public record TeamPlayer(Long profileId, String username, String displayName, String profilePicUrl) {}

    @Transactional(readOnly = true)
    public List<TeamPlayer> players(Long communityId, Long championshipId, Long teamId, String username) {
        requireMember(communityId, username);
        getSeasonTeam(communityId, championshipId, teamId);
        return playerRegistrations.findByTeamIdOrderByJoinedAtAscIdAsc(teamId)
                .stream().map(reg -> {
                    var profile = reg.getProfile();
                    return new TeamPlayer(profile.getId(), profile.getUsername(),
                            profile.getFullName() == null || profile.getFullName().isBlank()
                                    ? profile.getUsername() : profile.getFullName(),
                            profile.getProfilePicUrl());
                }).toList();
    }

    public List<TeamPlayer> addPlayer(Long communityId, Long championshipId, Long teamId,
                                      Long profileId, String username) {
        requireOrganizer(communityId, username);
        var season = getChampionship(communityId, championshipId);
        ensureOpen(season);
        var team = getSeasonTeam(communityId, championshipId, teamId);
        if (profileId == null || !members.existsByCommunityIdAndProfileId(communityId, profileId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O jogador tem de pertencer à comunidade");
        if (playerRegistrations.findByChampionshipIdAndProfileId(championshipId, profileId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O jogador já pertence a uma equipa nesta época");
        var profile = profiles.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));
        try {
            playerRegistrations.saveAndFlush(CommunityChampionshipTeamPlayer.builder()
                    .championship(season).team(team).profile(profile).joinedAt(Instant.now()).build());
        } catch (org.springframework.dao.DataIntegrityViolationException conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O jogador já pertence a uma equipa nesta época", conflict);
        }
        return players(communityId, championshipId, teamId, username);
    }

    public void removePlayer(Long communityId, Long championshipId, Long teamId,
                             Long profileId, String username) {
        requireOrganizer(communityId, username);
        ensureOpen(getChampionship(communityId, championshipId));
        getSeasonTeam(communityId, championshipId, teamId);
        var registration = playerRegistrations.findByChampionshipIdAndProfileId(championshipId, profileId)
                .filter(reg -> reg.getTeam().getId().equals(teamId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Jogador não inscrito nesta equipa"));
        playerRegistrations.delete(registration);
    }

    private CommunityChampionshipTeam getSeasonTeam(Long communityId, Long championshipId, Long teamId) {
        getChampionship(communityId, championshipId);
        return seasonTeams.findById(teamId)
                .filter(team -> team.getChampionship().getId().equals(championshipId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Equipa não encontrada neste campeonato"));
    }

    private void ensureOpen(CommunityChampionship championship) {
        if (championship.getClosedAt() != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O campeonato está encerrado");
    }

    private CommunityChampionship getChampionship(Long communityId, Long championshipId) {
        return championships.findByIdAndCommunityId(championshipId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campeonato não encontrado"));
    }

    private CommunityChampionshipTeamsResponse response(CommunityChampionship championship) {
        var teams = seasonTeams.findByChampionshipIdOrderByNameAscIdAsc(championship.getId())
                .stream().map(t -> new CommunityChampionshipTeamsResponse.Team(t.getId(), t.getName(),
                        t.getPermanentTeam() == null ? null : t.getPermanentTeam().getId())).toList();
        List<CommunityChampionshipTeamsResponse.CompetitionTeam> entries = new ArrayList<>();
        for (var tournament : championship.getCompetitions()) {
            for (var team : tournamentTeams.findByTournamentIdOrderByIdAsc(tournament.getId())) {
                entries.add(new CommunityChampionshipTeamsResponse.CompetitionTeam(
                        team.getId(), tournament.getId(), tournament.getName(), team.getName(),
                        team.getChampionshipTeam() == null ? null : team.getChampionshipTeam().getId()));
            }
        }
        return new CommunityChampionshipTeamsResponse(championship.getId(), teams, List.copyOf(entries));
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
