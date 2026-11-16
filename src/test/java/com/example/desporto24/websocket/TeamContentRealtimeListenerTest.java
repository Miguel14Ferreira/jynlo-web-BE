package com.example.desporto24.websocket;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.team.SportsTeamRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamContentRealtimeListenerTest {
    @Mock SportsTeamRepository teams;
    @Mock SportsTeamMemberRepository members;
    @Mock ChatRealtimeGateway gateway;
    @Mock org.springframework.transaction.PlatformTransactionManager transactionManager;
    @InjectMocks TeamContentRealtimeListener listener;

    @Test
    void onlyCurrentActiveTeamMembersReceiveContentFreeHint() {
        var team = mock(SportsTeam.class);
        var owner = mock(Profile.class);
        var member = mock(SportsTeamMember.class);
        var profile = mock(Profile.class);
        when(teams.findById(42L)).thenReturn(Optional.of(team));
        when(team.isChatEnabled()).thenReturn(true);
        when(team.getOwner()).thenReturn(owner);
        when(owner.getUsername()).thenReturn("owner");
        when(member.getProfile()).thenReturn(profile);
        when(profile.getUsername()).thenReturn("active");
        when(members.findByTeamIdAndStatusOrderByIdAsc(
                42L, SportsTeamMember.Status.ACTIVE)).thenReturn(List.of(member));

        listener.notifyCurrentMembers(42L, "chat");

        verify(gateway).publishTeamContentInvalidated(
                Set.of("owner", "active"), 42L, "chat");
        verify(members).findByTeamIdAndStatusOrderByIdAsc(
                42L, SportsTeamMember.Status.ACTIVE);
    }

    @Test
    void disabledTeamChatsNeverDeliverSocketHints() {
        var team = mock(SportsTeam.class);
        when(teams.findById(42L)).thenReturn(Optional.of(team));
        when(team.isChatEnabled()).thenReturn(false);
        listener.notifyCurrentMembers(42L, "chat");
        verifyNoInteractions(members, gateway);
    }

    @Test
    void deliveryHappensOnlyAfterDatabaseCommit() throws Exception {
        var annotation = TeamContentRealtimeListener.class
                .getMethod("onChange", TeamContentChangedEvent.class)
                .getAnnotation(TransactionalEventListener.class);
        assertNotNull(annotation);
        assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase());
    }
}
