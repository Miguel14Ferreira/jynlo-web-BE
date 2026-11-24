package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TeamChatNotificationListenerTest {
    private final NotificationsRepository notifications = mock(NotificationsRepository.class);
    private final SportsTeamRepository teams = mock(SportsTeamRepository.class);
    private final SportsTeamMemberRepository members = mock(SportsTeamMemberRepository.class);
    private final NotificationPreferencesService preferences = mock(NotificationPreferencesService.class);
    private final ConversationNotificationMuteService mutes = mock(ConversationNotificationMuteService.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final TeamChatNotificationListener listener =
            new TeamChatNotificationListener(notifications, teams, members, preferences, mutes, events);
    private SportsTeam team;

    private static Profile profile(String username) {
        Profile p = new Profile();
        p.setUsername(username);
        p.setEnabled(true);
        return p;
    }

    private static SportsTeamMember member(String username) {
        SportsTeamMember m = mock(SportsTeamMember.class);
        when(m.getProfile()).thenReturn(profile(username));
        return m;
    }

    @BeforeEach
    void setUp() {
        team = mock(SportsTeam.class);
        when(team.getId()).thenReturn(8L);
        when(team.getName()).thenReturn("Leões FC");
        when(team.isChatEnabled()).thenReturn(true);
        Profile owner = profile("capitao");
        when(team.getOwner()).thenReturn(owner);
        when(teams.findById(8L)).thenReturn(Optional.of(team));
        List<SportsTeamMember> active = List.of(member("ana"), member("Rui"), member("capitao"));
        when(members.findByTeamIdAndStatusOrderByIdAsc(8L, SportsTeamMember.Status.ACTIVE)).thenReturn(active);
        when(preferences.allowsInApp(anyString(), any(Notifications.class))).thenReturn(true);
        when(notifications.save(any(Notifications.class))).thenAnswer(i -> { Notifications n = i.getArgument(0); n.setId(100L); return n; });
    }

    private TeamChatNotificationListener.TeamChatMessagePosted message(String sender) {
        return new TeamChatNotificationListener.TeamChatMessagePosted(8L, 3L, sender, "Treino às 20h");
    }

    @Test
    void alertsOwnerAndActiveMembersButNotTheSender() {
        listener.onMessage(message("ana"));
        ArgumentCaptor<Notifications> saved = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications, times(2)).save(saved.capture());
        assertEquals(List.of("capitao", "Rui"), saved.getAllValues().stream().map(Notifications::getUsername).toList());
        Notifications n = saved.getAllValues().get(0);
        assertEquals(NotificationType.CHAT_MESSAGE, n.getType());
        assertEquals(8L, n.getSportsTeamId());
        assertEquals("Nova mensagem em Leões FC", n.getSubject());
        assertEquals("ana: Treino às 20h", n.getMessage());
        assertNull(n.getRelatedUsername(), "must not open the sender's direct chat");
        ArgumentCaptor<Object> pushed = ArgumentCaptor.forClass(Object.class);
        verify(events, times(2)).publishEvent(pushed.capture());
        var push = (MobilePushDeliveryService.StoredNotification) pushed.getValue();
        assertEquals(8L, push.sportsTeamId());
    }

    @Test
    void mutedMembersExistingAlertsAndDisabledChatAreSkipped() {
        when(mutes.silenced("rui", "team", "8")).thenReturn(true);
        when(preferences.allowsInApp(eq("capitao"), any(Notifications.class))).thenReturn(false);
        listener.onMessage(message("ana"));
        verify(notifications, never()).save(any(Notifications.class));
        when(preferences.allowsInApp(eq("capitao"), any(Notifications.class))).thenReturn(true);

        when(mutes.silenced("rui", "team", "8")).thenReturn(false);
        when(team.isChatEnabled()).thenReturn(false);
        listener.onMessage(message("ana"));
        verify(notifications, never()).save(any(Notifications.class));
        verifyNoInteractions(events);
    }

    @Test
    void openingTheChatMarksItsAlertsRead() {
        Notifications n = new Notifications();
        n.setRead(false);
        when(notifications.findByUsernameIgnoreCaseAndSportsTeamIdAndReadFalse("ana", 8L)).thenReturn(List.of(n));
        listener.markRead("ana", 8L);
        assertTrue(n.isRead());
        verify(notifications).saveAll(List.of(n));
    }
}
