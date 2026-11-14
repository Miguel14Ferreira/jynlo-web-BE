-- Read-only operational health check for durable profile media deletion.
-- Only aggregate statistics; does not print storage keys or user identifiers.
-- Run with an authorized PostgreSQL connection after the outbox migration.
SELECT COUNT(*)::bigint AS queued,
       COUNT(*) FILTER (WHERE next_attempt_at <= CURRENT_TIMESTAMP)::bigint AS due_now,
       COUNT(*) FILTER (WHERE next_attempt_at > CURRENT_TIMESTAMP)::bigint AS waiting_or_leased,
       COUNT(*) FILTER (WHERE attempts >= 3)::bigint AS repeated_failures,
       COUNT(*) FILTER (WHERE attempts >= 10)::bigint AS high_retry_count,
       COUNT(*) FILTER (WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '24 hours')::bigint AS older_than_day,
       COALESCE(MAX(attempts),0)::bigint AS max_attempts
FROM desporto24.profile_media_deletion_outbox;
