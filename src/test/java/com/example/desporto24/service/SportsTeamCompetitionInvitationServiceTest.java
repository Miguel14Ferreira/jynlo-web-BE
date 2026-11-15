package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentStatus;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamCompetitionInvitationServiceTest {
    @Mock private SportsTeamRepository teams;
    @Mock private SportsTeamCompetitionInvitationRepository invitations;
    @Mock private ProfileRepository profiles;
    @Mock private CommunityMemberRepository members;
    @Mock private CommunityTournamentRepository tournaments;
    @Mock private CommunityChampionshipRepository championships;
    @InjectMocks private SportsTeamCompetitionInvitationService service;

    private static Profile profile(long id,String username) {
        var profile = new Profile();
        profile.setId(id);
        profile.setUsername(username);
        return profile;
    }

    @Test
    void organizerCanInviteExistingTeamWithoutCreatingCompetitionRoster() {
        var organizer = profile(1L, "organizer");
        var owner = profile(2L, "captain");
        var community = Community.builder().id(4L).build();
        var competition = CommunityTournament.builder()
            .id(11L).community(community).entryType(TournamentEntryType.TEAM)
            .status(TournamentStatus.DRAFT).build();
        var team = SportsTeam.builder().id(10L).owner(owner).name("Os Convocados").build();
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(organizer));
        when(members.findByCommunityIdAndProfileId(4L,1L)).thenReturn(Optional.of(
            CommunityMember.builder().community(community).profile(organizer).role(CommunityRole.ADMIN).build()));
        when(tournaments.findById(11L)).thenReturn(Optional.of(competition));
        when(teams.findById(10L)).thenReturn(Optional.of(team));
        when(invitations.findByTeamIdAndCompetitionTypeAndCompetitionId(
            10L,SportsTeamCompetitionInvitation.CompetitionType.TOURNAMENT,11L)).thenReturn(Optional.empty());
        when(invitations.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        var result=service.invite(4L,"TOURNAMENT",11L,10L,"organizer");
        assertEquals("PENDING",result.status());
        assertEquals(10L,result.teamId());
        verify(invitations).saveAndFlush(any());
        verifyNoInteractions(championships);
    }

    @Test
    void ownerMayAcceptInvitationButItDoesNotEnrollPlayers() {
        var owner = profile(2L,"captain");
        var community = Community.builder().id(4L).build();
        var competition = CommunityTournament.builder().id(11L).community(community)
            .entryType(TournamentEntryType.TEAM).status(TournamentStatus.DRAFT).build();
        var team = SportsTeam.builder().id(10L).name("Os Convocados").owner(owner).build();
        var invitation = SportsTeamCompetitionInvitation.builder().id(15L).team(team)
            .communityId(4L).competitionId(11L)
            .competitionType(SportsTeamCompetitionInvitation.CompetitionType.TOURNAMENT)
            .status(SportsTeamCompetitionInvitation.Status.PENDING).createdAt(Instant.now()).build();
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(owner));
        when(invitations.findById(15L)).thenReturn(Optional.of(invitation));
        when(tournaments.findById(11L)).thenReturn(Optional.of(competition));
        when(invitations.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        var answer=service.respond(15L,"captain",true);
        assertEquals("ACCEPTED",answer.status());
        assertNotNull(invitation.getRespondedAt());
        verifyNoInteractions(members);
    }

    @Test
    void unrelatedUserCannotRespondToTeamInvitation() {
        var owner=profile(2L,"captain");
        var actor=profile(3L,"stranger");
        var team=SportsTeam.builder().id(10L).owner(owner).build();
        var invitation=SportsTeamCompetitionInvitation.builder().id(15L).team(team)
            .status(SportsTeamCompetitionInvitation.Status.PENDING).build();
        when(profiles.findUserByUsername("stranger")).thenReturn(Optional.of(actor));
        when(invitations.findById(15L)).thenReturn(Optional.of(invitation));
        assertThrows(AccessDeniedException.class,()->service.respond(15L,"stranger",true));
        verify(invitations,never()).saveAndFlush(any());
    }

    @Test
    void individualTournamentDoesNotAcceptTeamInvitation() {
        var organizer=profile(1L,"organizer");
        var community=Community.builder().id(4L).build();
        var individual=CommunityTournament.builder().id(11L).community(community)
            .entryType(TournamentEntryType.INDIVIDUAL).status(TournamentStatus.DRAFT).build();
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(organizer));
        when(members.findByCommunityIdAndProfileId(4L,1L)).thenReturn(Optional.of(
            CommunityMember.builder().community(community).profile(organizer).role(CommunityRole.ADMIN).build()));
        when(tournaments.findById(11L)).thenReturn(Optional.of(individual));
        assertThrows(ResponseStatusException.class,
            ()->service.invite(4L,"TOURNAMENT",11L,10L,"organizer"));
        verify(invitations,never()).saveAndFlush(any());
    }
}
