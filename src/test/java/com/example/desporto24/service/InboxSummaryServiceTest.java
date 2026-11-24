package com.example.desporto24.service;

import com.example.desporto24.Chat.ChatRepository;
import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Chat;
import com.example.desporto24.model.ChatReadCursor;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityTimelineEntry;
import com.example.desporto24.model.dto.chat.DirectReadReceiptResponse;
import com.example.desporto24.model.dto.chat.InboxReadRequest;
import com.example.desporto24.model.dto.chat.InboxRequest;
import com.example.desporto24.model.dto.chat.InboxSummaryResponse;
import com.example.desporto24.repository.ChatReadCursorRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.repository.community.CommunityTimelineEntryRepository;
import com.example.desporto24.repository.session.SessionMessageRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.service.interfaces.ChatService;
import com.example.desporto24.websocket.DirectChatReadConfirmedEvent;
import com.example.desporto24.websocket.InboxOwnReadUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxSummaryServiceTest {
    @Mock ProfileRepository profiles;
    @Mock ChatService chatService;
    @Mock ChatRepository chats;
    @Mock CommunityRepository communities;
    @Mock CommunityMemberRepository members;
    @Mock CommunityTimelineEntryRepository timelines;
    @Mock SessionRepository sessions;
    @Mock SessionParticipantRepository participants;
    @Mock SessionMessageRepository sessionMessages;
    @Mock ChatReadCursorRepository cursors;
    @Mock ApplicationEventPublisher events;
    @Mock com.example.desporto24.repository.PrivateGroupMemberRepository groupMembers;
    @Mock com.example.desporto24.repository.PrivateGroupMessageRepository groupMessages;
    @Mock ConversationNotificationMuteService mutes;
    @InjectMocks InboxSummaryService service;

    private Profile profile(long id, String username) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        return p;
    }

    private void actor() {
        when(profiles.findUserByUsernameIgnoreCase("ana"))
                .thenReturn(Optional.of(profile(1, "ana")));
    }

    private InboxRequest request(InboxRequest.Conversation... items) {
        return new InboxRequest(List.of(items));
    }

    private InboxRequest.Conversation reference(String kind, String id) {
        return new InboxRequest.Conversation(kind, id);
    }

    @Test
    void directSummaryProvidesPreviewAndPersistentUnreadCountWithoutLeakingOtherMessages() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2, "Bea")));
        Chat last = new Chat("Bea", "ana", "Olá", "", Date.from(Instant.parse("2026-10-23T08:00:00Z")));
        last.setId(45L);
        when(chats.findConversationPage(eq("ana"), eq("Bea"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(last)));
        ChatReadCursor cursor = new ChatReadCursor();
        cursor.setLastReadMessageId(40L);
        when(cursors.findByProfileIdAndConversationKindAndConversationKey(1L, "direct", "bea"))
                .thenReturn(Optional.of(cursor));
        when(chats.countUnread("ana", "Bea", 40L)).thenReturn(3L);

        List<InboxSummaryResponse> values = service.summaries("ana",
                request(reference("direct", "Bea"), reference("direct", "bea")));
        assertEquals(1, values.size(), "Canonical refs are deduplicated");
        assertEquals(45L, values.get(0).latestMessageId());
        assertEquals("Olá", values.get(0).latestText());
        assertEquals(3, values.get(0).unreadCount());
        verify(chatService, times(2)).getChatInfo("ana", "Bea");
    }

    @Test
    void revokedPermissionDuringDirectPreviewQueryDoesNotReturnSummary() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "bea")));
        Chat latest = new Chat("bea", "ana", "private", "",
                Date.from(Instant.parse("2026-10-23T08:00:00Z")));
        latest.setId(45L);
        when(chats.findConversationPage(eq("ana"), eq("bea"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(latest)));
        when(chatService.getChatInfo("ana", "bea"))
                .thenReturn(null)
                .thenThrow(new AccessDeniedException("Session contact permission expired"));

        assertTrue(service.summaries("ana",
                request(reference("direct", "bea"))).isEmpty());
        verify(chatService, times(2)).getChatInfo("ana", "bea");
        verify(chats).findConversationPage(eq("ana"), eq("bea"), any(Pageable.class));
        verifyNoInteractions(events);
    }

    @Test
    void revokedPermissionDuringEmptyDirectPreviewQueryDoesNotReturnConversation() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "bea")));
        when(chats.findConversationPage(eq("ana"), eq("bea"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(chatService.getChatInfo("ana", "bea"))
                .thenReturn(null)
                .thenThrow(new AccessDeniedException("Session contact permission expired"));

        assertTrue(service.summaries("ana",
                request(reference("direct", "bea"))).isEmpty());
        verify(chatService, times(2)).getChatInfo("ana", "bea");
        verifyNoInteractions(events);
    }

    @Test
    void removedCommunityMemberCannotSeePreviewNorAdvanceCursor() {
        actor();
        when(communities.findById(7L))
                .thenReturn(Optional.of(Community.builder()
                        .id(7L).owner(profile(90, "owner")).build()));
        var result = service.summaries("ana", request(reference("community", "7")));
        assertTrue(result.isEmpty());
        assertThrows(AccessDeniedException.class,
                () -> service.markRead("ana", new InboxReadRequest("community", "7", 999L)));
        verifyNoInteractions(timelines);
        verify(cursors, never()).advance(anyLong(), anyString(), anyString(), anyLong());
    }

    @Test
    void outsiderCannotSeeSessionMessagesOrMoveTheReadCursor() {
        actor();
        Session session = new Session();
        session.setId(8L);
        session.setGameDate(java.util.Date.from(Instant.now().plusSeconds(3600)));
        session.setProfile(profile(90, "organizer"));
        when(sessions.findById(8L)).thenReturn(Optional.of(session));
        assertTrue(service.summaries("ana", request(reference("session", "8"))).isEmpty());
        assertThrows(AccessDeniedException.class,
                () -> service.markRead("ana", new InboxReadRequest("session", "8", 1L)));
        verify(participants, atLeastOnce())
                .existsBySessionIdAndProfileIdAndState(8L, 1L, ParticipantState.ACTIVE);
        verifyNoInteractions(sessionMessages);
    }

    @Test
    void removedParticipantDuringEmptySessionPreviewCannotSeeConversation() {
        actor();
        Session session = new Session();
        session.setId(8L);
        session.setGameDate(Date.from(Instant.now().plusSeconds(3600)));
        session.setProfile(profile(90, "organizer"));
        when(sessions.findById(8L)).thenReturn(Optional.of(session));
        when(participants.existsBySessionIdAndProfileIdAndState(
                8L, 1L, ParticipantState.ACTIVE)).thenReturn(true, false);
        when(sessionMessages.findBySessionIdOrderByIdDesc(eq(8L), any(Pageable.class)))
                .thenReturn(List.of());

        assertTrue(service.summaries("ana", request(reference("session", "8"))).isEmpty());
        verify(participants, times(2))
                .existsBySessionIdAndProfileIdAndState(8L, 1L, ParticipantState.ACTIVE);
        verifyNoInteractions(events);
    }

    @Test
    void removedSessionParticipantBetweenPreviewAndCursorWriteCannotMarkRead() {
        actor();
        Session session = new Session();
        session.setId(8L);
        session.setGameDate(Date.from(Instant.now().plusSeconds(3600)));
        session.setProfile(profile(90, "organizer"));
        when(sessions.findById(8L)).thenReturn(Optional.of(session));
        when(participants.existsBySessionIdAndProfileIdAndState(
                8L, 1L, ParticipantState.ACTIVE)).thenReturn(true, true, false);
        var last = new com.example.desporto24.model.session.SessionMessage();
        last.setId(25L);
        last.setBody("Private session message");
        last.setSenderProfile(profile(90, "organizer"));
        last.setCreatedAt(Instant.now());
        when(sessionMessages.findBySessionIdOrderByIdDesc(eq(8L), any(Pageable.class)))
                .thenReturn(List.of(last));

        assertThrows(AccessDeniedException.class,
                () -> service.markRead("ana", new InboxReadRequest("session", "8", 25L)));
        verify(participants, times(3))
                .existsBySessionIdAndProfileIdAndState(8L, 1L, ParticipantState.ACTIVE);
        verify(cursors, never()).advance(anyLong(), anyString(), anyString(), anyLong());
        verifyNoInteractions(events);
    }

    @Test
    void readCursorNeverAdvancesBeyondTheLatestAuthorizedMessage() {
        actor();
        when(communities.findById(7L)).thenReturn(Optional.of(Community.builder()
                .id(7L).owner(profile(1, "ana")).build()));
        CommunityTimelineEntry last = CommunityTimelineEntry.builder()
                .id(12L).body("Olá").createdAt(Instant.now())
                .community(Community.builder().id(7L).build())
                .author(profile(90, "someone")).kind(CommunityTimelineEntry.Kind.CHAT).build();
        when(timelines.findByCommunityIdAndKindOrderByIdDesc(eq(7L),
                eq(CommunityTimelineEntry.Kind.CHAT), any(Pageable.class)))
                .thenReturn(List.of(last));
        when(timelines.countUnread(eq(7L), eq(CommunityTimelineEntry.Kind.CHAT), eq(1L), anyLong()))
                .thenReturn(2L);
        var result = service.markRead("ana", new InboxReadRequest("community", "7", Long.MAX_VALUE));
        verify(cursors).advance(1L, "community", "7", 12L);
        verify(events).publishEvent(new InboxOwnReadUpdatedEvent("ana", "community", "7", 12L));
        assertEquals(12L, result.latestMessageId());
    }

    @Test
    void readingACommunityChatClearsItsUnreadMessageAlerts() {
        actor();
        var bell = org.mockito.Mockito.mock(com.example.desporto24.repository.NotificationsRepository.class);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "notifications", bell);
        when(communities.findById(7L)).thenReturn(Optional.of(Community.builder()
                .id(7L).owner(profile(1, "ana")).build()));
        CommunityTimelineEntry last = CommunityTimelineEntry.builder()
                .id(12L).body("Olá").createdAt(Instant.now())
                .community(Community.builder().id(7L).build())
                .author(profile(90, "someone")).kind(CommunityTimelineEntry.Kind.CHAT).build();
        when(timelines.findByCommunityIdAndKindOrderByIdDesc(eq(7L),
                eq(CommunityTimelineEntry.Kind.CHAT), any(Pageable.class)))
                .thenReturn(List.of(last));
        var alert = new com.example.desporto24.register.Notifications.Notifications();
        alert.setRead(false);
        when(bell.findByUsernameIgnoreCaseAndCommunityIdAndTypeAndReadFalse("ana", 7L,
                com.example.desporto24.enumeration.NotificationType.CHAT_MESSAGE)).thenReturn(List.of(alert));
        service.markRead("ana", new InboxReadRequest("community", "7", 12L));
        assertTrue(alert.isRead());
        verify(bell).saveAll(List.of(alert));
    }

    @Test
    void authorizedDirectReceiptReadsOnlyThePeerCursorForThisConversation() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "Bea")));
        ChatReadCursor cursor = new ChatReadCursor();
        cursor.setLastReadMessageId(55L);
        when(cursors.findByProfileIdAndConversationKindAndConversationKey(
                2L, "direct", "ana")).thenReturn(Optional.of(cursor));

        DirectReadReceiptResponse receipt = service.peerDirectReadReceipt("ana", "BEA");
        assertEquals("bea", receipt.peerUsername());
        assertEquals(55L, receipt.lastReadMessageId());
        verify(chatService, times(2)).getChatInfo("ana", "Bea");
        verify(cursors).findByProfileIdAndConversationKindAndConversationKey(2L, "direct", "ana");
        verifyNoInteractions(chats);
    }

    @Test
    void absentReceiptDoesNotPretendMessageWasRead() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "bea")));
        assertEquals(0L, service.peerDirectReadReceipt("ana", "bea").lastReadMessageId());
    }

    @Test
    void deniedOrBlockedDirectChatNeverLeaksPeerCursor() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "bea")));
        doThrow(new AccessDeniedException("Chat blocked"))
                .when(chatService).getChatInfo("ana", "bea");
        assertThrows(AccessDeniedException.class,
                () -> service.peerDirectReadReceipt("ana", "bea"));
        verifyNoInteractions(cursors);
    }

    @Test
    void revokedSessionContactCannotReadDirectPreviewOrAdvanceCursor() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "bea")));
        doThrow(new AccessDeniedException("Session contact permission expired"))
                .when(chatService).getChatInfo("ana", "bea");

        assertTrue(service.summaries("ana",
                request(reference("direct", "bea"))).isEmpty());
        assertThrows(AccessDeniedException.class,
                () -> service.markRead("ana",
                        new InboxReadRequest("direct", "bea", Long.MAX_VALUE)));
        assertThrows(AccessDeniedException.class,
                () -> service.requireConversationAccess("ana", "direct", "bea"));

        verify(chatService, times(3)).getChatInfo("ana", "bea");
        verifyNoInteractions(chats);
        verify(cursors, never()).advance(anyLong(), anyString(), anyString(), anyLong());
        verifyNoInteractions(events);
    }

    @Test
    void revokedSessionContactCannotInspectPeerReadReceiptAfterEarlierAuthorizedAccess() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "bea")));
        ChatReadCursor peerCursor = new ChatReadCursor();
        peerCursor.setLastReadMessageId(31L);
        when(cursors.findByProfileIdAndConversationKindAndConversationKey(2L, "direct", "ana"))
                .thenReturn(Optional.of(peerCursor));
        when(chatService.getChatInfo("ana", "bea"))
                .thenReturn(null, null)
                .thenThrow(new AccessDeniedException("Session contact permission expired"));

        assertEquals(31L, service.peerDirectReadReceipt("ana", "bea").lastReadMessageId());
        assertThrows(AccessDeniedException.class,
                () -> service.peerDirectReadReceipt("ana", "bea"));

        verify(chatService, times(3)).getChatInfo("ana", "bea");
        verify(cursors, times(1))
                .findByProfileIdAndConversationKindAndConversationKey(2L, "direct", "ana");
        verifyNoInteractions(chats, events);
    }

    @Test
    void revokedPermissionDuringPeerCursorLookupDoesNotDiscloseReceipt() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "bea")));
        ChatReadCursor peerCursor = new ChatReadCursor();
        peerCursor.setLastReadMessageId(99L);
        when(cursors.findByProfileIdAndConversationKindAndConversationKey(2L, "direct", "ana"))
                .thenReturn(Optional.of(peerCursor));
        when(chatService.getChatInfo("ana", "bea"))
                .thenReturn(null)
                .thenThrow(new AccessDeniedException("Session contact permission expired"));

        assertThrows(AccessDeniedException.class,
                () -> service.peerDirectReadReceipt("ana", "bea"));
        verify(chatService, times(2)).getChatInfo("ana", "bea");
        verify(cursors).findByProfileIdAndConversationKindAndConversationKey(2L, "direct", "ana");
        verifyNoInteractions(chats, events);
    }

    @Test
    void malformedPeerAndSelfReceiptAreDeniedBeforeLookingUpCursor() {
        actor();
        assertThrows(ResponseStatusException.class,
                () -> service.peerDirectReadReceipt("ana", "../../admin"));
        when(profiles.findUserByUsernameIgnoreCase("ana"))
                .thenReturn(Optional.of(profile(1L, "ana")));
        assertThrows(AccessDeniedException.class,
                () -> service.peerDirectReadReceipt("ana", "ana"));
        verifyNoInteractions(cursors);
    }

    @Test
    void revokedDirectPermissionBetweenPreviewAndCursorWriteCannotMarkRead() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "bea")));
        Chat latest = new Chat("bea", "ana", "Olá", "",
                Date.from(Instant.parse("2026-10-23T08:00:00Z")));
        latest.setId(45L);
        when(chats.findConversationPage(eq("ana"), eq("bea"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(latest)));
        when(chatService.getChatInfo("ana", "bea"))
                .thenReturn(null)
                .thenThrow(new AccessDeniedException("Session contact permission expired"));

        assertThrows(AccessDeniedException.class,
                () -> service.markRead("ana", new InboxReadRequest("direct", "bea", 45L)));

        verify(chatService, times(2)).getChatInfo("ana", "bea");
        verify(cursors, never()).advance(anyLong(), anyString(), anyString(), anyLong());
        verifyNoInteractions(events);
    }

    @Test
    void advancingDirectReadCursorNotifiesOnlyAfterPersistingTheClampedWatermark() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "Bea")));
        Chat latest = new Chat("Bea", "ana", "Olá", "",
                Date.from(Instant.parse("2026-10-23T08:00:00Z")));
        latest.setId(45L);
        when(chats.findConversationPage(eq("ana"), eq("Bea"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(latest)));
        ChatReadCursor previous = new ChatReadCursor();
        previous.setLastReadMessageId(40L);
        when(cursors.findByProfileIdAndConversationKindAndConversationKey(1L, "direct", "bea"))
                .thenReturn(Optional.of(previous));

        service.markRead("ana", new InboxReadRequest("direct", "bea", Long.MAX_VALUE));

        var order = inOrder(cursors, events);
        order.verify(cursors).advance(1L, "direct", "bea", 45L);
        order.verify(events).publishEvent(new InboxOwnReadUpdatedEvent("ana", "direct", "bea", 45L));
        order.verify(events).publishEvent(new DirectChatReadConfirmedEvent("ana", "Bea", 45L));
    }

    @Test
    void alreadyReadDirectMessageDoesNotRepublishReadEvent() {
        actor();
        when(profiles.findUserByUsernameIgnoreCase("bea"))
                .thenReturn(Optional.of(profile(2L, "Bea")));
        Chat latest = new Chat("Bea", "ana", "Olá", "",
                Date.from(Instant.parse("2026-10-23T08:00:00Z")));
        latest.setId(45L);
        when(chats.findConversationPage(eq("ana"), eq("Bea"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(latest)));
        ChatReadCursor previous = new ChatReadCursor();
        previous.setLastReadMessageId(45L);
        when(cursors.findByProfileIdAndConversationKindAndConversationKey(1L, "direct", "bea"))
                .thenReturn(Optional.of(previous));

        service.markRead("ana", new InboxReadRequest("direct", "bea", 45L));

        verify(cursors, never()).advance(anyLong(), anyString(), anyString(), anyLong());
        verifyNoInteractions(events);
    }

    @Test
    void unchangedCommunityCursorDoesNotEmitAnotherOwnReadEvent() {
        actor();
        when(communities.findById(7L)).thenReturn(Optional.of(Community.builder()
                .id(7L).owner(profile(1, "ana")).build()));
        CommunityTimelineEntry last = CommunityTimelineEntry.builder()
                .id(12L).body("Olá").createdAt(Instant.now())
                .community(Community.builder().id(7L).build())
                .author(profile(90, "someone")).kind(CommunityTimelineEntry.Kind.CHAT).build();
        when(timelines.findByCommunityIdAndKindOrderByIdDesc(eq(7L),
                eq(CommunityTimelineEntry.Kind.CHAT), any(Pageable.class)))
                .thenReturn(List.of(last));
        ChatReadCursor cursor = new ChatReadCursor();
        cursor.setLastReadMessageId(12L);
        when(cursors.findByProfileIdAndConversationKindAndConversationKey(1L, "community", "7"))
                .thenReturn(Optional.of(cursor));
        service.markRead("ana", new InboxReadRequest("community", "7", 12L));
        verify(cursors, never()).advance(anyLong(), anyString(), anyString(), anyLong());
        verifyNoInteractions(events);
    }

    @Test
    void authorizedSessionReadAlsoNotifiesOnlyItsReader() {
        actor();
        Session session = new Session();
        session.setId(8L);
        session.setGameDate(Date.from(Instant.now().plusSeconds(3600)));
        session.setProfile(profile(1, "ana"));
        when(sessions.findById(8L)).thenReturn(Optional.of(session));
        var last = new com.example.desporto24.model.session.SessionMessage();
        last.setId(25L);
        last.setBody("Sessão");
        last.setSenderProfile(profile(90, "bea"));
        last.setCreatedAt(Instant.now());
        when(sessionMessages.findBySessionIdOrderByIdDesc(eq(8L), any(Pageable.class)))
                .thenReturn(List.of(last));
        service.markRead("ana", new InboxReadRequest("session", "8", 25L));
        verify(cursors).advance(1L, "session", "8", 25L);
        verify(events).publishEvent(new InboxOwnReadUpdatedEvent("ana", "session", "8", 25L));
        verify(events, never()).publishEvent(isA(DirectChatReadConfirmedEvent.class));
    }

    @Test
    void skipsUnknownKindsAndMalformedIdsWithoutQueryingMessages() {
        actor();
        // A malformed reference is ignored instead of failing the whole batch.
        assertTrue(service.summaries("ana", request(reference("unknown", "7"))).isEmpty());
        assertTrue(service.summaries("ana", request(reference("session", "1 OR 1=1"))).isEmpty());
        verifyNoInteractions(chats, timelines, sessionMessages);
    }

    private com.example.desporto24.model.PrivateGroupMember groupMember(Instant joined) {
        var m = new com.example.desporto24.model.PrivateGroupMember();
        m.setGroupId(5L); m.setUsername("ana"); m.setJoinedAt(joined);
        return m;
    }

    @Test
    void privateGroupSummaryCountsOnlyOthersMessagesSinceJoiningAfterTheCursor() {
        actor();
        Instant joined = Instant.parse("2026-11-01T10:00:00Z");
        when(groupMembers.findByGroupIdAndUsernameIgnoreCase(5L, "ana")).thenReturn(Optional.of(groupMember(joined)));
        var last = new com.example.desporto24.model.PrivateGroupMessage();
        last.setId(30L); last.setGroupId(5L); last.setSenderUsername("rui"); last.setBody("Bora?");
        last.setCreatedAt(Instant.parse("2026-11-21T18:00:00Z"));
        when(groupMessages.findByGroupIdAndCreatedAtGreaterThanEqualOrderByIdDesc(eq(5L), eq(joined), any(Pageable.class)))
                .thenReturn(List.of(last));
        ChatReadCursor cursor = new ChatReadCursor();
        cursor.setLastReadMessageId(27L);
        when(cursors.findByProfileIdAndConversationKindAndConversationKey(1L, "private", "5"))
                .thenReturn(Optional.of(cursor));
        when(groupMessages.countUnread(5L, joined, 27L, "ana")).thenReturn(2L);

        var values = service.summaries("ana", request(reference("PRIVATE", "5")));
        assertEquals(1, values.size());
        assertEquals("private", values.get(0).kind());
        assertEquals(30L, values.get(0).latestMessageId());
        assertEquals("rui", values.get(0).latestSender());
        assertEquals(2, values.get(0).unreadCount());
    }

    @Test
    void nonMembersGetNoPrivateGroupSummary() {
        actor();
        when(groupMembers.findByGroupIdAndUsernameIgnoreCase(5L, "ana")).thenReturn(Optional.empty());
        assertTrue(service.summaries("ana", request(reference("private", "5"))).isEmpty());
        verifyNoInteractions(groupMessages);
    }

    @Test
    void readingAPrivateGroupAdvancesTheCursorOnlyForCurrentMembers() {
        actor();
        Instant joined = Instant.parse("2026-11-01T10:00:00Z");
        when(groupMembers.findByGroupIdAndUsernameIgnoreCase(5L, "ana")).thenReturn(Optional.of(groupMember(joined)));
        var last = new com.example.desporto24.model.PrivateGroupMessage();
        last.setId(30L); last.setGroupId(5L); last.setSenderUsername("rui"); last.setBody("Bora?");
        last.setCreatedAt(Instant.parse("2026-11-21T18:00:00Z"));
        when(groupMessages.findByGroupIdAndCreatedAtGreaterThanEqualOrderByIdDesc(eq(5L), eq(joined), any(Pageable.class)))
                .thenReturn(List.of(last));
        when(groupMembers.existsByGroupIdAndUsernameIgnoreCase(5L, "ana")).thenReturn(true);

        service.markRead("ana", new com.example.desporto24.model.dto.chat.InboxReadRequest("private", "5", 30L));
        verify(cursors).advance(1L, "private", "5", 30L);
    }

    @Test
    void unreadTotalSumsAccessibleConversationsAndSkipsMutedOnes() {
        actor();
        org.springframework.test.util.ReflectionTestUtils.setField(service, "mutes", mutes);
        // Direct chat with 3 unread.
        when(chats.findConversationPeers(eq("ana"), any(Pageable.class))).thenReturn(List.of("Bea", "Rui"));
        when(profiles.findUserByUsernameIgnoreCase("bea")).thenReturn(Optional.of(profile(2, "Bea")));
        Chat last = new Chat("Bea", "ana", "Olá", "", Date.from(Instant.parse("2026-11-21T08:00:00Z")));
        last.setId(45L);
        when(chats.findConversationPage(eq("ana"), eq("Bea"), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(last)));
        when(chats.countUnread("ana", "Bea", 0L)).thenReturn(3L);
        // Rui is muted: never queried.
        lenient().when(mutes.muted(anyString(), anyString(), anyString())).thenReturn(false);
        when(mutes.muted("ana", "direct", "rui")).thenReturn(true);
        // Private group with 2 unread.
        Instant joined = Instant.parse("2026-11-01T10:00:00Z");
        when(groupMembers.findByUsernameIgnoreCaseOrderByGroupIdDesc("ana")).thenReturn(List.of(groupMember(joined)));
        when(groupMembers.findByGroupIdAndUsernameIgnoreCase(5L, "ana")).thenReturn(Optional.of(groupMember(joined)));
        var msg = new com.example.desporto24.model.PrivateGroupMessage();
        msg.setId(30L); msg.setGroupId(5L); msg.setSenderUsername("rui"); msg.setBody("Bora?");
        msg.setCreatedAt(Instant.parse("2026-11-21T18:00:00Z"));
        when(groupMessages.findByGroupIdAndCreatedAtGreaterThanEqualOrderByIdDesc(eq(5L), eq(joined), any(Pageable.class))).thenReturn(List.of(msg));
        when(groupMessages.countUnread(5L, joined, 0L, "ana")).thenReturn(2L);

        assertEquals(5L, service.unreadTotal("ana"));
        verify(chats, never()).findConversationPage(eq("ana"), eq("Rui"), any(Pageable.class));
    }

    @Test
    void unreadTotalIgnoresConversationsThatAreNoLongerAccessible() {
        actor();
        when(chats.findConversationPeers(eq("ana"), any(Pageable.class))).thenReturn(List.of("bea"));
        when(profiles.findUserByUsernameIgnoreCase("bea")).thenReturn(Optional.of(profile(2, "bea")));
        when(chatService.getChatInfo("ana", "bea")).thenThrow(new AccessDeniedException("blocked"));
        assertEquals(0L, service.unreadTotal("ana"));
    }
}
