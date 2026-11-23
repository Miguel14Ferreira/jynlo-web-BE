package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TournamentPaymentServiceTest {
    private final CommunityTournamentRepository tournaments = mock(CommunityTournamentRepository.class);
    private final CommunityMemberRepository members = mock(CommunityMemberRepository.class);
    private final CommunityTournamentParticipantRepository participants = mock(CommunityTournamentParticipantRepository.class);
    private final CommunityTournamentTeamPlayerRepository teamPlayers = mock(CommunityTournamentTeamPlayerRepository.class);
    private final TournamentPaymentSetupRepository setups = mock(TournamentPaymentSetupRepository.class);
    private final TournamentEntryPaymentRepository payments = mock(TournamentEntryPaymentRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final TournamentPaymentService service = new TournamentPaymentService(
            tournaments, members, participants, teamPlayers, setups, payments, profiles);

    private Profile owner;
    private Profile player;
    private Profile outsider;

    private Profile profile(long id, String username) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        when(profiles.findUserByUsername(username)).thenReturn(Optional.of(p));
        return p;
    }

    private void member(Profile p, CommunityRole role) {
        CommunityMember m = new CommunityMember();
        m.setProfile(p);
        m.setRole(role);
        when(members.findByCommunityIdAndProfileId(3L, p.getId())).thenReturn(Optional.of(m));
    }

    @BeforeEach
    void setUp() {
        owner = profile(1L, "ana");
        player = profile(2L, "rui");
        outsider = profile(4L, "zeca");
        member(owner, CommunityRole.OWNER);
        member(player, CommunityRole.MEMBER);
        member(outsider, CommunityRole.MEMBER);
        Community community = new Community();
        community.setId(3L);
        CommunityTournament tournament = new CommunityTournament();
        tournament.setId(7L);
        tournament.setName("Torneio de Padel");
        tournament.setCommunity(community);
        when(tournaments.findById(7L)).thenReturn(Optional.of(tournament));
        CommunityTournamentParticipant entry = new CommunityTournamentParticipant();
        entry.setProfile(player);
        when(participants.findByTournamentIdOrderByIdAsc(7L)).thenReturn(List.of(entry));
        when(participants.existsByTournamentIdAndProfileId(7L, 2L)).thenReturn(true);
        when(payments.findByTournamentIdAndProfileId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(payments.save(any(TournamentEntryPayment.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void onlyOrganisersSetTheFeeAndMbwayMustBeAPortugueseMobile() {
        assertThrows(ResponseStatusException.class, () -> service.setup(3L, 7L, "rui", 1000, "MBWAY", "912345678"));
        assertThrows(ResponseStatusException.class, () -> service.setup(3L, 7L, "ana", 1000, "MBWAY", "12345"));
        service.setup(3L, 7L, "ana", 1000, "MBWAY", "+351 913 000 111");
        verify(setups).save(argThat(s -> s.getEntryFeeCents() == 1000 && "913000111".equals(s.getMbwayPhone())));
    }

    @Test
    void onlyPlayersInTheTournamentSeeTheNumberAndMarkItPaid() {
        TournamentPaymentSetup setup = new TournamentPaymentSetup();
        setup.setTournamentId(7L);
        setup.setEntryFeeCents(1000);
        setup.setMethod("MBWAY");
        setup.setMbwayPhone("913000111");
        when(setups.findById(7L)).thenReturn(Optional.of(setup));

        TournamentPaymentService.Payments forOutsider = service.get(3L, 7L, "zeca");
        assertNull(forOutsider.mbwayPhone());
        assertThrows(ResponseStatusException.class, () -> service.claim(3L, 7L, "zeca", true));

        TournamentPaymentService.Payments forPlayer = service.claim(3L, 7L, "rui", true);
        assertEquals("913000111", forPlayer.mbwayPhone());
        verify(payments).save(argThat(p -> p.getProfileId() == 2L && p.getClaimedAt() != null));

        assertThrows(ResponseStatusException.class, () -> service.confirm(3L, 7L, "rui", "rui", true));
        assertThrows(ResponseStatusException.class, () -> service.confirm(3L, 7L, "ana", "zeca", true));
        service.confirm(3L, 7L, "ana", "rui", true);
        verify(payments).save(argThat(p -> p.getProfileId() == 2L && p.getConfirmedAt() != null));
    }
}
