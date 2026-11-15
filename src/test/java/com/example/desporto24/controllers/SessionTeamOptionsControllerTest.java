package com.example.desporto24.controllers;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.community.CommunityRoundEntryRepository;
import com.example.desporto24.repository.community.CommunityStandingsArchiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionTeamOptionsControllerTest {
    private final SessionRepository sessions=mock(SessionRepository.class);
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final CommunityRoundEntryRepository rounds=mock(CommunityRoundEntryRepository.class);
    private final CommunityStandingsArchiveRepository archives=mock(CommunityStandingsArchiveRepository.class);
    private final SessionTeamOptionsController controller=
            new SessionTeamOptionsController(sessions,profiles,rounds,archives);

    private Session session(String sport){
        Session s=new Session();
        s.setId(42L);s.setSport(sport);s.setMaxParticipants(10);
        Community community=mock(Community.class);
        when(community.getId()).thenReturn(11L);
        s.setCommunity(community);
        Profile owner=mock(Profile.class);
        when(owner.getId()).thenReturn(7L);
        s.setProfile(owner);
        when(sessions.findById(42L)).thenReturn(Optional.of(s));
        when(profiles.findUserByUsername("owner")).thenReturn(Optional.of(owner));
        return s;
    }
    private Authentication authentication(){
        Authentication a=mock(Authentication.class);
        when(a.isAuthenticated()).thenReturn(true);
        when(a.getName()).thenReturn("owner");
        return a;
    }
    @Test void withoutStoredClassificationsOnlyRandomIsOffered(){
        session("Futebol");
        var options=controller.options(42L,authentication());
        assertTrue(options.randomAvailable());
        assertFalse(options.communityRankingAvailable());
        assertFalse(options.excelRankingAvailable());
    }
    @Test void storedCommunityAndIndividualExcelClassificationsEnableBothRankedOptions(){
        session("Futebol");
        when(rounds.existsByRoundCommunityId(11L)).thenReturn(true);
        when(archives.findFirstByCommunityIdAndEntryTypeOrderByCreatedAtDescIdDesc(11L,"INDIVIDUAL"))
            .thenReturn(Optional.of(mock(com.example.desporto24.model.community.CommunityStandingsArchive.class)));
        var options=controller.options(42L,authentication());
        assertTrue(options.randomAvailable());
        assertTrue(options.communityRankingAvailable());
        assertTrue(options.excelRankingAvailable());
    }
    @Test void individualSportsDoNotUnlockTwoTeams(){
        session("Golfe");
        var options=controller.options(42L,authentication());
        assertFalse(options.randomAvailable());
        assertFalse(options.communityRankingAvailable());
        assertFalse(options.excelRankingAvailable());
        verifyNoInteractions(rounds,archives);
    }
    @Test void differentAccountCannotInspectOrganizersRankingSources(){
        session("Futebol");
        when(profiles.findUserByUsername("owner")).thenReturn(Optional.of(mock(Profile.class)));
        assertThrows(AccessDeniedException.class,()->controller.options(42L,authentication()));
        verifyNoInteractions(rounds,archives);
    }
    @Test void anonymousRequestIsRejected(){
        assertThrows(AccessDeniedException.class,()->controller.options(42L,null));
        verifyNoInteractions(sessions,profiles,rounds,archives);
    }
}
