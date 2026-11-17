package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.*;
import com.example.desporto24.service.interfaces.SessionService;
import com.example.desporto24.model.dto.session.SessionDetailResponse;
import com.example.desporto24.enumeration.SessionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class SportsTeamChallengeServiceTest {
    private final SportsTeamRepository teams=mock(SportsTeamRepository.class);
    private final SportsTeamChallengeRepository challenges=mock(SportsTeamChallengeRepository.class);
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final SessionService sessions=mock(SessionService.class);
    private final SportsTeamChallengeService service=new SportsTeamChallengeService(teams,challenges,profiles,sessions);
    private Profile a,b,outsider;
    private SportsTeam first,second;
    private SportsTeamChallenge invite;
    @BeforeEach void setup(){
        a=new Profile();a.setId(1L);a.setUsername("alice");
        b=new Profile();b.setId(2L);b.setUsername("bob");
        outsider=new Profile();outsider.setId(3L);outsider.setUsername("outsider");
        first=SportsTeam.builder().id(11L).name("Azuis").sport("Futebol").owner(a).build();
        second=SportsTeam.builder().id(12L).name("Verdes").sport("Futebol").owner(b).build();
        invite=SportsTeamChallenge.builder().id(90L).challenger(first).challenged(second)
                .status(SportsTeamChallenge.Status.PENDING).createdBy(a).createdAt(java.time.Instant.now()).build();
        when(profiles.findUserByUsername("alice")).thenReturn(Optional.of(a));
        when(profiles.findUserByUsername("bob")).thenReturn(Optional.of(b));
        when(profiles.findUserByUsername("outsider")).thenReturn(Optional.of(outsider));
        when(teams.findById(11L)).thenReturn(Optional.of(first));
        when(teams.findById(12L)).thenReturn(Optional.of(second));
        lenient().when(teams.findForDeletion(11L)).thenReturn(Optional.of(first));
        lenient().when(teams.findForDeletion(12L)).thenReturn(Optional.of(second));
        when(challenges.findWithTeams(90L)).thenReturn(Optional.of(invite));
        when(challenges.saveAndFlush(any())).thenAnswer(x->x.getArgument(0));
    }
    @Test void onlyOwnerCanInviteAndSelfChallengeIsRejected(){
        assertThrows(AccessDeniedException.class,()->service.create(11L,12L,"Bom jogo?","outsider"));
        assertEquals(400,assertThrows(ResponseStatusException.class,
                ()->service.create(11L,11L,null,"alice")).getStatusCode().value());
        verify(challenges,never()).saveAndFlush(any());
    }
    @Test void opposingOwnerCanAcceptOnlyOnceAndNobodyJoinsAutomatically(){
        assertThrows(AccessDeniedException.class,()->service.respond(90L,true,"alice"));
        var accepted=service.respond(90L,true,"bob");
        assertEquals("ACCEPTED",accepted.status());
        assertNull(accepted.sessionId());
        assertEquals(409,assertThrows(ResponseStatusException.class,
                ()->service.respond(90L,true,"bob")).getStatusCode().value());
        verifyNoInteractions(sessions);
    }
    @Test void ownerCanCancelPendingInvitationButNotAnAcceptedOne(){
        assertEquals("CANCELLED",service.cancel(90L,"alice").status());
        assertEquals(409,assertThrows(ResponseStatusException.class,
                ()->service.cancel(90L,"alice")).getStatusCode().value());
    }
    @Test void repeatedPairAndDifferentSportsCannotGenerateDuplicateInvitations(){
        when(challenges.hasActivePair(eq(11L),eq(12L),anyList())).thenReturn(true);
        assertEquals(409,assertThrows(ResponseStatusException.class,
                ()->service.create(11L,12L,null,"alice")).getStatusCode().value());
        reset(challenges);
        second.setSport("Padel");
        assertEquals(409,assertThrows(ResponseStatusException.class,
                ()->service.create(11L,12L,null,"alice")).getStatusCode().value());
        verify(challenges,never()).saveAndFlush(any());
    }
    @Test void linkingRequiresAcceptanceAndVerifiedPublicCreatorSession(){
        assertEquals(409,assertThrows(ResponseStatusException.class,
                ()->service.linkSession(90L,75L,"alice")).getStatusCode().value());
        invite.setStatus(SportsTeamChallenge.Status.ACCEPTED);
        assertThrows(AccessDeniedException.class,
                ()->service.linkSession(90L,75L,"outsider"));
        var detail=mock(SessionDetailResponse.class);
        when(sessions.getById(75L,"alice")).thenReturn(detail);
        when(detail.canSharePublicly()).thenReturn(false);
        assertEquals(403,assertThrows(ResponseStatusException.class,
                ()->service.linkSession(90L,75L,"alice")).getStatusCode().value());
        verify(challenges,never()).saveAndFlush(any());
    }
    @Test void successfullyLinksOnlyOneMatchingPublicSessionWithoutEnrollingPlayers(){
        invite.setStatus(SportsTeamChallenge.Status.ACCEPTED);
        var detail=mock(SessionDetailResponse.class);
        when(sessions.getById(75L,"alice")).thenReturn(detail);
        when(detail.canSharePublicly()).thenReturn(true);
        when(detail.creatorUsername()).thenReturn("alice");
        when(detail.sport()).thenReturn("Futebol");
        when(detail.status()).thenReturn(SessionStatus.OPEN);
        var linked=service.linkSession(90L,75L,"alice");
        assertEquals(75L,linked.sessionId());
        assertEquals(409,assertThrows(ResponseStatusException.class,
                ()->service.linkSession(90L,76L,"alice")).getStatusCode().value());
        verify(sessions,times(1)).getById(75L,"alice");
    }
    @Test void challengeInboxOnlyVisibleToTheTeamCaptain(){
        assertThrows(AccessDeniedException.class,()->service.forTeam(11L,"outsider"));
        when(challenges.forTeam(eq(11L),any())).thenReturn(List.of(invite));
        var mine=service.forTeam(11L,"alice");
        assertEquals(1,mine.size());
        assertEquals(90L,mine.get(0).id());
        verify(challenges,times(1)).forTeam(eq(11L),any());
    }
    @Test void suspendedOwnerCannotIssueOrAcceptChallenges(){
        a.setModerationSuspendedAt(java.time.Instant.now());
        assertThrows(AccessDeniedException.class,()->service.create(11L,12L,"","alice"));
        b.setEnabled(false);
        assertThrows(AccessDeniedException.class,()->service.respond(90L,true,"bob"));
        verify(challenges,never()).saveAndFlush(any());
    }
}
