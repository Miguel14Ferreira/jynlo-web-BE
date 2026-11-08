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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityChampionshipTeamsServiceTest {
    @Mock CommunityChampionshipRepository championships;
    @Mock CommunityChampionshipTeamRepository seasonTeams;
    @Mock CommunityTournamentTeamRepository tournamentTeams;
    @Mock ProfileRepository profiles;
    @Mock CommunityMemberRepository members;
    @InjectMocks CommunityChampionshipTeamsService service;
    private CommunityChampionship season;
    private CommunityTournament tournament;
    private CommunityChampionshipTeam canonical;

    @BeforeEach
    void setup() {
        season = CommunityChampionship.builder().id(15L).competitions(new ArrayList<>()).build();
        tournament = CommunityTournament.builder().id(70L).name("Taça").build();
        season.getCompetitions().add(tournament);
        canonical = CommunityChampionshipTeam.builder().id(99L).name("Águias")
                .normalizedName("águias").championship(season).createdAt(Instant.now()).build();
    }

    private void organizer() {
        var actor = new Profile();
        actor.setId(33L);
        when(profiles.findUserByUsername("owner")).thenReturn(Optional.of(actor));
        when(members.findByCommunityIdAndProfileId(7L,33L))
                .thenReturn(Optional.of(CommunityMember.builder().role(CommunityRole.OWNER).build()));
        when(championships.findByIdAndCommunityId(15L,7L)).thenReturn(Optional.of(season));
    }

    @Test
    void linksTeamOnlyWhenTournamentBelongsToSeason() {
        organizer();
        var participant = CommunityTournamentTeam.builder().id(88L).name("Águias na Taça")
                .tournament(tournament).build();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(tournamentTeams.findById(88L)).thenReturn(Optional.of(participant));
        when(tournamentTeams.findByTournamentIdOrderByIdAsc(70L)).thenReturn(List.of(participant));
        var response = service.link(7L,15L,88L,99L,"owner");
        assertEquals(99L,participant.getChampionshipTeam().getId());
        assertEquals(99L,response.competitionTeams().get(0).championshipTeamId());
        verify(tournamentTeams).saveAndFlush(participant);
    }

    @Test
    void refusesCanonicalTeamFromAnotherChampionship() {
        organizer();
        canonical.setChampionship(CommunityChampionship.builder().id(16L).build());
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        assertThrows(ResponseStatusException.class, () -> service.link(7L,15L,88L,99L,"owner"));
        verifyNoInteractions(tournamentTeams);
    }

    @Test
    void refusesTwoTournamentTeamsUsingSameSeasonIdentity() {
        organizer();
        var first = CommunityTournamentTeam.builder().id(88L).name("A").tournament(tournament).build();
        var other = CommunityTournamentTeam.builder().id(89L).name("B")
                .tournament(tournament).championshipTeam(canonical).build();
        when(seasonTeams.findById(99L)).thenReturn(Optional.of(canonical));
        when(tournamentTeams.findById(88L)).thenReturn(Optional.of(first));
        when(tournamentTeams.findByTournamentIdOrderByIdAsc(70L)).thenReturn(List.of(first,other));
        assertThrows(ResponseStatusException.class, () -> service.link(7L,15L,88L,99L,"owner"));
        verify(tournamentTeams,never()).saveAndFlush(first);
    }
}
