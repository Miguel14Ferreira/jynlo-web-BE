package com.example.desporto24.service;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionGuestRequest;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionGuestRequestRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionTeamsShareServiceTest {
    private final SessionRepository sessions = mock(SessionRepository.class);
    private final SessionParticipantRepository participants = mock(SessionParticipantRepository.class);
    private final SessionGuestRequestRepository guests = mock(SessionGuestRequestRepository.class);
    private final SessionTeamsShareService service = new SessionTeamsShareService(sessions, participants, guests);

    @Test
    void organiserSharesTheTeamsAndAnyoneWithTheLinkSeesNamesOnly() {
        Profile owner = new Profile(); owner.setId(1L); owner.setUsername("tmf14");
        Session session = new Session(); session.setId(9L); session.setProfile(owner); session.setSport("Futebol");
        session.setGameDate(new Date());
        when(sessions.findById(9L)).thenReturn(Optional.of(session));
        SessionParticipant me = SessionParticipant.builder().profile(owner).build();
        me.setTeamCode("B");
        when(participants.findBySessionIdAndStateOrderByJoinedAtAsc(9L, ParticipantState.ACTIVE)).thenReturn(List.of(me));
        SessionGuestRequest vasco = new SessionGuestRequest(); vasco.setId(3L); vasco.setDisplayName("Vasquinho"); vasco.setTeamCode("A");
        when(guests.findBySessionIdAndStatusOrderByCreatedAtAsc(9L, SessionGuestRequest.State.APPROVED)).thenReturn(List.of(vasco));

        assertThrows(AccessDeniedException.class, () -> service.link(9L, "outro"));
        var link = service.link(9L, "TMF14");
        assertEquals(43, link.token().length());
        assertEquals(link.token(), service.link(9L, "tmf14").token(), "the same link keeps working");

        var page = service.page(9L, link.token());
        assertEquals(List.of("Vasquinho"), page.teamA().stream().map(SessionTeamsShareService.Player::name).toList());
        assertEquals(List.of("@tmf14"), page.teamB().stream().map(SessionTeamsShareService.Player::name).toList());
        assertEquals("tmf14", page.teamB().get(0).username());
        assertNull(page.teamA().get(0).username(), "players without an account have no handle");
        assertThrows(ResponseStatusException.class, () -> service.page(9L, "errado"));
    }

    @Test
    void cannotShareBeforeBothTeamsAreConfirmed() {
        Profile owner = new Profile(); owner.setId(1L); owner.setUsername("tmf14");
        Session session = new Session(); session.setId(9L); session.setProfile(owner);
        when(sessions.findById(9L)).thenReturn(Optional.of(session));
        when(participants.findBySessionIdAndStateOrderByJoinedAtAsc(9L, ParticipantState.ACTIVE)).thenReturn(List.of());
        when(guests.findBySessionIdAndStatusOrderByCreatedAtAsc(9L, SessionGuestRequest.State.APPROVED)).thenReturn(List.of());
        assertThrows(ResponseStatusException.class, () -> service.link(9L, "tmf14"));
    }
}
