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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityChampionshipEventServiceTest {
    @Mock CommunityChampionshipRepository championships;
    @Mock CommunityChampionshipEventRepository events;
    @Mock CommunityChampionshipEventTeamRepository eventTeams;
    @Mock CommunityChampionshipFixtureRepository fixtures;
    @Mock CommunityChampionshipCupByeRepository cupByes;
    @Mock CommunityChampionshipTeamRepository championshipTeams;
    @Mock CommunityMemberRepository members;
    @Mock ProfileRepository profiles;
    @InjectMocks CommunityChampionshipEventService service;

    private CommunityChampionship championship;

    @BeforeEach
    void setup() {
        Community community = Community.builder().id(7L).name("Amigos").build();
        championship = CommunityChampionship.builder().id(15L).community(community)
                .name("2026/27").sport("Futebol").seasonLabel("2026/27")
                .createdAt(Instant.now()).build();
    }

    private void member(CommunityRole role) {
        Profile user = new Profile();
        user.setId(33L);
        user.setUsername("member");
        when(profiles.findUserByUsername("member")).thenReturn(Optional.of(user));
        when(members.findByCommunityIdAndProfileId(7L, 33L))
                .thenReturn(Optional.of(CommunityMember.builder().role(role).build()));
    }

    @Test
    void organizerCreatesOwnDraftWithoutTouchingStandaloneTournaments() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        when(events.save(any())).thenAnswer(invocation -> {
            CommunityChampionshipEvent event = invocation.getArgument(0);
            event.setId(100L);
            return event;
        });
        var created = service.create(7L, 15L, "member",
                new CommunityChampionshipEventService.CreateEvent("Taça de Inverno", "CUP"));
        assertEquals(100L, created.id());
        assertEquals("CUP", created.format());
        assertEquals("DRAFT", created.status());
        assertEquals(15L, created.championshipId());
        verify(events).save(any(CommunityChampionshipEvent.class));
    }

    @Test
    void refusesCreationByOrdinaryMember() {
        member(CommunityRole.MEMBER);
        assertThrows(AccessDeniedException.class, () -> service.create(7L, 15L,
                "member", new CommunityChampionshipEventService.CreateEvent("Liga", "LEAGUE")));
        verifyNoInteractions(events);
    }

    @Test
    void rejectsUnknownFormatsAndClosedChampionships() {
        member(CommunityRole.ADMIN);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        assertThrows(ResponseStatusException.class, () -> service.create(7L, 15L,
                "member", new CommunityChampionshipEventService.CreateEvent("Taça", "FOOTBALL")));
        championship.setClosedAt(Instant.now());
        assertThrows(ResponseStatusException.class, () -> service.create(7L, 15L,
                "member", new CommunityChampionshipEventService.CreateEvent("Taça", "CUP")));
        verifyNoInteractions(events);
    }

    @Test
    void cannotListEventsInAnotherCommunity() {
        member(CommunityRole.MEMBER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.list(7L, 15L, "member"));
        verifyNoInteractions(events);
    }

    @Test
    void ownerMayRenameDraftAndChangeItsFormatWithoutCreatingStandaloneTournament() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var draft = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Liga Original").format("LEAGUE").status("DRAFT").createdAt(Instant.now()).build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(draft));
        when(events.save(draft)).thenReturn(draft);
        var result = service.update(7L, 15L, 100L, "member",
                new CommunityChampionshipEventService.CreateEvent("Taça da Comunidade", "CUP"));
        assertEquals("Taça da Comunidade", result.name());
        assertEquals("CUP", result.format());
        verify(events).save(draft);
    }

    @Test
    void cannotModifyOrDeleteAnotherChampionshipEvent() {
        member(CommunityRole.ADMIN);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        when(events.lockForScheduling(200L, 15L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> service.update(7L, 15L, 200L,
                "member", new CommunityChampionshipEventService.CreateEvent("Taça", "CUP")));
        assertThrows(ResponseStatusException.class, () -> service.delete(7L, 15L, 200L, "member"));
        verify(events, never()).save(any());
        verify(events, never()).delete(any());
    }

    @Test
    void ordinaryMemberCannotEditOrDeleteOwnedCompetition() {
        member(CommunityRole.MEMBER);
        assertThrows(AccessDeniedException.class, () -> service.update(7L, 15L, 100L,
                "member", new CommunityChampionshipEventService.CreateEvent("Liga", "LEAGUE")));
        assertThrows(AccessDeniedException.class, () -> service.delete(7L, 15L, 100L, "member"));
        verifyNoInteractions(events);
    }

    @Test
    void cannotDeleteCompetitionAlreadyStartedOrBelongingToClosedChampionship() {
        member(CommunityRole.ADMIN);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var started = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Liga").format("LEAGUE").status("ACTIVE").createdAt(Instant.now()).build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(started));
        assertThrows(ResponseStatusException.class, () -> service.delete(7L, 15L, 100L, "member"));
        championship.setClosedAt(Instant.now());
        assertThrows(ResponseStatusException.class, () -> service.delete(7L, 15L, 100L, "member"));
        verify(events, never()).delete(any());
    }

    @Test
    void organizerCanDeleteOwnDraft() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var draft = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Supertaça").format("SUPERCUP").status("DRAFT").createdAt(Instant.now()).build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(draft));
        service.delete(7L, 15L, 100L, "member");
        verify(events).delete(draft);
    }

    @Test
    void generatesAndPersistsSingleLegOnlyForOwnLeague() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var league = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Liga").format("LEAGUE").status("DRAFT").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(league));
        var home = CommunityChampionshipTeam.builder().id(1L).championship(championship).name("Águias").build();
        var away = CommunityChampionshipTeam.builder().id(2L).championship(championship).name("Leões").build();
        when(eventTeams.findByEventIdOrderByIdAsc(100L)).thenReturn(List.of(
                CommunityChampionshipEventTeam.builder().id(21L).event(league).team(home).build(),
                CommunityChampionshipEventTeam.builder().id(22L).event(league).team(away).build()));
        when(fixtures.save(any())).thenAnswer(invocation -> {
            CommunityChampionshipFixture fixture = invocation.getArgument(0);
            fixture.setId(900L);
            return fixture;
        });
        when(events.saveAndFlush(league)).thenReturn(league);
        var generated = service.scheduleLeague(7L, 15L, 100L, 1, "member");
        assertEquals(1, generated.size());
        assertEquals(1, generated.get(0).round());
        assertEquals("SCHEDULED", league.getStatus());
        assertEquals(Set.of(1L, 2L),
                Set.of(generated.get(0).homeTeamId(), generated.get(0).awayTeamId()));
        verify(fixtures).save(any(CommunityChampionshipFixture.class));
    }

    @Test
    void refusesToGenerateScheduleTwice() {
        member(CommunityRole.ADMIN);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var league = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Liga").format("LEAGUE").status("DRAFT").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(league));
        when(fixtures.existsByEventId(100L)).thenReturn(true);
        assertThrows(ResponseStatusException.class,
                () -> service.scheduleLeague(7L, 15L, 100L, 1, "member"));
        verify(fixtures, never()).save(any());
        verify(events, never()).saveAndFlush(any());
    }

    @Test
    void cannotGenerateLeagueFixturesInsideACupOrAsOrdinaryMember() {
        member(CommunityRole.MEMBER);
        assertThrows(AccessDeniedException.class,
                () -> service.scheduleLeague(7L, 15L, 100L, 1, "member"));
        verifyNoInteractions(fixtures);
    }

    @Test
    void cannotGenerateLeagueScheduleForCupFormat() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var cup = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Taça").format("CUP").status("DRAFT").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(cup));
        assertThrows(ResponseStatusException.class,
                () -> service.scheduleLeague(7L, 15L, 100L, 1, "member"));
        verifyNoInteractions(fixtures);
    }

    @Test
    void scheduledLeagueRejectsFurtherRegistrations() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var league = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Liga").format("LEAGUE").status("SCHEDULED").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(league));
        assertThrows(ResponseStatusException.class,
                () -> service.addTeam(7L, 15L, 100L, 30L, "member"));
        verifyNoInteractions(eventTeams, championshipTeams);
    }

    @Test
    void openingCupDrawPersistsMatchesAndByesWithoutTouchingOtherTournaments() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var cup = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Taça").format("CUP").status("DRAFT").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(cup));
        var registrations = new java.util.ArrayList<CommunityChampionshipEventTeam>();
        for (long id = 1; id <= 3; id++) {
            var team = CommunityChampionshipTeam.builder().id(id).championship(championship)
                    .name("Equipa " + id).build();
            registrations.add(CommunityChampionshipEventTeam.builder()
                    .id(10 + id).event(cup).team(team).build());
        }
        when(eventTeams.findByEventIdOrderByIdAsc(100L)).thenReturn(registrations);
        when(fixtures.save(any())).thenAnswer(invocation -> {
            CommunityChampionshipFixture fixture = invocation.getArgument(0);
            fixture.setId(900L);
            return fixture;
        });
        when(cupByes.save(any())).thenAnswer(invocation -> {
            CommunityChampionshipCupBye bye = invocation.getArgument(0);
            bye.setId(901L);
            return bye;
        });
        when(events.saveAndFlush(cup)).thenReturn(cup);
        var result = service.drawCup(7L, 15L, 100L, "member");
        assertEquals(1, result.matches().size());
        assertEquals(1, result.byes().size());
        assertEquals("SCHEDULED", cup.getStatus());
        verify(fixtures).save(any(CommunityChampionshipFixture.class));
        verify(cupByes).save(any(CommunityChampionshipCupBye.class));
    }

    @Test
    void refusesCupDrawForNonOrganizerAndWrongFormat() {
        member(CommunityRole.MEMBER);
        assertThrows(AccessDeniedException.class,
                () -> service.drawCup(7L, 15L, 100L, "member"));
        verifyNoInteractions(fixtures, cupByes);
    }

    @Test
    void refusesRedrawOfExistingCup() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var cup = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Taça").format("CUP").status("DRAFT").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(cup));
        when(fixtures.existsByEventId(100L)).thenReturn(true);
        assertThrows(ResponseStatusException.class,
                () -> service.drawCup(7L, 15L, 100L, "member"));
        verify(fixtures, never()).save(any());
        verify(cupByes, never()).save(any());
    }

    @Test
    void organizerRegistersSeasonTeamForOwnedCupWithoutTouchingStandaloneTournaments() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var cup = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Taça").format("CUP").status("DRAFT").createdAt(Instant.now()).build();
        var team = CommunityChampionshipTeam.builder().id(30L).championship(championship)
                .name("Águias").normalizedName("águias").createdAt(Instant.now()).build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(cup));
        when(championshipTeams.findById(30L)).thenReturn(Optional.of(team));
        when(eventTeams.saveAndFlush(any())).thenAnswer(invocation -> {
            CommunityChampionshipEventTeam saved = invocation.getArgument(0);
            saved.setId(900L);
            return saved;
        });
        var registered = service.addTeam(7L, 15L, 100L, 30L, "member");
        assertEquals(900L, registered.id());
        assertEquals(30L, registered.championshipTeamId());
        assertEquals("Águias", registered.name());
        verify(eventTeams).saveAndFlush(any(CommunityChampionshipEventTeam.class));
    }

    @Test
    void cannotRegisterTeamFromAnotherChampionship() {
        member(CommunityRole.ADMIN);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var cup = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Taça").format("CUP").status("DRAFT").build();
        var otherChampionship = CommunityChampionship.builder().id(16L).build();
        var team = CommunityChampionshipTeam.builder().id(30L).championship(otherChampionship)
                .name("Equipa externa").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(cup));
        when(championshipTeams.findById(30L)).thenReturn(Optional.of(team));
        assertThrows(ResponseStatusException.class,
                () -> service.addTeam(7L, 15L, 100L, 30L, "member"));
        verify(eventTeams, never()).saveAndFlush(any());
    }

    @Test
    void duplicateTeamRegistrationIsRejected() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var league = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Liga").format("LEAGUE").status("DRAFT").build();
        var team = CommunityChampionshipTeam.builder().id(30L).championship(championship)
                .name("Águias").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(league));
        when(championshipTeams.findById(30L)).thenReturn(Optional.of(team));
        when(eventTeams.existsByEventIdAndTeamId(100L, 30L)).thenReturn(true);
        assertThrows(ResponseStatusException.class,
                () -> service.addTeam(7L, 15L, 100L, 30L, "member"));
        verify(eventTeams, never()).saveAndFlush(any());
    }

    @Test
    void ordinaryMembersCannotRegisterOrRemoveTeams() {
        member(CommunityRole.MEMBER);
        assertThrows(AccessDeniedException.class,
                () -> service.addTeam(7L, 15L, 100L, 30L, "member"));
        assertThrows(AccessDeniedException.class,
                () -> service.removeTeam(7L, 15L, 100L, 30L, "member"));
        verifyNoInteractions(eventTeams);
    }

    @Test
    void removeTeamAffectsOnlyTheSpecificOwnedCompetition() {
        member(CommunityRole.ADMIN);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var league = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .name("Liga").format("LEAGUE").status("DRAFT").build();
        var team = CommunityChampionshipTeam.builder().id(30L).championship(championship).name("Águias").build();
        var registration = CommunityChampionshipEventTeam.builder()
                .id(900L).event(league).team(team).build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(league));
        when(eventTeams.findByEventIdAndTeamId(100L, 30L)).thenReturn(Optional.of(registration));
        service.removeTeam(7L, 15L, 100L, 30L, "member");
        verify(eventTeams).delete(registration);
        verify(championshipTeams, never()).delete(any());
    }

    @Test
    void organizerRecordsScoreForOwnScheduledLeagueFixture() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var league = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .format("LEAGUE").status("SCHEDULED").build();
        var home = CommunityChampionshipTeam.builder().id(30L).name("Águias").build();
        var away = CommunityChampionshipTeam.builder().id(31L).name("Leões").build();
        var fixture = CommunityChampionshipFixture.builder().id(80L).event(league)
                .roundNumber(1).matchNumber(1).homeTeam(home).awayTeam(away).build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(league));
        when(fixtures.findById(80L)).thenReturn(Optional.of(fixture));
        when(fixtures.saveAndFlush(fixture)).thenReturn(fixture);
        var score = service.recordFixtureScore(7L, 15L, 100L, 80L,
                new CommunityChampionshipEventService.MatchScore(3, 1), "member");
        assertEquals(3, score.homeScore());
        assertEquals(1, score.awayScore());
        assertNotNull(fixture.getResultRecordedAt());
    }

    @Test
    void refusesInvalidOrCrossEventScoresWithoutPersisting() {
        member(CommunityRole.OWNER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        var league = CommunityChampionshipEvent.builder().id(100L).championship(championship)
                .format("LEAGUE").status("SCHEDULED").build();
        when(events.lockForScheduling(100L, 15L)).thenReturn(Optional.of(league));
        assertThrows(ResponseStatusException.class, () -> service.recordFixtureScore(
                7L, 15L, 100L, 80L, new CommunityChampionshipEventService.MatchScore(-1, 3), "member"));
        var other = CommunityChampionshipEvent.builder().id(101L).build();
        var fixture = CommunityChampionshipFixture.builder().id(80L).event(other).build();
        when(fixtures.findById(80L)).thenReturn(Optional.of(fixture));
        assertThrows(ResponseStatusException.class, () -> service.recordFixtureScore(
                7L, 15L, 100L, 80L, new CommunityChampionshipEventService.MatchScore(1, 0), "member"));
        verify(fixtures, never()).saveAndFlush(any());
    }

    @Test
    void regularMemberCannotRecordOwnedFixtureScores() {
        member(CommunityRole.MEMBER);
        assertThrows(AccessDeniedException.class, () -> service.recordFixtureScore(
                7L, 15L, 100L, 80L, new CommunityChampionshipEventService.MatchScore(1, 0), "member"));
        verifyNoInteractions(fixtures);
    }

    @Test
    void memberMayListOnlyTheSpecifiedChampionshipOwnEvents() {
        member(CommunityRole.MEMBER);
        when(championships.findByIdAndCommunityId(15L, 7L)).thenReturn(Optional.of(championship));
        when(events.findByChampionshipIdOrderByCreatedAtAscIdAsc(15L)).thenReturn(List.of(
                CommunityChampionshipEvent.builder().id(100L).championship(championship)
                        .name("Liga Principal").format("LEAGUE").status("DRAFT")
                        .createdAt(Instant.now()).build()));
        var listed = service.list(7L, 15L, "member");
        assertEquals(1, listed.size());
        assertEquals("LEAGUE", listed.get(0).format());
    }
}
