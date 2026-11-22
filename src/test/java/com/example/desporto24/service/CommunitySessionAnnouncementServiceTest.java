package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.moderation.UserBlockRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CommunitySessionAnnouncementServiceTest {
    private final CommunityMemberRepository members = mock(CommunityMemberRepository.class);
    private final UserBlockRepository blocks = mock(UserBlockRepository.class);
    private final SessionNotificationService notifications = mock(SessionNotificationService.class);
    private final CommunitySessionAnnouncementService service =
            new CommunitySessionAnnouncementService(members, blocks, notifications);
    private final Instant now = Instant.parse("2026-11-22T12:00:00Z");

    private Profile profile(long id, String username) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        p.setEnabled(true);
        p.setNotLocked(true);
        return p;
    }

    private Community community(long id, String name) {
        Community c = new Community();
        c.setId(id);
        c.setName(name);
        return c;
    }

    private CommunityMember member(Community c, Profile p) {
        CommunityMember m = new CommunityMember();
        m.setCommunity(c);
        m.setProfile(p);
        return m;
    }

    private Session session() {
        Session s = new Session();
        s.setId(9L);
        s.setSport("Futsal");
        s.setGameDate(Date.from(now.plus(Duration.ofHours(30))));
        return s;
    }

    @Test
    void everyMemberButTheCreatorIsToldOnce() {
        Profile ana = profile(1, "ana"), rui = profile(2, "rui"), ze = profile(3, "ze");
        Community amigos = community(5, "Futsal Amigos"), liga = community(6, "Liga");
        when(members.findByCommunityIdOrderByJoinedAtAsc(5L)).thenReturn(List.of(member(amigos, ana), member(amigos, rui)));
        when(members.findByCommunityIdOrderByJoinedAtAsc(6L)).thenReturn(List.of(member(liga, rui), member(liga, ze)));

        int sent = service.announce(session(), ana, List.of(amigos, liga), false, now);

        assertEquals(2, sent);
        verify(notifications).notifyCommunitySession(any(), eq(rui), eq("Novo jogo em Futsal Amigos"),
                eq("ana marcou Futsal, amanhã às 18:00. Inscreve-te na Jynlo."));
        verify(notifications).notifyCommunitySession(any(), eq(ze), eq("Novo jogo em Liga"), anyString());
        verify(notifications, never()).notifyCommunitySession(any(), eq(ana), anyString(), anyString());
    }

    @Test
    void blockedSuspendedAndDisabledMembersAreSkipped() {
        Profile ana = profile(1, "ana"), blocked = profile(2, "blocked"),
                suspended = profile(3, "suspended"), disabled = profile(4, "disabled");
        suspended.setModerationSuspendedAt(now);
        disabled.setEnabled(false);
        Community c = community(5, "Futsal");
        when(members.findByCommunityIdOrderByJoinedAtAsc(5L)).thenReturn(List.of(
                member(c, blocked), member(c, suspended), member(c, disabled)));
        when(blocks.existsBetween(2L, 1L)).thenReturn(true);

        assertEquals(0, service.announce(session(), ana, List.of(c), false, now));
        verifyNoInteractions(notifications);
    }

    @Test
    void recurringGamesSayTheyRepeat() {
        String[] text = CommunitySessionAnnouncementService.text(session(), profile(1, "ana"),
                community(5, "Futsal"), true, now);
        assertTrue(text[1].contains("(jogo recorrente)"));
    }
}
