package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.community.CommunityTournamentParticipant;
import com.example.desporto24.model.community.CommunityTournamentMatch;
import com.example.desporto24.enumeration.TournamentFormat;
import com.example.desporto24.repository.community.CommunityTournamentParticipantRepository;
import com.example.desporto24.repository.community.CommunityTournamentMatchRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityTournamentRepository;
import com.example.desporto24.service.interfaces.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PublicProfileCompetitionHistoryServiceTest {
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final ProfileService publicProfiles = mock(ProfileService.class);
    private final CommunityTournamentRepository tournaments = mock(CommunityTournamentRepository.class);
    private final CommunityTournamentParticipantRepository participants = mock(CommunityTournamentParticipantRepository.class);
    private final CommunityTournamentMatchRepository matches = mock(CommunityTournamentMatchRepository.class);
    private final PublicProfileCompetitionHistoryService history =
            new PublicProfileCompetitionHistoryService(profiles, publicProfiles, tournaments, participants, matches);

    @Test void privateProfileNeverQueriesCompetitionHistory() {
        var target = new Profile(); target.setId(7L); target.setPrivateProfile(true);
        when(profiles.findUserByUsernameIgnoreCase("player")).thenReturn(Optional.of(target));
        assertTrue(history.individualTitles("viewer", "player").isEmpty());
        verifyNoInteractions(tournaments);
    }

    @Test void publicTitlesUseOfficialSnapshotAndCommunityIdentity() {
        var target = new Profile(); target.setId(7L);
        when(profiles.findUserByUsernameIgnoreCase("player")).thenReturn(Optional.of(target));
        var community = Community.builder().id(9L).name("Liga Aberta").build();
        var finished = Instant.parse("2026-09-20T17:00:00Z");
        var winner = CommunityTournament.builder().id(12L).name("Taça Individual")
                .sport("Ténis").community(community).completedAt(finished).build();
        when(tournaments.findPublicIndividualTitlesForProfile(eq(7L), any(Pageable.class)))
                .thenReturn(List.of(winner));
        assertEquals(List.of(new PublicProfileCompetitionHistoryService.IndividualTitle(
                12L, "Taça Individual", 9L, "Liga Aberta", "Ténis", finished)),
                history.individualTitles("viewer", "player"));
        verify(publicProfiles).findPublicProfile("viewer","player");
    }

    @Test void confirmedFinishedParticipationIsNotMistakenForAChampionshipTitle() {
        var target = new Profile(); target.setId(7L);
        when(profiles.findUserByUsernameIgnoreCase("player")).thenReturn(Optional.of(target));
        var community = Community.builder().id(9L).name("Liga Aberta").build();
        var finished = Instant.parse("2026-09-20T17:00:00Z");
        var tournament = CommunityTournament.builder().id(12L).name("Taça Individual")
                .sport("Ténis").community(community).completedAt(finished)
                .championProfileId(42L).build();
        var entry = CommunityTournamentParticipant.builder()
                .tournament(tournament).profile(target).build();
        when(participants.findPublicCompletedIndividualParticipations(eq(7L), any(Pageable.class)))
                .thenReturn(List.of(entry));
        assertEquals(List.of(new PublicProfileCompetitionHistoryService.CompletedParticipation(
                12L, "Taça Individual", 9L, "Liga Aberta", "Ténis", finished, false, null, null, null)),
                history.completedParticipations("viewer", "player"));
    }

    @Test void completedLeagueReportsOnlyVerifiedWinDrawLoss() {
        var target = new Profile(); target.setId(7L);
        var opponent = new Profile(); opponent.setId(8L);
        when(profiles.findUserByUsernameIgnoreCase("player")).thenReturn(Optional.of(target));
        var community = Community.builder().id(9L).name("Liga Aberta").build();
        var finished = Instant.parse("2026-09-20T17:00:00Z");
        var tournament = CommunityTournament.builder().id(12L).name("Liga Individual")
                .sport("Ténis").community(community).format(TournamentFormat.LEAGUE)
                .completedAt(finished).build();
        var home = CommunityTournamentParticipant.builder().id(1L).profile(target)
                .tournament(tournament).build();
        var away = CommunityTournamentParticipant.builder().id(2L).profile(opponent)
                .tournament(tournament).build();
        when(participants.findPublicCompletedIndividualParticipations(eq(7L), any(Pageable.class)))
                .thenReturn(List.of(home));
        var victory = CommunityTournamentMatch.builder()
                .homeParticipant(home).awayParticipant(away).homeScore(2).awayScore(0).build();
        var draw = CommunityTournamentMatch.builder()
                .homeParticipant(away).awayParticipant(home).homeScore(1).awayScore(1).build();
        var loss = CommunityTournamentMatch.builder()
                .homeParticipant(away).awayParticipant(home).homeScore(3).awayScore(1).build();
        when(matches.findCompletedMatchesForParticipant(eq(12L), eq(7L), any(Pageable.class)))
                .thenReturn(List.of(victory, draw, loss));
        assertEquals(List.of(new PublicProfileCompetitionHistoryService.CompletedParticipation(
                12L, "Liga Individual", 9L, "Liga Aberta", "Ténis", finished,
                false, 1, 1, 1)), history.completedParticipations("viewer", "player"));
    }

    @Test void unsupportedResultModesNeverInventMatchTotals() {
        var target = new Profile(); target.setId(7L);
        when(profiles.findUserByUsernameIgnoreCase("player")).thenReturn(Optional.of(target));
        var tournament = CommunityTournament.builder().id(12L).name("Circuito")
                .community(Community.builder().id(9L).name("Circuito Público").build())
                .scoringMode(com.example.desporto24.enumeration.TournamentScoringMode.RACE_POINTS)
                .format(TournamentFormat.LEAGUE).build();
        when(participants.findPublicCompletedIndividualParticipations(eq(7L), any(Pageable.class)))
                .thenReturn(List.of(CommunityTournamentParticipant.builder().tournament(tournament).build()));
        var result = history.completedParticipations("viewer", "player").get(0);
        assertNull(result.wins());
        assertNull(result.draws());
        assertNull(result.losses());
        verifyNoInteractions(matches);
    }

    @Test void privateProfileNeverDisclosesParticipationHistory() {
        var target = new Profile(); target.setId(7L); target.setPrivateProfile(true);
        when(profiles.findUserByUsernameIgnoreCase("player")).thenReturn(Optional.of(target));
        assertTrue(history.completedParticipations("viewer", "player").isEmpty());
        verifyNoInteractions(participants);
    }

    @Test void blockedViewerNeverSeesParticipationHistory() {
        doThrow(new AccessDeniedException("blocked"))
                .when(publicProfiles).findPublicProfile("viewer", "player");
        assertThrows(AccessDeniedException.class,
                () -> history.completedParticipations("viewer", "player"));
        verifyNoInteractions(participants);
    }

    @Test void existingBlockPolicyCannotBeBypassed() {
        doThrow(new AccessDeniedException("blocked"))
                .when(publicProfiles).findPublicProfile("viewer", "player");
        assertThrows(AccessDeniedException.class, () -> history.individualTitles("viewer","player"));
        verifyNoInteractions(tournaments);
    }
}
