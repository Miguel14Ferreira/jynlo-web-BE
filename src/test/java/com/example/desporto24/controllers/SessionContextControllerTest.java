package com.example.desporto24.controllers;

import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunitySeason;
import com.example.desporto24.model.session.SessionCommunityShare;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionCommunityShareRepository;
import com.example.desporto24.service.interfaces.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionContextControllerTest {
    private final SessionService service=mock(SessionService.class);
    private final SessionRepository sessions=mock(SessionRepository.class);
    private final SessionCommunityShareRepository shares=mock(SessionCommunityShareRepository.class);
    private final SessionContextController controller=new SessionContextController(service,sessions,shares);
    private final Authentication auth=mock(Authentication.class);

    @Test void showsDirectAndSharedCommunitiesWithoutDuplicatingAndMarksActualSeason() {
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("organizer");
        Session session=new Session();session.setId(18L);
        Community direct=new Community();direct.setId(3L);direct.setName("Pádel Amigos");
        Community another=new Community();another.setId(8L);another.setName("Futsal Lisboa");
        session.setCommunity(direct);
        CommunitySeason season=new CommunitySeason();season.setName("Época 2026/27");
        session.setSeasonCompetition(season);
        when(sessions.findById(18L)).thenReturn(Optional.of(session));
        when(shares.findBySession_IdOrderByIdAsc(18L)).thenReturn(List.of(
                new SessionCommunityShare(session,direct),
                new SessionCommunityShare(session,another)));
        var data=controller.context(18L,auth);
        assertEquals(List.of("Pádel Amigos","Futsal Lisboa"),
                data.communities().stream().map(SessionContextController.CommunityLabel::name).toList());
        assertTrue(data.seasonGame());
        assertEquals("Época 2026/27",data.seasonName());
        verify(service).getById(18L,"organizer");
    }

    @Test void sharedOnlyCommunityIsVisibleEvenWhenSessionHasNoDirectCommunity() {
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("member");
        Session session=new Session();session.setId(19L);
        Community group=new Community();group.setId(4L);group.setName("Amigos da Bola");
        when(sessions.findById(19L)).thenReturn(Optional.of(session));
        when(shares.findBySession_IdOrderByIdAsc(19L))
                .thenReturn(List.of(new SessionCommunityShare(session,group)));
        var data=controller.context(19L,auth);
        assertEquals("Amigos da Bola",data.communities().getFirst().name());
        assertFalse(data.seasonGame());
        assertNull(data.seasonName());
    }

    @Test void automaticallyCreatedSeasonIsVisibleBeforeAnyGameResults() {
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("member");
        Community group=new Community();group.setId(6L);
        group.setName("Colégio Pio");group.setRankingsEnabled(true);
        // Creation uses the game date's Europe/Lisbon year when the community
        // has no active season; it must remain visible before any results exist.
        CommunitySeason automatic=CommunitySeason.builder()
                .id(42L).community(group).name("Época 2027").active(true)
                .startsAt(java.time.Instant.now()).createdAt(java.time.Instant.now()).build();
        Session session=new Session();session.setId(21L);
        session.setCommunity(group);
        session.setSeasonCompetition(automatic);
        when(sessions.findById(21L)).thenReturn(Optional.of(session));
        when(shares.findBySession_IdOrderByIdAsc(21L)).thenReturn(List.of());

        var context=controller.context(21L,auth);
        assertEquals(List.of("Colégio Pio"),
                context.communities().stream().map(SessionContextController.CommunityLabel::name).toList());
        assertTrue(context.seasonGame());
        assertEquals("Época 2027",context.seasonName());
        verify(service).getById(21L,"member");
    }

    @Test void enabledCommunityStandingsDoNotMislabelAnOrdinarySessionAsSeasonGame() {
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("organizer");
        Session session=new Session();session.setId(20L);
        Community group=new Community();group.setId(9L);
        group.setName("Colégio Pio");group.setRankingsEnabled(true);
        session.setCommunity(group);
        // The community has active rankings, but this particular session
        // was not marked as a season game when created.
        assertNull(session.getSeasonCompetition());
        when(sessions.findById(20L)).thenReturn(Optional.of(session));
        when(shares.findBySession_IdOrderByIdAsc(20L)).thenReturn(List.of());

        var context=controller.context(20L,auth);
        assertEquals(List.of("Colégio Pio"),
                context.communities().stream().map(SessionContextController.CommunityLabel::name).toList());
        assertFalse(context.seasonGame());
        assertNull(context.seasonName());
        verify(service).getById(20L,"organizer");
    }

    @Test void historicalGameRetainsItsOriginalSeasonAfterCommunityAdvances() {
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("member");
        Community community=new Community();community.setId(6L);
        community.setName("Colégio Pio");community.setRankingsEnabled(true);
        CommunitySeason finished=CommunitySeason.builder()
                .id(40L).community(community).name("Época 2026")
                .active(false).build();
        CommunitySeason current=CommunitySeason.builder()
                .id(41L).community(community).name("Época 2027")
                .active(true).build();
        Session oldGame=new Session();oldGame.setId(30L);
        oldGame.setCommunity(community);oldGame.setSeasonCompetition(finished);
        Session newGame=new Session();newGame.setId(31L);
        newGame.setCommunity(community);newGame.setSeasonCompetition(current);
        when(sessions.findById(30L)).thenReturn(Optional.of(oldGame));
        when(sessions.findById(31L)).thenReturn(Optional.of(newGame));
        when(shares.findBySession_IdOrderByIdAsc(30L)).thenReturn(List.of());
        when(shares.findBySession_IdOrderByIdAsc(31L)).thenReturn(List.of());

        var historical=controller.context(30L,auth);
        var active=controller.context(31L,auth);
        assertTrue(historical.seasonGame());
        assertEquals("Época 2026",historical.seasonName());
        assertTrue(active.seasonGame());
        assertEquals("Época 2027",active.seasonName());
        verify(service).getById(30L,"member");
        verify(service).getById(31L,"member");
    }

    @Test void refusesUnauthorizedContextBeforeReadingSessionOrGroups() {
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("outsider");
        when(service.getById(12L,"outsider")).thenThrow(
                new org.springframework.security.access.AccessDeniedException("private"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                ()->controller.context(12L,auth));
        verifyNoInteractions(sessions,shares);
        assertEquals(401,assertThrows(ResponseStatusException.class,
                ()->controller.context(12L,null)).getStatusCode().value());
    }
}
