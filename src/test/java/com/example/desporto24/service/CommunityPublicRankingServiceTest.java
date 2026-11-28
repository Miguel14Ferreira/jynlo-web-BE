package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.model.dto.community.CommunityCompetitionOverviewResponse;
import com.example.desporto24.model.dto.community.CommunitySeasonResponse;
import com.example.desporto24.model.dto.community.CommunityStandingResponse;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CommunityPublicRankingServiceTest {
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final CommunityMemberRepository members = mock(CommunityMemberRepository.class);
    private final CommunityRepository communities = mock(CommunityRepository.class);
    private final CommunityCompetitionService competition = mock(CommunityCompetitionService.class);
    private final CommunityPublicRankingService service =
            new CommunityPublicRankingService(profiles, members, communities, competition);
    private Community community;

    private void member(long id, String username, CommunityRole role) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        when(profiles.findUserByUsernameIgnoreCase(username)).thenReturn(Optional.of(p));
        when(members.findByCommunityIdAndProfileId(5L, id))
                .thenReturn(Optional.of(CommunityMember.builder().role(role).build()));
    }

    @BeforeEach
    void setUp() {
        community = new Community();
        community.setId(5L);
        community.setName("Colégio Pio");
        community.setSport("Futebol");
        when(communities.findById(5L)).thenReturn(Optional.of(community));
    }

    private CommunityStandingResponse row(int position, String username, String fullName, int games, int wins) {
        return new CommunityStandingResponse(position, (long) position, username, fullName, "https://pic", games, wins,
                0, games - wins, wins * 3, 0, 0, 0, 0, games == 0 ? 0 : 100.0 * wins / games, "V2", 2);
    }

    @Test
    void onlyAdminsShareAndDisabledAnswers404() {
        assertThrows(ResponseStatusException.class, () -> service.page(5L, null));
        member(1, "ana", CommunityRole.OWNER);
        member(2, "rui", CommunityRole.MEMBER);
        assertThrows(ResponseStatusException.class, () -> service.update(5L, "rui", true));
        var setting = service.update(5L, "ana", true);
        assertEquals("/rankings/5", setting.path());
        assertTrue(community.isPublicRankingEnabled());
        assertTrue(service.setting(5L, "rui").enabled());
    }

    private Profile account(long id, String username, String fullName) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        p.setFullName(fullName);
        when(profiles.findById(id)).thenReturn(Optional.of(p));
        return p;
    }

    @Test
    void publicTableShowsTheSpreadsheetNamesButNeverAccountFullNames() {
        community.setPublicRankingEnabled(true);
        account(1L, "rui", "Rui Real Silva");
        account(2L, "ana", "Ana Real");
        CommunityStandingResponse guest = new CommunityStandingResponse(3, null, null, "Zé Guarda-Redes", null, 8, 4,
                0, 4, 12, 0, 0, 0, 0, 50.0, "W1", 2);
        when(competition.publicOverview(5L, null)).thenReturn(new CommunityCompetitionOverviewResponse(
                null, List.of(new CommunitySeasonResponse(3L, "Época 25/26", null, null, true, null, null, null, "Futebol")),
                null, 3L, List.of(),
                List.of(row(1, "rui", "Rui Real Silva", 10, 7), row(2, "ana", "Aninhas", 10, 5), guest,
                        row(4, "bea", "Bea", 0, 0)),
                List.of(), List.of()));
        var page = service.page(5L, null);
        assertEquals(3L, page.selectedSeasonId());
        assertEquals(3, page.table().size(), "players without games are left out");
        assertEquals("rui", page.table().get(0).name(), "an account known only by its full name shows its handle");
        assertEquals("Aninhas", page.table().get(1).name(), "the name used in the table wins over the handle");
        assertEquals("Zé Guarda-Redes", page.table().get(2).name(), "players imported without an account keep their name");
        assertEquals(70.0, page.table().getFirst().winRate());
        assertFalse(page.toString().contains("Real"), "account full names never leave the app");
        assertFalse(page.toString().contains("https://pic"), "no photos");
    }
}
