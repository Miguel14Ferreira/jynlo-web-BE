package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.community.CommunityChampionshipResponse;
import com.example.desporto24.model.dto.community.CreateCommunityChampionshipRequest;
import com.example.desporto24.service.CommunityChampionshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities/{communityId}/championships")
@PreAuthorize("isAuthenticated()")
public class CommunityChampionshipController {
    private final CommunityChampionshipService championships;
    private final com.example.desporto24.service.CommunityChampionshipEventService ownedEvents;
    private final com.example.desporto24.service.CommunityChampionshipTeamsService championshipTeams;
    private final com.example.desporto24.service.CommunityPermanentTeamsService permanentTeams;

    @GetMapping
    public List<CommunityChampionshipResponse> list(@PathVariable Long communityId, Authentication auth) {
        return championships.list(communityId, auth.getName());
    }

    @GetMapping("/permanent-teams")
    public com.example.desporto24.model.dto.community.CommunityPermanentTeamsResponse permanentTeams(
            @PathVariable Long communityId, Authentication auth) {
        return permanentTeams.list(communityId, auth.getName());
    }

    @PostMapping("/permanent-teams")
    public com.example.desporto24.model.dto.community.CommunityPermanentTeamsResponse createPermanentTeam(
            @PathVariable Long communityId,
            @RequestBody @Valid com.example.desporto24.model.dto.community.CreateCommunityChampionshipTeamRequest request,
            Authentication auth) {
        return permanentTeams.create(communityId, request.name(), auth.getName());
    }

    @PutMapping("/{championshipId}/teams/{seasonTeamId}/permanent-team")
    public com.example.desporto24.model.dto.community.CommunityPermanentTeamsResponse linkPermanentTeam(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long seasonTeamId,
            @RequestBody @Valid com.example.desporto24.model.dto.community.LinkCommunityPermanentTeamRequest request,
            Authentication auth) {
        return permanentTeams.link(communityId, championshipId,
                seasonTeamId, request.permanentTeamId(), auth.getName());
    }

    @GetMapping("/honours")
    public com.example.desporto24.model.dto.community.CommunityChampionshipHonoursResponse honours(
            @PathVariable Long communityId, Authentication auth) {
        return championships.honours(communityId, auth.getName());
    }

    @GetMapping("/{championshipId}/teams")
    public com.example.desporto24.model.dto.community.CommunityChampionshipTeamsResponse teams(
            @PathVariable Long communityId, @PathVariable Long championshipId, Authentication auth) {
        return championshipTeams.list(communityId, championshipId, auth.getName());
    }

    @PostMapping("/{championshipId}/teams")
    public com.example.desporto24.model.dto.community.CommunityChampionshipTeamsResponse createTeam(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @RequestBody @Valid com.example.desporto24.model.dto.community.CreateCommunityChampionshipTeamRequest request,
            Authentication auth) {
        return championshipTeams.create(communityId, championshipId, request.name(), auth.getName());
    }

    public record TeamPlayerInviteRequest(@jakarta.validation.constraints.NotNull Long profileId) {}

    @PostMapping("/{championshipId}/teams/{teamId}/invitations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void invitePlayerToSeasonTeam(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, @RequestBody @Valid TeamPlayerInviteRequest request,
            Authentication auth) {
        championshipTeams.invitePlayer(communityId, championshipId, teamId, request.profileId(), auth.getName());
    }

    @GetMapping("/{championshipId}/teams/{teamId}/invitations/mine")
    public java.util.Map<String, Boolean> mySeasonTeamInvitation(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, Authentication auth) {
        return java.util.Map.of("invited", championshipTeams.hasPlayerInvite(
                communityId, championshipId, teamId, auth.getName()));
    }

    @PostMapping("/{championshipId}/teams/{teamId}/invitations/accept")
    public List<com.example.desporto24.service.CommunityChampionshipTeamsService.TeamPlayer> acceptSeasonTeamInvitation(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, Authentication auth) {
        return championshipTeams.respondPlayerInvite(communityId, championshipId, teamId, true, auth.getName());
    }

    @PostMapping("/{championshipId}/teams/{teamId}/invitations/decline")
    public List<com.example.desporto24.service.CommunityChampionshipTeamsService.TeamPlayer> declineSeasonTeamInvitation(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, Authentication auth) {
        return championshipTeams.respondPlayerInvite(communityId, championshipId, teamId, false, auth.getName());
    }

    public record TeamAdministratorInviteRequest(@jakarta.validation.constraints.NotNull Long profileId) {}

    @GetMapping("/{championshipId}/teams/{teamId}/administrator")
    public com.example.desporto24.service.CommunityChampionshipTeamsService.TeamAdminStatus teamAdministrator(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, Authentication auth) {
        return championshipTeams.administrator(communityId, championshipId, teamId, auth.getName());
    }

