package com.example.desporto24.service;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.CommunityTimelineEntry;
import com.example.desporto24.model.session.SessionMessage;
import com.example.desporto24.repository.ChatPollVoteRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.community.CommunityTimelineEntryRepository;
import com.example.desporto24.repository.session.SessionMessageRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.websocket.ChatPollChangedEvent;
import com.example.desporto24.websocket.CommunityChatRecipientResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Poll results and votes. Like reactions, every read and write rechecks the
 * caller's access to the exact conversation, and message IDs are matched
 * against that conversation so IDs from other chats cannot be probed.
 * Results carry counts only; who voted for what is never exposed.
 */
@Service
@RequiredArgsConstructor
public class ChatPollService {
    public record PollResult(Long messageId, List<Long> counts, long total, Integer mine) {}

    private final InboxSummaryService inbox;
    private final ProfileRepository profiles;
    private final SessionMessageRepository sessionMessages;
    private final CommunityTimelineEntryRepository communityMessages;
    private final ChatPollVoteRepository votes;
    private final SessionRepository sessions;
    private final SessionParticipantRepository participants;
    private final CommunityChatRecipientResolver communityRecipients;
    private final ApplicationEventPublisher events;

    @Transactional(readOnly = true)
    public List<PollResult> results(String username, String kind, String conversationId, List<Long> ids) {
        Profile actor = requireActor(username, kind, conversationId);
        if (ids == null || ids.isEmpty() || ids.size() > 100 || ids.stream().anyMatch(id -> id == null || id <= 0)) {
            throw badRequest();
        }
        Map<Long, ChatPoll.Poll> polls = polls(kind, conversationId, new LinkedHashSet<>(ids));
        return tally(actor.getId(), kind, polls);
    }

    @Transactional
    public PollResult vote(String username, String kind, String conversationId, Long messageId, Integer option) {
        Profile actor = requireActor(username, kind, conversationId);
        if (messageId == null || messageId <= 0) throw badRequest();
        ChatPoll.Poll poll = polls(kind, conversationId, Set.of(messageId)).get(messageId);
        if (poll == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        if (option == null) {
            votes.deleteByProfileIdAndConversationKindAndMessageId(actor.getId(), kind, messageId);
        } else {
            if (option < 0 || option >= poll.options().size()) throw badRequest();
            votes.upsert(actor.getId(), kind, messageId, option.shortValue());
        }
        PollResult result = tally(actor.getId(), kind, Map.of(messageId, poll)).getFirst();
        events.publishEvent(new ChatPollChangedEvent(kind, conversationId, messageId,
                result.counts(), result.total(), recipients(kind, conversationId)));
        return result;
    }

    private Profile requireActor(String username, String kind, String conversationId) {
        if (!"session".equals(kind) && !"community".equals(kind)) throw badRequest();
        try {
            Long.parseLong(conversationId);
        } catch (RuntimeException invalid) {
            throw badRequest();
        }
        inbox.requireConversationAccess(username, kind, conversationId);
        return profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    /** Live (not deleted) poll messages of this exact conversation, by id. */
    private Map<Long, ChatPoll.Poll> polls(String kind, String conversationId, Set<Long> requested) {
        long id = Long.parseLong(conversationId);
        Map<Long, ChatPoll.Poll> result = new LinkedHashMap<>();
        if ("session".equals(kind)) {
            for (SessionMessage msg : sessionMessages.findAllById(requested)) {
                if (msg.getSession() == null || id != msg.getSession().getId() || msg.getDeletedAt() != null) continue;
                ChatPoll.parse(msg.getBody()).ifPresent(p -> result.put(msg.getId(), p));
            }
        } else {
            for (CommunityTimelineEntry msg : communityMessages.findAllById(requested)) {
                if (msg.getKind() != CommunityTimelineEntry.Kind.CHAT || msg.getCommunity() == null
                        || id != msg.getCommunity().getId() || msg.getDeletedAt() != null) continue;
                ChatPoll.parse(msg.getBody()).ifPresent(p -> result.put(msg.getId(), p));
            }
        }
        // Keep the caller's order.
        Map<Long, ChatPoll.Poll> ordered = new LinkedHashMap<>();
        for (Long requestedId : requested) if (result.containsKey(requestedId)) ordered.put(requestedId, result.get(requestedId));
        return ordered;
    }

    private List<PollResult> tally(Long actorId, String kind, Map<Long, ChatPoll.Poll> polls) {
        if (polls.isEmpty()) return List.of();
        Map<Long, long[]> counts = new HashMap<>();
        Map<Long, Integer> mine = new HashMap<>();
        polls.forEach((id, poll) -> counts.put(id, new long[poll.options().size()]));
        for (var vote : votes.findByConversationKindAndMessageIdIn(kind, polls.keySet())) {
            long[] row = counts.get(vote.getMessageId());
            int option = vote.getOptionIndex();
            if (row == null || option < 0 || option >= row.length) continue;
            row[option]++;
            if (actorId.equals(vote.getProfileId())) mine.put(vote.getMessageId(), option);
        }
        List<PollResult> result = new ArrayList<>();
        for (Long id : polls.keySet()) {
            long[] row = counts.get(id);
            List<Long> list = Arrays.stream(row).boxed().toList();
            result.add(new PollResult(id, list, Arrays.stream(row).sum(), mine.get(id)));
        }
        return result;
    }

    private Set<String> recipients(String kind, String conversationId) {
        Long id = Long.parseLong(conversationId);
        if ("community".equals(kind)) return communityRecipients.currentRecipients(id);
        Set<String> recipients = new LinkedHashSet<>();
        sessions.findById(id).map(Session::getProfile).ifPresent(owner -> recipients.add(owner.getUsername()));
        participants.findBySessionIdAndStateOrderByJoinedAtAsc(id, ParticipantState.ACTIVE)
                .forEach(member -> recipients.add(member.getProfile().getUsername()));
        return recipients;
    }

    private ResponseStatusException badRequest() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid poll vote");
    }
}
