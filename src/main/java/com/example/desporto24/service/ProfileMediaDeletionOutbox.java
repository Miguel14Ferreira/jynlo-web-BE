package com.example.desporto24.service;

import com.example.desporto24.storage.MediaStorage;
import com.example.desporto24.storage.PrivateProfileMediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Durable cleanup of public and private profile post/story objects.
 *
 * Enqueue inside the same transaction as the deletion of the owning DB row.
 * A PostgreSQL claim lease allows multiple workers to retry safely even after
 * a process crash. Object deletions must be idempotent (S3 DELETE and local
 * deleteIfExists both are).
 */
@Service
@RequiredArgsConstructor
public class ProfileMediaDeletionOutbox {
    private final JdbcTemplate jdbc;
    private final MediaStorage publicStorage;
    @Autowired(required = false)
    private PrivateProfileMediaStorage privateStorage;

    public record Pending(long id, String storageKey, int attempts) {}

    /**
     * Must be called before commit, in the same transaction as removing the
     * referencing post or story. An unavailable queue prevents the deletion.
     */
    public void enqueue(String key) {
        if (key == null || key.isBlank()) return;
        if (!org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive())
            throw new IllegalStateException("Media deletion outbox requires an active database transaction");
        jdbc.update("""
                INSERT INTO desporto24.profile_media_deletion_outbox
                    (storage_key, attempts, next_attempt_at, created_at)
                VALUES (?, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT (storage_key) DO NOTHING
                """, key);
    }

    @Transactional
    public void enqueueAll(List<String> keys) {
        for (String key : keys) enqueue(key);
    }

    /**
     * Claim rows in PostgreSQL atomically and lease them for up to five minutes.
     * Concurrent workers never claim the same row in the same lease window.
     */
    public List<Pending> claimBatch() {
        return jdbc.query("""
                WITH due AS (
                    SELECT id FROM desporto24.profile_media_deletion_outbox
                    WHERE next_attempt_at <= CURRENT_TIMESTAMP
                    ORDER BY next_attempt_at, id
                    LIMIT 25 FOR UPDATE SKIP LOCKED
                )
                UPDATE desporto24.profile_media_deletion_outbox AS target
                   SET attempts = target.attempts + 1,
                       next_attempt_at = CURRENT_TIMESTAMP + INTERVAL '5 minutes'
                  FROM due WHERE target.id = due.id
                RETURNING target.id, target.storage_key, target.attempts
                """, (rs, index) -> new Pending(
                rs.getLong("id"), rs.getString("storage_key"), rs.getInt("attempts")));
    }

    private void remove(Pending pending) {
        // The attempts value acts as a claim token, stopping a stale worker
        // from clearing a row after a new worker has claimed it.
        jdbc.update("""
                DELETE FROM desporto24.profile_media_deletion_outbox
                WHERE id = ? AND attempts = ?
                """, pending.id(), pending.attempts());
    }

    private void backoff(Pending pending) {
        int minutes = Math.min(1440, 1 << Math.min(10, pending.attempts()));
        jdbc.update("""
                UPDATE desporto24.profile_media_deletion_outbox
                   SET next_attempt_at = CURRENT_TIMESTAMP + (? * INTERVAL '1 minute')
                 WHERE id = ? AND attempts = ?
                """, minutes, pending.id(), pending.attempts());
    }

    void deleteObject(String key) {
        if (PrivateProfileMediaStorage.isPrivateKey(key)) {
            if (privateStorage == null || !privateStorage.isConfigured())
                throw new IllegalStateException("Private media bucket unavailable");
            privateStorage.delete(key);
        } else if (key.matches("^(?:posts|stories)/[1-9][0-9]*/[A-Za-z0-9_-]+\\.(?:jpg|jpeg|png|webp|mp4|webm)$")) {
            publicStorage.delete(key);
        } else {
            throw new IllegalArgumentException("Unrecognized media object namespace");
        }
    }

    /**
     * Runs without a surrounding transaction for the potentially slow R2 call;
     * claiming and completion are distinct short PostgreSQL transactions.
     */
    @Scheduled(fixedDelay = 60_000L, initialDelay = 30_000L)
    public void processPending() {
        List<Pending> due = claimBatch();
        for (Pending pending : due) {
            try {
                deleteObject(pending.storageKey());
                remove(pending);
            } catch (RuntimeException failure) {
                backoff(pending);
                org.slf4j.LoggerFactory.getLogger(ProfileMediaDeletionOutbox.class)
                        .warn("Media object cleanup failed; retry queued, id={}", pending.id());
            }
        }
    }
}
