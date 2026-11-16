package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityChampionshipTeamRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamRepository;
import com.example.desporto24.repository.team.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamDeletionServiceTest {
    @Mock SportsTeamRepository teams;
    @Mock ProfileRepository profiles;
    @Mock SportsTeamMemberRepository members;
    @Mock SportsTeamContentRepository content;
    @Mock SportsTeamCommunityMembershipRepository communities;
    @Mock SportsTeamCompetitionApplicationRepository applications;
    @Mock SportsTeamCompetitionInvitationRepository invitations;
    @Mock CommunityTournamentTeamRepository tournamentTeams;
    @Mock CommunityChampionshipTeamRepository championshipTeams;
    @InjectMocks SportsTeamDeletionService service;

    private final Long id = 17L;
    private Profile owner;
    private SportsTeam team;

    @BeforeEach
    void setup() {
        owner = new Profile();
        owner.setId(2L);
        owner.setUsername("owner");
        team = SportsTeam.builder().id(id).owner(owner).name("Empty QA Team").build();
    }

    private void ownerCanAccessTeam() {
        when(profiles.findUserByUsername("owner")).thenReturn(Optional.of(owner));
        when(teams.findForDeletion(id)).thenReturn(Optional.of(team));
    }

    @Test
    void deletesOnlyAnEmptyTeamAfterDependencyChecksAndFlushes() {
        ownerCanAccessTeam();
        service.deleteEmpty(id, "owner");
        verify(members).existsByTeamId(id);
        verify(content).existsByTeamId(id);
        verify(communities).existsByTeamId(id);
        verify(applications).existsByTeamId(id);
        verify(invitations).existsByTeamId(id);
        verify(tournamentTeams).existsByGlobalTeamId(id);
        verify(championshipTeams).existsByGlobalTeamId(id);
        var order = inOrder(teams);
        order.verify(teams).delete(team);
        order.verify(teams).flush();
    }

    @Test
    void refusesToDeleteWhenAnInvitationOrMemberStillExists() {
        ownerCanAccessTeam();
        when(members.existsByTeamId(id)).thenReturn(true);
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.deleteEmpty(id,"owner")).getStatusCode().value());
        verify(teams, never()).delete(any());
    }

    @Test
    void refusesToDeletePrivateChatHistoryEvenWithoutPlayers() {
        ownerCanAccessTeam();
        when(content.existsByTeamId(id)).thenReturn(true);
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.deleteEmpty(id,"owner")).getStatusCode().value());
        verify(teams, never()).delete(any());
    }

    @Test
    void refusesToDeletePastCompetitionLinks() {
        ownerCanAccessTeam();
        when(tournamentTeams.existsByGlobalTeamId(id)).thenReturn(true);
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.deleteEmpty(id,"owner")).getStatusCode().value());
        verify(teams, never()).delete(any());
    }

    @Test
    void refusesToDeleteACommunityMembership() {
        ownerCanAccessTeam();
        when(communities.existsByTeamId(id)).thenReturn(true);
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.deleteEmpty(id,"owner")).getStatusCode().value());
        verify(teams, never()).delete(any());
    }

    @Test
    void refusesToDeleteAnExistingCompetitionApplication() {
        ownerCanAccessTeam();
        when(applications.existsByTeamId(id)).thenReturn(true);
        assertEquals(409, assertThrows(ResponseStatusException.class,
                () -> service.deleteEmpty(id,"owner")).getStatusCode().value());
        verify(teams, never()).delete(any());
    }

    @Test
    void rejectsOtherUsersAndMissingTeamsWithoutDeletion() {
        var outsider = new Profile();
        outsider.setId(99L);
        when(profiles.findUserByUsername("outsider")).thenReturn(Optional.of(outsider));
        when(teams.findForDeletion(id)).thenReturn(Optional.of(team));
        assertThrows(AccessDeniedException.class, () -> service.deleteEmpty(id,"outsider"));
        verify(teams, never()).delete(any());
    }

    @Test
    void rejectsInvalidIdentifiersWithoutTouchingRepositories() {
        assertEquals(400,assertThrows(ResponseStatusException.class,
                () -> service.deleteEmpty(0L,"owner")).getStatusCode().value());
        verifyNoInteractions(teams, profiles);
    }
}
