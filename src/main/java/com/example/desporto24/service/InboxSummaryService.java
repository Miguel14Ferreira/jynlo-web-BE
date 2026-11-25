package com.example.desporto24.service;

import com.example.desporto24.Chat.ChatRepository;
import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Chat;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityTimelineEntry;
import com.example.desporto24.model.dto.chat.DirectReadReceiptResponse;
import com.example.desporto24.model.dto.chat.InboxReadRequest;
import com.example.desporto24.model.dto.chat.InboxRequest;
import com.example.desporto24.model.dto.chat.InboxSummaryResponse;
import com.example.desporto24.model.session.SessionMessage;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Per-profile read watermarks, not a copy of chat messages or a global unread
 * notification count. Every lookup rechecks membership against original models.
 */
@Service
@RequiredArgsConstructor
public class InboxSummaryService {
    private final ProfileRepository profiles;
    private final ChatService chatService;
    private final ChatRepository chats;
    private final CommunityRepository communities;
    private final CommunityMemberRepository members;
    private final CommunityTimelineEntryRepository timelines;
    private final SessionRepository sessions;
    private final SessionParticipantRepository participants;
    private final SessionMessageRepository sessionMessages;
    private final ChatReadCursorRepository cursors;
    private final ApplicationEventPublisher events;
    private final com.example.desporto24.repository.PrivateGroupMemberRepository groupMembers;
    private final com.example.desporto24.repository.PrivateGroupMessageRepository groupMessages;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private ConversationNotificationMuteService mutes;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private SportsTeamContentService teamContent;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.desporto24.repository.NotificationsRepository notifications;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.desporto24.repository.team.SportsTeamContentRepository teamEntries;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.desporto24.repository.team.SportsTeamRepository sportsTeams;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.desporto24.repository.team.SportsTeamMemberRepository sportsTeamMembers;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.desporto24.repository.PrivateGroupRepository privateGroups;

    @Transactional(readOnly = true)
    public List<InboxSummaryResponse> summaries(String username, InboxRequest request) {
        Profile actor = requireActor(username);
        // Deduplicate and cap input; inaccessible conversations are omitted,
        // not described with a different status that would disclose their contents.
        Map<String, InboxRequest.Conversation> unique = new LinkedHashMap<>();
        for (var item : request.conversations()) {
            try {
                String kind = normalizeKind(item.kind());
                String key = canonicalKey(kind, item.id());
                unique.putIfAbsent(kind + ":" + key, new InboxRequest.Conversation(kind, key));
            } catch (ResponseStatusException invalid) {
                // One malformed reference must never hide every other conversation's unread state.
            }
        }
        List<InboxSummaryResponse> result = new ArrayList<>();
        for (var item : unique.values()) {
            try {
                result.add(summary(actor, item.kind(), item.id()));
            } catch (AccessDeniedException | ResponseStatusException e) {
                // Membership may change between listing and fetching summaries.
                // Never leak the latest message or read state to former members.
            }
        }
        return result;
    }

    /** Cap on conversations counted per request (same bound as the clients' summary batches). */
    static final int MAX_COUNTED_CONVERSATIONS = 300;

