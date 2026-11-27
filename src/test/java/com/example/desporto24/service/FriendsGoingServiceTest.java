package com.example.desporto24.service;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.enumeration.SessionAudience;
import com.example.desporto24.model.Friend;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.repository.FriendRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FriendsGoingServiceTest {
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final FriendRepository friends = mock(FriendRepository.class);
    private final SessionParticipantRepository participants = mock(SessionParticipantRepository.class);
    private final FriendsGoingService service = new FriendsGoingService(profiles, friends, participants);

    private static Profile profile(long id, String username) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        return p;
    }

    private static Session session(long id, boolean privateGame) {
        Session s = new Session();
        s.setId(id);
        s.setPrivateGame(privateGame);
        s.setAudience(SessionAudience.PUBLIC);
        return s;
    }

    private static SessionParticipant place(Session s, Profile p) {
        SessionParticipant sp = new SessionParticipant();
        sp.setSession(s);
        sp.setProfile(p);
        sp.setState(ParticipantState.ACTIVE);
        return sp;
    }

    @Test
    void showsFriendsInPublicGamesButNotInPrivateOnesIAmNotIn() {
        Profile me = profile(1, "miguel");
        Profile joao = profile(2, "joao");
        when(profiles.findUserByUsernameIgnoreCase("miguel")).thenReturn(Optional.of(me));
        Friend f = new Friend();
        f.setProfile1(joao);
        f.setProfile2(me);
        when(friends.findConnectionsForProfile(1L)).thenReturn(List.of(f));
        Session open = session(10, false);
        Session closed = session(11, true);
        when(participants.findSessionIdsByProfileAndState(any(), eq(1L), eq(ParticipantState.ACTIVE))).thenReturn(Set.of());
        when(participants.findWithProfilesBySessionIdsAndProfileIds(any(), eq(Set.of(2L)), eq(ParticipantState.ACTIVE)))
                .thenReturn(List.of(place(open, joao), place(closed, joao)));

        List<FriendsGoingService.SessionFriends> result = service.friendsGoing("miguel", List.of(10L, 11L));
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).sessionId());
        assertEquals("joao", result.get(0).friends().get(0).username());
    }

    @Test
    void nothingWithoutFriendsOrSignIn() {
        assertTrue(service.friendsGoing(null, List.of(1L)).isEmpty());
        when(profiles.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(profile(3, "ana")));
        when(friends.findConnectionsForProfile(3L)).thenReturn(List.of());
        assertTrue(service.friendsGoing("ana", List.of(1L)).isEmpty());
        verify(participants, never()).findWithProfilesBySessionIdsAndProfileIds(any(), any(), any());
    }
}
