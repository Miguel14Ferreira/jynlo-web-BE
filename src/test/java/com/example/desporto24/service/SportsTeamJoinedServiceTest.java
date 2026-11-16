package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamJoinedServiceTest {
    @Mock SportsTeamRepository teams;
    @Mock SportsTeamMemberRepository members;
    @Mock ProfileRepository profiles;
    @InjectMocks SportsTeamMembershipService service;

    @Test
    void listsConfirmedMemberTeamsOnly() {
        var player=new Profile();
        player.setId(4L);player.setUsername("player");
        var owner=new Profile();
        owner.setId(5L);owner.setUsername("owner");
        var squad=SportsTeam.builder().id(20L).name("Squad").slug("squad")
                .owner(owner).chatEnabled(true).feedEnabled(true).build();
        var active=SportsTeamMember.builder().team(squad).profile(player)
                .status(SportsTeamMember.Status.ACTIVE).build();
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(player));
        when(members.findByProfileIdAndStatusOrderByIdDesc(4L,SportsTeamMember.Status.ACTIVE))
                .thenReturn(List.of(active));

        var result=service.joined("player");
        assertEquals(1,result.size());
        assertEquals(20L,result.get(0).id());
        assertEquals("Squad",result.get(0).name());
        assertTrue(result.get(0).chatEnabled());
        verify(members,never()).findByProfileIdAndStatusOrderByIdDesc(
                4L,SportsTeamMember.Status.INVITED);
    }

    @Test
    void ownerMembershipIsNotDuplicatedAsAPlayerTeam() {
        var owner=new Profile();
        owner.setId(5L);owner.setUsername("owner");
        var squad=SportsTeam.builder().id(20L).owner(owner).build();
        var active=SportsTeamMember.builder().team(squad).profile(owner)
                .status(SportsTeamMember.Status.ACTIVE).build();
        when(profiles.findUserByUsername("owner")).thenReturn(Optional.of(owner));
        when(members.findByProfileIdAndStatusOrderByIdDesc(5L,SportsTeamMember.Status.ACTIVE))
                .thenReturn(List.of(active));
        assertTrue(service.joined("owner").isEmpty());
    }

    @Test
    void rosterNeverListsTheCaptainTwiceEvenWithLegacyMembershipRow() {
        var owner = new Profile();
        owner.setId(5L);
        owner.setUsername("owner");
        var member = new Profile();
        member.setId(6L);
        member.setUsername("member");
        var team = SportsTeam.builder().id(20L).owner(owner).build();
        var ownerRow = SportsTeamMember.builder().team(team).profile(owner)
                .status(SportsTeamMember.Status.ACTIVE).build();
        var playerRow = SportsTeamMember.builder().team(team).profile(member)
                .status(SportsTeamMember.Status.ACTIVE).build();
        when(teams.findById(20L)).thenReturn(Optional.of(team));
        when(members.findByTeamIdAndStatusOrderByIdAsc(20L, SportsTeamMember.Status.ACTIVE))
                .thenReturn(List.of(ownerRow, playerRow));

        var roster = service.roster(20L);
        assertEquals(2, roster.size());
        assertEquals("OWNER", roster.get(0).role());
        assertEquals(5L, roster.get(0).profileId());
        assertEquals("PLAYER", roster.get(1).role());
        assertEquals(6L, roster.get(1).profileId());
    }
}
