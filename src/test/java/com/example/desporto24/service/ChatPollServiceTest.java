package com.example.desporto24.service;

import com.example.desporto24.model.ChatPollVote;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityTimelineEntry;
import com.example.desporto24.repository.ChatPollVoteRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.community.CommunityTimelineEntryRepository;
import com.example.desporto24.repository.session.SessionMessageRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.websocket.ChatPollChangedEvent;
import com.example.desporto24.websocket.CommunityChatRecipientResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatPollServiceTest {
    private final InboxSummaryService inbox = mock(InboxSummaryService.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final CommunityTimelineEntryRepository community = mock(CommunityTimelineEntryRepository.class);
    private final ChatPollVoteRepository votes = mock(ChatPollVoteRepository.class);
    private final CommunityChatRecipientResolver recipients = mock(CommunityChatRecipientResolver.class);
    private final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    private final ChatPollService service = new ChatPollService(inbox, profiles,
            mock(SessionMessageRepository.class), community, votes, mock(SessionRepository.class),
            mock(SessionParticipantRepository.class), recipients, events);

    private CommunityTimelineEntry entry(long id, long communityId, String body) {
        Community c = new Community();
        c.setId(communityId);
        CommunityTimelineEntry e = new CommunityTimelineEntry();
        e.setId(id);
        e.setCommunity(c);
        e.setKind(CommunityTimelineEntry.Kind.CHAT);
        e.setBody(body);
        e.setCreatedAt(Instant.now());
        return e;
    }

    private ChatPollVote vote(long profileId, long messageId, int option) {
        ChatPollVote v = new ChatPollVote();
        v.setProfileId(profileId);
        v.setConversationKind("community");
        v.setMessageId(messageId);
        v.setOptionIndex((short) option);
        return v;
    }

    @BeforeEach
    void actor() {
        Profile me = new Profile();
        me.setId(1L);
        me.setUsername("rui");
        when(profiles.findUserByUsernameIgnoreCase("rui")).thenReturn(Optional.of(me));
        when(recipients.currentRecipients(5L)).thenReturn(Set.of("rui", "ana"));
    }

    @Test
    void resultsCountVotesAndMarkMine() {
        when(community.findAllById(any())).thenReturn(List.of(entry(10, 5, "[JYNLO:POLL:1]Dia?\nSábado\nDomingo")));
        when(votes.findByConversationKindAndMessageIdIn(eq("community"), any()))
                .thenReturn(List.of(vote(1, 10, 1), vote(2, 10, 1), vote(3, 10, 0)));
        var result = service.results("rui", "community", "5", List.of(10L)).getFirst();
        assertEquals(List.of(1L, 2L), result.counts());
        assertEquals(3, result.total());
        assertEquals(1, result.mine());
        verify(inbox).requireConversationAccess("rui", "community", "5");
    }

    @Test
    void pollsFromOtherConversationsOrPlainMessagesAreNotFound() {
        when(community.findAllById(any())).thenReturn(List.of(entry(10, 99, "[JYNLO:POLL:1]Dia?\nSábado\nDomingo")));
        assertThrows(ResponseStatusException.class, () -> service.vote("rui", "community", "5", 10L, 0));
        when(community.findAllById(any())).thenReturn(List.of(entry(10, 5, "só texto")));
        assertThrows(ResponseStatusException.class, () -> service.vote("rui", "community", "5", 10L, 0));
        verify(votes, never()).upsert(any(), any(), any(), anyShort());
    }

    @Test
    void voteValidatesTheOptionAndPublishesCountsOnly() {
        when(community.findAllById(any())).thenReturn(List.of(entry(10, 5, "[JYNLO:POLL:1]Dia?\nSábado\nDomingo")));
        assertThrows(ResponseStatusException.class, () -> service.vote("rui", "community", "5", 10L, 2));
        when(votes.findByConversationKindAndMessageIdIn(eq("community"), any())).thenReturn(List.of(vote(1, 10, 0)));
        var result = service.vote("rui", "community", "5", 10L, 0);
        verify(votes).upsert(1L, "community", 10L, (short) 0);
        assertEquals(0, result.mine());
        verify(events).publishEvent(any(ChatPollChangedEvent.class));
    }

    @Test
    void directChatsHaveNoPolls() {
        assertThrows(ResponseStatusException.class, () -> service.results("rui", "direct", "ana", List.of(1L)));
        verifyNoInteractions(inbox);
    }
}
