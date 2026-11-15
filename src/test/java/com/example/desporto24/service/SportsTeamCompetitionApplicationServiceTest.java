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
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamCompetitionApplicationServiceTest {
  @Mock SportsTeamRepository teams;
  @Mock SportsTeamCompetitionApplicationRepository applications;
  @Mock ProfileRepository profiles;
  @Mock CommunityMemberRepository members;
  @Mock CommunityTournamentRepository tournaments;
  @Mock CommunityChampionshipRepository championships;
  @InjectMocks SportsTeamCompetitionApplicationService service;

  private static Profile profile(long id,String name) {
    var profile=new Profile();profile.setId(id);profile.setUsername(name);return profile;
  }

  @Test void discoverOnlyUsesPublicOptInRepositoryQueries() {
    var community=Community.builder().id(7L).name("Lisboa").visibility(CommunityVisibility.PUBLIC).build();
    var tournament=CommunityTournament.builder().id(10L).community(community)
      .name("Liga Aberta").sport("Futebol").teamApplicationsOpen(true)
      .entryType(TournamentEntryType.TEAM).status(TournamentStatus.DRAFT).build();
    when(tournaments.discoverOpenTeams(eq("Liga"),eq("Futebol"),any(Pageable.class))).thenReturn(List.of(tournament));
    when(championships.discoverOpenTeams(eq("Liga"),eq("Futebol"),any(Pageable.class))).thenReturn(List.of());
    var result=service.discover("Liga","Futebol");
    assertEquals(1,result.size());assertEquals("Liga Aberta",result.get(0).name());
  }

  @Test void onlyOwnerCanApplyAndItDoesNotEnrollPlayers() {
    var owner=profile(1L,"captain");
    var community=Community.builder().id(7L).visibility(CommunityVisibility.PUBLIC).build();
    var tournament=CommunityTournament.builder().id(10L).community(community).entryType(TournamentEntryType.TEAM)
      .status(TournamentStatus.DRAFT).teamApplicationsOpen(true).build();
    var team=SportsTeam.builder().id(4L).owner(owner).name("Convocados").build();
    when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(owner));
    when(teams.findById(4L)).thenReturn(Optional.of(team));
    when(tournaments.findByIdForUpdate(10L)).thenReturn(Optional.of(tournament));
    when(applications.findByTeamIdAndCompetitionTypeAndCompetitionId(eq(4L),
      eq(SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT),eq(10L))).thenReturn(Optional.empty());
    when(applications.saveAndFlush(any())).thenAnswer(a->a.getArgument(0));

    var result=service.apply(7L,"TOURNAMENT",10L,4L,"captain");
    assertEquals("PENDING",result.status());
    verify(applications).saveAndFlush(any());
    verifyNoInteractions(members);
  }

  @Test void unauthorizedUserCannotApply() {
    var stranger=profile(2L,"stranger");
    var owner=profile(1L,"captain");
    when(profiles.findUserByUsername("stranger")).thenReturn(Optional.of(stranger));
    when(teams.findById(4L)).thenReturn(Optional.of(SportsTeam.builder().id(4L).owner(owner).build()));
    assertThrows(AccessDeniedException.class,()->service.apply(7L,"TOURNAMENT",10L,4L,"stranger"));
    verifyNoInteractions(tournaments,applications);
  }

  @Test void privateCompetitionCannotReceiveApplicationEvenWhenFlagIsSet() {
    var owner=profile(1L,"captain");
    var community=Community.builder().id(7L).visibility(CommunityVisibility.PRIVATE).build();
    var tournament=CommunityTournament.builder().id(10L).community(community).entryType(TournamentEntryType.TEAM)
      .status(TournamentStatus.DRAFT).teamApplicationsOpen(true).build();
    when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(owner));
    when(teams.findById(4L)).thenReturn(Optional.of(SportsTeam.builder().id(4L).owner(owner).build()));
    when(tournaments.findByIdForUpdate(10L)).thenReturn(Optional.of(tournament));
    assertThrows(ResponseStatusException.class,()->service.apply(7L,"TOURNAMENT",10L,4L,"captain"));
    verify(applications,never()).saveAndFlush(any());
  }

  @Test void approvalRequiresOrganizerAndDoesNotCreateRoster() {
    var organizer=profile(2L,"organizer");
    var owner=profile(1L,"captain");
    var community=Community.builder().id(7L).visibility(CommunityVisibility.PUBLIC).build();
    var tournament=CommunityTournament.builder().id(10L).community(community).entryType(TournamentEntryType.TEAM)
      .status(TournamentStatus.DRAFT).teamApplicationsOpen(true).build();
    var team=SportsTeam.builder().id(4L).owner(owner).build();
    var application=SportsTeamCompetitionApplication.builder().id(30L).team(team).communityId(7L)
      .competitionType(SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT).competitionId(10L)
      .status(SportsTeamCompetitionApplication.Status.PENDING).build();
    when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(organizer));
    when(members.findByCommunityIdAndProfileId(7L,2L)).thenReturn(Optional.of(
      CommunityMember.builder().community(community).profile(organizer).role(CommunityRole.ADMIN).build()));
    when(applications.findById(30L)).thenReturn(Optional.of(application));
    when(tournaments.findByIdForUpdate(10L)).thenReturn(Optional.of(tournament));
    when(applications.saveAndFlush(any())).thenAnswer(a->a.getArgument(0));
    var result=service.decide(30L,true,"organizer");
    assertEquals("APPROVED",result.status());
    verify(applications).saveAndFlush(any());
    verifyNoInteractions(championships,teams);
  }
}