    @PostMapping("/{championshipId}/teams/{teamId}/administrator/invite")
    public com.example.desporto24.service.CommunityChampionshipTeamsService.TeamAdminStatus inviteTeamAdministrator(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, @RequestBody @Valid TeamAdministratorInviteRequest request,
            Authentication auth) {
        return championshipTeams.inviteAdministrator(communityId, championshipId,
                teamId, request.profileId(), auth.getName());
    }

    @PostMapping("/{championshipId}/teams/{teamId}/administrator/accept")
    public com.example.desporto24.service.CommunityChampionshipTeamsService.TeamAdminStatus acceptTeamAdministrator(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, Authentication auth) {
        return championshipTeams.respondAdministrator(communityId, championshipId, teamId, true, auth.getName());
    }

    @PostMapping("/{championshipId}/teams/{teamId}/administrator/cancel")
    public com.example.desporto24.service.CommunityChampionshipTeamsService.TeamAdminStatus cancelTeamAdminTransfer(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, Authentication auth) {
        return championshipTeams.cancelAdministratorTransfer(communityId, championshipId, teamId, auth.getName());
    }

    @PostMapping("/{championshipId}/teams/{teamId}/administrator/decline")
    public com.example.desporto24.service.CommunityChampionshipTeamsService.TeamAdminStatus declineTeamAdministrator(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, Authentication auth) {
        return championshipTeams.respondAdministrator(communityId, championshipId, teamId, false, auth.getName());
    }

    public record AddSeasonTeamPlayerRequest(@jakarta.validation.constraints.NotNull Long profileId) {}

    @GetMapping("/{championshipId}/teams/{teamId}/players")
    public List<com.example.desporto24.service.CommunityChampionshipTeamsService.TeamPlayer> teamPlayers(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, Authentication auth) {
        return championshipTeams.players(communityId, championshipId, teamId, auth.getName());
    }

    @PostMapping("/{championshipId}/teams/{teamId}/players")
    public List<com.example.desporto24.service.CommunityChampionshipTeamsService.TeamPlayer> addTeamPlayer(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, @RequestBody @Valid AddSeasonTeamPlayerRequest request,
            Authentication auth) {
        // Legacy endpoint also sends an invitation; only explicit acceptance
        // can create team membership or assign the first administrator.
        championshipTeams.invitePlayer(communityId, championshipId, teamId, request.profileId(), auth.getName());
        return championshipTeams.players(communityId, championshipId, teamId, auth.getName());
    }

    @DeleteMapping("/{championshipId}/teams/{teamId}/players/{profileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTeamPlayer(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long teamId, @PathVariable Long profileId, Authentication auth) {
        championshipTeams.removePlayer(communityId, championshipId, teamId, profileId, auth.getName());
    }

    @PutMapping("/{championshipId}/competition-teams/{tournamentTeamId}")
    public com.example.desporto24.model.dto.community.CommunityChampionshipTeamsResponse linkTeam(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long tournamentTeamId,
            @RequestBody @Valid com.example.desporto24.model.dto.community.LinkCommunityChampionshipTeamRequest request,
            Authentication auth) {
        return championshipTeams.link(communityId, championshipId,
                tournamentTeamId, request.championshipTeamId(), auth.getName());
    }

    public record StandingsModeRequest(@jakarta.validation.constraints.NotBlank String mode) {}

    @PutMapping("/{championshipId}/standings-mode")
    public CommunityChampionshipResponse setStandingsMode(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @RequestBody @Valid StandingsModeRequest request, Authentication auth) {
        return championships.setStandingsMode(communityId, championshipId, request.mode(), auth.getName());
    }

    @PostMapping("/{championshipId}/close")
    public CommunityChampionshipResponse close(
            @PathVariable Long communityId, @PathVariable Long championshipId, Authentication auth) {
        return championships.close(communityId, championshipId, auth.getName());
    }

    @GetMapping("/{championshipId}/standings")
    public com.example.desporto24.model.dto.community.CommunityChampionshipStandingsResponse standings(
            @PathVariable Long communityId, @PathVariable Long championshipId, Authentication auth) {
        return championships.standings(communityId, championshipId, auth.getName());
    }

    @PostMapping
    public ResponseEntity<CommunityChampionshipResponse> create(
            @PathVariable Long communityId,
            @RequestBody @Valid CreateCommunityChampionshipRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(championships.create(communityId, auth.getName(), request));
    }

    @PutMapping("/{championshipId}/competitions/{tournamentId}/placement-points")
    public CommunityChampionshipResponse setPlacementPoints(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long tournamentId,
            @RequestBody @Valid com.example.desporto24.model.dto.community.ChampionshipPlacementPointsRequest request,
            Authentication auth) {
        return championships.setPlacementPoints(communityId, championshipId,
                tournamentId, request.pointsByPlace(), auth.getName());
    }

