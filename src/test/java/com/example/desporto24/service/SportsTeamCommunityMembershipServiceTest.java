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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamCommunityMembershipServiceTest {
    @Mock SportsTeamCommunityMembershipRepository memberships;
    @Mock SportsTeamRepository teams;
    @Mock CommunityRepository communities;
    @Mock CommunityMemberRepository communityMembers;
    @Mock ProfileRepository profiles;
    @InjectMocks SportsTeamCommunityMembershipService service;

    private static Profile profile(long id,String username) {
        var person=new Profile();person.setId(id);person.setUsername(username);return person;
    }

    @Test void teamOwnerMayRequestPublicCommunityWithoutJoiningItIndividually() {
        var captain=profile(1L,"captain");
        var team=SportsTeam.builder().id(5L).name("Convocados").owner(captain).build();
        var community=Community.builder().id(7L).name("Liga Lisboa")
            .visibility(CommunityVisibility.PUBLIC).build();
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));
        when(teams.findById(5L)).thenReturn(Optional.of(team));
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        when(memberships.findByTeamIdAndCommunityId(5L,7L)).thenReturn(Optional.empty());
        when(memberships.saveAndFlush(any())).thenAnswer(inv->inv.getArgument(0));

        var created=service.request(5L,7L,"captain");
        assertEquals("PENDING_COMMUNITY",created.status());
        verify(memberships).saveAndFlush(any());
        verifyNoInteractions(communityMembers);
    }

    @Test void inviteNeedsCommunityOrganizerAndAcceptanceNeedsTeamOwner() {
        var organizer=profile(2L,"organizer");
        var captain=profile(1L,"captain");
        var community=Community.builder().id(7L).name("Liga Lisboa")
            .visibility(CommunityVisibility.PUBLIC).build();
        var team=SportsTeam.builder().id(5L).name("Convocados").owner(captain).build();
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(organizer));
        when(communityMembers.findByCommunityIdAndProfileId(7L,2L)).thenReturn(Optional.of(
            CommunityMember.builder().community(community).profile(organizer)
                .role(CommunityRole.ADMIN).build()));
        when(teams.findById(5L)).thenReturn(Optional.of(team));
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        when(memberships.findByTeamIdAndCommunityId(5L,7L)).thenReturn(Optional.empty());
        when(memberships.saveAndFlush(any())).thenAnswer(inv->inv.getArgument(0));

        var invite=service.invite(7L,5L,"organizer");
        assertEquals("PENDING_TEAM",invite.status());
        verifyNoMoreInteractions(communityMembers);
    }

    @Test void unrelatedProfileCannotAcceptInvite() {
        var stranger=profile(9L,"stranger");
        var captain=profile(1L,"captain");
        var team=SportsTeam.builder().id(5L).owner(captain).build();
        var membership=SportsTeamCommunityMembership.builder().id(12L).team(team)
            .status(SportsTeamCommunityMembership.Status.PENDING_TEAM).build();
        when(profiles.findUserByUsername("stranger")).thenReturn(Optional.of(stranger));
        when(memberships.findById(12L)).thenReturn(Optional.of(membership));
        assertThrows(AccessDeniedException.class,()->service.respond(12L,true,"stranger"));
        verify(memberships,never()).saveAndFlush(any());
    }

    @Test void privateCommunityDoesNotAcceptUnsolicitedRequests() {
        var captain=profile(1L,"captain");
        var team=SportsTeam.builder().id(5L).owner(captain).build();
        var community=Community.builder().id(7L).visibility(CommunityVisibility.PRIVATE).build();
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));
        when(teams.findById(5L)).thenReturn(Optional.of(team));
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        assertThrows(AccessDeniedException.class,()->service.request(5L,7L,"captain"));
        verify(memberships,never()).saveAndFlush(any());
    }

    @Test void activeMembershipDoesNotCreateIndividualCommunityMember() {
        var admin=profile(2L,"organizer");
        var captain=profile(1L,"captain");
        var community=Community.builder().id(7L).build();
        var membership=SportsTeamCommunityMembership.builder().id(12L)
            .team(SportsTeam.builder().id(5L).owner(captain).build()).community(community)
            .status(SportsTeamCommunityMembership.Status.PENDING_COMMUNITY).build();
        when(profiles.findUserByUsername("organizer")).thenReturn(Optional.of(admin));
        when(memberships.findById(12L)).thenReturn(Optional.of(membership));
        when(communityMembers.findByCommunityIdAndProfileId(7L,2L)).thenReturn(Optional.of(
            CommunityMember.builder().community(community).profile(admin).role(CommunityRole.OWNER).build()));
        when(memberships.saveAndFlush(any())).thenAnswer(inv->inv.getArgument(0));

        assertEquals("ACTIVE",service.respond(12L,true,"organizer").status());
        verify(memberships).saveAndFlush(any());
        verify(communityMembers,never()).save(any());
    }
}
