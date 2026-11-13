package com.example.desporto24.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.PersonalLiveEvent;
import com.example.desporto24.model.social.PersonalLiveEvent.Status;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.PersonalLiveEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Independent personal broadcasts: every logged-in user can host without owning a community.
 * LiveKit tokens grant viewers subscribe-only privileges and expire after five minutes. */
@Service
@RequiredArgsConstructor
public class PersonalLiveService {
    private final PersonalLiveEventRepository events;
    private final ProfileRepository profiles;
    private final UserBlockService blocks;
    private final DirectCallService provider;
    private final ConcurrentHashMap<Long,String> pendingClose = new ConcurrentHashMap<>();

    @Value("${LIVEKIT_URL:}") private String serverUrl;
    @Value("${LIVEKIT_API_KEY:}") private String apiKey;
    @Value("${LIVEKIT_API_SECRET:}") private String apiSecret;

    public record Event(Long id,String username,String avatarUrl,String title,Status status,
                        Instant startedAt,boolean canManage) {}
    public record Connection(String serverUrl,String token,String role,Instant expiresAt) {}

    private Profile actor(String username) {
        return profiles.findUserByUsernameIgnoreCase(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
    private void requireProvider() {
        if (serverUrl == null || !serverUrl.startsWith("wss://") ||
            apiKey == null || apiKey.isBlank() || apiSecret == null || apiSecret.isBlank() ||
            !"ready".equals(provider.providerStatus()))
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Diretos indisponíveis");
    }
    private Event view(PersonalLiveEvent event,Profile viewer) {
        Profile host=event.getHost();
        return new Event(event.getId(),host.getUsername(),host.getProfilePicUrl(),
            event.getTitle(),event.getStatus(),event.getCreatedAt(),
            host.getId().equals(viewer.getId()));
    }
    private PersonalLiveEvent find(Long id) {
        return events.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Direto indisponível"));
    }
    private void requireHost(PersonalLiveEvent event,Profile actor) {
        if (!event.getHost().getId().equals(actor.getId()))
            throw new AccessDeniedException("Apenas o apresentador pode gerir este direto");
    }

    @Transactional(readOnly=true)
    public List<Event> active(String username) {
        Profile viewer=actor(username);
        return events.findTop30ByStatusOrderByCreatedAtDesc(Status.LIVE).stream()
            .filter(e -> !blocks.isBlockedBetween(viewer,e.getHost()))
            .map(e -> view(e,viewer)).toList();
    }

    @Transactional
    public synchronized Event start(String username,String title) {
        Profile host=actor(username);
        requireProvider();
        if (title==null || title.isBlank() || title.strip().length()>120)
            throw new IllegalArgumentException("Título inválido");
        if (events.findByHostIdAndStatus(host.getId(),Status.LIVE).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já tens um direto ativo");
        Instant now=Instant.now();
        PersonalLiveEvent event=events.save(PersonalLiveEvent.builder()
            .host(host).title(title.strip()).status(Status.LIVE)
            .createdAt(now).hostHeartbeatAt(now).build());
        return view(event,host);
    }

    @Transactional(readOnly=true)
    public Connection join(String username,Long id) {
        Profile viewer=actor(username);
        PersonalLiveEvent event=find(id);
        if (event.getStatus()!=Status.LIVE ||
                blocks.isBlockedBetween(viewer,event.getHost()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Direto indisponível");
        requireProvider();
        boolean host=event.getHost().getId().equals(viewer.getId());
        Instant now=Instant.now(),expiry=now.plus(Duration.ofMinutes(5));
        String room=room(id),identity="jynlo-live-personal-"+id+"-user-"+viewer.getId();
        String token=JWT.create().withIssuer(apiKey).withSubject(identity)
            .withIssuedAt(Date.from(now)).withNotBefore(Date.from(now.minusSeconds(5)))
            .withExpiresAt(Date.from(expiry))
            .withClaim("video",Map.of("roomJoin",true,"room",room,
                    "canPublish",host,"canSubscribe",true,"canPublishData",false))
            .sign(Algorithm.HMAC256(apiSecret));
        return new Connection(serverUrl,token,host?"HOST":"VIEWER",expiry);
    }

    @Transactional
    public Event end(String username,Long id) {
        Profile host=actor(username);
        PersonalLiveEvent event=find(id);
        requireHost(event,host);
        if (event.getStatus()==Status.ENDED) return view(event,host);
        event.setStatus(Status.ENDED);
        event.setEndedAt(Instant.now());
        Event ended=view(events.save(event),host);
        queueClose(id);
        return ended;
    }

    @Transactional
    public void heartbeat(String username,Long id) {
        Profile host=actor(username);
        if (events.heartbeat(id,host.getId(),Status.LIVE,Instant.now())!=1)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"O direto terminou");
    }

    @Scheduled(fixedDelay=60000)
    @Transactional
    public void expireUnattended() {
        Instant now=Instant.now(),cutoff=now.minus(Duration.ofMinutes(2));
        for (PersonalLiveEvent event:events.findTop100ByStatusAndCreatedAtBefore(Status.LIVE,cutoff)) {
            Instant heartbeat=event.getHostHeartbeatAt()!=null
                ?event.getHostHeartbeatAt():event.getCreatedAt();
            if (heartbeat.isAfter(cutoff) && event.getCreatedAt().isAfter(now.minus(Duration.ofHours(6))))
                continue;
            event.setStatus(Status.ENDED);
            event.setEndedAt(now);
            events.save(event);
            queueClose(event.getId());
        }
    }
    private String room(Long id) {return "jynlo-live-personal-"+id;}
    private void queueClose(Long id) {
        if (TransactionSynchronizationManager.isSynchronizationActive())
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    pendingClose.put(id,room(id));
                    closeRooms();
                }
            });
        else {pendingClose.put(id,room(id));closeRooms();}
    }
    @Scheduled(fixedDelay=5000)
    public void closeRooms() {
        for (var entry:pendingClose.entrySet()) {
            try {
                if (provider.deleteLiveKitRoom(entry.getValue()))
                    pendingClose.remove(entry.getKey(),entry.getValue());
            } catch (Exception ignored) { /* retry transient provider failures */ }
        }
    }
}
