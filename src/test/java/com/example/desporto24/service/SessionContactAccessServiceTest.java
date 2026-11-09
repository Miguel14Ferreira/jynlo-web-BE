package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionContactAccessServiceTest {
    @Mock ProfileRepository profiles;
    @Mock SessionRepository sessions;
    @Mock UserBlockService blocks;
    @InjectMocks SessionContactAccessService access;

    private Profile ana;
    private Profile bea;

    @BeforeEach
    void members() {
        ana = mock(Profile.class);
        bea = mock(Profile.class);
        lenient().when(ana.getId()).thenReturn(11L);
        lenient().when(bea.getId()).thenReturn(22L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(ana));
        lenient().when(profiles.findUserByUsernameIgnoreCase("bea")).thenReturn(Optional.of(bea));
    }

    @Test
    void allowsBothDirectionsWhenUsersShareAnUpcomingConfirmedSession() {
        when(sessions.existsSharedUpcomingContactSession(eq(11L), eq(22L), any(Date.class)))
                .thenReturn(true);
        when(sessions.existsSharedUpcomingContactSession(eq(22L), eq(11L), any(Date.class)))
                .thenReturn(true);
        assertTrue(access.mayContact("ana", "bea"));
        assertTrue(access.mayContact("bea", "ana"));
    }

    @Test
    void deniesUnknownPeerAndSelfContact() {
        assertFalse(access.mayContact("ana", "missing"));
        assertFalse(access.mayContact("ana", "ana"));
        verifyNoInteractions(sessions);
    }

    @Test
    void deniesBlockedContactEvenWithSharedSession() {
        when(blocks.isBlockedBetween(ana, bea)).thenReturn(true);
        assertFalse(access.mayContact("ana", "bea"));
        verifyNoInteractions(sessions);
    }

    @Test
    void deniesWhenNoActiveSharedSession() {
        assertFalse(access.mayContact("ana", "bea"));
        assertThrows(AccessDeniedException.class, () -> access.requireContact("ana", "bea"));
    }
}
