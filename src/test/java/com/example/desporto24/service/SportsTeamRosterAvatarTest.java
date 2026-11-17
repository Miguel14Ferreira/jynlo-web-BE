package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SportsTeamRosterAvatarTest {
    private final SportsTeamRepository teams=mock(SportsTeamRepository.class);
    private final SportsTeamMemberRepository members=mock(SportsTeamMemberRepository.class);
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final SportsTeamMembershipService service=
            new SportsTeamMembershipService(teams,members,profiles);

    private Profile profile(long id,String username,String photo,boolean privateProfile) {
        var p=new Profile();
        p.setId(id);
        p.setUsername(username);
        p.setProfilePicUrl(photo);
        p.setPrivateProfile(privateProfile);
        return p;
    }

    @Test
    void exposesOnlyPublicMemberAvatarsInThePublicTeamRoster() {
        var owner=profile(1,"captain","https://example.test/captain.png",false);
        var player=profile(2,"player","https://example.test/player.png",true);
        var club=SportsTeam.builder().id(42L).owner(owner).build();
        when(teams.findById(42L)).thenReturn(Optional.of(club));
        when(members.findByTeamIdAndStatusOrderByIdAsc(
                42L,SportsTeamMember.Status.ACTIVE)).thenReturn(List.of(
                SportsTeamMember.builder().team(club).profile(player)
                        .status(SportsTeamMember.Status.ACTIVE).build()));
        var roster=service.roster(42L);
        assertEquals(2,roster.size());
        assertEquals("OWNER",roster.get(0).role());
        assertEquals("https://example.test/captain.png",roster.get(0).profilePicUrl());
        assertEquals("PLAYER",roster.get(1).role());
        assertNull(roster.get(1).profilePicUrl(),
                "The public roster must not disclose private profile photos");
    }

    @Test
    void fallsBackGracefullyWhenThePublicPlayerHasNoPhoto() {
        var owner=profile(1,"captain",null,false);
        var club=SportsTeam.builder().id(42L).owner(owner).build();
        when(teams.findById(42L)).thenReturn(Optional.of(club));
        var roster=service.roster(42L);
        assertEquals(1,roster.size());
        assertNull(roster.get(0).profilePicUrl());
    }
}
