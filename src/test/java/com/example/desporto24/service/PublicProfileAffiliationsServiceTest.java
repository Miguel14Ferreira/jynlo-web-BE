package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityVisibility;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.service.interfaces.ProfileService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicProfileAffiliationsServiceTest {
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final ProfileService profileService = mock(ProfileService.class);
    private final SportsTeamMemberRepository teams = mock(SportsTeamMemberRepository.class);
    private final CommunityMemberRepository communities = mock(CommunityMemberRepository.class);
    private final PublicProfileAffiliationsService service =
            new PublicProfileAffiliationsService(profiles, profileService, teams, communities);

    @Test
    void privateProfilesNeverExposeTheirMemberships() {
        Profile target = new Profile();
        target.setId(5L);target.setPrivateProfile(true);
        when(profiles.findUserByUsernameIgnoreCase("player")).thenReturn(Optional.of(target));
        var result = service.visible("viewer","player");
        assertTrue(result.teams().isEmpty());
        assertTrue(result.communities().isEmpty());
        verifyNoInteractions(teams, communities);
    }

    @Test
    void displaysOnlyAcceptedTeamMembershipsAndPublicCommunities() {
        Profile target = new Profile();
        target.setId(7L);target.setPrivateProfile(false);
        when(profiles.findUserByUsernameIgnoreCase("player")).thenReturn(Optional.of(target));
        SportsTeam team = SportsTeam.builder().id(3L).name("Public Team").sport("Futebol").build();
        SportsTeamMember active = SportsTeamMember.builder().team(team).profile(target)
                .status(SportsTeamMember.Status.ACTIVE).build();
        when(teams.findByProfileIdAndStatusOrderByIdDesc(7L, SportsTeamMember.Status.ACTIVE))
                .thenReturn(List.of(active));

        Community publicCommunity = Community.builder().id(8L).name("Public League")
                .sport("Padel").visibility(CommunityVisibility.PUBLIC).build();
        Community privateCommunity = Community.builder().id(9L).name("Secret League")
                .visibility(CommunityVisibility.PRIVATE).build();
        when(communities.findByProfileIdOrderByJoinedAtDesc(7L))
                .thenReturn(List.of(
                        CommunityMember.builder().community(privateCommunity).build(),
                        CommunityMember.builder().community(publicCommunity).build()));
        var result = service.visible("viewer","player");
        assertEquals(List.of(new PublicProfileAffiliationsService.TeamLink(3L,"Public Team","Futebol")),
                result.teams());
        assertEquals(List.of(new PublicProfileAffiliationsService.CommunityLink(8L,"Public League","Padel")),
                result.communities());
        verify(profileService).findPublicProfile("viewer","player");
    }

    @Test
    void blockedViewerCannotEnumerateRelationships() {
        doThrow(new org.springframework.security.access.AccessDeniedException("blocked"))
                .when(profileService).findPublicProfile("viewer","player");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.visible("viewer","player"));
        verifyNoInteractions(teams, communities);
    }
}
