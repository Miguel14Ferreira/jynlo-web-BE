package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityTournament;
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
    private final PublicProfileCompetitionHistoryService history =
            new PublicProfileCompetitionHistoryService(profiles, publicProfiles, tournaments);

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

    @Test void existingBlockPolicyCannotBeBypassed() {
        doThrow(new AccessDeniedException("blocked"))
                .when(publicProfiles).findPublicProfile("viewer", "player");
        assertThrows(AccessDeniedException.class, () -> history.individualTitles("viewer","player"));
        verifyNoInteractions(tournaments);
    }
}
