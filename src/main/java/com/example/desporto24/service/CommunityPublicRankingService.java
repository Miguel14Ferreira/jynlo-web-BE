package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.dto.community.CommunityStandingResponse;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/**
 * Shareable season ranking of a community ("quem tem mais vitórias esta época"),
 * opt-in by an Owner/Admin. Shows the table's own player names (or public handles) and results only: no account full names,
 * photos, emails, member lists or private data. Disabled answers 404.
 */
@Service
@RequiredArgsConstructor
public class CommunityPublicRankingService {

    public record Setting(boolean enabled, String path) {}
    public record Season(Long id, String name, String sport, boolean active, Instant startsAt, Instant endedAt) {}
    public record Row(int position, String name, int games, int wins, int draws, int losses, int points,
                      double winRate, String streak) {}
    public record Page(Long communityId, String communityName, String sport, List<Season> seasons,
                       Long selectedSeasonId, List<Row> table) {}

    private final ProfileRepository profiles;
    private final CommunityMemberRepository members;
    private final CommunityRepository communities;
    private final CommunityCompetitionService competition;

    @Transactional(readOnly = true)
    public Setting setting(Long communityId, String username) {
        Community community = manageable(communityId, username, false);
        return new Setting(community.isPublicRankingEnabled(), path(community));
    }

    @Transactional
    public Setting update(Long communityId, String username, boolean enabled) {
        Community community = manageable(communityId, username, true);
        community.setPublicRankingEnabled(enabled);
        communities.save(community);
        return new Setting(enabled, path(community));
    }

    @Transactional(readOnly = true)
    public Page page(Long communityId, Long seasonId) {
        Community community = communities.findById(communityId)
                .filter(Community::isPublicRankingEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classificação não encontrada"));
        var overview = competition.publicOverview(communityId, seasonId);
        Long selected = seasonId != null ? seasonId
                : overview.selectedSeasonId() != null ? overview.selectedSeasonId() : overview.activeSeasonId();
        List<Season> seasons = overview.seasons() == null ? List.of() : overview.seasons().stream()
                .map(s -> new Season(s.id(), clean(s.name()), clean(s.sport()), s.active(), s.startsAt(), s.endedAt()))
                .toList();
        List<CommunityStandingResponse> standings = overview.standings() == null ? List.of() : overview.standings();
        if (standings.isEmpty() && overview.sportStandings() != null && !overview.sportStandings().isEmpty()) {
            // Multi-sport seasons group the table by sport: show the first (the community's main) one.
            standings = overview.sportStandings().getFirst().standings();
        }
        List<Row> table = standings.stream()
                .filter(row -> row.games() > 0)
                .map(row -> new Row(row.position(), clean(publicName(row)),
                        row.games(), row.wins(), row.draws(), row.losses(), row.points(),
                        Math.round(row.winRate() * 10.0) / 10.0, row.currentStreak()))
                .toList();
        return new Page(community.getId(), clean(community.getName()), clean(community.getSport()),
                seasons, selected, table);
    }

    /**
     * The name the organiser uses in the table (the one imported from the spreadsheet or typed in the
     * community). A registered account with no such name shows its @handle, never its private full name.
     */
    String publicName(CommunityStandingResponse row) {
        String shown = row.fullName() == null ? "" : row.fullName().trim();
        if (row.profileId() == null) return shown.isEmpty() ? "Jogador" : shown;
        String handle = row.username() == null ? "Jogador" : row.username();
        if (shown.isEmpty()) return handle;
        String accountName = profiles.findById(row.profileId())
                .map(p -> p.getFullName() == null ? "" : p.getFullName().trim()).orElse("");
        return !accountName.isEmpty() && accountName.equalsIgnoreCase(shown) ? handle : shown;
    }

    private Community manageable(Long communityId, String username, boolean write) {
        Profile actor = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta não encontrada"));
        var membership = members.findByCommunityIdAndProfileId(communityId, actor.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Não pertences a esta comunidade"));
        if (write && membership.getRole() != CommunityRole.OWNER && membership.getRole() != CommunityRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só Owner/Admin pode partilhar a classificação");
        }
        return communities.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidade não encontrada"));
    }

    private static String path(Community community) {
        return "/rankings/" + community.getId();
    }

    private static String clean(String value) {
        if (value == null) return null;
        String cleaned = value.strip().replaceAll("[\\p{Cntrl}]+", " ");
        return cleaned.length() > 80 ? cleaned.substring(0, 80).strip() : cleaned;
    }
}