    /**
     * One number for the app's Messages badge: unread messages in every conversation the
     * account can currently open (direct, community, session, private group), excluding muted
     * conversations. Same per-conversation rules as {@link #summaries}; inaccessible ones count 0.
     */
    @Transactional(readOnly = true)
    public long unreadTotal(String username) {
        Profile actor = requireActor(username);
        java.util.LinkedHashSet<String> refs = new java.util.LinkedHashSet<>();
        for (String peer : chats.findConversationPeers(actor.getUsername(), PageRequest.of(0, 150))) {
            try { refs.add("direct:" + canonicalKey("direct", peer)); } catch (ResponseStatusException ignored) { }
        }
        members.findByProfileIdOrderByJoinedAtDesc(actor.getId()).forEach(m -> {
            if (m.getCommunity() != null && m.getCommunity().getId() != null) refs.add("community:" + m.getCommunity().getId());
        });
        communities.findByOwnerId(actor.getId()).forEach(c -> refs.add("community:" + c.getId()));
        java.util.function.Consumer<Session> session = s -> {
            if (s != null && s.getId() != null && s.getStatus() != com.example.desporto24.enumeration.SessionStatus.CANCELLED
                    && s.getStatus() != com.example.desporto24.enumeration.SessionStatus.COMPLETED) refs.add("session:" + s.getId());
        };
        sessions.findByProfileIdOrderByGameDateAsc(actor.getId()).forEach(session);
        participants.findByProfileIdAndStateOrderByJoinedAtAsc(actor.getId(), ParticipantState.ACTIVE)
                .forEach(p -> session.accept(p.getSession()));
        groupMembers.findByUsernameIgnoreCaseOrderByGroupIdDesc(actor.getUsername())
                .forEach(g -> refs.add("private:" + g.getGroupId()));
        if (sportsTeams != null && sportsTeamMembers != null && teamContent != null && teamEntries != null) {
            sportsTeams.findByOwnerIdOrderByIdDesc(actor.getId()).forEach(t -> refs.add("team:" + t.getId()));
            sportsTeamMembers.findByProfileIdAndStatusOrderByIdDesc(actor.getId(),
                    com.example.desporto24.model.team.SportsTeamMember.Status.ACTIVE)
                    .forEach(m -> { if (m.getTeam() != null) refs.add("team:" + m.getTeam().getId()); });
        }
        long total = 0;
        int counted = 0;
        for (String ref : refs) {
            if (++counted > MAX_COUNTED_CONVERSATIONS) break;
            String kind = ref.substring(0, ref.indexOf(':'));
            String key = ref.substring(ref.indexOf(':') + 1);
            if (mutes != null && mutes.muted(actor.getUsername(), kind, key)) continue;
            try {
                total += Math.max(0, summary(actor, kind, key).unreadCount());
            } catch (AccessDeniedException | ResponseStatusException | IllegalArgumentException e) {
                // Membership or contact permission changed: this conversation simply does not count.
            }
        }
        return total;
    }

    /**
     * Recipient-authenticated read receipt for a direct chat, never an inferred
     * delivery acknowledgement. Only authorized friends can see this watermark.
     */
    @Transactional(readOnly = true)
    public DirectReadReceiptResponse peerDirectReadReceipt(String actorUsername, String peerUsername) {
        Profile actor = requireActor(actorUsername);
        String peerKey = canonicalKey("direct", peerUsername);
        Profile peer = profiles.findUserByUsernameIgnoreCase(peerKey)
                .orElseThrow(() -> new AccessDeniedException("No accessible chat"));
        if (Objects.equals(actor.getId(), peer.getId())) {
            throw new AccessDeniedException("No accessible chat");
        }
        // Existing friendship and block checks must pass before the read cursor
        // can be queried or disclosed. Never leak cursors from other chats.
        chatService.getChatInfo(actor.getUsername(), peer.getUsername());
        String readerConversationKey = canonicalKey("direct", actor.getUsername());
        long watermark = cursors.findByProfileIdAndConversationKindAndConversationKey(
                peer.getId(), "direct", readerConversationKey)
                .map(c -> c.getLastReadMessageId()).orElse(0L);
        // Permission can be revoked while the cursor is being fetched. Do not
        // disclose a previously authorized receipt after a block or session expiry.
        chatService.getChatInfo(actor.getUsername(), peer.getUsername());
        return new DirectReadReceiptResponse(peerKey, Math.max(0L, watermark));
    }

    @Transactional(readOnly = true)
    public void requireConversationAccess(String username, String kind, String conversationId) {
        String normalized = normalizeKind(kind);
        summary(requireActor(username), normalized, canonicalKey(normalized, conversationId));
    }

