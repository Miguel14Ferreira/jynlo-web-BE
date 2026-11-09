package com.example.desporto24.service;

import com.example.desporto24.Chat.ChatRepository;
import com.example.desporto24.model.Friend;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.FriendRepository;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionContactChatAuthorizationTest {
    @Mock FriendRepository friendships;
    @Mock ChatRepository messages;
    @Mock ProfileRepository profiles;
    @Mock NotificationsRepository notifications;
    @Mock ApplicationEventPublisher events;
    @Mock UserBlockService blocks;
    @Mock SessionContactAccessService sessionContacts;
    @InjectMocks ChatServiceImpl chat;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(chat, "sessionContactAccess", sessionContacts);
        Profile ana = mock(Profile.class);
        Profile bea = mock(Profile.class);
        lenient().when(ana.getId()).thenReturn(1L);
        lenient().when(bea.getId()).thenReturn(2L);
        lenient().when(ana.getUsername()).thenReturn("ana");
        lenient().when(bea.getUsername()).thenReturn("bea");
        lenient().when(profiles.findUserByUsername("ana")).thenReturn(Optional.of(ana));
        lenient().when(profiles.findUserByUsername("bea")).thenReturn(Optional.of(bea));
    }

    @Test
    void permitsDirectConversationForConfirmedSessionContactsWithoutFriendship() {
        when(sessionContacts.mayContact("ana", "bea")).thenReturn(true);
        Friend access = chat.getChatInfo("ana", "bea");
        assertNotNull(access);
        assertFalse(access.isChatBlocked());
        verify(friendships).findConnection(1L, 2L);
        verify(blocks).ensureInteractionAllowed(any(Profile.class), any(Profile.class));
    }

    @Test
    void deniesUnknownUsersWithoutFriendshipOrSharedSession() {
        assertThrows(AccessDeniedException.class, () -> chat.getChatInfo("ana", "bea"));
    }

    @Test
    void keepsExistingFriendshipBehaviorWithoutSessionPermission() {
        Friend existing = new Friend();
        when(friendships.findConnection(1L, 2L)).thenReturn(Optional.of(existing));
        assertSame(existing, chat.getChatInfo("ana", "bea"));
        verifyNoInteractions(sessionContacts);
    }

    @Test
    void neverBypassesUserBlocksThroughSessionMembership() {
        doThrow(new AccessDeniedException("Blocked"))
                .when(blocks).ensureInteractionAllowed(any(Profile.class), any(Profile.class));
        assertThrows(AccessDeniedException.class, () -> chat.getChatInfo("ana", "bea"));
        verifyNoInteractions(sessionContacts);
    }
}
