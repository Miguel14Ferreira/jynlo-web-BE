package com.example.desporto24.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.websocket.ChatRealtimeGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.Duration;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ephemeral private-call signalling. The existing chat ACL is authoritative;
 * credentials stay on the server, never in HTTP call payloads or socket events.
 *
 * This first stage is deliberately opt-in. Without LiveKit environment values,
 * /api/calls/direct remains unavailable; it never exposes a phantom call feature.
 * Calls are node-local and intentionally do not survive a Render restart.
 */
@Service
@RequiredArgsConstructor
public class DirectCallService {
    private static final int RING_SECONDS = 60;
    private static final int MAX_CALL_MINUTES = 120;
    // LiveKit Cloud revocation retention is shorter than the call lifetime.
    // Keep join credentials short-lived even for a two-hour connected call.
    private static final int MAX_JOIN_TOKEN_MINUTES = 10;
    private static final int START_COOLDOWN_SECONDS = 10;
    private static final int MAX_PARTICIPANTS = 6;
    private static final int MAX_INVITES_PER_PEER_PER_MINUTE = 3;
    private static final int MAX_DIRECT_RINGS_PER_PEER_PER_MINUTE = 3;
    private static final Duration INVITE_WINDOW = Duration.ofMinutes(1);
    private static final Logger log = LoggerFactory.getLogger(DirectCallService.class);
    private static final Duration PROVIDER_CACHE = Duration.ofSeconds(60);
    private static final HttpClient PROVIDER_HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private volatile Instant providerCheckedAt = Instant.EPOCH;
    private volatile String providerState = "unknown";

    private final InboxSummaryService inbox;
    private final ProfileRepository profiles;
    private final ChatRealtimeGateway gateway;

    @Value("${LIVEKIT_URL:}")
    private String serverUrl;
    @Value("${LIVEKIT_API_KEY:}")
    private String apiKey;
    @Value("${LIVEKIT_API_SECRET:}")
    private String apiSecret;

    private final ConcurrentHashMap<UUID, Call> calls = new ConcurrentHashMap<>();
    /** Room deletion retries after users hang up, until LiveKit confirms closure. */
    private final ConcurrentHashMap<UUID, String> roomsPendingDeletion = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> lastStarted = new ConcurrentHashMap<>();
    /** Per-sender/recipient throttling; rejecting or declining a ring does not reset it. */
    private final ConcurrentHashMap<String, InviteBudget> inviteBudgets = new ConcurrentHashMap<>();
    /** Repeated 1:1 calls are capped separately from group invitations. */
    private final ConcurrentHashMap<String, InviteBudget> directRingBudgets = new ConcurrentHashMap<>();
    private record InviteBudget(Instant since, int count) {}
    /** Node-local, explicit invitations: no LiveKit credential is minted
     * until an authenticated invitee accepts. Mutations are synchronized. */
    private final ConcurrentHashMap<UUID, LinkedHashMap<String, Guest>> guests = new ConcurrentHashMap<>();
    private record Guest(String username, String inviter, Instant expiresAt, boolean accepted) {}
    public record Member(String username, String identity) {}
    public record CallMembers(List<Member> participants, List<String> pending) {}

    private record Call(UUID id, String caller, String peer, String media,
                        String room, Instant expiresAt, String state) {}

    public record CallSession(UUID callId, String peer, String media,
                              String serverUrl, String token, Instant expiresAt) {}

    /** Incoming invitation only. Never includes the LiveKit token or credentials. */
    public record PendingInvitation(UUID callId, String caller, String peer,
                                    String media, Instant expiresAt) {}

