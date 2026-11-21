package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.enumeration.TournamentMatchStatus;
import com.example.desporto24.enumeration.TournamentParticipantStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.community.CommunityTournamentMatch;
import com.example.desporto24.model.community.CommunityTournamentParticipant;
import com.example.desporto24.model.dto.community.CommunityTournamentStandingRowResponse;
import com.example.desporto24.model.dto.community.CommunityTournamentStandingsResponse;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.community.CommunityTournamentMatchRepository;
import com.example.desporto24.repository.community.CommunityTournamentParticipantRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamMatchRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamPlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TournamentResultNotificationServiceTest {
    private final NotificationsRepository notifications = mock(NotificationsRepository.class);
    private final CommunityTournamentMatchRepository matches = mock(CommunityTournamentMatchRepository.class);
    private final CommunityTournamentParticipantRepository participants = mock(CommunityTournamentParticipantRepository.class);
    private final CommunityTournamentStandingsService standings = mock(CommunityTournamentStandingsService.class);
    private final TournamentResultNotificationService service = new TournamentResultNotificationService(
            notifications, matches, mock(CommunityTournamentTeamMatchRepository.class),
            mock(CommunityTournamentTeamPlayerRepository.class), participants, standings);
    private CommunityTournament tournament;

    private Profile profile(String username) {
        Profile p = new Profile();
        p.setUsername(username);
        return p;
    }

    private CommunityTournamentParticipant participant(String username) {
        CommunityTournamentParticipant p = new CommunityTournamentParticipant();
        p.setProfile(profile(username));
        return p;
    }

    private CommunityTournamentStandingRowResponse row(int position, String username, int played, long points) {
        return new CommunityTournamentStandingRowResponse(position, null, username, null,
                played, 0, 0, 0, 0, 0, 0, points);
    }

    @BeforeEach
    void setUp() {
        Community community = new Community();
        community.setId(5L);
        tournament = new CommunityTournament();
        tournament.setId(9L);
        tournament.setName("Liga de Padel");
        tournament.setCommunity(community);
        CommunityTournamentMatch match = new CommunityTournamentMatch();
        match.setId(30L);
        match.setTournament(tournament);
        match.setHomeParticipant(participant("rui"));
        match.setAwayParticipant(participant("ana"));
        match.setHomeScore(3);
        match.setAwayScore(1);
        match.setStatus(TournamentMatchStatus.COMPLETED);
        when(matches.findById(30L)).thenReturn(Optional.of(match));
        when(notifications.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void table(CommunityTournamentStandingRowResponse... rows) {
        when(standings.standings(5L, 9L, "org")).thenReturn(new CommunityTournamentStandingsResponse(
                9L, null, 6, 1, 3, 1, 0, List.of(rows)));
    }

    private List<Notifications> sent() {
        ArgumentCaptor<Notifications> captor = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications, atLeast(0)).save(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void bothPlayersGetTheScoreAndTheirPosition() {
        table(row(1, "rui", 1, 3), row(2, "bea", 0, 0), row(3, "ana", 1, 0));
        service.onResultRecorded(new TournamentResultNotificationService.ResultRecorded(
                5L, 9L, 30L, false, false, "org", "rui"));
        List<Notifications> sent = sent();
        assertEquals(2, sent.size(), "same leader: only the two players");
        Notifications rui = sent.get(0);
        assertEquals("rui", rui.getUsername());
        assertEquals(NotificationType.TOURNAMENT_RESULT, rui.getType());
        assertEquals("Resultado: rui 3–1 ana", rui.getSubject());
        assertTrue(rui.getMessage().contains("Estás em 1.º lugar com 3 pontos."), rui.getMessage());
        assertEquals(9L, rui.getCompetitionTournamentId());
        assertEquals(5L, rui.getCommunityId());
        assertTrue(sent.get(1).getMessage().contains("Estás em 3.º lugar com 0 pontos."));
    }

    @Test
    void aNewLeaderIsAnnouncedToEveryConfirmedPlayer() {
        table(row(1, "rui", 2, 6), row(2, "bea", 2, 3), row(3, "ana", 2, 0));
        when(participants.findByTournamentIdAndStatus(eq(9L), eq(TournamentParticipantStatus.CONFIRMED), any()))
                .thenReturn(new SliceImpl<>(List.of(participant("rui"), participant("bea"), participant("ana"))));
        service.onResultRecorded(new TournamentResultNotificationService.ResultRecorded(
                5L, 9L, 30L, false, true, "org", "bea"));
        List<Notifications> sent = sent();
        assertEquals(5, sent.size());
        assertTrue(sent.get(0).getSubject().startsWith("Resultado corrigido: "));
        var leader = sent.stream().filter(n -> n.getType() == NotificationType.TOURNAMENT_LEADER).toList();
        assertEquals(3, leader.size());
        assertEquals("Estás em 1.º lugar!", leader.get(0).getSubject());
        assertEquals("Novo líder: rui", leader.get(1).getSubject());
    }

    @Test
    void theOrganiserIsNotToldAboutTheirOwnResult() {
        table(row(1, "rui", 1, 3), row(2, "ana", 1, 0));
        service.onResultRecorded(new TournamentResultNotificationService.ResultRecorded(
                5L, 9L, 30L, false, false, "Rui", "rui"));
        // "Rui" recorded it (case-insensitive): only ana is told. Standings lookup as Rui fails → no position.
        List<Notifications> sent = sent();
        assertEquals(1, sent.size());
        assertEquals("ana", sent.getFirst().getUsername());
    }

    @Test
    void noTableMeansNoPositionAndNoLeader() {
        when(standings.standings(anyLong(), anyLong(), anyString())).thenThrow(new RuntimeException("knockout"));
        assertNull(service.leader(5L, 9L, "org"));
        service.onResultRecorded(new TournamentResultNotificationService.ResultRecorded(
                5L, 9L, 30L, false, false, "org", null));
        List<Notifications> sent = sent();
        assertEquals(2, sent.size());
        assertFalse(sent.getFirst().getMessage().contains("lugar"));
    }
}