    @PutMapping("/{championshipId}/competitions/{tournamentId}/winner-points")
    public CommunityChampionshipResponse setWinnerPoints(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long tournamentId, @RequestBody @Valid WinnerPointsRequest request,
            Authentication auth) {
        return championships.setWinnerPoints(communityId, championshipId,
                tournamentId, request.points(), auth.getName());
    }

    public record WinnerPointsRequest(@jakarta.validation.constraints.Min(0)
                                      @jakarta.validation.constraints.Max(10000) int points) {}

    @GetMapping("/{championshipId}/owned-events")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.Event> ownedEvents(
            @PathVariable Long communityId, @PathVariable Long championshipId, Authentication auth) {
        return ownedEvents.list(communityId, championshipId, auth.getName());
    }

    @PostMapping("/{championshipId}/owned-events")
    public ResponseEntity<com.example.desporto24.service.CommunityChampionshipEventService.Event> createOwnedEvent(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @RequestBody com.example.desporto24.service.CommunityChampionshipEventService.CreateEvent request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ownedEvents.create(communityId, championshipId, auth.getName(), request));
    }

    @PutMapping("/{championshipId}/owned-events/{eventId}")
    public com.example.desporto24.service.CommunityChampionshipEventService.Event updateOwnedEvent(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId,
            @RequestBody com.example.desporto24.service.CommunityChampionshipEventService.CreateEvent request,
            Authentication auth) {
        return ownedEvents.update(communityId, championshipId, eventId, auth.getName(), request);
    }

    @DeleteMapping("/{championshipId}/owned-events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnedEvent(@PathVariable Long communityId, @PathVariable Long championshipId,
                                 @PathVariable Long eventId, Authentication auth) {
        ownedEvents.delete(communityId, championshipId, eventId, auth.getName());
    }

    @PostMapping("/{championshipId}/owned-events/{eventId}/cup-advance")
    public com.example.desporto24.service.CommunityChampionshipEventService.CupOpeningRound advanceOwnedCup(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, Authentication auth) {
        return ownedEvents.advanceCup(communityId, championshipId, eventId, auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/cup-draw")
    public com.example.desporto24.service.CommunityChampionshipEventService.CupOpeningRound cupDraw(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, Authentication auth) {
        return ownedEvents.cupOpeningRound(communityId, championshipId, eventId, auth.getName());
    }

    @PostMapping("/{championshipId}/owned-events/{eventId}/cup-draw")
    public com.example.desporto24.service.CommunityChampionshipEventService.CupOpeningRound drawCup(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, Authentication auth) {
        return ownedEvents.drawCup(communityId, championshipId, eventId, auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/lineup")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.LineupPlayer> fixtureLineup(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId, Authentication auth) {
        return ownedEvents.matchLineup(communityId, championshipId, eventId, fixtureId, auth.getName());
    }

    @PutMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/lineup")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.LineupPlayer> setFixtureLineup(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @RequestBody com.example.desporto24.service.CommunityChampionshipEventService.LineupInput input,
            Authentication auth) {
        return ownedEvents.setLineupPlayer(communityId, championshipId, eventId, fixtureId,
                input, auth.getName());
    }

    @DeleteMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/lineup/{profileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFixtureLineupPlayer(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @PathVariable Long profileId, Authentication auth) {
        ownedEvents.removeLineupPlayer(communityId, championshipId, eventId, fixtureId,
                profileId, auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/substitutions")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.SubstitutionEvent> fixtureSubstitutions(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId, Authentication auth) {
        return ownedEvents.matchSubstitutions(communityId, championshipId, eventId, fixtureId, auth.getName());
    }

    @PostMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/substitutions")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.SubstitutionEvent> addFixtureSubstitution(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @RequestBody com.example.desporto24.service.CommunityChampionshipEventService.SubstitutionInput input,
            Authentication auth) {
        return ownedEvents.addSubstitution(communityId, championshipId, eventId, fixtureId, input, auth.getName());
    }

    @DeleteMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/substitutions/{substitutionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFixtureSubstitution(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @PathVariable Long substitutionId, Authentication auth) {
        ownedEvents.removeSubstitution(communityId, championshipId, eventId,
                fixtureId, substitutionId, auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/cards")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.CardEvent> fixtureCards(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId, Authentication auth) {
        return ownedEvents.cards(communityId, championshipId, eventId, fixtureId, auth.getName());
    }

    @PostMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/cards")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.CardEvent> addFixtureCard(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @RequestBody com.example.desporto24.service.CommunityChampionshipEventService.CardInput input,
            Authentication auth) {
        return ownedEvents.addCard(communityId, championshipId, eventId, fixtureId, input, auth.getName());
    }

    @DeleteMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFixtureCard(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @PathVariable Long cardId, Authentication auth) {
        ownedEvents.removeCard(communityId, championshipId, eventId, fixtureId, cardId, auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/goals")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.GoalEvent> fixtureGoals(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId, Authentication auth) {
        return ownedEvents.goalScorers(communityId, championshipId, eventId, fixtureId, auth.getName());
    }

    @PostMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/goals")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.GoalEvent> addFixtureGoal(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @RequestBody com.example.desporto24.service.CommunityChampionshipEventService.GoalInput input,
            Authentication auth) {
        return ownedEvents.addGoal(communityId, championshipId, eventId, fixtureId, input, auth.getName());
    }

    @DeleteMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/goals/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFixtureGoal(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @PathVariable Long goalId, Authentication auth) {
        ownedEvents.removeGoal(communityId, championshipId, eventId, fixtureId, goalId, auth.getName());
    }

    @PutMapping("/{championshipId}/owned-events/{eventId}/fixtures/{fixtureId}/score")
    public com.example.desporto24.service.CommunityChampionshipEventService.LeagueFixture recordFixtureScore(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @PathVariable Long fixtureId,
            @RequestBody @Valid com.example.desporto24.service.CommunityChampionshipEventService.MatchScore score,
            Authentication auth) {
        return ownedEvents.recordFixtureScore(communityId, championshipId, eventId, fixtureId, score, auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/player-stats")
    public List<com.example.desporto24.service.ChampionshipPlayerEventStatistics.Row> ownedLeaguePlayerStats(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, Authentication auth) {
        return ownedEvents.ownedLeaguePlayerStats(communityId, championshipId, eventId, auth.getName());
    }

    @PostMapping("/{championshipId}/owned-events/{eventId}/finalize-league")
    public com.example.desporto24.service.CommunityChampionshipEventService.Event finalizeOwnedLeague(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, Authentication auth) {
        return ownedEvents.finalizeLeague(communityId, championshipId, eventId, auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/standings")
    public com.example.desporto24.service.ChampionshipOwnedLeagueStandings.Table ownedLeagueStandings(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, Authentication auth) {
        return ownedEvents.ownedLeagueStandings(communityId, championshipId, eventId, auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/fixtures")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.LeagueFixture> ownedLeagueFixtures(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, Authentication auth) {
        return ownedEvents.fixtures(communityId, championshipId, eventId, auth.getName());
    }

    public record ScheduleOwnedLeagueRequest(@jakarta.validation.constraints.Min(1)
                                             @jakarta.validation.constraints.Max(2) int legs) {}

    @PostMapping("/{championshipId}/owned-events/{eventId}/schedule")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.LeagueFixture> scheduleOwnedLeague(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, @RequestBody @Valid ScheduleOwnedLeagueRequest request,
            Authentication auth) {
        return ownedEvents.scheduleLeague(communityId, championshipId, eventId, request.legs(), auth.getName());
    }

    @GetMapping("/{championshipId}/owned-events/{eventId}/teams")
    public List<com.example.desporto24.service.CommunityChampionshipEventService.TeamRegistration> ownedEventTeams(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long eventId, Authentication auth) {
        return ownedEvents.teams(communityId, championshipId, eventId, auth.getName());
    }

    public record OwnedEventTeamRequest(@jakarta.validation.constraints.NotNull Long championshipTeamId) {}

    @PostMapping("/{championshipId}/owned-events/{eventId}/teams")
    public ResponseEntity<com.example.desporto24.service.CommunityChampionshipEventService.TeamRegistration> addOwnedEventTeam(
            @PathVariable Long communityId, @PathVariable Long championshipId, @PathVariable Long eventId,
            @RequestBody @Valid OwnedEventTeamRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownedEvents.addTeam(
                communityId, championshipId, eventId, request.championshipTeamId(), auth.getName()));
    }

    @DeleteMapping("/{championshipId}/owned-events/{eventId}/teams/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOwnedEventTeam(@PathVariable Long communityId, @PathVariable Long championshipId,
                                     @PathVariable Long eventId, @PathVariable Long teamId, Authentication auth) {
        ownedEvents.removeTeam(communityId, championshipId, eventId, teamId, auth.getName());
    }

    @PostMapping("/{championshipId}/competitions/{tournamentId}")
    public CommunityChampionshipResponse addCompetition(
            @PathVariable Long communityId, @PathVariable Long championshipId,
            @PathVariable Long tournamentId, Authentication auth) {
        return championships.addCompetition(communityId, championshipId, tournamentId, auth.getName());
    }
}
