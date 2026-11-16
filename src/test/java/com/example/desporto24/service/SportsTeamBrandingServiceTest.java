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
    void onlyOwnerCanUpdateKitAndColorsAreNormalized() {
        var team=owned();
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        assertThrows(AccessDeniedException.class,
                ()->service.kit(21L,new SportsTeamService.KitSettings("#00ccaa","#ffffff"),"outsider"));
        verify(teams,never()).saveAndFlush(any());
        when(teams.saveAndFlush(team)).thenReturn(team);
        var saved=service.kit(21L,new SportsTeamService.KitSettings("#00ccaa","#ffffff"),"captain");
        assertEquals("#00CCAA",saved.kitPrimary());
        assertEquals("#FFFFFF",saved.kitSecondary());
    }

    @Test
    void invalidColorsAreRejectedBeforePersistence() {
        var team=owned();
        when(teams.findById(21L)).thenReturn(Optional.of(team));
        assertThrows(ResponseStatusException.class,
                ()->service.kit(21L,new SportsTeamService.KitSettings("red","#ffffff"),"captain"));
        verify(teams,never()).saveAndFlush(any());
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
