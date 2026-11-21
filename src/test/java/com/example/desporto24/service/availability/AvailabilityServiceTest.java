package com.example.desporto24.service.availability;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.PrivateGroup;
import com.example.desporto24.model.PrivateGroupMember;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.PrivateGroupMemberRepository;
import com.example.desporto24.repository.PrivateGroupRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.service.ConversationNotificationMuteService;
import com.example.desporto24.service.NotificationPreferencesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-21T18:00:00Z");
    private static final Instant SAT = Instant.parse("2026-11-28T10:00:00Z");
    private static final Instant SUN = Instant.parse("2026-11-29T10:00:00Z");

    private final AvailabilityStore store = mock(AvailabilityStore.class);
    private final PrivateGroupRepository groups = mock(PrivateGroupRepository.class);
    private final PrivateGroupMemberRepository groupMembers = mock(PrivateGroupMemberRepository.class);
    private final NotificationsRepository notifications = mock(NotificationsRepository.class);
    private final NotificationPreferencesService preferences = mock(NotificationPreferencesService.class);
    private final ConversationNotificationMuteService mutes = mock(ConversationNotificationMuteService.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final AvailabilityService service = new AvailabilityService(store, groups, groupMembers,
            mock(CommunityRepository.class), mock(CommunityMemberRepository.class), notifications, preferences, mutes, events);

    private final AvailabilityStore.Request request =
            new AvailabilityStore.Request(9, "private", 5, "ana", "Padel?", NOW, null, null);

    @BeforeEach
    void setUp() {
        service.clock = Clock.fixed(NOW, ZoneOffset.UTC);
        List<PrivateGroupMember> members = new ArrayList<>();
        for (String name : List.of("ana", "Rui", "joao")) {
            PrivateGroupMember m = new PrivateGroupMember();
            m.setGroupId(5L);
            m.setUsername(name);
            members.add(m);
        }
        when(groupMembers.findByGroupIdOrderByJoinedAtAsc(5L)).thenReturn(members);
        PrivateGroup group = new PrivateGroup();
        group.setTitle("Padel de sábado");
        when(groups.findById(5L)).thenReturn(Optional.of(group));
        when(preferences.allowsInApp(anyString(), any(Notifications.class))).thenReturn(true);
        when(notifications.save(any())).thenAnswer(i -> { Notifications n = i.getArgument(0); n.setId(1L); return n; });
        when(store.find(9L)).thenReturn(Optional.of(request));
        when(store.slots(9L)).thenReturn(List.of(new AvailabilityStore.Slot(1, 9, SAT), new AvailabilityStore.Slot(2, 9, SUN)));
        when(store.answers(9L)).thenReturn(List.of());
    }

    @Test
    void createsAndAlertsTheOtherMembersOnly() {
        when(store.create(eq("private"), eq(5L), eq("ana"), eq("Padel?"), anyList(), eq(NOW))).thenReturn(9L);
        var view = service.create(new AvailabilityService.CreateRequest("private", 5L, " Padel? ", List.of(SUN, SAT, SAT)), "ana");
        assertEquals(2, view.slots().size());
        ArgumentCaptor<List<Instant>> slots = ArgumentCaptor.captor();
        verify(store).create(any(), anyLong(), any(), any(), slots.capture(), any());
        assertEquals(List.of(SAT, SUN), slots.getValue(), "sorted and without duplicates");
        ArgumentCaptor<Notifications> sent = ArgumentCaptor.forClass(Notifications.class);
        verify(notifications, times(2)).save(sent.capture());
        assertTrue(sent.getAllValues().stream().allMatch(n -> n.getType() == NotificationType.AVAILABILITY_REQUEST
                && n.getPrivateGroupId() == 5L && !n.getUsername().equals("ana")));
        assertTrue(sent.getAllValues().getFirst().getSubject().contains("Padel de sábado"));
    }

    @Test
    void respectsConversationMutes() {
        when(store.create(any(), anyLong(), any(), any(), anyList(), any())).thenReturn(9L);
        when(mutes.muted("rui", "private", "5")).thenReturn(true);
        service.create(new AvailabilityService.CreateRequest("private", 5L, null, List.of(SAT)), "ana");
        verify(notifications, times(1)).save(argThat(n -> n.getUsername().equals("joao")));
    }

    @Test
    void outsidersCannotSeeCreateOrAnswer() {
        assertThrows(ResponseStatusException.class, () -> service.open("private", 5L, "stranger"));
        assertThrows(ResponseStatusException.class, () -> service.create(
                new AvailabilityService.CreateRequest("private", 5L, null, List.of(SAT)), "stranger"));
        assertThrows(ResponseStatusException.class, () -> service.answer(9L, 1L,
                new AvailabilityService.AnswerRequest("YES"), "stranger"));
        verify(store, never()).answer(anyLong(), any(), any(), any());
    }

    @Test
    void validatesOptions() {
        assertThrows(ResponseStatusException.class, () -> AvailabilityService.slots(List.of(), NOW));
        assertThrows(ResponseStatusException.class, () -> AvailabilityService.slots(List.of(NOW.plusSeconds(600)), NOW));
        assertThrows(ResponseStatusException.class, () -> AvailabilityService.slots(List.of(NOW.plus(Duration.ofDays(61))), NOW));
        List<Instant> six = new ArrayList<>();
        for (int i = 1; i <= 6; i++) six.add(NOW.plus(Duration.ofDays(i)));
        assertThrows(ResponseStatusException.class, () -> AvailabilityService.slots(six, NOW));
        assertThrows(ResponseStatusException.class, () -> AvailabilityService.note("x".repeat(201)));
        assertNull(AvailabilityService.note("  "));
    }

    @Test
    void limitsOpenRequestsPerGroup() {
        when(store.openCount(eq("private"), eq(5L), any())).thenReturn(AvailabilityService.MAX_OPEN_PER_TARGET);
        assertThrows(ResponseStatusException.class, () -> service.create(
                new AvailabilityService.CreateRequest("private", 5L, null, List.of(SAT)), "ana"));
    }

    @Test
    void answersUseTheMembersOwnNameAndRejectBadValues() {
        service.answer(9L, 2L, new AvailabilityService.AnswerRequest("maybe"), "rui");
        verify(store).answer(2L, "Rui", "MAYBE", NOW);
        assertThrows(ResponseStatusException.class, () -> service.answer(9L, 2L, new AvailabilityService.AnswerRequest("sure"), "rui"));
        assertThrows(ResponseStatusException.class, () -> service.answer(9L, 77L, new AvailabilityService.AnswerRequest("YES"), "rui"));
    }

    @Test
    void viewCountsAnswersAndIgnoresPeopleWhoLeft() {
        when(store.answers(9L)).thenReturn(List.of(
                new AvailabilityStore.Answer(1, "Rui", "YES"),
                new AvailabilityStore.Answer(1, "joao", "MAYBE"),
                new AvailabilityStore.Answer(2, "joao", "NO"),
                new AvailabilityStore.Answer(1, "left", "YES")));
        var view = service.open("private", 5L, "joao");
        assertEquals(0, view.size(), "store.open is not stubbed");
        var detail = service.view(request, "joao", List.of("ana", "Rui", "joao"));
        assertEquals(List.of("Rui"), detail.slots().get(0).yes());
        assertEquals(List.of("joao"), detail.slots().get(0).maybe());
        assertEquals("MAYBE", detail.slots().get(0).mine());
        assertEquals(1, detail.slots().get(1).no());
        assertEquals(2, detail.answered());
        assertEquals(3, detail.members());
        assertFalse(detail.mine());
    }

    @Test
    void onlyTheCreatorCloses() {
        assertThrows(ResponseStatusException.class, () -> service.close(9L, "rui"));
        service.close(9L, "ANA");
        verify(store).close(9L, NOW);
    }

    @Test
    void remindsOnlyMembersWhoHaveNotAnsweredOnce() {
        when(store.claimReminder(9L, NOW)).thenReturn(true, false);
        when(store.answers(9L)).thenReturn(List.of(new AvailabilityStore.Answer(1, "rui", "YES")));
        assertEquals(1, service.remind(9L, NOW));
        verify(notifications).save(argThat(n -> n.getUsername().equals("joao")
                && n.getType() == NotificationType.AVAILABILITY_REMINDER));
        assertEquals(0, service.remind(9L, NOW));
    }
}
