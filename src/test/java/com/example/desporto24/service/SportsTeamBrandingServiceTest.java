package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import com.example.desporto24.storage.MediaStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamBrandingServiceTest {
    @Mock SportsTeamRepository teams;
    @Mock ProfileRepository profiles;
    @Mock CommunityTournamentTeamRepository tournamentTeams;
    @Mock MediaStorage mediaStorage;
    @Mock org.springframework.context.ApplicationEventPublisher realtimeEvents;
    @InjectMocks SportsTeamService service;

    @BeforeEach
    void provideMediaStorage() {
        ReflectionTestUtils.setField(service,"mediaStorage",mediaStorage);
    }

    private SportsTeam owned() {
        var owner=new Profile();
        owner.setId(7L);
        owner.setUsername("captain");
        return SportsTeam.builder().id(21L).name("Atlético").slug("atletico")
                .owner(owner).build();
    }

    @Test
    void creatingPermanentTeamEnablesPrivateChatAndFeedWithoutAddingPlayers() {
        var owner=new Profile();
        owner.setId(7L);
        owner.setUsername("captain");
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(owner));
        when(teams.saveAndFlush(any(SportsTeam.class))).thenAnswer(call->{
            SportsTeam entity=call.getArgument(0);
            entity.setId(21L);
            return entity;
        });

        var result=service.create(
            new SportsTeamService.CreateTeam("Clube Atlântico", "Futebol", "Lisboa", null),
            "captain");

        assertTrue(result.chatEnabled());
        assertTrue(result.feedEnabled());
        assertEquals(7L,result.ownerProfileId());
        assertEquals("Clube Atlântico",result.name());
        assertTrue(result.slug().startsWith("clube-atlantico-"));
        verify(teams).saveAndFlush(argThat(team->
            team.isChatEnabled() && team.isFeedEnabled() &&
            team.getOwner().getId().equals(7L)));
    }

    @Test
    void teamOwnerCanDisablePrivateChatWithoutDisablingFeed() {
        var team=owned();
        team.setChatEnabled(true);
        team.setFeedEnabled(true);
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        when(teams.saveAndFlush(team)).thenReturn(team);

        var result=service.features(21L,
            new SportsTeamService.TeamFeatures(null, false),"captain");

        assertFalse(result.chatEnabled());
        assertTrue(result.feedEnabled());
        verify(realtimeEvents).publishEvent(
            new com.example.desporto24.websocket.TeamContentChangedEvent(21L,"chat"));
        verify(realtimeEvents,never()).publishEvent(
            new com.example.desporto24.websocket.TeamContentChangedEvent(21L,"feed"));

        // Re-submitting identical settings must not broadcast duplicate refreshes.
        service.features(21L,new SportsTeamService.TeamFeatures(null,false),"captain");
        verifyNoMoreInteractions(realtimeEvents);
    }

    @Test
    void changingBothTeamFeaturesInvalidatesBothRealtimeSurfaces() {
        var team=owned();
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        when(teams.saveAndFlush(team)).thenReturn(team);
        service.features(21L,new SportsTeamService.TeamFeatures(true,true),"captain");
        verify(realtimeEvents).publishEvent(
            new com.example.desporto24.websocket.TeamContentChangedEvent(21L,"chat"));
        verify(realtimeEvents).publishEvent(
            new com.example.desporto24.websocket.TeamContentChangedEvent(21L,"feed"));
    }

    @Test
    void suspendedOwnerCannotCreateTeamsOrManageBrandingAndFeatures() {
        var owner=new Profile();
        owner.setId(7L);
        owner.setUsername("captain");
        owner.setModerationSuspendedAt(java.time.Instant.now());
        var team=SportsTeam.builder().id(21L).name("Atlético").owner(owner).build();
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(owner));
        when(teams.findById(21L)).thenReturn(Optional.of(team));

        assertThrows(AccessDeniedException.class, () -> service.create(
            new SportsTeamService.CreateTeam("Novo clube","Futebol",null,null),"captain"));
        assertThrows(AccessDeniedException.class, () -> service.features(
            21L,new SportsTeamService.TeamFeatures(true,true),"captain"));
        assertThrows(AccessDeniedException.class, () -> service.kit(
            21L,new SportsTeamService.KitSettings("#112233","#ffffff","PLAIN"),"captain"));
        verify(teams,never()).saveAndFlush(any());
        verifyNoInteractions(mediaStorage);
    }

    @Test
    void onlyOwnerCanUpdateKitAndColorsAreNormalized() {
        var team=owned();
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        assertThrows(AccessDeniedException.class,
                ()->service.kit(21L,new SportsTeamService.KitSettings("#00ccaa","#ffffff","STRIPES"),"outsider"));
        verify(teams,never()).saveAndFlush(any());
        when(teams.saveAndFlush(team)).thenReturn(team);
        var saved=service.kit(21L,new SportsTeamService.KitSettings("#00ccaa","#ffffff","STRIPES"),"captain");
        assertEquals("#00CCAA",saved.kitPrimary());
        assertEquals("#FFFFFF",saved.kitSecondary());
        assertEquals("STRIPES",saved.kitPattern());
    }

    @Test
    void invalidColorsAreRejectedBeforePersistence() {
        var team=owned();
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        assertThrows(ResponseStatusException.class,
                ()->service.kit(21L,new SportsTeamService.KitSettings("red","#ffffff","PLAIN"),"captain"));
        verify(teams,never()).saveAndFlush(any());
    }

    @Test
    void kitPatternsRejectUnknownValuesWithoutUpdatingExistingTeam() {
        var team=owned();
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        assertThrows(ResponseStatusException.class,
                ()->service.kit(21L,new SportsTeamService.KitSettings("#112233","#ffffff","SCRIPT"),"captain"));
        verify(teams,never()).saveAndFlush(any());
    }

    @Test
    void legacyKitUpdateDoesNotResetAlreadySavedPattern() {
        var team=owned();
        team.setKitPattern("DIAGONAL");
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        when(teams.saveAndFlush(team)).thenReturn(team);
        var saved=service.kit(21L,new SportsTeamService.KitSettings("#112233","#ffffff",null),"captain");
        assertEquals("DIAGONAL",saved.kitPattern());
    }

    @Test
    void noOutsiderCanUploadOrDeleteTeamLogo() {
        var team=owned();
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        var file=new MockMultipartFile("logo","club.png","image/png",new byte[]{1,2,3});
        assertThrows(AccessDeniedException.class,()->service.uploadLogo(21L,file,"outsider"));
        assertThrows(AccessDeniedException.class,()->service.removeLogo(21L,"outsider"));
        verifyNoInteractions(mediaStorage);
    }

    @Test
    void ownerUploadUsesExistingValidatedCommunityImageStorage() {
        var team=owned();
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        var file=new MockMultipartFile("logo","club.png","image/png",new byte[]{1,2,3});
        when(mediaStorage.storeCommunityPhoto(startsWith("teams/21/"),same(file)))
                .thenReturn(URI.create("https://cdn.example.com/teams/21/new.png"));
        when(teams.saveAndFlush(team)).thenReturn(team);
        var saved=service.uploadLogo(21L,file,"captain");
        assertEquals("https://cdn.example.com/teams/21/new.png",saved.logoUrl());
        verify(mediaStorage).storeCommunityPhoto(startsWith("teams/21/"),same(file));
        verify(mediaStorage,never()).store(anyString(),any());
    }

    @Test
    void removingLogoDoesNotDeleteMediaOutsideOwnTeamPrefix() {
        var team=owned();
        team.setLogoUrl("https://cdn.example.com/teams/99/outsider.png");
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        when(teams.saveAndFlush(team)).thenReturn(team);
        assertNull(service.removeLogo(21L,"captain").logoUrl());
        verify(mediaStorage,never()).delete(anyString());
    }
}
