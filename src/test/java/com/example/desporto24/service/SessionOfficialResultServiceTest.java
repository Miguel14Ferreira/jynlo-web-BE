package com.example.desporto24.service;

import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityRound;
import com.example.desporto24.model.community.CommunityRoundEntry;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.community.CommunityRoundEntryRepository;
import com.example.desporto24.model.dto.session.SessionDetailResponse;
import com.example.desporto24.repository.community.CommunityRoundRepository;
import com.example.desporto24.service.interfaces.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionOfficialResultServiceTest {
    @Mock SessionService sessions;
    @Mock CommunityRoundRepository rounds;
    @Mock CommunityRoundEntryRepository entries;
    @InjectMocks SessionOfficialResultService service;

    private final Long sessionId = 63L;
    private SessionDetailResponse detail(String creator, boolean joined) {
        SessionDetailResponse response = mock(SessionDetailResponse.class);
        when(response.creatorUsername()).thenReturn(creator);
        lenient().when(response.joined()).thenReturn(joined);
        return response;
    }
    private CommunityRound round(Integer a, Integer b) {
        return CommunityRound.builder().id(25L)
                .community(Community.builder().id(8L).build())
                .sourceSessionId(sessionId).teamScoreA(a).teamScoreB(b).build();
    }


    @Test void realRecordedScorersAreVisibleOnlyToConfirmedParticipants() {
        var authorized = detail("organizer", true);
        when(sessions.getById(sessionId, "player")).thenReturn(authorized);
        when(rounds.findBySourceSessionId(sessionId)).thenReturn(Optional.of(round(3, 1)));
        var ana = new Profile(); ana.setUsername("ana");
        var bea = new Profile(); bea.setUsername("bea");
        var scored = CommunityRoundEntry.builder().profile(ana).team("A")
                .goals(2).assists(1).build();
        var assisted = CommunityRoundEntry.builder().profile(bea).team("B")
                .goals(0).assists(1).build();
        var unknown = CommunityRoundEntry.builder().profile(ana).team("A")
                .goals(0).assists(0).build();
        when(entries.findByRoundIdOrderByIdAsc(25L))
                .thenReturn(java.util.List.of(scored, assisted, unknown));

        var result = service.result(sessionId, "player");
        assertEquals(2, result.contributions().size());
        assertEquals("ana", result.contributions().get(0).username());
        assertEquals(2, result.contributions().get(0).goals());
        assertEquals(1, result.contributions().get(1).assists());
        assertEquals(3, result.scoreA());
    }

    @Test void confirmedPlayerCanReadRealScoreWithoutAccessingPrivateRoster() {
        var authorized = detail("organizer", true);
        when(sessions.getById(sessionId, "player")).thenReturn(authorized);
        when(rounds.findBySourceSessionId(sessionId)).thenReturn(Optional.of(round(3, 1)));
        var result = service.result(sessionId, "player");
        assertEquals(3, result.scoreA());
        assertEquals(1, result.scoreB());
        assertEquals(8L, result.communityId());
        assertEquals(25L, result.roundId());
    }

    @Test void creatorCanReadTheResultWithoutJoiningOwnSession() {
        var authorized = detail("organizer", false);
        when(sessions.getById(sessionId, "organizer")).thenReturn(authorized);
        when(rounds.findBySourceSessionId(sessionId)).thenReturn(Optional.of(round(2, 2)));
        var result = service.result(sessionId, "organizer");
        assertEquals(2, result.scoreA());
        assertEquals(2, result.scoreB());
    }

    @Test void unjoinedViewerCannotReadPrivateScoreOrProbeRoundExistence() {
        var unauthorized = detail("organizer", false);
        when(sessions.getById(sessionId, "outsider")).thenReturn(unauthorized);
        assertThrows(ResponseStatusException.class,
                () -> service.result(sessionId, "outsider"));
        verifyNoInteractions(rounds, entries);
    }

    @Test void missingRoundDoesNotInventOfficialScores() {
        var authorized = detail("organizer", true);
        when(sessions.getById(sessionId, "player")).thenReturn(authorized);
        when(rounds.findBySourceSessionId(sessionId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> service.result(sessionId, "player"));
    }

    @Test void historicalWinnerOnlyRoundExplicitlyReturnsUnknownScore() {
        var authorized = detail("organizer", true);
        when(sessions.getById(sessionId, "player")).thenReturn(authorized);
        when(rounds.findBySourceSessionId(sessionId)).thenReturn(Optional.of(round(null, null)));
        var result = service.result(sessionId, "player");
        assertNull(result.scoreA());
        assertNull(result.scoreB());
    }

    @Test void missingIdentityFailsBeforeFetchingAnyPrivateDetails() {
        assertThrows(ResponseStatusException.class, () -> service.result(sessionId, null));
        assertThrows(ResponseStatusException.class, () -> service.result(sessionId, " "));
        verifyNoInteractions(sessions, rounds, entries);
    }
}
