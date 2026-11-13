package com.example.desporto24.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityLiveEvent;
import com.example.desporto24.model.community.CommunityLiveEvent.Status;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityLiveEventRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Community broadcasts are NOT DirectCallService rooms. A viewer only gets
 * subscribe privileges; hosts can publish camera/microphone tracks.
 * Credentials are issued per request and never persisted or returned in lists.
 */
@Service
@RequiredArgsConstructor
public class CommunityLiveService {
    private final CommunityLiveEventRepository events;
    private final CommunityRepository communities;
    private final CommunityMemberRepository members;
    private final ProfileRepository profiles;
    private final DirectCallService provider;

    // Failed provider room deletions are retried without exposing a public room.
    private final ConcurrentHashMap<Long, String> pendingRoomClosures = new ConcurrentHashMap<>();

    @Value("${LIVEKIT_URL:}") private String serverUrl;
    @Value("${LIVEKIT_API_KEY:}") private String apiKey;
    @Value("${LIVEKIT_API_SECRET:}") private String apiSecret;

    public record LiveEvent(Long id, Long communityId, String title, String description,
                            Status status, Instant scheduledAt, Instant startedAt, Instant endedAt,
                            boolean canManage) {}
    public record Connection(String serverUrl, String token, String role, Instant expiresAt) {}
    public record Capabilities(boolean canManage, boolean enabled) {}

    @Transactional(readOnly = true)
    public Capabilities capabilities(Long communityId, String username) {
        Profile person = actor(username);
        Community community = accessible(communityId, person);
        boolean owner = community.getOwner().getId().equals(person.getId());
        boolean configured = serverUrl != null && serverUrl.startsWith("wss://")
                && apiKey != null && !apiKey.isBlank()
                && apiSecret != null && !apiSecret.isBlank();
        return new Capabilities(owner, configured && "ready".equals(provider.providerStatus()));
    }

    private Profile actor(String username) {
        return profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private Community accessible(Long communityId, Profile actor) {
        Community community = communities.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!community.getOwner().getId().equals(actor.getId())
                && !members.existsByCommunityIdAndProfileId(communityId, actor.getId()))
            throw new AccessDeniedException("Só membros da comunidade podem assistir ao direto");
        return community;
    }

    private void requireOwner(Community community, Profile actor) {
        if (!community.getOwner().getId().equals(actor.getId()))
            throw new AccessDeniedException("Só o organizador da comunidade pode gerir diretos");
    }

    private LiveEvent response(CommunityLiveEvent event, Profile viewer) {
        return new LiveEvent(event.getId(), event.getCommunity().getId(), event.getTitle(),
                event.getDescription(), event.getStatus(), event.getScheduledAt(),
                event.getStartedAt(), event.getEndedAt(),
                event.getCommunity().getOwner().getId().equals(viewer.getId()));
    }

    @Transactional(readOnly = true)
    public List<LiveEvent> list(Long communityId, String username) {
        Profile viewer = actor(username);
        accessible(communityId, viewer);
        return events.findTop30ByCommunityIdOrderByScheduledAtDescIdDesc(communityId)
                .stream().map(event -> response(event, viewer)).toList();
    }

