package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityPermanentTeamsServiceTest {
    @Mock CommunityRepository communities;
    @Mock CommunityPermanentTeamRepository permanentTeams;
    @Mock CommunityChampionshipTeamRepository seasonTeams;
    @Mock CommunityChampionshipRepository championships;
    @Mock ProfileRepository profiles;
    @Mock CommunityMemberRepository members;
    @InjectMocks CommunityPermanentTeamsService service;

    private Community community;
    private CommunityChampionship championship;

    @BeforeEach
    void setup() {
        community = Community.builder().id(7L).build();
        championship = CommunityChampionship.builder().id(15L).community(community).build();
    }

    private void organizer() {
        var profile = new Profile();
        profile.setId(33L);
        when(profiles.findUserByUsername("owner")).thenReturn(Optional.of(profile));
        when(members.findByCommunityIdAndProfileId(7L,33L))
                .thenReturn(Optional.of(CommunityMember.builder().role(CommunityRole.OWNER).build()));
    }

    @Test
    void linksHistoricSeasonWithoutChangingChampionSnapshot() {
        organizer();
        championship.setClosedAt(java.time.Instant.parse("2026-06-30T00:00:00Z"));
        championship.setChampionEntryType("TEAM");
        championship.setChampionParticipantId(10L);
        championship.setChampionDisplayName("Águias 2025");
        var seasonTeam = CommunityChampionshipTeam.builder().id(10L).championship(championship).build();
        var permanent = CommunityPermanentTeam.builder().id(90L).community(community).name("Águias FC").build();
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(championship));
        when(seasonTeams.findById(10L)).thenReturn(Optional.of(seasonTeam));
        when(permanentTeams.findByIdAndCommunityId(90L,7L)).thenReturn(Optional.of(permanent));
        when(seasonTeams.findByChampionshipIdOrderByNameAscIdAsc(15L)).thenReturn(List.of(seasonTeam));
        when(permanentTeams.findByCommunityIdOrderByNameAscIdAsc(7L)).thenReturn(List.of(permanent));

        service.link(7L,15L,10L,90L,"owner");
        assertEquals(90L,seasonTeam.getPermanentTeam().getId());
        assertEquals("Águias 2025",championship.getChampionDisplayName());
        assertEquals(10L,championship.getChampionParticipantId());
        verify(seasonTeams).saveAndFlush(seasonTeam);
        verify(championships,never()).saveAndFlush(any());
    }

    @Test
    void refusesPermanentTeamOutsideCommunity() {
        organizer();
        var seasonTeam = CommunityChampionshipTeam.builder().id(10L).championship(championship).build();
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(championship));
        when(seasonTeams.findById(10L)).thenReturn(Optional.of(seasonTeam));
        when(permanentTeams.findByIdAndCommunityId(90L,7L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class,
                () -> service.link(7L,15L,10L,90L,"owner"));
        verify(seasonTeams,never()).saveAndFlush(any());
    }

    @Test
    void refusesTwoDistinctTeamsFromOneSeasonMappingToSamePermanentTeam() {
        organizer();
        var permanent = CommunityPermanentTeam.builder().id(90L).community(community).build();
        var first = CommunityChampionshipTeam.builder().id(10L).championship(championship).build();
        var second = CommunityChampionshipTeam.builder().id(11L).championship(championship)
                .permanentTeam(permanent).build();
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(championship));
        when(seasonTeams.findById(10L)).thenReturn(Optional.of(first));
        when(permanentTeams.findByIdAndCommunityId(90L,7L)).thenReturn(Optional.of(permanent));
        when(seasonTeams.findByChampionshipIdOrderByNameAscIdAsc(15L)).thenReturn(List.of(first,second));
        assertThrows(ResponseStatusException.class,
                () -> service.link(7L,15L,10L,90L,"owner"));
        verify(seasonTeams,never()).saveAndFlush(any());
    }
}
