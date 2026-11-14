package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.ProfileFollowRequest;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.ProfileFollowRepository;
import com.example.desporto24.repository.social.ProfileFollowRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfilePrivacyServiceTest {
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final ProfileFollowRepository follows=mock(ProfileFollowRepository.class);
    private final ProfileFollowRequestRepository requests=mock(ProfileFollowRequestRepository.class);
    private final UserBlockService blocks=mock(UserBlockService.class);
    private final ProfilePrivacyService service=new ProfilePrivacyService(profiles,follows,requests,blocks);
    private Profile owner,viewer;

    @BeforeEach void setup() {
        owner=new Profile();owner.setId(4L);owner.setUsername("owner");
        viewer=new Profile();viewer.setId(9L);viewer.setUsername("viewer");
        owner.setPrivateProfile(true);
        when(profiles.findUserByUsernameIgnoreCase("owner")).thenReturn(Optional.of(owner));
        when(profiles.findUserByUsernameIgnoreCase("viewer")).thenReturn(Optional.of(viewer));
    }

    @Test void privateContentIsRestrictedToApprovedFollowersAndOwner() {
        assertFalse(service.canView(viewer,owner));
        assertTrue(service.canView(owner,owner));
        when(follows.existsByFollowerIdAndFollowingId(9L,4L)).thenReturn(true);
        assertTrue(service.canView(viewer,owner));
    }

    @Test void revokedFollowImmediatelyRemovesPrivateContentPermission() {
        when(follows.existsByFollowerIdAndFollowingId(9L,4L))
                .thenReturn(true,false);
        assertDoesNotThrow(()->service.requireContentAccess(viewer,owner));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class,
                        ()->service.requireContentAccess(viewer,owner)).getStatusCode());
    }

    @Test void blockingOverridesExistingFollowPermission() {
        when(blocks.isBlockedBetween(viewer,owner)).thenReturn(true);
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND,
                assertThrows(ResponseStatusException.class,
                        ()->service.requireContentAccess(viewer,owner)).getStatusCode());
        verify(follows,never()).existsByFollowerIdAndFollowingId(anyLong(),anyLong());
    }

    @Test void publicProfilesRemainVisibleWithoutFollowing() {
        owner.setPrivateProfile(false);
        assertTrue(service.canView(viewer,owner));
    }

    @Test void cannotActivatePrivateProfileBeforeMediaProtection() {
        ReflectionTestUtils.setField(service,"privateModeEnabled",false);
        assertThrows(ResponseStatusException.class,()->service.update("owner",true));
        verify(profiles,never()).save(any());
    }

    @Test void switchingPrivateProfileToPublicRequiresReverseMediaMigration() {
        ProfileMediaReadinessService readiness=mock(ProfileMediaReadinessService.class);
        ReflectionTestUtils.setField(service,"mediaReadiness",readiness);
        when(readiness.count(owner)).thenReturn(
            new ProfileMediaReadinessService.MediaReadiness(0,2,true));
        var thrown=assertThrows(ResponseStatusException.class,
            ()->service.update("owner",false));
        assertEquals(org.springframework.http.HttpStatus.CONFLICT,thrown.getStatusCode());
        assertTrue(owner.isPrivateProfile());
        verify(profiles,never()).save(any());
        verify(requests,never()).deleteByTargetId(anyLong());
    }

    @Test void emptyPrivateProfileCanReturnToPublicWithoutMediaRelocation() {
        ProfileMediaReadinessService readiness=mock(ProfileMediaReadinessService.class);
        ReflectionTestUtils.setField(service,"mediaReadiness",readiness);
        when(readiness.count(owner)).thenReturn(
            new ProfileMediaReadinessService.MediaReadiness(0,0,true));
        service.update("owner",false);
        assertFalse(owner.isPrivateProfile());
        verify(profiles).save(owner);
        verify(requests).deleteByTargetId(4L);
    }

    @Test void aRequestDoesNotAutomaticallyCreateAFollower() {
        service.request("viewer","owner");
        verify(requests).save(any(ProfileFollowRequest.class));
        verify(follows,never()).save(any());
    }

    @Test void onlyTargetCanAcceptFollowRequest() {
        ProfileFollowRequest request=ProfileFollowRequest.builder().id(12L)
            .requester(viewer).target(owner).build();
        when(requests.findById(12L)).thenReturn(Optional.of(request));
        assertThrows(ResponseStatusException.class,()->service.decide("viewer",12L,true));
        verify(follows,never()).save(any());
        service.decide("owner",12L,true);
        verify(follows).save(any());
        verify(requests).delete(request);
    }
}