    @Transactional
    public InboxSummaryResponse markRead(String username, InboxReadRequest request) {
        Profile actor = requireActor(username);
        String kind = normalizeKind(request.kind());
        String key = canonicalKey(kind, request.id());
        InboxSummaryResponse snapshot = summary(actor, kind, key);
        if (snapshot.latestMessageId() == null) return snapshot;
        long watermark = Math.min(request.lastSeenMessageId(), snapshot.latestMessageId());
        long previous = cursors.findByProfileIdAndConversationKindAndConversationKey(
                actor.getId(), kind, key)
                .map(c -> c.getLastReadMessageId()).orElse(0L);
        if (watermark > previous) {
            // A participant can lose temporary session-contact permission while
            // the preview is being assembled. Recheck immediately before the
            // durable cursor write and any read notifications.
            if ("direct".equals(kind)) {
                Profile peer = profiles.findUserByUsernameIgnoreCase(key)
                        .orElseThrow(() -> new AccessDeniedException("No accessible chat"));
                chatService.getChatInfo(actor.getUsername(), peer.getUsername());
            } else if ("session".equals(kind)) {
                // Membership can be revoked after the inbox preview. Recheck
                // the current session and participant state before writing.
                Long sessionId = Long.parseLong(key);
                Session current = sessions.findById(sessionId)
                        .orElseThrow(() -> new AccessDeniedException("No accessible session"));
                requireSessionSummaryAccess(actor, current, sessionId);
            } else if ("private".equals(kind)
                    && !groupMembers.existsByGroupIdAndUsernameIgnoreCase(Long.parseLong(key), actor.getUsername())) {
                // Removed from the group after the preview: never write a cursor.
                throw new AccessDeniedException("No accessible group");
            } else if ("team".equals(kind)) {
                // Removed from the team or chat turned off after the preview: recheck before writing.
                teamSummary(actor, Long.parseLong(key), key, 0L);
            }
            // Atomic monotonic upsert: concurrent tabs/devices must never move
            // the read cursor backwards, nor create duplicate rows.
            cursors.advance(actor.getId(), kind, key, watermark);
            // A second tab/device can now refresh only this conversation's
            // server-authoritative unread count without waiting for polling.
            events.publishEvent(new InboxOwnReadUpdatedEvent(
                    actor.getUsername(), kind, key, watermark));
            // Reading the conversation also clears its "new message" alerts in the bell:
            // someone with the chat open should not see notifications pile up.
            clearChatNotifications(actor.getUsername(), kind, key);
            if ("direct".equals(kind)) {
                // This durable cursor (never an inferred device receipt) is
                // announced only AFTER_COMMIT. The listener rechecks access.
                Profile sender = profiles.findUserByUsernameIgnoreCase(key)
                        .orElseThrow(() -> new AccessDeniedException("No accessible chat"));
                events.publishEvent(new DirectChatReadConfirmedEvent(
                        actor.getUsername(), sender.getUsername(), watermark));
            }
        }
        return summary(actor, kind, key);
    }

    private void clearChatNotifications(String username, String kind, String key) {
        if (notifications == null) return;
        var type = com.example.desporto24.enumeration.NotificationType.CHAT_MESSAGE;
        List<com.example.desporto24.register.Notifications.Notifications> unread = switch (kind) {
            case "direct" -> notifications.findByUsernameIgnoreCaseAndRelatedUsernameIgnoreCaseAndTypeAndReadFalse(username, key, type);
            case "session" -> notifications.findByUsernameIgnoreCaseAndSessionIdAndTypeAndReadFalse(username, Long.parseLong(key), type);
            case "community" -> notifications.findByUsernameIgnoreCaseAndCommunityIdAndTypeAndReadFalse(username, Long.parseLong(key), type);
            case "private" -> notifications.findByUsernameIgnoreCaseAndPrivateGroupIdAndReadFalse(username, Long.parseLong(key));
            case "team" -> notifications.findByUsernameIgnoreCaseAndSportsTeamIdAndReadFalse(username, Long.parseLong(key));
            default -> List.of();
        };
        if (unread == null || unread.isEmpty()) return;
        unread.forEach(n -> n.setRead(true));
        notifications.saveAll(unread);
    }

