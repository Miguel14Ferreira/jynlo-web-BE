package com.example.desporto24.service;

import com.example.desporto24.enumeration.*;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import com.example.desporto24.repository.team.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamCompetitionEnrollmentServiceTest {
    @Mock SportsTeamRepository teams;
    @Mock SportsTeamMemberRepository globalMembers;
    @Mock SportsTeamCompetitionInvitationRepository invitations;
    @Mock SportsTeamCompetitionApplicationRepository applications;
    @Mock CommunityTournamentRepository tournaments;
    @Mock CommunityChampionshipRepository championships;
    @Mock CommunityTournamentTeamRepository tournamentTeams;
    @Mock CommunityChampionshipTeamRepository championshipTeams;
    @Mock CommunityMemberRepository communityMembers;
    @Mock ProfileRepository profiles;
    @Mock CommunityTournamentTeamService tournamentRosters;
    @Mock CommunityChampionshipTeamsService championshipRosters;
    @InjectMocks SportsTeamCompetitionEnrollmentService service;

    private static Profile profile(long id,String username) {
        var p=new Profile();p.setId(id);p.setUsername(username);return p;
    }
    private static Community community() {
        return Community.builder().id(4L).name("Liga Lisboa").build();
    }
    private void organizer() {
        var actor=profile(1L,"organizer");
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(actor));
        when(communityMembers.findByCommunityIdAndProfileId(4L,1L)).thenReturn(Optional.of(
            CommunityMember.builder().profile(actor).community(community()).role(CommunityRole.OWNER).build()));
    }
    private static SportsTeam team() {
        return SportsTeam.builder().id(9L).name("Os Convocados").owner(profile(2L,"captain")).build();
    }
    private static CommunityTournament tournament() {
        return CommunityTournament.builder().id(12L).community(community())
            .entryType(TournamentEntryType.TEAM).status(TournamentStatus.DRAFT).build();
    }

    @Test
    void approvedApplicationCreatesOneLinkedTournamentEntryWithoutAddingPlayers() {
        organizer();
        when(teams.findById(9L)).thenReturn(Optional.of(team()));
        when(globalMembers.findByTeamIdAndStatusOrderByIdAsc(9L,SportsTeamMember.Status.ACTIVE))
            .thenReturn(List.of());
        when(tournaments.findByIdForUpdate(12L)).thenReturn(Optional.of(tournament()));
        var approved=SportsTeamCompetitionApplication.builder()
            .communityId(4L).status(SportsTeamCompetitionApplication.Status.APPROVED).build();
        when(applications.findByTeamIdAndCompetitionTypeAndCompetitionId(9L,
            SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT,12L))
            .thenReturn(Optional.of(approved));
        when(tournamentTeams.saveAndFlush(any())).thenAnswer(call->call.getArgument(0));

        var result=service.finalizeEntry(4L,"TOURNAMENT",12L,9L,"organizer");

        assertEquals(9L,result.globalTeamId());
        assertEquals("Os Convocados",result.name());
        assertEquals(1,result.confirmedGlobalPlayers());
        verify(tournamentTeams).saveAndFlush(argThat(entry->
            entry.getGlobalTeam().getId().equals(9L)&&entry.getAdminProfile()==null));
        verifyNoInteractions(tournamentRosters,championshipRosters);
    }

    @Test
    void pendingApplicationNeverGrantsEnrollmentPermission() {
        organizer();
        when(teams.findById(9L)).thenReturn(Optional.of(team()));
        when(globalMembers.findByTeamIdAndStatusOrderByIdAsc(9L,SportsTeamMember.Status.ACTIVE))
            .thenReturn(List.of());
        when(tournaments.findByIdForUpdate(12L)).thenReturn(Optional.of(tournament()));
        when(applications.findByTeamIdAndCompetitionTypeAndCompetitionId(9L,
            SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT,12L))
            .thenReturn(Optional.of(SportsTeamCompetitionApplication.builder().communityId(4L)
                .status(SportsTeamCompetitionApplication.Status.PENDING).build()));

        var ex=assertThrows(ResponseStatusException.class,
            ()->service.finalizeEntry(4L,"TOURNAMENT",12L,9L,"organizer"));
        assertEquals(HttpStatus.CONFLICT,ex.getStatusCode());
        verify(tournamentTeams,never()).saveAndFlush(any());
    }

    @Test
    void acceptedInvitationCreatesOneLinkedChampionshipEntry() {
        organizer();
        when(teams.findById(9L)).thenReturn(Optional.of(team()));
        when(globalMembers.findByTeamIdAndStatusOrderByIdAsc(9L,SportsTeamMember.Status.ACTIVE))
            .thenReturn(List.of());
        when(championships.findForClosing(24L,4L)).thenReturn(Optional.of(
            CommunityChampionship.builder().id(24L).community(community()).name("Liga Anual").build()));
        when(invitations.findByTeamIdAndCompetitionTypeAndCompetitionId(9L,
            SportsTeamCompetitionInvitation.CompetitionType.CHAMPIONSHIP,24L))
            .thenReturn(Optional.of(SportsTeamCompetitionInvitation.builder().communityId(4L)
                .status(SportsTeamCompetitionInvitation.Status.ACCEPTED).build()));
        when(championshipTeams.saveAndFlush(any())).thenAnswer(call->call.getArgument(0));
        var result=service.finalizeEntry(4L,"CHAMPIONSHIP",24L,9L,"organizer");
        assertEquals("CHAMPIONSHIP",result.competitionType());
        verify(championshipTeams).saveAndFlush(argThat(entry->
            entry.getGlobalTeam()!=null&&entry.getGlobalTeam().getId().equals(9L)));
    }

    @Test
    void finishedTournamentCannotRegisterTeamEvenAfterApproval() {
        organizer();
        when(teams.findById(9L)).thenReturn(Optional.of(team()));
        when(globalMembers.findByTeamIdAndStatusOrderByIdAsc(9L,SportsTeamMember.Status.ACTIVE))
            .thenReturn(List.of());
        when(tournaments.findByIdForUpdate(12L)).thenReturn(Optional.of(
            CommunityTournament.builder().id(12L).community(community())
                .entryType(TournamentEntryType.TEAM).status(TournamentStatus.COMPLETED).build()));
        assertThrows(ResponseStatusException.class,
            ()->service.finalizeEntry(4L,"TOURNAMENT",12L,9L,"organizer"));
        verifyNoInteractions(tournamentTeams);
    }
}
