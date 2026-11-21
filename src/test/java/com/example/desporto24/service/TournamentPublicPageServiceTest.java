package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentMatchStatus;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.community.CommunityTournamentMatch;
import com.example.desporto24.model.community.CommunityTournamentParticipant;
import com.example.desporto24.model.dto.community.CommunityTournamentStandingRowResponse;
import com.example.desporto24.model.dto.community.CommunityTournamentStandingsResponse;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityTournamentMatchRepository;
import com.example.desporto24.repository.community.CommunityTournamentRepository;
import com.example.desporto24.repository.community.CommunityTournamentTeamMatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.SliceImpl;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TournamentPublicPageServiceTest {
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final CommunityMemberRepository members = mock(CommunityMemberRepository.class);
    private final CommunityTournamentRepository tournaments = mock(CommunityTournamentRepository.class);
    private final CommunityTournamentMatchRepository matches = mock(CommunityTournamentMatchRepository.class);
    private final CommunityTournamentStandingsService standings = mock(CommunityTournamentStandingsService.class);
    private final TournamentPublicPageService service = new TournamentPublicPageService(profiles, members, tournaments,
            matches, mock(CommunityTournamentTeamMatchRepository.class), standings,
            mock(CommunityTournamentTeamStandingsService.class));
    private CommunityTournament tournament;

    private Profile profile(long id, String username, String nickname) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        p.setNickname(nickname);
        when(profiles.findUserByUsernameIgnoreCase(username)).thenReturn(Optional.of(p));
        return p;
    }

    private void role(Profile profile, CommunityRole role) {
        when(members.findByCommunityIdAndProfileId(5L, profile.getId()))
                .thenReturn(Optional.of(CommunityMember.builder().role(role).build()));
    }

    @BeforeEach
    void setUp() {
        Community community = new Community();
        community.setId(5L);
        community.setName("Colégio Pio");
        tournament = new CommunityTournament();
        tournament.setId(9L);
        tournament.setName("Liga de Inverno");
        tournament.setSport("Futsal");
        tournament.setCommunity(community);
        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournament.setEntryType(TournamentEntryType.INDIVIDUAL);
        when(tournaments.findById(9L)).thenReturn(Optional.of(tournament));
    }

    @Test
    void pageIsHiddenUntilAnAdminEnablesIt() {
        assertThrows(ResponseStatusException.class, () -> service.page(9L));
        Profile ana = profile(1, "ana", null), rui = profile(2, "rui", null);
        role(ana, CommunityRole.ADMIN);
        role(rui, CommunityRole.MEMBER);
        assertThrows(ResponseStatusException.class, () -> service.update(5L, 9L, "rui", true), "members cannot share");
        var setting = service.update(5L, 9L, "ana", true);
        assertTrue(setting.enabled());
        assertEquals("/torneio/9", setting.path());
        assertTrue(tournament.isPublicPageEnabled());
        assertThrows(ResponseStatusException.class, () -> service.update(99L, 9L, "ana", true));
    }

    @Test
    void publicPageShowsTableAndGamesWithDisplayNamesOnly() {
        tournament.setPublicPageEnabled(true);
        when(standings.publicStandings(5L, 9L)).thenReturn(new CommunityTournamentStandingsResponse(9L,
                TournamentStatus.IN_PROGRESS, 3, 1, 3, 1, 0, List.of(
                        new CommunityTournamentStandingRowResponse(1, 1L, "rui_secret", "Rui", 1, 1, 0, 0, 3, 1, 2, 3),
                        new CommunityTournamentStandingRowResponse(2, 2L, "ana_secret", "Ana", 1, 0, 0, 1, 1, 3, -2, 0))));
        CommunityTournamentParticipant home = new CommunityTournamentParticipant();
        home.setProfile(profile(1, "rui_secret", "Rui"));
        CommunityTournamentParticipant away = new CommunityTournamentParticipant();
        away.setProfile(profile(2, "ana_secret", "Ana"));
        CommunityTournamentMatch game = new CommunityTournamentMatch();
        game.setRoundNumber(1);
        game.setHomeParticipant(home);
        game.setAwayParticipant(away);
        game.setStatus(TournamentMatchStatus.COMPLETED);
        game.setHomeScore(3);
        game.setAwayScore(1);
        when(matches.findByTournamentId(eq(9L), any())).thenReturn(new SliceImpl<>(List.of(game)));

        var page = service.page(9L);
        assertEquals("Liga de Inverno", page.name());
        assertEquals("Colégio Pio", page.communityName());
        assertEquals("Rui", page.table().getFirst().name());
        assertEquals("Rui", page.games().getFirst().home());
        assertNull(page.champion(), "no champion while in progress");
        assertFalse(page.toString().contains("_secret"), "usernames are never exposed");
    }
}