    @Transactional
    public LiveEvent schedule(Long communityId, String username, String title,
                              String description, Instant startsAt) {
        Profile organizer = actor(username);
        Community community = accessible(communityId, organizer);
        requireOwner(community, organizer);
        if (title == null || title.isBlank() || title.strip().length() > 120
                || description != null && description.length() > 1000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título ou descrição inválidos");
        Instant now = Instant.now();
        Instant date = startsAt == null ? now : startsAt;
        if (date.isBefore(now.minusSeconds(60))
                || date.isAfter(now.plus(Duration.ofDays(90))))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data do direto inválida");
        CommunityLiveEvent created = events.save(CommunityLiveEvent.builder()
                .community(community).creator(organizer).title(title.strip())
                .description(description == null ? null : description.strip())
                .status(Status.SCHEDULED).scheduledAt(date).createdAt(now).build());
        return response(created, organizer);
    }

    @Transactional
    public synchronized LiveEvent start(Long communityId, Long eventId, String username) {
        Profile organizer = actor(username);
        Community community = accessible(communityId, organizer);
        requireOwner(community, organizer);
        // Hold a row lock until this transaction commits, so two application
        // replicas cannot both observe zero active events and start streams.
        Community locked = communities.lockForLiveStart(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireOwner(locked, organizer);
        CommunityLiveEvent event = find(communityId, eventId);
        if (event.getStatus() == Status.LIVE) return response(event, organizer);
        if (event.getStatus() != Status.SCHEDULED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este direto já terminou");
        if (event.getScheduledAt().isAfter(Instant.now().plus(Duration.ofMinutes(15))))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Direto ainda não disponível");
        if (events.existsByCommunityIdAndStatus(communityId, Status.LIVE))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um direto nesta comunidade");
        requireProvider();
        event.setStatus(Status.LIVE);
        event.setStartedAt(Instant.now());
        return response(events.save(event), organizer);
    }

    @Transactional
    public LiveEvent end(Long communityId, Long eventId, String username) {
        Profile organizer = actor(username);
        Community community = accessible(communityId, organizer);
        requireOwner(community, organizer);
        CommunityLiveEvent event = find(communityId, eventId);
        if (event.getStatus() == Status.ENDED) return response(event, organizer);
        event.setStatus(Status.ENDED);
        event.setEndedAt(Instant.now());
        LiveEvent finished = response(events.save(event), organizer);
        String room = "jynlo-live-community-" + communityId + "-" + eventId;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    pendingRoomClosures.put(eventId, room);
                    closePendingRooms();
                }
            });
        } else {
            // Fallback for controlled nontransactional invocations and tests.
            pendingRoomClosures.put(eventId, room);
            closePendingRooms();
        }
        return finished;
    }

    @Transactional(readOnly = true)
    public Connection join(Long communityId, Long eventId, String username) {
        Profile viewer = actor(username);
        Community community = accessible(communityId, viewer);
        CommunityLiveEvent event = find(communityId, eventId);
        if (event.getStatus() != Status.LIVE)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O direto ainda não começou");
        requireProvider();
        boolean host = community.getOwner().getId().equals(viewer.getId());
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(5));
        String room = "jynlo-live-community-" + communityId + "-" + eventId;
        String identity = "jynlo-live-" + eventId + "-user-" + viewer.getId();
        // Viewers are strictly subscribe-only, including data messages.
        String token = JWT.create()
                .withIssuer(apiKey).withSubject(identity)
                .withIssuedAt(Date.from(now))
                .withNotBefore(Date.from(now.minusSeconds(5)))
                .withExpiresAt(Date.from(expiresAt))
                .withClaim("video", Map.of(
                        "roomJoin", true, "room", room,
                        "canPublish", host, "canSubscribe", true,
                        "canPublishData", false))
                .sign(Algorithm.HMAC256(apiSecret));
        return new Connection(serverUrl, token, host ? "HOST" : "VIEWER", expiresAt);
    }

    /** Revoke all active viewers from LiveKit after the end status commits.
     * A provider outage leaves an entry for the next retry; never log tokens.
     */
    @Scheduled(fixedDelay = 5000)
    public void closePendingRooms() {
        for (var item : pendingRoomClosures.entrySet()) {
            try {
                if (provider.deleteLiveKitRoom(item.getValue()))
                    pendingRoomClosures.remove(item.getKey(), item.getValue());
            } catch (Exception ignored) {
                // Disconnected provider; keep the room in the cleanup queue.
            }
        }
    }

    private CommunityLiveEvent find(Long communityId, Long eventId) {
        if (eventId == null || eventId < 1)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return events.findByIdAndCommunityId(eventId, communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void requireProvider() {
        if (serverUrl == null || !serverUrl.startsWith("wss://")
                || apiKey == null || apiKey.isBlank() || apiSecret == null || apiSecret.isBlank()
                || !"ready".equals(provider.providerStatus()))
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Transmissões temporariamente indisponíveis");
    }
}
