package com.example.desporto24.service.impl;

import com.example.desporto24.Chat.ChatRepository;
import com.example.desporto24.model.Chat;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.FriendRepository;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.service.SessionContactAccessService;
import com.example.desporto24.service.UserBlockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplSessionContactRaceTest {
    @Mock FriendRepository friends;
    @Mock ChatRepository chats;
    @Mock ProfileRepository profiles;
    @Mock NotificationsRepository notifications;
    @Mock ApplicationEventPublisher events;
    @Mock UserBlockService blocks;
    @Mock SessionContactAccessService sessionContacts;
    @InjectMocks ChatServiceImpl service;

    @Test
    void revokedSessionContactWhileWaitingForQuotaLockCannotSendMessage() {
        Profile sender = mock(Profile.class);
        Profile recipient = mock(Profile.class);
        when(sender.getId()).thenReturn(11L);
        when(recipient.getId()).thenReturn(22L);
        when(sender.getUsername()).thenReturn("ana");
        when(recipient.getUsername()).thenReturn("bea");
        when(profiles.findUserByUsername("ana")).thenReturn(Optional.of(sender));
        when(profiles.findUserByUsername("bea")).thenReturn(Optional.of(recipient));
        when(friends.findConnection(11L, 22L)).thenReturn(Optional.empty());
        when(profiles.lockForSessionChatQuota("ana")).thenReturn(Optional.of(sender));
        // The first check passes, but participation is revoked before the lock is acquired.
        when(sessionContacts.mayContact("ana", "bea")).thenReturn(true, false);
        ReflectionTestUtils.setField(service, "sessionContactAccess", sessionContacts);

        Chat message = mock(Chat.class);
        when(message.getUsername1()).thenReturn("ana");
        when(message.getUsername2()).thenReturn("bea");

        assertThrows(AccessDeniedException.class, () -> service.sendMessage(message));
        verify(sessionContacts, times(2)).mayContact("ana", "bea");
        verify(profiles).lockForSessionChatQuota("ana");
        verifyNoInteractions(chats, notifications, events);
    }
    @Test
    void revokedSessionContactCannotReadConversationHistory() {
        Profile sender = mock(Profile.class);
        Profile recipient = mock(Profile.class);
        when(sender.getId()).thenReturn(11L);
        when(recipient.getId()).thenReturn(22L);
        when(sender.getUsername()).thenReturn("ana");
        when(recipient.getUsername()).thenReturn("bea");
        when(profiles.findUserByUsername("ana")).thenReturn(Optional.of(sender));
        when(profiles.findUserByUsername("bea")).thenReturn(Optional.of(recipient));
        when(friends.findConnection(11L, 22L)).thenReturn(Optional.empty());
        when(sessionContacts.mayContact("ana", "bea")).thenReturn(false);
        ReflectionTestUtils.setField(service, "sessionContactAccess", sessionContacts);

        assertThrows(AccessDeniedException.class, () -> service.findChatMessages("ana", "bea"));
        assertThrows(AccessDeniedException.class, () -> service.findChatMessagesPage("ana", "bea", 0, 20));
        assertThrows(AccessDeniedException.class, () -> service.getChatInfo("ana", "bea"));
        verify(sessionContacts, times(3)).mayContact("ana", "bea");
        verifyNoInteractions(chats, notifications, events);
    }

    @Test
    void activeSessionContactMayReadHistoryWithoutFriendship() {
        Profile sender = mock(Profile.class);
        Profile recipient = mock(Profile.class);
        when(sender.getId()).thenReturn(11L);
        when(recipient.getId()).thenReturn(22L);
        when(sender.getUsername()).thenReturn("ana");
        when(recipient.getUsername()).thenReturn("bea");
        when(profiles.findUserByUsername("ana")).thenReturn(Optional.of(sender));
        when(profiles.findUserByUsername("bea")).thenReturn(Optional.of(recipient));
        when(friends.findConnection(11L, 22L)).thenReturn(Optional.empty());
        when(sessionContacts.mayContact("ana", "bea")).thenReturn(true);
        ReflectionTestUtils.setField(service, "sessionContactAccess", sessionContacts);

        service.findChatMessages("ana", "bea");
        verify(chats).findConversation("ana", "bea");
        org.junit.jupiter.api.Assertions.assertNull(service.getChatInfo("ana", "bea").getId());
        verify(sessionContacts, times(2)).mayContact("ana", "bea");
    }

}
