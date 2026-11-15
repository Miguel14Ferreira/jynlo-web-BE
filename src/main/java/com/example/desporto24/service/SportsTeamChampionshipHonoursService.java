package com.example.desporto24.service;

import com.example.desporto24.model.dto.community.CommunityChampionshipTrophySnapshot;
import com.example.desporto24.repository.community.CommunityChampionshipTeamRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Verified, read-only championship honours. Never matches teams by display name:
 * a season team must be explicitly linked to this global identity, and the
 * closed season's immutable trophy snapshot must name that exact season team.
 */
@Service
@RequiredArgsConstructor
public class SportsTeamChampionshipHonoursService {
    private final SportsTeamRepository globalTeams;
    private final CommunityChampionshipTeamRepository seasonTeams;
    private final ObjectMapper mapper;

    public record Honour(Long championshipId, String championshipName, String seasonLabel,
                         Long communityId, String communityName, Long tournamentId,
                         Long ownedEventId, String competitionName, String format,
                         String sport, Instant awardedAt) {}

    @Transactional(readOnly = true)
    public List<Honour> honours(Long teamId) {
        if (!globalTeams.existsById(teamId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada");
        List<Honour> result = new ArrayList<>();
        Set<String> recorded = new HashSet<>();
        for (var seasonTeam : seasonTeams.closedPublicSeasonsForGlobalTeam(teamId)) {
            var championship = seasonTeam.getChampionship();
            if (championship.getClosedAt() == null || championship.getFinalTrophiesJson() == null)
                continue;
            final List<CommunityChampionshipTrophySnapshot> snapshots;
            try {
                snapshots = mapper.readValue(championship.getFinalTrophiesJson(),
                    new TypeReference<List<CommunityChampionshipTrophySnapshot>>() {});
            } catch (com.fasterxml.jackson.core.JsonProcessingException error) {
                throw new IllegalStateException("Histórico oficial de troféus inválido", error);
            }
            // The aggregate season title is separate from its individual League/Cup trophies.
            if ("TEAM".equals(championship.getChampionEntryType())
                    && Objects.equals(championship.getChampionParticipantId(), seasonTeam.getId())
                    && championship.getChampionDisplayName() != null
                    && !championship.getChampionDisplayName().isBlank()
                    && recorded.add(championship.getId() + ":championship")) {
                result.add(new Honour(championship.getId(), championship.getName(),
                    championship.getSeasonLabel(), championship.getCommunity().getId(),
                    championship.getCommunity().getName(), null, null,
                    championship.getName(), "CHAMPIONSHIP",
                    championship.getSport(), championship.getClosedAt()));
            }
            if (snapshots == null) continue;
            for (var award : snapshots) {
                if (award == null || !"TEAM".equals(award.entryType())
                        || !Objects.equals(award.winnerSeasonTeamId(), seasonTeam.getId())
                        || (award.tournamentId() == null && award.ownedEventId() == null)
                        || award.winnerName() == null || award.winnerName().isBlank()
                        || award.tournamentName() == null || award.tournamentName().isBlank()) continue;
                String awardId = championship.getId() + ":" +
                    (award.ownedEventId() != null ? "event:" + award.ownedEventId() :
                            "tournament:" + award.tournamentId());
                if (!recorded.add(awardId)) continue;
                result.add(new Honour(championship.getId(), championship.getName(),
                    championship.getSeasonLabel(), championship.getCommunity().getId(),
                    championship.getCommunity().getName(), award.tournamentId(),
                    award.ownedEventId(), award.tournamentName(), award.format(),
                    championship.getSport(), championship.getClosedAt()));
            }
        }
        return List.copyOf(result);
    }
}