    /**
     * Recover a missed WebSocket ringing notification after reconnect/reload.
     * Only the authenticated callee can retrieve the still-ringing invitation.
     * Friendship and block rules are rechecked at lookup time.
     */
    public synchronized PendingInvitation pendingFor(String recipient) {
        if (recipient == null || recipient.isBlank()) return null;
        Instant now = Instant.now();
        calls.entrySet().removeIf(entry -> !now.isBefore(entry.getValue().expiresAt));
        for (Call call : calls.values()) {
            if ("RINGING".equals(call.state) && call.peer.equalsIgnoreCase(recipient)) {
                try {
                    // Both sides can lose access after a ring is issued. Never
                    // recover an invitation from a caller who can no longer call.
                    inbox.requireConversationAccess(recipient, "direct", call.caller);
                    inbox.requireConversationAccess(call.caller, "direct", call.peer);
                } catch (org.springframework.security.access.AccessDeniedException | ResponseStatusException denied) {
                    // Revoked invitations must not hide other valid incoming calls.
                    continue;
                }
                return new PendingInvitation(call.id, call.caller, call.peer,
                        call.media, call.expiresAt);
            }
            if (!"ACTIVE".equals(call.state)) continue;
            Guest guest = guestFor(call.id, recipient);
            if (guest != null && !guest.accepted() && now.isBefore(guest.expiresAt())) {
                // A stale group invitation must not be advertised after its inviter
                // loses permission to participate in the original call.
                try {
                    requireInviterOriginalAccess(call, guest.inviter());
                    inbox.requireConversationAccess(recipient, "direct", guest.inviter());
                } catch (org.springframework.security.access.AccessDeniedException | ResponseStatusException denied) {
                    // Skip stale group invitations without suppressing valid ones.
                    continue;
                }
                return new PendingInvitation(call.id, guest.inviter(), guest.username(),
                        call.media, guest.expiresAt());
            }
        }
        return null;
    }

    public boolean available() {
        return serverUrl != null && serverUrl.startsWith("wss://")
                && apiKey != null && !apiKey.isBlank()
                && apiSecret != null && apiSecret.length() >= 32;
    }

    /**
     * Environment variables alone do not prove that LiveKit accepts the API
     * key and secret. Probe the standard authenticated RoomService endpoint
     * without creating a room, exposing credentials, or touching actual calls.
     */
    public synchronized String providerStatus() {
        if (!available()) return "notConfigured";
        Instant now = Instant.now();
        if (now.isBefore(providerCheckedAt.plus(PROVIDER_CACHE))) return providerState;
        String result;
        int httpStatus = -1;
        try {
            httpStatus = probeProvider();
            result = httpStatus == 200 ? "ready"
                    : httpStatus == 401 ? "invalidCredentials"
                    : httpStatus == 403 ? "insufficientPermissions"
                    : "providerUnavailable";
        } catch (Exception e) {
            result = "providerUnavailable";
        }
        // The numeric HTTP status is sufficient to distinguish invalid
        // authentication from insufficient permission. Never log the URL,
        // token, key, secret, participant or response body.
        if (!result.equals(providerState) || httpStatus != 200)
            log.warn("LiveKit provider probe outcome={} httpStatus={}", result, httpStatus);
        providerState = result;
        providerCheckedAt = now;
        return result;
    }

