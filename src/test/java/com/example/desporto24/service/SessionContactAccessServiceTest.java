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
        when(sessions.existsSharedUpcomingContactSession(eq(11L), eq(22L), any(Date.class), any(Date.class)))
                .thenReturn(true);
        when(sessions.existsSharedUpcomingContactSession(eq(22L), eq(11L), any(Date.class), any(Date.class)))
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
    void deniesAliasedUsernamesResolvingToTheSameProfileId() {
        // Distinct usernames must not bypass the self-contact guard if they
        // resolve to the same account (for example during a username migration).
        when(bea.getId()).thenReturn(11L);
        assertFalse(access.mayContact("ana", "bea"));
        assertThrows(AccessDeniedException.class, () -> access.requireContact("ana", "bea"));
        verifyNoInteractions(sessions, blocks);
    }

    @Test
    void deniesUnpersistedProfilesWithoutQueryingSharedSessions() {
        when(ana.getId()).thenReturn(null);
        assertFalse(access.mayContact("ana", "bea"));
        assertThrows(AccessDeniedException.class, () -> access.requireContact("ana", "bea"));
        verifyNoInteractions(sessions, blocks);
    }

    @Test
    void deniesUnpersistedRecipientWithoutQueryingSharedSessions() {
        when(bea.getId()).thenReturn(null);
        assertFalse(access.mayContact("ana", "bea"));
        verifyNoInteractions(sessions, blocks);
    }

    @Test
    void deniesBlockedContactEvenWithSharedSession() {
        when(blocks.isBlockedBetween(ana, bea)).thenReturn(true);
        assertFalse(access.mayContact("ana", "bea"));
        verifyNoInteractions(sessions);
    }

    @Test
    void deniesContactWhenEitherAccountIsMissing() {
        assertFalse(access.mayContact("missing", "bea"));
        assertFalse(access.mayContact("ana", "missing"));
        verifyNoInteractions(sessions, blocks);
    }

    @Test
    void deniesBlankOrSameAccountWithoutLookingUpSessions() {
        assertFalse(access.mayContact(null, "bea"));
        assertFalse(access.mayContact(" ", "bea"));
        assertFalse(access.mayContact("ana", "ANA"));
        verifyNoInteractions(sessions, blocks);
    }

    @Test
    void deniesReverseDirectionWhenEitherUserHasBlockedTheOther() {
        when(blocks.isBlockedBetween(bea, ana)).thenReturn(true);
        assertFalse(access.mayContact("bea", "ana"));
        verifyNoInteractions(sessions);
    }

    @Test
    void limitsSharedSessionContactToFourHoursAfterKickoff() {
        java.time.Instant before = java.time.Instant.now();
        when(sessions.existsSharedUpcomingContactSession(eq(11L), eq(22L), any(Date.class), any(Date.class)))
                .thenReturn(true);
        assertTrue(access.mayContact("ana", "bea"));
        java.time.Instant after = java.time.Instant.now();
        org.mockito.ArgumentCaptor<Date> cutoff = org.mockito.ArgumentCaptor.forClass(Date.class);
        verify(sessions).existsSharedUpcomingContactSession(eq(11L), eq(22L), cutoff.capture(), any(Date.class));
        // java.util.Date truncates Instant nanoseconds to milliseconds. Allow that precision loss.
        assertFalse(cutoff.getValue().toInstant().isBefore(before.minus(java.time.Duration.ofHours(4)).minusMillis(1)));
        assertFalse(cutoff.getValue().toInstant().isAfter(after.minus(java.time.Duration.ofHours(4))));
    }

    @Test
    void limitsContactToSevenDaysBeforeKickoff() {
        java.time.Instant before = java.time.Instant.now();
        when(sessions.existsSharedUpcomingContactSession(eq(11L), eq(22L),
                any(Date.class), any(Date.class))).thenReturn(true);
        assertTrue(access.mayContact("ana", "bea"));
        java.time.Instant after = java.time.Instant.now();
        org.mockito.ArgumentCaptor<Date> upper = org.mockito.ArgumentCaptor.forClass(Date.class);
        verify(sessions).existsSharedUpcomingContactSession(eq(11L), eq(22L),
                any(Date.class), upper.capture());
        assertFalse(upper.getValue().toInstant().isBefore(before.plus(java.time.Duration.ofDays(7)).minusMillis(1)));
        assertFalse(upper.getValue().toInstant().isAfter(after.plus(java.time.Duration.ofDays(7))));
    }

    @Test
    void repositoryRequiresActiveMembershipForBothPeersIncludingOrganizer() throws Exception {
        var method = SessionRepository.class.getMethod(
                "existsSharedUpcomingContactSession", Long.class, Long.class, Date.class, Date.class);
        var query = method.getAnnotation(org.springframework.data.jpa.repository.Query.class).value();
        assertTrue(query.contains("s.gameDate <= :contactUntil"));
        assertTrue(query.contains("p1.profile.id = :firstId"));
        assertTrue(query.contains("p2.profile.id = :secondId"));
        assertTrue(query.contains("p1.state = com.example.desporto24.enumeration.ParticipantState.ACTIVE"));
        assertTrue(query.contains("p2.state = com.example.desporto24.enumeration.ParticipantState.ACTIVE"));
        assertFalse(query.contains("s.profile.id = :firstId"),
                "Creating a session alone must not bypass confirmed membership");
        assertFalse(query.contains("s.profile.id = :secondId"),
                "Organizer access must be revoked when membership is no longer active");
    }

    @Test
    void repositoryRejectsEndedCancelledDisabledAndOutOfWindowSessions() throws Exception {
        var method = SessionRepository.class.getMethod(
                "existsSharedUpcomingContactSession", Long.class, Long.class, Date.class, Date.class);
        var query = method.getAnnotation(org.springframework.data.jpa.repository.Query.class).value();
        assertTrue(query.contains("s.gameDate > :now"),
                "A session more than four hours past kickoff must not grant contact");
        assertTrue(query.contains("s.gameDate <= :contactUntil"),
                "A session more than seven days away must not grant contact");
        assertTrue(query.contains("SessionStatus.OPEN"));
        assertTrue(query.contains("SessionStatus.FULL"));
        assertTrue(query.contains("s.status in"),
                "Ended or cancelled sessions must not grant contact");
        assertTrue(query.contains("s.enabled is null or s.enabled = true"),
                "Disabled sessions must not grant contact");
    }

    @Test
    void revokesContactImmediatelyWhenSharedMembershipIsNoLongerEligible() {
        // Permission is never cached or turned into friendship: each operation
        // must query the current confirmed participation state again.
        when(sessions.existsSharedUpcomingContactSession(eq(11L), eq(22L),
                any(Date.class), any(Date.class))).thenReturn(true, false);
        assertTrue(access.mayContact("ana", "bea"));
        assertThrows(AccessDeniedException.class, () -> access.requireContact("ana", "bea"));
        verify(sessions, times(2)).existsSharedUpcomingContactSession(eq(11L), eq(22L),
                any(Date.class), any(Date.class));
    }

    @Test
    void revokesSessionContactImmediatelyAfterEitherParticipantBlocksTheOther() {
        when(sessions.existsSharedUpcomingContactSession(eq(11L), eq(22L),
                any(Date.class), any(Date.class))).thenReturn(true);
        when(blocks.isBlockedBetween(ana, bea)).thenReturn(false, true);

        assertTrue(access.mayContact("ana", "bea"));
        assertThrows(AccessDeniedException.class, () -> access.requireContact("ana", "bea"));

        verify(blocks, times(2)).isBlockedBetween(ana, bea);
        verify(sessions, times(1)).existsSharedUpcomingContactSession(eq(11L), eq(22L),
                any(Date.class), any(Date.class));
    }

    @Test
    void deniesWhenNoActiveSharedSession() {
        assertFalse(access.mayContact("ana", "bea"));
        assertThrows(AccessDeniedException.class, () -> access.requireContact("ana", "bea"));
    }
}
