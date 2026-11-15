package com.example.desporto24.service;

import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SportsTeamService {
    private final SportsTeamRepository teams;
    private final com.example.desporto24.repository.community.CommunityTournamentTeamRepository tournamentTeams;
    private final ProfileRepository profiles;

    public record TeamView(Long id, String slug, String name, String sport, String location,
                           String description, Long ownerProfileId, boolean feedEnabled,
                           boolean chatEnabled) {}
    public record CreateTeam(String name, String sport, String location, String description) {}
    public record TeamFeatures(Boolean feedEnabled, Boolean chatEnabled) {}

    private TeamView view(SportsTeam team) {
        return new TeamView(team.getId(), team.getSlug(), team.getName(), team.getSport(),
                team.getLocation(), team.getDescription(), team.getOwner().getId(),
                team.isFeedEnabled(), team.isChatEnabled());
    }

    @Transactional(readOnly = true)
    public List<TeamView> discover(String query) {
        return teams.findByNameContainingIgnoreCaseOrderByIdDesc(query == null ? "" : query.trim(),
                org.springframework.data.domain.PageRequest.of(0, 30)).stream().map(this::view).toList();
    }

    /** Results are derived from officially finalized tournaments; never user-editable. */
    public record TeamCompetitionHistory(Long tournamentId, String tournamentName,
            Long communityId, String communityName, String sport, String status,
            Instant completedAt, boolean champion, boolean archived) {}

    @Transactional(readOnly = true)
    public List<TeamCompetitionHistory> competitionHistory(Long id) {
        required(id);
        return tournamentTeams.historyForGlobalTeam(id).stream().map(entry -> {
            var tournament = entry.getTournament();
            boolean completed = tournament.getStatus() ==
                    com.example.desporto24.enumeration.TournamentStatus.COMPLETED;
            boolean winner = completed && !tournament.isChampionTied()
                    && java.util.Objects.equals(tournament.getChampionTeamId(), entry.getId());
            return new TeamCompetitionHistory(tournament.getId(), tournament.getName(),
                    tournament.getCommunity().getId(), tournament.getCommunity().getName(),
                    tournament.getSport(), tournament.getStatus().name(),
                    tournament.getCompletedAt(), winner, completed);
        }).toList();
    }

    @Transactional(readOnly = true)
    public TeamView get(Long id) {
        return view(required(id));
    }

    @Transactional(readOnly = true)
    public List<TeamView> mine(String username) {
        var owner = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        return teams.findByOwnerIdOrderByIdDesc(owner.getId()).stream().map(this::view).toList();
    }

    @Transactional
    public TeamView create(CreateTeam request, String username) {
        var owner = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        if (request == null || request.name() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da equipa obrigatório");
        String name = request.name().trim().replaceAll("\\s+", " ");
        if (name.isEmpty() || name.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da equipa inválido");
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (base.isBlank()) base = "equipa";
        base = base.substring(0, Math.min(base.length(), 75));
        // Unique slug is enforced by the database as well; never overwrite another team's identity.
        String slug = base + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        SportsTeam team = SportsTeam.builder().owner(owner).name(name).slug(slug)
                .sport(clean(request.sport(), 100)).location(clean(request.location(), 100))
                .description(clean(request.description(), 500)).createdAt(Instant.now())
                .feedEnabled(false).chatEnabled(false).build();
        return view(teams.saveAndFlush(team));
    }

    @Transactional
    public TeamView features(Long id, TeamFeatures request, String username) {
        SportsTeam team = required(id);
        if (!team.getOwner().getUsername().equals(username))
            throw new AccessDeniedException("Apenas o responsável pode configurar a equipa");
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Configuração obrigatória");
        if (request.feedEnabled() != null) team.setFeedEnabled(request.feedEnabled());
        if (request.chatEnabled() != null) team.setChatEnabled(request.chatEnabled());
        return view(teams.saveAndFlush(team));
    }

    private String clean(String value, int max) {
        if (value == null || value.isBlank()) return null;
        String clean = value.trim();
        if (clean.length() > max)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo demasiado longo");
        return clean;
    }

    /** Internal reference for associating a permanent identity with a competition entry. */
    @Transactional(readOnly = true)
    public SportsTeam entity(Long id) {
        return required(id);
    }

    private SportsTeam required(Long id) {
        return teams.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada"));
    }
}
