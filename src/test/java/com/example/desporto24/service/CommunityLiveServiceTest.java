package com.example.desporto24.service;

import com.auth0.jwt.JWT;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityLiveEvent;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityLiveEventRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Never accesses real accounts, LiveKit or a database. */
class CommunityLiveServiceTest {
    private final CommunityLiveEventRepository events = mock(CommunityLiveEventRepository.class);
    private final CommunityRepository communities = mock(CommunityRepository.class);
    private final CommunityMemberRepository members = mock(CommunityMemberRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final DirectCallService provider = mock(DirectCallService.class);
    private final CommunityLiveService service = new CommunityLiveService(
            events, communities, members, profiles, provider);
    private Profile owner;
    private Profile spectator;
    private Community community;
    private CommunityLiveEvent live;

    @BeforeEach
    void setup() {
        owner = new Profile();
        owner.setId(1L);
        owner.setUsername("organizador");
        spectator = new Profile();
        spectator.setId(2L);
        spectator.setUsername("espectador");
        community = Community.builder().id(7L).name("Futebol")
                .owner(owner).build();
        live = CommunityLiveEvent.builder().id(25L).community(community).creator(owner)
                .title("Jogo em direto").status(CommunityLiveEvent.Status.LIVE)
                .scheduledAt(Instant.now().minusSeconds(40))
                .startedAt(Instant.now().minusSeconds(10))
                .createdAt(Instant.now().minusSeconds(70)).build();
        when(profiles.findUserByUsernameIgnoreCase("organizador")).thenReturn(Optional.of(owner));
        when(profiles.findUserByUsernameIgnoreCase("espectador")).thenReturn(Optional.of(spectator));
        when(communities.findById(7L)).thenReturn(Optional.of(community));
        when(communities.lockForLiveStart(7L)).thenReturn(Optional.of(community));
        when(events.findByIdAndCommunityId(25L, 7L)).thenReturn(Optional.of(live));
        when(members.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(true);
        when(provider.providerStatus()).thenReturn("ready");
        when(events.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ReflectionTestUtils.setField(service, "serverUrl", "wss://livekit.example.test");
        ReflectionTestUtils.setField(service, "apiKey", "test-public-key");
        ReflectionTestUtils.setField(service, "apiSecret", "test-only-not-production");
    }

    @Test
    void hostCanPublishButCommunityViewerCannot() {
        var host = service.join(7L, 25L, "organizador");
        var viewer = service.join(7L, 25L, "espectador");
        assertEquals("HOST", host.role());
        assertEquals("VIEWER", viewer.role());
        assertTrue(JWT.decode(host.token()).getClaim("video").asMap()
                .get("canPublish") instanceof Boolean);
        assertEquals(true, JWT.decode(host.token()).getClaim("video").asMap().get("canPublish"));
        assertEquals(false, JWT.decode(viewer.token()).getClaim("video").asMap().get("canPublish"));
        assertEquals(true, JWT.decode(viewer.token()).getClaim("video").asMap().get("canSubscribe"));
        assertEquals(false, JWT.decode(viewer.token()).getClaim("video").asMap().get("canPublishData"));
        assertEquals("jynlo-live-community-7-25",
                JWT.decode(viewer.token()).getClaim("video").asMap().get("room"));
        assertNotEquals(host.token(), viewer.token());
        assertTrue(viewer.expiresAt().isBefore(Instant.now().plusSeconds(310)));
    }

    @Test
    void outsidersCannotListOrJoinPrivateCommunityLive() {
        when(members.existsByCommunityIdAndProfileId(7L, 2L)).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> service.join(7L, 25L, "espectador"));
        assertThrows(AccessDeniedException.class, () -> service.list(7L, "espectador"));
        verify(events, never()).findTop30ByCommunityIdOrderByScheduledAtDescIdDesc(anyLong());
    }

    @Test
    void onlyOwnerCanScheduleAndStartOrEnd() {
        assertThrows(AccessDeniedException.class,
                () -> service.schedule(7L, "espectador", "Jogo", null, Instant.now()));
        assertThrows(AccessDeniedException.class,
                () -> service.start(7L, 25L, "espectador"));
        assertThrows(AccessDeniedException.class,
                () -> service.end(7L, 25L, "espectador"));
        verify(events, never()).save(any());
    }

    @Test
    void scheduledLiveCannotBeWatchedBeforeItStartsOrAfterItEnds() {
        live.setStatus(CommunityLiveEvent.Status.SCHEDULED);
        assertThrows(ResponseStatusException.class, () -> service.join(7L, 25L, "espectador"));
        live.setStatus(CommunityLiveEvent.Status.ENDED);
        assertThrows(ResponseStatusException.class, () -> service.join(7L, 25L, "espectador"));
        verify(provider, never()).providerStatus();
    }

    @Test
    void startsOnlyWhenLiveKitIsReadyAndNeverResurrectsEndedBroadcasts() {
        live.setStatus(CommunityLiveEvent.Status.SCHEDULED);
        when(provider.providerStatus()).thenReturn("unavailable");
        assertEquals(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                assertThrows(ResponseStatusException.class,
                        () -> service.start(7L, 25L, "organizador")).getStatusCode());
        assertEquals(CommunityLiveEvent.Status.SCHEDULED, live.getStatus());
        when(provider.providerStatus()).thenReturn("ready");
        var started = service.start(7L, 25L, "organizador");
        verify(communities, times(2)).lockForLiveStart(7L);
        assertEquals(CommunityLiveEvent.Status.LIVE, started.status());
        var ended = service.end(7L, 25L, "organizador");
        assertEquals(CommunityLiveEvent.Status.ENDED, ended.status());
        assertThrows(ResponseStatusException.class, () -> service.start(7L, 25L, "organizador"));
    }

    @Test
    void endingBroadcastClosesRoomAndRetriesWhenProviderFails() throws Exception {
        when(provider.deleteLiveKitRoom("jynlo-live-community-7-25"))
                .thenReturn(false, true);
        service.end(7L, 25L, "organizador");
        assertEquals(CommunityLiveEvent.Status.ENDED, live.getStatus());
        // No active join tokens are minted after the application marks it ended.
        assertThrows(ResponseStatusException.class, () -> service.join(7L, 25L, "espectador"));
        service.closePendingRooms();
        verify(provider, times(2)).deleteLiveKitRoom("jynlo-live-community-7-25");
        service.closePendingRooms();
        verify(provider, times(2)).deleteLiveKitRoom("jynlo-live-community-7-25");
    }

    @Test
    void preventsEarlyStartAndConcurrentLiveEvents() {
        live.setStatus(CommunityLiveEvent.Status.SCHEDULED);
        live.setScheduledAt(Instant.now().plusSeconds(3600));
        assertThrows(ResponseStatusException.class, () -> service.start(7L, 25L, "organizador"));
        live.setScheduledAt(Instant.now());
        when(events.existsByCommunityIdAndStatus(7L, CommunityLiveEvent.Status.LIVE)).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> service.start(7L, 25L, "organizador"));
        verify(events, never()).save(any());
    }

    @Test
    void eventTitlesAreValidatedBeforePersistence() {
        assertThrows(ResponseStatusException.class,
                () -> service.schedule(7L, "organizador", "  ", null, Instant.now()));
        assertThrows(ResponseStatusException.class,
                () -> service.schedule(7L, "organizador", "Jogo", null,
                        Instant.now().plusSeconds(100L * 24 * 3600)));
        verify(events, never()).save(any());
    }
}
