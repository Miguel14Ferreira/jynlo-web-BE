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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityChampionshipEventServiceTest {
    @Mock CommunityChampionshipRepository championships;
    @Mock CommunityChampionshipEventRepository events;
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
        when(events.findByIdAndChampionshipId(100L, 15L)).thenReturn(Optional.of(draft));
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
        when(events.findByIdAndChampionshipId(200L, 15L)).thenReturn(Optional.empty());
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
        when(events.findByIdAndChampionshipId(100L, 15L)).thenReturn(Optional.of(started));
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
        when(events.findByIdAndChampionshipId(100L, 15L)).thenReturn(Optional.of(draft));
        service.delete(7L, 15L, 100L, "member");
        verify(events).delete(draft);
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
