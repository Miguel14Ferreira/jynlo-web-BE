package com.example.desporto24.service;

import com.auth0.jwt.JWT;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.websocket.ChatRealtimeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectCallServiceTest {
    @Mock InboxSummaryService inbox;
    @Mock ProfileRepository profiles;
    @Mock ChatRealtimeGateway gateway;
    @InjectMocks DirectCallService calls;

    @BeforeEach
    void configure() {
        ReflectionTestUtils.setField(calls, "serverUrl", "wss://example.livekit.cloud");
        ReflectionTestUtils.setField(calls, "apiKey", "jynlo-api-key");
        ReflectionTestUtils.setField(calls, "apiSecret", "only-for-tests-long-enough-livekit-secret");
        Profile caller = mock(Profile.class);
        Profile recipient = mock(Profile.class);
        lenient().when(caller.getId()).thenReturn(12L);
        lenient().when(recipient.getId()).thenReturn(13L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(caller));
        lenient().when(profiles.findUserByUsernameIgnoreCase("bea")).thenReturn(Optional.of(recipient));
    }

    @Test
    void createsScopedTokensForExactlyTwoAuthorizedMembers() {
        var outgoing = calls.start("ana", "bea", "video");
        verify(inbox).requireConversationAccess("ana", "direct", "bea");
        assertEquals("bea", outgoing.peer());
        assertEquals("video", outgoing.media());
        assertEquals("wss://example.livekit.cloud", outgoing.serverUrl());
        var first = JWT.decode(outgoing.token());
        assertFalse(first.getExpiresAt().toInstant().isAfter(outgoing.expiresAt()),
                "Ringing token must not outlive the invitation");
        assertEquals("jynlo-api-key", first.getIssuer());
        assertEquals("jynlo-user-12", first.getSubject());
        assertEquals(true, first.getClaim("video").asMap().get("roomJoin"));
        assertEquals(false, first.getClaim("video").asMap().get("canPublishData"));
        assertTrue(first.getClaim("video").asMap().get("room").toString().startsWith("jynlo-"));
        verify(gateway).publishDirectCallEvent(eq("ana"), eq("bea"), eq(outgoing.callId().toString()),
                eq("video"), eq("RINGING"), eq("ana"), any());

        var accepted = calls.accept(outgoing.callId(), "bea");
        verify(inbox).requireConversationAccess("bea", "direct", "ana");
        assertEquals(outgoing.callId(), accepted.callId());
        assertEquals("ana", accepted.peer());
        assertEquals("jynlo-user-13", JWT.decode(accepted.token()).getSubject());
        assertEquals(first.getClaim("video").asMap().get("room"),
                JWT.decode(accepted.token()).getClaim("video").asMap().get("room"));

        calls.end(outgoing.callId(), "ana");
        verify(gateway).publishDirectCallEvent(eq("ana"), eq("bea"), eq(outgoing.callId().toString()),
                eq("video"), eq("ENDED"), eq("ana"), any());
    }

    @Test
    void missedIncomingInvitationCanBeRecoveredWithoutExposingCredentials() {
        var outgoing = calls.start("ana", "bea", "video");
        assertNull(calls.pendingFor("ana"), "The caller cannot fetch an incoming invitation");
        assertNull(calls.pendingFor("mallory"), "Other accounts cannot see the invitation");

        var recovered = calls.pendingFor("bea");
        assertNotNull(recovered);
        assertEquals(outgoing.callId(), recovered.callId());
        assertEquals("ana", recovered.caller());
        assertEquals("bea", recovered.peer());
        assertEquals("video", recovered.media());
        assertEquals(outgoing.expiresAt(), recovered.expiresAt());
        verify(inbox).requireConversationAccess("bea", "direct", "ana");

        calls.accept(outgoing.callId(), "bea");
        assertNull(calls.pendingFor("bea"), "Accepted calls must not ring again");

        calls.end(outgoing.callId(), "ana");
        assertNull(calls.pendingFor("bea"));
    }

    @Test
    void pendingRecoveryRevalidatesChatAccessAndCannotBypassBlocks() {
        calls.start("ana", "bea", "voice");
        doThrow(new org.springframework.security.access.AccessDeniedException("Blocked"))
                .when(inbox).requireConversationAccess("bea", "direct", "ana");
        assertNull(calls.pendingFor("bea"), "Revoked invitations must not be recovered");
        assertNull(calls.pendingFor("mallory"));
    }

    @Test
    void pendingRecoveryIgnoresHttpForbiddenAfterSessionContactExpires() {
        var outgoing = calls.start("ana", "bea", "voice");
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Session contact expired"))
                .when(inbox).requireConversationAccess("bea", "direct", "ana");

        assertNull(calls.pendingFor("bea"),
                "A denied invitation must not leak an error or a recoverable ring");
        verify(gateway, never()).publishDirectCallEvent(eq("ana"), eq("bea"),
                eq(outgoing.callId().toString()), eq("voice"), eq("ACTIVE"), anyString(), any());
    }

    @Test
    void guestRecoveryIgnoresHttpForbiddenAfterInviterLosesContact() {
        var outgoing = calls.start("ana", "bea", "voice");
        calls.accept(outgoing.callId(), "bea");
        calls.invite(outgoing.callId(), "ana", "carlos");
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Session contact expired"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        assertNull(calls.pendingFor("carlos"));
        verify(inbox, never()).requireConversationAccess("carlos", "direct", "ana");
    }

    @Test
    void simultaneousIncomingRingsAreRejectedEvenIfEarlierPermissionIsRevoked() {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));

        calls.start("ana", "bea", "voice");
        doThrow(new org.springframework.security.access.AccessDeniedException("Blocked"))
                .when(inbox).requireConversationAccess("bea", "direct", "ana");

        assertNull(calls.pendingFor("bea"), "Revoked ring must not be exposed");
        var conflict = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> calls.start("carlos", "bea", "voice"));
        assertEquals(org.springframework.http.HttpStatus.CONFLICT, conflict.getStatusCode(),
                "A still-ringing call reserves its recipient until it expires or ends");
    }

    @Test
    void callerLosingSessionAccessBeforeAnswerCannotActivateCall() {
        var outgoing = calls.start("ana", "bea", "voice");
        doThrow(new org.springframework.security.access.AccessDeniedException("Caller no longer confirmed"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.accept(outgoing.callId(), "bea"));
        verify(gateway, never()).publishDirectCallEvent(eq("ana"), eq("bea"),
                eq(outgoing.callId().toString()), eq("voice"), eq("ACTIVE"), anyString(), any());
        assertNull(calls.pendingFor("bea"), "A revoked caller must not keep a recoverable ring");
    }

    @Test
    void revokedSessionPermissionPreventsAcceptingAnAlreadyRingingCall() {
        var outgoing = calls.start("ana", "bea", "voice");
        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Session participation revoked"))
                .when(inbox).requireConversationAccess("bea", "direct", "ana");

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.accept(outgoing.callId(), "bea"));
        verify(gateway, never()).publishDirectCallEvent(eq("ana"), eq("bea"),
                eq(outgoing.callId().toString()), eq("voice"), eq("ACTIVE"), anyString(), any());
        assertNull(calls.pendingFor("bea"), "Revoked invitations must not be recovered");
    }

    @Test
    void revokedSessionPermissionPreventsAcceptingGuestInvitation() {
        var outgoing = calls.start("ana", "bea", "voice");
        calls.accept(outgoing.callId(), "bea");
        calls.invite(outgoing.callId(), "ana", "carlos");
        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Session participation revoked"))
                .when(inbox).requireConversationAccess("carlos", "direct", "ana");

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.accept(outgoing.callId(), "carlos"));
        assertEquals(2, calls.members(outgoing.callId(), "ana").participants().size());
    }

    @Test
    void revokedOriginalPairBlocksSecondGenerationInvitesAndRosterAccess() {
        Profile carlos = mock(Profile.class);
        Profile dora = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(dora.getId()).thenReturn(15L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));
        lenient().when(profiles.findUserByUsernameIgnoreCase("dora"))
                .thenReturn(Optional.of(dora));

        var call = calls.start("ana", "bea", "voice");
        calls.accept(call.callId(), "bea");
        calls.invite(call.callId(), "ana", "carlos");
        calls.accept(call.callId(), "carlos");
        calls.invite(call.callId(), "carlos", "dora");

        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Original contact revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        assertNull(calls.pendingFor("dora"), "Revoked original pair invalidates guest invitation");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.accept(call.callId(), "dora"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.members(call.callId(), "carlos"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.invite(call.callId(), "carlos", "other"));
        verify(gateway, never()).publishDirectCallEvent(eq("carlos"), eq("other"),
                eq(call.callId().toString()), eq("voice"), eq("RINGING"), anyString(), any());
    }

    @Test
    void revokedInviterOriginalContactPreventsGuestInvitationRecovery() {
        var outgoing = calls.start("ana", "bea", "voice");
        calls.accept(outgoing.callId(), "bea");
        calls.invite(outgoing.callId(), "ana", "carlos");
        doThrow(new org.springframework.security.access.AccessDeniedException("Session participation revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        assertNull(calls.pendingFor("carlos"), "Revoked inviter access invalidates guest invitation");
        verify(inbox, never()).requireConversationAccess("carlos", "direct", "ana");
    }

    @Test
    void groupInviteReservesRecipientEvenAfterOriginalContactIsRevoked() {
        Profile carlos = mock(Profile.class);
        Profile dora = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(dora.getId()).thenReturn(15L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));
        lenient().when(profiles.findUserByUsernameIgnoreCase("dora"))
                .thenReturn(Optional.of(dora));

        var group = calls.start("ana", "bea", "voice");
        calls.accept(group.callId(), "bea");
        calls.invite(group.callId(), "ana", "carlos");
        doThrow(new org.springframework.security.access.AccessDeniedException("Session permission revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        assertNull(calls.pendingFor("carlos"), "Revoked group invite must not be recovered");
        var conflict = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> calls.start("dora", "carlos", "voice"));
        assertEquals(org.springframework.http.HttpStatus.CONFLICT, conflict.getStatusCode(),
                "A pending group invitation still reserves the recipient");
        verify(inbox, never()).requireConversationAccess("carlos", "direct", "ana");
    }

    @Test
    void revokedInviterOriginalContactPreventsGuestAcceptance() {
        var outgoing = calls.start("ana", "bea", "voice");
        calls.accept(outgoing.callId(), "bea");
        calls.invite(outgoing.callId(), "ana", "carlos");
        doThrow(new org.springframework.security.access.AccessDeniedException("Session participation revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.accept(outgoing.callId(), "carlos"));
        verify(inbox, never()).requireConversationAccess("carlos", "direct", "ana");
    }

    @Test
    void deniesUnknownParticipantAndCallerAcceptance() {
        var outgoing = calls.start("ana", "bea", "voice");
        ResponseStatusException outsider = assertThrows(ResponseStatusException.class,
                () -> calls.accept(outgoing.callId(), "mallory"));
        assertEquals(HttpStatus.NOT_FOUND, outsider.getStatusCode());
        ResponseStatusException caller = assertThrows(ResponseStatusException.class,
                () -> calls.accept(outgoing.callId(), "ana"));
        assertEquals(HttpStatus.FORBIDDEN, caller.getStatusCode());
        calls.decline(outgoing.callId(), "bea");
        assertThrows(ResponseStatusException.class, () -> calls.accept(outgoing.callId(), "bea"));
    }


    @Test
    void invitesOnlyAuthorizedRecipientsAndIssuesRoomTokensAfterAccept() {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos")).thenReturn(Optional.of(carlos));
        var call = calls.start("ana","bea","voice");
        calls.accept(call.callId(),"bea");
        assertEquals(HttpStatus.NOT_FOUND,assertThrows(ResponseStatusException.class,
                () -> calls.accept(call.callId(),"carlos")).getStatusCode());
        calls.invite(call.callId(),"bea","carlos");
        verify(inbox).requireConversationAccess("bea","direct","carlos");
        var invitation = calls.pendingFor("carlos");
        assertNotNull(invitation);
        assertEquals("bea",invitation.caller());
        assertEquals("carlos",invitation.peer());
        assertNull(calls.pendingFor("mallory"));
        assertEquals(HttpStatus.FORBIDDEN,assertThrows(ResponseStatusException.class,
                () -> calls.members(call.callId(),"carlos")).getStatusCode());
        var extra = calls.accept(call.callId(),"carlos");
        assertEquals("jynlo-user-14",JWT.decode(extra.token()).getSubject());
        assertEquals(JWT.decode(call.token()).getClaim("video").asMap().get("room"),
                JWT.decode(extra.token()).getClaim("video").asMap().get("room"));
        assertEquals(3,calls.members(call.callId(),"ana").participants().size());
        assertTrue(calls.members(call.callId(),"ana").pending().isEmpty());
        calls.end(call.callId(),"carlos");
        assertEquals(2,calls.members(call.callId(),"ana").participants().size());
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> calls.members(call.callId(),"carlos")).getStatusCode());
    }

    @Test
    void acceptedGuestLeavingQueuesProviderEvictionAndRetriesTransientFailure() throws Exception {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));

        var call = calls.start("ana", "bea", "voice");
        calls.accept(call.callId(), "bea");
        calls.invite(call.callId(), "ana", "carlos");
        calls.accept(call.callId(), "carlos");
        assertEquals(3, calls.members(call.callId(), "ana").participants().size());
        calls.end(call.callId(), "carlos");

        assertEquals(2, calls.members(call.callId(), "ana").participants().size());
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> calls.members(call.callId(), "carlos")).getStatusCode());

        @SuppressWarnings("unchecked")
        var queued = (java.util.concurrent.ConcurrentHashMap<String, ?>)
                ReflectionTestUtils.getField(calls, "pendingGuestEvictions");
        assertEquals(1, queued.size(), "Provider removal must survive a transient outage");

        DirectCallService retrying = spy(calls);
        doReturn(false).when(retrying).removeFromLiveKit(
                "jynlo-" + call.callId(), "jynlo-user-14");
        retrying.revokeDisconnectedGuests();
        assertEquals(1, queued.size());
        doReturn(true).when(retrying).removeFromLiveKit(
                "jynlo-" + call.callId(), "jynlo-user-14");
        retrying.revokeDisconnectedGuests();
        assertTrue(queued.isEmpty());
        verify(retrying, times(2)).removeFromLiveKit(
                "jynlo-" + call.callId(), "jynlo-user-14");
    }

    @Test
    void repeatedGroupRingingToSamePlayerIsRateLimitedAfterThreeInvites() {
        var call = calls.start("ana", "bea", "voice");
        calls.accept(call.callId(), "bea");

        for (int attempt = 0; attempt < 3; attempt++) {
            calls.invite(call.callId(), "ana", "carlos");
            calls.decline(call.callId(), "carlos");
        }

        ResponseStatusException throttled = assertThrows(ResponseStatusException.class,
                () -> calls.invite(call.callId(), "ana", "carlos"));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, throttled.getStatusCode());
        assertNull(calls.pendingFor("carlos"));
        assertEquals(2, calls.members(call.callId(), "bea").participants().size());
        verify(gateway, times(3)).publishDirectCallEvent(eq("ana"), eq("carlos"),
                eq(call.callId().toString()), eq("voice"), eq("RINGING"), eq("ana"), any());

        // One recipient's throttle does not prevent contacting another authorized peer.
        calls.invite(call.callId(), "ana", "dora");
        assertNotNull(calls.pendingFor("dora"));
    }

    @Test
    void declinedOrExpiredGuestNeverTerminatesTheExistingRoom() {
        var call=calls.start("ana","bea","video");
        calls.accept(call.callId(),"bea");
        calls.invite(call.callId(),"ana","carlos");
        calls.decline(call.callId(),"carlos");
        assertNull(calls.pendingFor("carlos"));
        assertEquals(2,calls.members(call.callId(),"ana").participants().size());
        calls.invite(call.callId(),"ana","carlos");
        calls.end(call.callId(),"carlos"); // timer expired while invitation pending
        assertEquals(2,calls.members(call.callId(),"bea").participants().size());
        assertNull(calls.pendingFor("carlos"));
        assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ResponseStatusException.class,
                () -> calls.invite(call.callId(),"ana","ana")).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,assertThrows(ResponseStatusException.class,
                () -> calls.invite(call.callId(),"outsider","other")).getStatusCode());
    }

    @Test
    void rejectsBlockedInviteesAndRevokesTokensWhenOriginalCallEnds() {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos")).thenReturn(Optional.of(carlos));
        var call=calls.start("ana","bea","voice");
        calls.accept(call.callId(),"bea");
        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("blocked"))
                .when(inbox).requireConversationAccess("ana","direct","mallory");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.invite(call.callId(),"ana","mallory"));
        calls.invite(call.callId(),"ana","carlos");
        assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ResponseStatusException.class,
                () -> calls.invite(call.callId(),"ana","carlos")).getStatusCode());
        calls.accept(call.callId(),"carlos");
        calls.end(call.callId(),"ana");
        assertThrows(ResponseStatusException.class,()->calls.members(call.callId(),"carlos"));
        assertNull(calls.pendingFor("carlos"));
        verify(gateway).publishDirectCallEvent(eq("ana"),eq("carlos"),
                eq(call.callId().toString()),eq("voice"),eq("ENDED"),eq("ana"),any());
    }

    @Test
    void neverStartsWithoutProviderSettings() {
        ReflectionTestUtils.setField(calls, "apiSecret", "");
        var failure = assertThrows(ResponseStatusException.class,
                () -> calls.start("ana", "bea", "voice"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, failure.getStatusCode());
        verifyNoInteractions(inbox, gateway);
    }

    @Test
    @SuppressWarnings("unchecked")
    void repeatedDirectRingingToSamePlayerIsThrottledAcrossDeclines() {
        // Simulate the existing 10-second global cooldown having elapsed while
        // keeping the independent, one-minute recipient budget intact.
        var lastStarted = (java.util.concurrent.ConcurrentHashMap<String, java.time.Instant>)
                ReflectionTestUtils.getField(calls, "lastStarted");
        assertNotNull(lastStarted);
        for (int attempt = 0; attempt < 3; attempt++) {
            var invite = calls.start("ana", "bea", "voice");
            calls.decline(invite.callId(), "bea");
            lastStarted.remove("ana");
        }

        ResponseStatusException throttled = assertThrows(ResponseStatusException.class,
                () -> calls.start("ana", "bea", "voice"));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, throttled.getStatusCode());
        verify(gateway, times(3)).publishDirectCallEvent(eq("ana"), eq("bea"),
                anyString(), eq("voice"), eq("RINGING"), eq("ana"), any());

        // The caller can still contact a different eligible player.
        var other = calls.start("ana", "carlos", "voice");
        assertEquals("carlos", other.peer());
    }

    @Test
    void respectsChatAclAndPreventsOverlappingCalls() {
        var start = calls.start("ana", "bea", "video");
        var conflict = assertThrows(ResponseStatusException.class,
                () -> calls.start("bea", "ana", "voice"));
        assertEquals(HttpStatus.CONFLICT, conflict.getStatusCode());
        calls.end(start.callId(), "ana");
        doThrow(new org.springframework.security.access.AccessDeniedException("No chat"))
                .when(inbox).requireConversationAccess("bea", "direct", "ana");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.start("bea", "ana", "voice"));
    }

    @Test
    void refusesSelfCallsAndInvalidMediaWithoutSendingNotifications() {
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> calls.start("ana", "ana", "voice")).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> calls.start("ana", "bea", "screen")).getStatusCode());
        verifyNoInteractions(gateway);
    }

    @Test
    void availabilityRequiresActualLiveKitServerAndMatchingCredentials() throws Exception {
        DirectCallService healthy = spy(calls);
        doReturn(200).when(healthy).probeProvider();
        assertEquals("ready", healthy.providerStatus());
        assertEquals("ready", healthy.providerStatus(), "Successful probe is cached");
        verify(healthy, times(1)).probeProvider();

        DirectCallService invalidKey = spy(calls);
        doReturn(401).when(invalidKey).probeProvider();
        assertEquals("invalidCredentials", invalidKey.providerStatus());

        DirectCallService insufficientPermissions = spy(calls);
        doReturn(403).when(insufficientPermissions).probeProvider();
        assertEquals("insufficientPermissions", insufficientPermissions.providerStatus(),
                "HTTP 403 must not be described as an invalid API key");

        DirectCallService unreachable = spy(calls);
        doThrow(new java.io.IOException("No network")).when(unreachable).probeProvider();
        assertEquals("providerUnavailable", unreachable.providerStatus());
    }

    @Test
    void missingSettingsNeverProbeOrExposeLiveKitCredentials() throws Exception {
        ReflectionTestUtils.setField(calls, "apiKey", "");
        DirectCallService unavailable = spy(calls);
        assertEquals("notConfigured", unavailable.providerStatus());
        verify(unavailable, never()).probeProvider();
    }

    @Test
    void activeCallJoinTokenExpiresWithinTenMinutesEvenForLongCalls() {
        var outgoing = calls.start("ana", "bea", "video");
        var accepted = calls.accept(outgoing.callId(), "bea");
        var expires = JWT.decode(accepted.token()).getExpiresAt().toInstant();
        assertFalse(expires.isAfter(java.time.Instant.now().plus(10, java.time.temporal.ChronoUnit.MINUTES)));
        assertTrue(accepted.expiresAt().isAfter(expires.plus(100, java.time.temporal.ChronoUnit.MINUTES)),
                "A short room-join token must not shorten the active call itself");
    }

    @Test
    void originalHangupQueuesLiveKitRoomDeletionAndRetriesUntilConfirmed() throws Exception {
        DirectCallService protectedCalls = spy(calls);
        doReturn(false).when(protectedCalls).deleteLiveKitRoom(anyString());

        var call = protectedCalls.start("ana", "bea", "voice");
        protectedCalls.accept(call.callId(), "bea");
        protectedCalls.end(call.callId(), "ana");

        // The application call ends immediately; provider deletion is retried
        // without keeping a stale conversation/call entry accessible.
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> protectedCalls.members(call.callId(), "bea")).getStatusCode());
        protectedCalls.revokeDisconnectedGuests();
        verify(protectedCalls).deleteLiveKitRoom("jynlo-" + call.callId());

        doReturn(true).when(protectedCalls).deleteLiveKitRoom(anyString());
        protectedCalls.revokeDisconnectedGuests();
        verify(protectedCalls, times(2)).deleteLiveKitRoom("jynlo-" + call.callId());

        protectedCalls.revokeDisconnectedGuests();
        verify(protectedCalls, times(2)).deleteLiveKitRoom("jynlo-" + call.callId());
    }

    @Test
    void revokedOriginalParticipantClosesLiveKitRoomForEveryone() throws Exception {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));
        DirectCallService protectedCalls = spy(calls);
        doReturn(true).when(protectedCalls).deleteLiveKitRoom(anyString());

        var call = protectedCalls.start("ana", "bea", "voice");
        protectedCalls.accept(call.callId(), "bea");
        protectedCalls.invite(call.callId(), "ana", "carlos");
        protectedCalls.accept(call.callId(), "carlos");

        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        protectedCalls.revokeDisconnectedGuests();
        verify(protectedCalls).deleteLiveKitRoom("jynlo-" + call.callId());
        verify(gateway).publishDirectCallEvent(eq("ana"), eq("bea"),
                eq(call.callId().toString()), eq("voice"), eq("ENDED"), eq("ana"), any());
        verify(gateway).publishDirectCallEventTo(eq("carlos"), eq("ana"), eq("carlos"),
                eq(call.callId().toString()), eq("voice"), eq("ENDED"), eq("ana"), any());
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> protectedCalls.members(call.callId(), "bea")).getStatusCode());
        assertNull(protectedCalls.pendingFor("carlos"));
    }

    @Test
    void providerRoomDeletionFailureRetainsOriginalCallForRetry() throws Exception {
        DirectCallService protectedCalls = spy(calls);
        doReturn(false).when(protectedCalls).deleteLiveKitRoom(anyString());
        var call = protectedCalls.start("ana", "bea", "voice");
        protectedCalls.accept(call.callId(), "bea");
        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        protectedCalls.revokeDisconnectedGuests();
        verify(protectedCalls).deleteLiveKitRoom("jynlo-" + call.callId());
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> protectedCalls.members(call.callId(), "ana"));
        doReturn(true).when(protectedCalls).deleteLiveKitRoom(anyString());
        protectedCalls.revokeDisconnectedGuests();
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> protectedCalls.members(call.callId(), "bea")).getStatusCode());
    }

    @Test
    void revokedAcceptedGuestIsDisconnectedFromProviderAndRemovedLocally() throws Exception {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));
        DirectCallService protectedCalls = spy(calls);
        doReturn(true).when(protectedCalls)
                .removeFromLiveKit(anyString(), anyString());

        var call = protectedCalls.start("ana", "bea", "voice");
        protectedCalls.accept(call.callId(), "bea");
        protectedCalls.invite(call.callId(), "ana", "carlos");
        protectedCalls.accept(call.callId(), "carlos");

        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Revoked"))
                .when(inbox).requireConversationAccess("carlos", "direct", "ana");

        protectedCalls.revokeDisconnectedGuests();

        verify(protectedCalls).removeFromLiveKit(eq("jynlo-" + call.callId()),
                eq("jynlo-user-14"));
        assertEquals(2, protectedCalls.members(call.callId(), "bea").participants().size());
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> protectedCalls.members(call.callId(), "carlos")).getStatusCode());
    }

    @Test
    void failedProviderRemovalKeepsGuestEligibleForRetry() throws Exception {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));
        DirectCallService protectedCalls = spy(calls);
        doReturn(false).when(protectedCalls)
                .removeFromLiveKit(anyString(), anyString());

        var call = protectedCalls.start("ana", "bea", "voice");
        protectedCalls.accept(call.callId(), "bea");
        protectedCalls.invite(call.callId(), "ana", "carlos");
        protectedCalls.accept(call.callId(), "carlos");

        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Revoked"))
                .when(inbox).requireConversationAccess("carlos", "direct", "ana");
        protectedCalls.revokeDisconnectedGuests();

        verify(protectedCalls).removeFromLiveKit(eq("jynlo-" + call.callId()),
                eq("jynlo-user-14"));
        // The guest cannot read the roster, but remains locally tracked until
        // LiveKit actually confirms removal, allowing a later retry.
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> protectedCalls.members(call.callId(), "carlos"));
        doReturn(true).when(protectedCalls)
                .removeFromLiveKit(anyString(), anyString());
        protectedCalls.revokeDisconnectedGuests();
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> protectedCalls.members(call.callId(), "carlos")).getStatusCode());
    }

    @Test
    void authorizedMembersCannotSeeRevokedGuestsOrPendingInvitations() {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));

        var call = calls.start("ana", "bea", "voice");
        calls.accept(call.callId(), "bea");
        calls.invite(call.callId(), "ana", "carlos");
        calls.accept(call.callId(), "carlos");
        calls.invite(call.callId(), "bea", "dora");
        assertEquals(3, calls.members(call.callId(), "ana").participants().size());
        assertTrue(calls.members(call.callId(), "ana").pending().contains("dora"));

        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Guest removed"))
                .when(inbox).requireConversationAccess("carlos", "direct", "ana");
        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Pending invite blocked"))
                .when(inbox).requireConversationAccess("dora", "direct", "bea");

        var visible = calls.members(call.callId(), "ana");
        assertEquals(2, visible.participants().size());
        assertTrue(visible.participants().stream().noneMatch(p -> p.username().equals("carlos")));
        assertTrue(visible.pending().isEmpty());
        assertEquals(2, calls.members(call.callId(), "bea").participants().size());
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.members(call.callId(), "carlos"));
    }

    @Test
    void revokedGuestReceivesNoFurtherGroupRosterNotifications() {
        Profile carlos = mock(Profile.class);
        lenient().when(carlos.getId()).thenReturn(14L);
        lenient().when(profiles.findUserByUsernameIgnoreCase("carlos"))
                .thenReturn(Optional.of(carlos));

        var call = calls.start("ana", "bea", "voice");
        calls.accept(call.callId(), "bea");
        calls.invite(call.callId(), "ana", "carlos");
        calls.accept(call.callId(), "carlos");
        clearInvocations(gateway);

        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Revoked"))
                .when(inbox).requireConversationAccess("carlos", "direct", "ana");
        calls.invite(call.callId(), "bea", "dora");

        verify(gateway, never()).publishDirectCallEventTo(eq("carlos"), eq("ana"), eq("carlos"),
                eq(call.callId().toString()), eq("voice"), eq("MEMBERS_CHANGED"), anyString(), any());
        verify(gateway).publishDirectCallEventTo(eq("bea"), eq("ana"), eq("bea"),
                eq(call.callId().toString()), eq("voice"), eq("MEMBERS_CHANGED"), eq("bea"), any());
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.members(call.callId(), "carlos"));
        assertNotNull(calls.pendingFor("dora"));
    }

    @Test
    void revokedOriginalCallerReceivesNoRosterEventsButPeerStillDoes() {
        var call = calls.start("ana", "bea", "voice");
        calls.accept(call.callId(), "bea");
        clearInvocations(gateway);

        lenient().doThrow(new org.springframework.security.access.AccessDeniedException("Revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");
        calls.invite(call.callId(), "bea", "carlos");

        verify(gateway, never()).publishDirectCallEventTo(eq("ana"), anyString(), anyString(),
                eq(call.callId().toString()), eq("voice"), eq("MEMBERS_CHANGED"), anyString(), any());
        verify(gateway).publishDirectCallEventTo(eq("bea"), eq("ana"), eq("bea"),
                eq(call.callId().toString()), eq("voice"), eq("MEMBERS_CHANGED"), eq("bea"), any());
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.members(call.callId(), "ana"));
        assertNotNull(calls.pendingFor("carlos"));
    }

    @Test
    void revokedSessionContactCannotReadActiveCallRoster() {
        var call = calls.start("ana", "bea", "voice");
        calls.accept(call.callId(), "bea");
        assertEquals(2, calls.members(call.callId(), "ana").participants().size());

        doThrow(new org.springframework.security.access.AccessDeniedException("Session permission revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.members(call.callId(), "ana"));
        // Revoking one participant must not prevent the other from inspecting their own call.
        assertEquals(2, calls.members(call.callId(), "bea").participants().size());
    }
    @Test
    void revokedSessionContactCannotInviteMorePeopleIntoAnActiveCall() {
        var call = calls.start("ana", "bea", "voice");
        calls.accept(call.callId(), "bea");
        doThrow(new org.springframework.security.access.AccessDeniedException("Session permission revoked"))
                .when(inbox).requireConversationAccess("ana", "direct", "bea");

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> calls.invite(call.callId(), "ana", "carlos"));
        verify(inbox, never()).requireConversationAccess("ana", "direct", "carlos");
        verify(gateway, never()).publishDirectCallEvent(eq("ana"), eq("carlos"),
                anyString(), anyString(), anyString(), anyString(), any());
        assertTrue(calls.members(call.callId(), "bea").pending().isEmpty());
    }

    @Test
    void declinedInvitationQueuesLiveKitRoomRevocationAndRetriesCleanup() throws Exception {
        // The caller is issued a room-scoped token before the callee answers,
        // so declining must also close any room that the caller opened.
        var outgoing = calls.start("ana", "bea", "voice");
        String room = JWT.decode(outgoing.token()).getClaim("video").asMap().get("room").toString();

        calls.decline(outgoing.callId(), "bea");
        assertNull(calls.pendingFor("bea"));
        assertThrows(ResponseStatusException.class,
                () -> calls.accept(outgoing.callId(), "bea"));

        @SuppressWarnings("unchecked")
        var queued = (java.util.concurrent.ConcurrentHashMap<UUID, String>)
                ReflectionTestUtils.getField(calls, "roomsPendingDeletion");
        assertNotNull(queued);
        assertEquals(room, queued.get(outgoing.callId()),
                "The declined call must be scheduled for provider-side room deletion");

        // A provider outage must preserve the retry entry instead of
        // silently marking the LiveKit room as disconnected.
        var retrying = spy(calls);
        doReturn(false).when(retrying).deleteLiveKitRoom(room);
        retrying.revokeDisconnectedGuests();
        assertEquals(room, queued.get(outgoing.callId()));

        doReturn(true).when(retrying).deleteLiveKitRoom(room);
        retrying.revokeDisconnectedGuests();
        assertFalse(queued.containsKey(outgoing.callId()));
        verify(retrying, times(2)).deleteLiveKitRoom(room);
    }

}
