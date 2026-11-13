package com.example.desporto24.service;

import com.auth0.jwt.JWT;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.PersonalLiveEvent;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.PersonalLiveEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonalLiveServiceTest {
    private final PersonalLiveEventRepository events=mock(PersonalLiveEventRepository.class);
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final UserBlockService blocks=mock(UserBlockService.class);
    private final DirectCallService provider=mock(DirectCallService.class);
    private final PersonalLiveService service=new PersonalLiveService(events,profiles,blocks,provider);
    private Profile owner,viewer;
    private PersonalLiveEvent live;

    @BeforeEach void setup(){
        owner=new Profile();owner.setId(12L);owner.setUsername("host");
        viewer=new Profile();viewer.setId(19L);viewer.setUsername("spectator");
        live=PersonalLiveEvent.builder().id(5L).host(owner).title("Treino")
            .status(PersonalLiveEvent.Status.LIVE).createdAt(Instant.now())
            .hostHeartbeatAt(Instant.now()).build();
        when(profiles.findUserByUsernameIgnoreCase("host")).thenReturn(Optional.of(owner));
        when(profiles.findUserByUsernameIgnoreCase("spectator")).thenReturn(Optional.of(viewer));
        when(events.findById(5L)).thenReturn(Optional.of(live));
        when(provider.providerStatus()).thenReturn("ready");
        ReflectionTestUtils.setField(service,"serverUrl","wss://live.example.test");
        ReflectionTestUtils.setField(service,"apiKey","test-key");
        ReflectionTestUtils.setField(service,"apiSecret","local-secret");
    }
    @Test void viewerTokensCannotPublishTracks(){
        var connection=service.join("spectator",5L);
        var claim=JWT.decode(connection.token()).getClaim("video").asMap();
        assertEquals(false,claim.get("canPublish"));
        assertEquals(true,claim.get("canSubscribe"));
        assertEquals("VIEWER",connection.role());
    }
    @Test void hostMayPublishButOthersCannotEnd(){
        var connection=service.join("host",5L);
        assertEquals("HOST",connection.role());
        assertEquals(true,JWT.decode(connection.token()).getClaim("video").asMap().get("canPublish"));
        assertThrows(AccessDeniedException.class,()->service.end("spectator",5L));
    }
    @Test void blockedUsersCannotWatchPersonalStreams(){
        when(blocks.isBlockedBetween(viewer,owner)).thenReturn(true);
        assertThrows(ResponseStatusException.class,()->service.join("spectator",5L));
        when(events.findTop30ByStatusOrderByCreatedAtDesc(PersonalLiveEvent.Status.LIVE))
            .thenReturn(List.of(live));
        assertTrue(service.active("spectator").isEmpty());
    }
    @Test void oneHostCannotStartAnotherLive(){
        when(events.findByHostIdAndStatus(12L,PersonalLiveEvent.Status.LIVE))
            .thenReturn(Optional.of(live));
        assertThrows(ResponseStatusException.class,()->service.start("host","Outro jogo"));
        verify(events,never()).save(any());
    }
}