    private InboxSummaryResponse summary(Profile actor, String kind, String key) {
        long readId = cursors.findByProfileIdAndConversationKindAndConversationKey(
                actor.getId(), kind, key)
                .map(c -> c.getLastReadMessageId()).orElse(0L);

        return switch (kind) {
            case "direct" -> directSummary(actor, key, readId);
            case "community" -> communitySummary(actor, Long.parseLong(key), key, readId);
            case "session" -> sessionSummary(actor, Long.parseLong(key), key, readId);
            case "private" -> privateGroupSummary(actor, Long.parseLong(key), key, readId);
            case "team" -> teamSummary(actor, Long.parseLong(key), key, readId);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation kind");
        };
    }

    private InboxSummaryResponse directSummary(Profile actor, String key, long readId) {
        Profile friend = profiles.findUserByUsernameIgnoreCase(key)
                .orElseThrow(() -> new AccessDeniedException("No accessible chat"));
        // The existing chat service additionally enforces friendship,
        // user blocks and the backend's normal authorization rules.
        chatService.getChatInfo(actor.getUsername(), friend.getUsername());
        var latest = chats.findConversationPage(actor.getUsername(), friend.getUsername(),
                PageRequest.of(0, 1, org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "date", "id")))
                .getContent();
        if (latest.isEmpty()) {
            chatService.getChatInfo(actor.getUsername(), friend.getUsername());
            return empty("direct", key).withImageUrl(friend.getProfilePicUrl());
        }
        Chat message = latest.get(0);
        Date at = message.getDate();
        long unread = chats.countUnread(actor.getUsername(), friend.getUsername(), readId);
        // Session-contact permission can expire or be revoked while message and
        // unread queries execute. Never return a stale preview after revocation.
        chatService.getChatInfo(actor.getUsername(), friend.getUsername());
        return new InboxSummaryResponse("direct", key, message.getId(), ChatMessagePreview.forMessage(message),
                message.getUsername1(), at == null ? null : at.toInstant(), unread)
                .withImageUrl(friend.getProfilePicUrl());
    }

    private InboxSummaryResponse communitySummary(Profile actor, Long id, String key, long readId) {
        Community community = communities.findById(id)
                .orElseThrow(() -> new AccessDeniedException("No accessible community"));
        boolean owner = community.getOwner() != null
                && Objects.equals(community.getOwner().getId(), actor.getId());
        if (!owner && !members.existsByCommunityIdAndProfileId(id, actor.getId())) {
            throw new AccessDeniedException("No accessible community");
        }
        var latest = timelines.findByCommunityIdAndKindOrderByIdDesc(
                id, CommunityTimelineEntry.Kind.CHAT, PageRequest.of(0, 1));
        if (latest.isEmpty()) return empty("community", key).withImageUrl(community.getPhotoUrl());
        var entry = latest.get(0);
        return new InboxSummaryResponse("community", key, entry.getId(), ChatMessagePreview.forText(entry.getBody()),
                entry.getAuthor().getUsername(), entry.getCreatedAt(),
                timelines.countUnread(id, CommunityTimelineEntry.Kind.CHAT, actor.getId(), readId))
                .withImageUrl(community.getPhotoUrl());
    }

    private InboxSummaryResponse sessionSummary(Profile actor, Long id, String key, long readId) {
        Session session = sessions.findById(id)
                .orElseThrow(() -> new AccessDeniedException("No accessible session"));
        requireSessionSummaryAccess(actor, session, id);
        var latest = sessionMessages.findBySessionIdOrderByIdDesc(id, PageRequest.of(0, 1));
        if (latest.isEmpty()) {
            requireSessionSummaryAccess(actor, session, id);
            return empty("session", key);
        }
        SessionMessage entry = latest.get(0);
        long unread = sessionMessages.countUnread(id, actor.getId(), readId);
        // Recheck after reading messages: cancellation, expiry or removal can
        // revoke access while the preview/unread queries are in progress.
        requireSessionSummaryAccess(actor, session, id);
        return new InboxSummaryResponse("session", key, entry.getId(), ChatMessagePreview.forText(entry.getBody()),
                entry.getSenderProfile().getUsername(), entry.getCreatedAt(), unread);
    }

    /** Private group chats: members only, and only messages sent since they joined. */
    private InboxSummaryResponse privateGroupSummary(Profile actor, Long id, String key, long readId) {
        var membership = groupMembers.findByGroupIdAndUsernameIgnoreCase(id, actor.getUsername())
                .orElseThrow(() -> new AccessDeniedException("No accessible group"));
        var latest = groupMessages.findByGroupIdAndCreatedAtGreaterThanEqualOrderByIdDesc(
                id, membership.getJoinedAt(), PageRequest.of(0, 1));
        String groupPhoto = privateGroups == null ? null
                : privateGroups.findById(id).map(com.example.desporto24.model.PrivateGroup::getPhotoUrl).orElse(null);
        if (latest.isEmpty()) return empty("private", key).withImageUrl(groupPhoto);
        var entry = latest.get(0);
        long unread = groupMessages.countUnread(id, membership.getJoinedAt(), readId, actor.getUsername());
        return new InboxSummaryResponse("private", key, entry.getId(), ChatMessagePreview.forText(entry.getBody()),
                entry.getSenderUsername(), entry.getCreatedAt(), unread).withImageUrl(groupPhoto);
    }

    /** Sports team chats: owner and active members, while the team chat is on. */
    private InboxSummaryResponse teamSummary(Profile actor, Long id, String key, long readId) {
        if (teamContent == null || teamEntries == null) throw new AccessDeniedException("No accessible team");
        try {
            teamContent.requireChatAccess(id, actor);
        } catch (org.springframework.security.access.AccessDeniedException | ResponseStatusException denied) {
            throw new AccessDeniedException("No accessible team");
        }
        var latest = teamEntries.findByTeamIdAndKindAndDeletedAtIsNullOrderByIdDesc(
                id, com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT, PageRequest.of(0, 1));
        String teamLogo = sportsTeams == null ? null
                : sportsTeams.findById(id).map(com.example.desporto24.model.team.SportsTeam::getLogoUrl).orElse(null);
        if (latest.isEmpty()) return empty("team", key).withImageUrl(teamLogo);
        var entry = latest.get(0);
        long unread = teamEntries.countUnread(id, com.example.desporto24.model.team.SportsTeamContent.Kind.CHAT,
                readId, actor.getId());
        return new InboxSummaryResponse("team", key, entry.getId(), ChatMessagePreview.forText(entry.getBody()),
                entry.getAuthor() == null ? null : entry.getAuthor().getUsername(), entry.getCreatedAt(), unread)
                .withImageUrl(teamLogo);
    }

    private void requireSessionSummaryAccess(Profile actor, Session session, Long id) {
        if (!SessionChatService.isChatOpen(session, java.time.Instant.now())) {
            throw new AccessDeniedException("Session chat is no longer available");
        }
        boolean owner = session.getProfile() != null
                && Objects.equals(session.getProfile().getId(), actor.getId());
        if (!owner && !participants.existsBySessionIdAndProfileIdAndState(
                id, actor.getId(), ParticipantState.ACTIVE)) {
            throw new AccessDeniedException("No accessible session");
        }
    }

    private InboxSummaryResponse empty(String kind, String key) {
        return new InboxSummaryResponse(kind, key, null, null, null, null, 0);
    }

    private Profile requireActor(String username) {
        return profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new AccessDeniedException("Not authenticated"));
    }

    private String normalizeKind(String kind) {
        if (kind == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid kind");
        String normalized = kind.toLowerCase(Locale.ROOT);
        if (!normalized.equals("direct") && !normalized.equals("community")
                && !normalized.equals("session") && !normalized.equals("private")
                && !normalized.equals("team")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid kind");
        }
        return normalized;
    }

    private String canonicalKey(String kind, String value) {
        if (value == null || value.isBlank() || value.length() > 128) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation");
        }
        if (kind.equals("direct")) {
            String username = value.trim().toLowerCase(Locale.ROOT);
            // Some older accounts have a space in their username.
            if (!username.matches("[a-z0-9_.@ -]{1,128}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username");
            }
            return username;
        }
        if (!value.matches("[1-9][0-9]{0,17}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation");
        }
        try {
            return Long.toString(Long.parseLong(value));
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation");
        }
    }
}
