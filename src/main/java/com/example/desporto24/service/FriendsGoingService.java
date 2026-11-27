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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Which of my friends signed up for these games. Only public games (anyone can already see who plays there),
 * or games I am in myself; only accepted friends.
 */
@Service
@RequiredArgsConstructor
public class FriendsGoingService {
    static final int MAX_SESSIONS = 100;
    static final int MAX_SHOWN = 5;

    public record FriendGoing(String username, String fullName, String profilePicUrl) {}
    public record SessionFriends(Long sessionId, int count, List<FriendGoing> friends) {}

    private final ProfileRepository profiles;
    private final FriendRepository friends;
    private final SessionParticipantRepository participants;

    @Transactional(readOnly = true)
    public List<SessionFriends> friendsGoing(String username, Collection<Long> sessionIds) {
        if (username == null || sessionIds == null || sessionIds.isEmpty()) return List.of();
        Set<Long> ids = new LinkedHashSet<>();
        for (Long id : sessionIds) {
            if (id != null && id > 0) ids.add(id);
            if (ids.size() >= MAX_SESSIONS) break;
        }
        if (ids.isEmpty()) return List.of();
        Profile me = profiles.findUserByUsernameIgnoreCase(username).orElse(null);
        if (me == null) return List.of();
        Set<Long> friendIds = new HashSet<>();
        for (Friend f : friends.findConnectionsForProfile(me.getId())) {
            Profile other = f.getProfile1() != null && me.getId().equals(f.getProfile1().getId()) ? f.getProfile2() : f.getProfile1();
            if (other != null && other.getId() != null && !other.getId().equals(me.getId())) friendIds.add(other.getId());
        }
        if (friendIds.isEmpty()) return List.of();
        Set<Long> mine = participants.findSessionIdsByProfileAndState(ids, me.getId(), ParticipantState.ACTIVE);
        Map<Long, List<FriendGoing>> bySession = new LinkedHashMap<>();
        for (SessionParticipant p : participants.findWithProfilesBySessionIdsAndProfileIds(ids, friendIds, ParticipantState.ACTIVE)) {
            Session s = p.getSession();
            if (!visible(s, me, mine)) continue;
            Profile f = p.getProfile();
            if (f == null || f.getUsername() == null) continue;
            bySession.computeIfAbsent(s.getId(), k -> new ArrayList<>())
                    .add(new FriendGoing(f.getUsername(), blankToNull(f.getFullName()), blankToNull(f.getProfilePicUrl())));
        }
        List<SessionFriends> out = new ArrayList<>();
        for (Long id : ids) {
            List<FriendGoing> list = bySession.get(id);
            if (list == null || list.isEmpty()) continue;
            out.add(new SessionFriends(id, list.size(), List.copyOf(list.subList(0, Math.min(MAX_SHOWN, list.size())))));
        }
        return out;
    }

    static boolean visible(Session s, Profile me, Set<Long> mine) {
        if (s == null) return false;
        if (mine.contains(s.getId())) return true;
        if (s.getProfile() != null && me.getId().equals(s.getProfile().getId())) return true;
        return !Boolean.TRUE.equals(s.getPrivateGame()) && s.getAudience() != SessionAudience.COMMUNITY_ONLY;
    }

    private static String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v;
    }
}