    protected int probeProvider() throws Exception {
        String host = serverUrl.replaceFirst("^wss://", "https://").replaceAll("/+$", "");
        String jwt = JWT.create()
                .withIssuer(apiKey)
                .withSubject("jynlo-readiness")
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(45)))
                .withClaim("video", Map.of("roomList", true))
                .sign(Algorithm.HMAC256(apiSecret));
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(host + "/twirp/livekit.RoomService/ListRooms"))
                .timeout(Duration.ofSeconds(4))
                .header("Authorization", "Bearer " + jwt)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        return PROVIDER_HTTP.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }

    /**
     * Server-only LiveKit removal. A room-scoped admin token is minted here,
     * never sent to the client. The caller must first establish revocation
     * using the application's own contact authorization.
     */
    protected boolean removeFromLiveKit(String room, String identity) throws Exception {
        if (!available() || room == null || !room.startsWith("jynlo-")
                || identity == null || !identity.startsWith("jynlo-user-")) return false;
        String host = serverUrl.replaceFirst("^wss://", "https://").replaceAll("/+$", "");
        Instant now = Instant.now();
        String jwt = JWT.create()
                .withIssuer(apiKey)
                .withSubject("jynlo-room-revocation")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(45)))
                .withClaim("video", Map.of("roomAdmin", true, "room", room))
                .sign(Algorithm.HMAC256(apiSecret));
        var body = new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode()
                .put("room", room)
                .put("identity", identity);
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(host + "/twirp/livekit.RoomService/RemoveParticipant"))
                .timeout(Duration.ofSeconds(4))
                .header("Authorization", "Bearer " + jwt)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        int response = PROVIDER_HTTP.send(request, HttpResponse.BodyHandlers.discarding())
                .statusCode();
        return response >= 200 && response < 300;
    }

    /**
     * End a LiveKit room when either original participant loses authorization.
     * Deleting the room disconnects everyone, avoiding a surviving guest room.
     */
    protected boolean deleteLiveKitRoom(String room) throws Exception {
        if (!available() || room == null || !room.startsWith("jynlo-")) return false;
        String host = serverUrl.replaceFirst("^wss://", "https://").replaceAll("/+$", "");
        Instant now = Instant.now();
        String jwt = JWT.create()
                .withIssuer(apiKey)
                .withSubject("jynlo-room-revocation")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(45)))
                .withClaim("video", Map.of("roomAdmin", true, "room", room))
                .sign(Algorithm.HMAC256(apiSecret));
        var body = new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode()
                .put("room", room);
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(host + "/twirp/livekit.RoomService/DeleteRoom"))
                .timeout(Duration.ofSeconds(4))
                .header("Authorization", "Bearer " + jwt)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        int status = PROVIDER_HTTP.send(request, HttpResponse.BodyHandlers.discarding())
                .statusCode();
        return status >= 200 && status < 300;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkProviderOnStartup() {
        // The readiness log contains only a fixed result code, never URLs,
        // credentials, room information, usernames or LiveKit tokens.
        log.info("Direct calling provider startup check: {}", providerStatus());
    }

    public synchronized CallSession start(String caller, String peer, String media) {
        configured();
        if (caller == null || caller.isBlank() || peer == null || peer.isBlank()
                || peer.length() > 128 || caller.equalsIgnoreCase(peer)
                || !("voice".equals(media) || "video".equals(media))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid call");
        }
        inbox.requireConversationAccess(caller, "direct", peer);
        Instant now = Instant.now();
        calls.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiresAt));
        for (Call current : calls.values()) {
            if (("RINGING".equals(current.state) || "ACTIVE".equals(current.state))
                    && (isParticipant(current, caller) || isParticipant(current, peer))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Already in a call");
            }
        }
        String normalized = caller.toLowerCase(java.util.Locale.ROOT);
        Instant previous = lastStarted.get(normalized);
        if (previous != null && now.isBefore(previous.plusSeconds(START_COOLDOWN_SECONDS))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Call rate limit");
        }
        String peerBudgetKey = normalized + ":" + key(peer);
        InviteBudget ringBudget = directRingBudgets.get(peerBudgetKey);
        if (ringBudget != null && now.isBefore(ringBudget.since().plus(INVITE_WINDOW))
                && ringBudget.count() >= MAX_DIRECT_RINGS_PER_PEER_PER_MINUTE) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Repeated call rate limit");
        }

        UUID id = UUID.randomUUID();
        Call call = new Call(id, caller, peer, media, "jynlo-" + id,
                now.plusSeconds(RING_SECONDS), "RINGING");
        CallSession session = access(call, caller);
        calls.put(id, call);
        lastStarted.put(normalized, now);
        if (directRingBudgets.size() >= 512) {
            directRingBudgets.entrySet().removeIf(entry ->
                    !now.isBefore(entry.getValue().since().plus(INVITE_WINDOW)));
        }
        directRingBudgets.put(peerBudgetKey,
                ringBudget == null || !now.isBefore(ringBudget.since().plus(INVITE_WINDOW))
                        ? new InviteBudget(now, 1)
                        : new InviteBudget(ringBudget.since(), ringBudget.count() + 1));
        gateway.publishDirectCallEvent(caller, peer, id.toString(), media,
                "RINGING", caller, call.expiresAt);
        return session;
    }

    public synchronized CallSession accept(UUID id, String actor) {
        configured();
        Call call = requireCall(id, actor);
        if ("ACTIVE".equals(call.state)) {
            Guest guest = guestFor(id, actor);
            if (guest == null || guest.accepted() || !Instant.now().isBefore(guest.expiresAt())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation not ringing");
            }
            // A guest invitation must not survive revocation of the inviter\'s original call access.
            // Checking only the guest/inviter pair would let a removed participant extend a room.
            requireInviterOriginalAccess(call, guest.inviter());
            inbox.requireConversationAccess(actor, "direct", guest.inviter());
            // Mint the join credential before committing acceptance. If token creation
            // fails, leave the invitation pending rather than admitting a guest
            // who never received a usable credential.
            CallSession accepted = access(call, actor);
            guests.get(id).put(key(actor), new Guest(guest.username(), guest.inviter(), guest.expiresAt(), true));
            notifyParticipantsChanged(call, actor);
            return accepted;
        }
        if (!actor.equalsIgnoreCase(call.peer)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (!"RINGING".equals(call.state)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Call not ringing");
        }
        // Both original participants must still be authorized at acceptance time.
        // A caller may have lost session membership after initiating the ring.
        inbox.requireConversationAccess(call.caller, "direct", call.peer);
        inbox.requireConversationAccess(actor, "direct", call.caller);
        Call accepted = new Call(id, call.caller, call.peer, call.media, call.room,
                Instant.now().plus(MAX_CALL_MINUTES, ChronoUnit.MINUTES), "ACTIVE");
        CallSession session = access(accepted, actor);
        calls.put(id, accepted);
        gateway.publishDirectCallEvent(call.caller, call.peer, id.toString(), call.media,
                "ACTIVE", actor, accepted.expiresAt);
        return session;
    }

    /** Revalidate the invitation chain before revealing or accepting a guest invitation. */
    /**
     * Revalidate the complete invitation chain back to the original pair.
     * Checking only the immediate inviter would let a second-generation guest
     * continue extending a room after an earlier inviter loses access.
     */
    private void requireInviterOriginalAccess(Call call, String inviter) {
        java.util.Set<String> visited = new java.util.HashSet<>();
        String current = inviter;
        for (int depth = 0; depth < MAX_PARTICIPANTS; depth++) {
            if (current == null || !visited.add(key(current))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (current.equalsIgnoreCase(call.caller())) {
                inbox.requireConversationAccess(current, "direct", call.peer());
                return;
            }
            if (current.equalsIgnoreCase(call.peer())) {
                inbox.requireConversationAccess(current, "direct", call.caller());
                return;
            }
            Guest guest = guestFor(call.id(), current);
            if (guest == null || !guest.accepted()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            inbox.requireConversationAccess(current, "direct", guest.inviter());
            current = guest.inviter();
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    public synchronized void decline(UUID id, String actor) {
        Call call = requireCall(id, actor);
        if ("ACTIVE".equals(call.state)) {
            Guest guest = guestFor(id, actor);
            if (guest == null || guest.accepted() || !Instant.now().isBefore(guest.expiresAt()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Invitation not ringing");
            guests.get(id).remove(key(actor));
            notifyParticipantsChanged(call, actor);
            return;
        }
        if (!actor.equalsIgnoreCase(call.peer)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (!"RINGING".equals(call.state)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Call not ringing");
        }
        calls.remove(id);
        guests.remove(id);
        gateway.publishDirectCallEvent(call.caller, call.peer, id.toString(), call.media,
                "DECLINED", actor, Instant.now());
    }

    public synchronized void end(UUID id, String actor) {
        Call call = requireCall(id, actor);
        Guest leaving = guestFor(id, actor);
        if (leaving != null) {
            guests.get(id).remove(key(actor));
            if (leaving.accepted())
                gateway.publishDirectCallEvent(call.caller, actor, id.toString(), call.media,
                        "LEFT", actor, Instant.now());
            notifyParticipantsChanged(call, actor);
            return;
        }
        calls.remove(id);
        roomsPendingDeletion.put(id, call.room());
        var invited = guests.remove(id);
        gateway.publishDirectCallEvent(call.caller, call.peer, id.toString(), call.media,
                "ENDED", actor, Instant.now());
        if (invited != null) for (Guest guest : invited.values()) {
            gateway.publishDirectCallEvent(call.caller, guest.username(), id.toString(), call.media,
                    "ENDED", actor, Instant.now());
        }
    }

    /** Only an accepted member can invite an existing contact, never an outsider. */
    public synchronized void invite(UUID id, String actor, String recipient) {
        configured();
        Call call = requireCall(id, actor);
        if (!"ACTIVE".equals(call.state) || !isConnectedParticipant(call, actor))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Call not active");
        if (recipient == null || recipient.isBlank() || recipient.length() > 128
                || recipient.equalsIgnoreCase(actor) || isParticipant(call, recipient)
                || guestFor(id, recipient) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid invitee");
        // A previously accepted caller may have lost session membership or become blocked.
        // Recheck their original call access before allowing further invitations.
        requireInviterOriginalAccess(call, actor);
        inbox.requireConversationAccess(actor, "direct", recipient);
        for (Call other : calls.values()) {
            if (!other.id().equals(id) && ("RINGING".equals(other.state()) || "ACTIVE".equals(other.state()))
                    && isParticipant(other, recipient))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Already in a call");
        }
        LinkedHashMap<String, Guest> list = guests.computeIfAbsent(id, ignored -> new LinkedHashMap<>());
        list.entrySet().removeIf(e -> !e.getValue().accepted()
                && !Instant.now().isBefore(e.getValue().expiresAt()));
        if (list.size() >= MAX_PARTICIPANTS - 2)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Call full");
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(RING_SECONDS);
        enforceInviteRateLimit(actor, recipient, now);
        list.put(key(recipient), new Guest(recipient, actor, expiry, false));
        gateway.publishDirectCallEvent(actor, recipient, id.toString(), call.media(),
                "RINGING", actor, expiry);
        notifyParticipantsChanged(call, actor);
    }

    /** Avoid repeated ringing after a decline. Never consume the budget for a rejected ACL. */
    private void enforceInviteRateLimit(String inviter, String recipient, Instant now) {
        String budgetKey = key(inviter) + ":" + key(recipient);
        InviteBudget previous = inviteBudgets.get(budgetKey);
        if (previous != null && now.isBefore(previous.since().plus(INVITE_WINDOW))
                && previous.count() >= MAX_INVITES_PER_PEER_PER_MINUTE) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Invite rate limit");
        }
        // Node-local signalling: expire stale counters instead of accumulating
        // an entry for every person a user has ever called.
        if (inviteBudgets.size() >= 512) {
            inviteBudgets.entrySet().removeIf(entry ->
                    !now.isBefore(entry.getValue().since().plus(INVITE_WINDOW)));
        }
        inviteBudgets.put(budgetKey,
                previous == null || !now.isBefore(previous.since().plus(INVITE_WINDOW))
                        ? new InviteBudget(now, 1)
                        : new InviteBudget(previous.since(), previous.count() + 1));
    }

    /**
     * Reconcile connected guests with current session and block permissions.
     * A failed provider eviction is retried on the next pass; it must not be
     * recorded locally as a successful disconnection.
     */
    @org.springframework.scheduling.annotation.Scheduled(fixedDelay = 30000)
    public synchronized void revokeDisconnectedGuests() {
        if (!available()) return;
        retryEndedRoomDeletions();
        for (Call call : calls.values()) {
            if (!"ACTIVE".equals(call.state()) || !Instant.now().isBefore(call.expiresAt())) continue;
            boolean originalAccessRevoked;
            try {
                inbox.requireConversationAccess(call.caller(), "direct", call.peer());
                inbox.requireConversationAccess(call.peer(), "direct", call.caller());
                originalAccessRevoked = false;
            } catch (org.springframework.security.access.AccessDeniedException
                     | ResponseStatusException revoked) {
                originalAccessRevoked = true;
            } catch (RuntimeException transientFailure) {
                // Do not delete a room on uncertain ACL/database availability.
                continue;
            }
            if (originalAccessRevoked) {
                try {
                    if (!deleteLiveKitRoom(call.room())) continue;
                    calls.remove(call.id());
                    var removedGuests = guests.remove(call.id());
                    gateway.publishDirectCallEvent(call.caller(), call.peer(),
                            call.id().toString(), call.media(), "ENDED",
                            call.caller(), Instant.now());
                    if (removedGuests != null) for (Guest guest : removedGuests.values()) {
                        gateway.publishDirectCallEventTo(guest.username(), call.caller(),
                                guest.username(), call.id().toString(), call.media(),
                                "ENDED", call.caller(), Instant.now());
                    }
                } catch (Exception providerFailure) {
                    log.warn("LiveKit room revocation attempt failed");
                }
                continue;
            }
            var list = guests.get(call.id());
            if (list == null) continue;
            for (Guest guest : new ArrayList<>(list.values())) {
                if (!guest.accepted()) continue;
                boolean revoked = false;
                try {
                    requireInviterOriginalAccess(call, guest.username());
                    inbox.requireConversationAccess(guest.username(), "direct", guest.inviter());
                } catch (org.springframework.security.access.AccessDeniedException
                         | ResponseStatusException denied) {
                    revoked = true;
                } catch (RuntimeException transientFailure) {
                    // Do not evict on an uncertain ACL/database failure.
                    continue;
                }
                if (!revoked) continue;
                try {
                    var profile = profiles.findUserByUsernameIgnoreCase(guest.username()).orElse(null);
                    if (profile == null || profile.getId() == null) continue;
                    if (!removeFromLiveKit(call.room(), "jynlo-user-" + profile.getId())) continue;
                    list.remove(key(guest.username()));
                    gateway.publishDirectCallEventTo(guest.username(), call.caller(), guest.username(),
                            call.id().toString(), call.media(), "ENDED", guest.username(), Instant.now());
                    notifyParticipantsChanged(call, guest.username());
                } catch (Exception providerFailure) {
                    // No secrets, identities or provider response bodies in application logs.
                    log.warn("LiveKit participant revocation attempt failed");
                }
            }
        }
    }

    /**
     * An ended application call must not leave a playable LiveKit room behind.
     * Keep failed deletions node-locally so the next scheduled pass retries.
     */
    private void retryEndedRoomDeletions() {
        for (var pending : roomsPendingDeletion.entrySet()) {
            try {
                if (deleteLiveKitRoom(pending.getValue())) {
                    roomsPendingDeletion.remove(pending.getKey(), pending.getValue());
                }
            } catch (Exception providerFailure) {
                log.warn("LiveKit ended room deletion attempt failed");
            }
        }
    }

    /** Only current participants can inspect participants and invitations. */
    public synchronized CallMembers members(UUID id, String actor) {
        Call call = requireCall(id, actor);
        if (!"ACTIVE".equals(call.state()) || !isConnectedParticipant(call, actor))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        // Recheck authorization after a session removal or block before exposing the roster.
        requireInviterOriginalAccess(call, actor);
        List<Member> participants = new ArrayList<>();
        participants.add(member(call.caller()));
        participants.add(member(call.peer()));
        List<String> pending = new ArrayList<>();
        var list = guests.get(id);
        if (list != null) for (Guest guest : list.values()) {
            try {
                // Invited players can lose session membership or be blocked after
                // joining. Their identity must not remain visible in the roster.
                requireInviterOriginalAccess(call, guest.inviter());
                inbox.requireConversationAccess(guest.username(), "direct", guest.inviter());
            } catch (org.springframework.security.access.AccessDeniedException
                     | ResponseStatusException revoked) {
                continue;
            }
            if (guest.accepted()) participants.add(member(guest.username()));
            else if (Instant.now().isBefore(guest.expiresAt())) pending.add(guest.username());
        }
        return new CallMembers(List.copyOf(participants), List.copyOf(pending));
    }

    private Member member(String username) {
        var profile = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new Member(username, "jynlo-user-" + profile.getId());
    }

    private void notifyParticipantsChanged(Call call, String actor) {
        // Never fan a roster update out to the original pair in one operation:
        // their current contact permissions can differ after moderation/removal.
        for (String recipient : List.of(call.caller(), call.peer())) {
            try {
                requireInviterOriginalAccess(call, recipient);
            } catch (org.springframework.security.access.AccessDeniedException
                     | ResponseStatusException revoked) {
                continue;
            }
            gateway.publishDirectCallEventTo(recipient, call.caller(), call.peer(),
                    call.id().toString(), call.media(), "MEMBERS_CHANGED", actor, call.expiresAt());
        }
        var list = guests.get(call.id());
        if (list != null) for (Guest guest : list.values()) {
            if (!guest.accepted()) continue;
            try {
                requireInviterOriginalAccess(call, guest.username());
            } catch (org.springframework.security.access.AccessDeniedException
                     | ResponseStatusException revoked) {
                continue;
            }
            gateway.publishDirectCallEventTo(guest.username(), call.caller(), guest.username(),
                    call.id().toString(), call.media(), "MEMBERS_CHANGED", actor, call.expiresAt());
        }
    }

    private static String key(String username) { return username.toLowerCase(Locale.ROOT); }
    private Guest guestFor(UUID id, String username) {
        var map = guests.get(id);
        return map == null || username == null ? null : map.get(key(username));
    }
    private boolean isConnectedParticipant(Call call, String username) {
        Guest guest = guestFor(call.id(), username);
        return call.caller().equalsIgnoreCase(username) || call.peer().equalsIgnoreCase(username)
                || (guest != null && guest.accepted());
    }

    private Call requireCall(UUID id, String actor) {
        if (id == null || actor == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Call call = calls.get(id);
        if (call == null || !isParticipant(call, actor)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (Instant.now().isAfter(call.expiresAt)) {
            calls.remove(id);
            guests.remove(id);
            throw new ResponseStatusException(HttpStatus.GONE, "Call expired");
        }
        return call;
    }

    private boolean isParticipant(Call call, String username) {
        return username != null && (call.caller.equalsIgnoreCase(username)
                || call.peer.equalsIgnoreCase(username) || guestFor(call.id, username) != null);
    }

    private void configured() {
        if (!available()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Calling not configured");
        }
    }

    private CallSession access(Call call, String username) {
        var profile = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        // Opaque participant identity: never send usernames, emails or names
        // to LiveKit as participant identity, room name, or token metadata.
        String identity = "jynlo-user-" + profile.getId();
        Instant now = Instant.now();
        String token = JWT.create()
                .withIssuer(apiKey)
                .withSubject(identity)
                .withIssuedAt(Date.from(now))
                .withNotBefore(Date.from(now.minusSeconds(5)))
                .withExpiresAt(Date.from(call.expiresAt().isBefore(now.plus(MAX_JOIN_TOKEN_MINUTES, ChronoUnit.MINUTES))
                        ? call.expiresAt() : now.plus(MAX_JOIN_TOKEN_MINUTES, ChronoUnit.MINUTES)))
                .withClaim("video", Map.of(
                        "roomJoin", true,
                        "room", call.room,
                        "canPublish", true,
                        "canSubscribe", true,
                        "canPublishData", false))
                .sign(Algorithm.HMAC256(apiSecret));
        String peer = username.equalsIgnoreCase(call.caller) ? call.peer : call.caller;
        return new CallSession(call.id, peer, call.media, serverUrl, token, call.expiresAt);
    }
}
