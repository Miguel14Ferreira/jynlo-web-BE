package com.example.desporto24.service;

import com.example.desporto24.model.ConversationNotificationMute;
import com.example.desporto24.repository.ConversationNotificationMuteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Locale;

/**
 * Mutes affect only the per-conversation notification channel. Message delivery,
 * access, history and unread inbox cursors are not altered.
 */
@Service
@RequiredArgsConstructor
public class ConversationNotificationMuteService {
    private final ConversationNotificationMuteRepository repository;

    public record ConversationMute(String kind, String id) {}
    public record ChangeMute(String kind, String id, boolean muted) {}

    public static String kind(String raw) {
        if (raw == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation kind");
        String value = raw.toLowerCase(Locale.ROOT);
        if (!List.of("direct", "community", "session", "private", "team").contains(value))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation kind");
        return value;
    }

    public static String key(String kind, String raw) {
        if (raw == null || raw.isBlank() || raw.length() > 128)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation");
        if ("direct".equals(kind)) {
            String normalized = raw.toLowerCase(Locale.ROOT);
            if (!normalized.matches("[a-z0-9._-]{1,128}"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation");
            return normalized;
        }
        try {
            long value = Long.parseLong(raw);
            if (value <= 0) throw new NumberFormatException();
            return Long.toString(value);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid conversation");
        }
    }

    /** Conversations open on someone's screen right now (refreshed every ~20 s by the client). */
    private final java.util.concurrent.ConcurrentHashMap<String, Long> viewing = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long VIEWING_TTL_MS = 45_000;

    private static String viewingKey(String username, String kind, String key) {
        return user(username) + "|" + kind + "|" + key;
    }

    public void markViewing(String username, String rawKind, String rawId, boolean open) {
        String kind = kind(rawKind);
        String id = key(kind, rawId);
        long now = System.currentTimeMillis();
        if (open) viewing.put(viewingKey(username, kind, id), now + VIEWING_TTL_MS);
        else viewing.remove(viewingKey(username, kind, id));
        if (viewing.size() > 5_000) viewing.values().removeIf(expiry -> expiry < now);
    }

    public boolean isViewing(String username, String kind, String key) {
        if (username == null || kind == null || key == null) return false;
        Long expiry = viewing.get(viewingKey(username, kind, key.toLowerCase(Locale.ROOT)));
        return expiry != null && expiry > System.currentTimeMillis();
    }

    /** No alert for a muted conversation, nor for one the person has open right now. */
    public boolean silenced(String username, String kind, String key) {
        return isViewing(username, kind, key) || muted(username, kind, key);
    }

    private static String user(String username) {
        return username.toLowerCase(Locale.ROOT);
    }

    @Transactional(readOnly = true)
    public List<ConversationMute> list(String username) {
        return repository.findByUsername(user(username)).stream()
            .map(row -> new ConversationMute(row.getConversationKind(), row.getConversationKey()))
            .toList();
    }

    @Transactional(readOnly = true)
    public boolean muted(String username, String kind, String key) {
        return repository.findByUsernameAndConversationKindAndConversationKey(
            user(username), kind, key).isPresent();
    }

    @Transactional
    public ConversationMute update(String username, String kind, String key, boolean muted) {
        String actor = user(username);
        var existing = repository.findByUsernameAndConversationKindAndConversationKey(actor, kind, key);
        if (muted && existing.isEmpty()) {
            var row = new ConversationNotificationMute();
            row.setUsername(actor);
            row.setConversationKind(kind);
            row.setConversationKey(key);
            repository.save(row);
        } else if (!muted) {
            existing.ifPresent(repository::delete);
        }
        return new ConversationMute(kind, key);
    }
}
