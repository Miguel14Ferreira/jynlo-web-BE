package com.example.desporto24.service.availability;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Plain SQL for availability requests ("quem pode?"), their time options and the answers. */
@Repository
public class AvailabilityStore {

    public record Request(long id, String kind, long targetId, String creator, String note,
                          Instant createdAt, Instant reminderSentAt, Instant closedAt) {}
    public record Slot(long id, long requestId, Instant startsAt) {}
    public record Answer(long slotId, String username, String answer) {}

    private final JdbcTemplate jdbc;

    public AvailabilityStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long create(String kind, long targetId, String creator, String note, List<Instant> slots, Instant now) {
        GeneratedKeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    insert into availability_request (scope_kind, scope_id, creator_username, note, created_at)
                    values (?, ?, ?, ?, ?)
                    """, new String[]{"id"});
            ps.setString(1, kind);
            ps.setLong(2, targetId);
            ps.setString(3, creator);
            ps.setString(4, note);
            ps.setTimestamp(5, Timestamp.from(now));
            return ps;
        }, keys);
        Number key = keys.getKey();
        if (key == null) throw new IllegalStateException("Missing availability request id");
        long id = key.longValue();
        int position = 0;
        for (Instant start : slots) {
            jdbc.update("insert into availability_slot (request_id, starts_at, position) values (?, ?, ?)",
                    id, Timestamp.from(start), position++);
        }
        return id;
    }

    public Optional<Request> find(long id) {
        return jdbc.query(SELECT + " where id = ?", this::request, id).stream().findFirst();
    }

    /** Open requests of a group or community whose last option has not started yet, newest first. */
    public List<Request> open(String kind, long targetId, Instant now) {
        return jdbc.query(SELECT + """
                 where scope_kind = ? and scope_id = ? and closed_at is null
                   and exists (select 1 from availability_slot s where s.request_id = availability_request.id and s.starts_at > ?)
                 order by created_at desc limit 20
                """, this::request, kind, targetId, Timestamp.from(now));
    }

    public int openCount(String kind, long targetId, Instant now) {
        return open(kind, targetId, now).size();
    }

    public List<Slot> slots(long requestId) {
        return jdbc.query("select id, request_id, starts_at from availability_slot where request_id = ? order by position, id",
                (rs, row) -> new Slot(rs.getLong(1), rs.getLong(2), rs.getTimestamp(3).toInstant()), requestId);
    }

    public List<Answer> answers(long requestId) {
        return jdbc.query("""
                select a.slot_id, a.username, a.answer from availability_answer a
                join availability_slot s on s.id = a.slot_id where s.request_id = ?
                order by a.updated_at
                """, (rs, row) -> new Answer(rs.getLong(1), rs.getString(2), rs.getString(3)), requestId);
    }

    public void answer(long slotId, String username, String answer, Instant now) {
        jdbc.update("""
                insert into availability_answer (slot_id, username, answer, updated_at) values (?, ?, ?, ?)
                on conflict (slot_id, username) do update set answer = excluded.answer, updated_at = excluded.updated_at
                """, slotId, username, answer, Timestamp.from(now));
    }

    public void close(long requestId, Instant now) {
        jdbc.update("update availability_request set closed_at = ? where id = ? and closed_at is null",
                Timestamp.from(now), requestId);
    }

    /** Open requests older than `createdBefore` that have not had their reminder yet. */
    public List<Long> dueForReminder(Instant createdBefore, Instant firstStartAfter) {
        return jdbc.query("""
                select r.id from availability_request r
                where r.closed_at is null and r.reminder_sent_at is null and r.created_at < ?
                  and (select min(s.starts_at) from availability_slot s where s.request_id = r.id) > ?
                order by r.id limit 50
                """, (rs, row) -> rs.getLong(1), Timestamp.from(createdBefore), Timestamp.from(firstStartAfter));
    }

    /** @return true for the caller that claimed the reminder. */
    public boolean claimReminder(long requestId, Instant now) {
        return jdbc.update("update availability_request set reminder_sent_at = ? where id = ? and reminder_sent_at is null",
                Timestamp.from(now), requestId) == 1;
    }

    public void deleteFor(String username) {
        jdbc.update("delete from availability_answer where lower(username) = lower(?)", username);
        jdbc.update("delete from availability_request where lower(creator_username) = lower(?)", username);
    }

    private static final String SELECT = """
            select id, scope_kind, scope_id, creator_username, note, created_at, reminder_sent_at, closed_at
            from availability_request
            """;

    private Request request(java.sql.ResultSet rs, int row) throws java.sql.SQLException {
        Timestamp reminder = rs.getTimestamp(7), closed = rs.getTimestamp(8);
        return new Request(rs.getLong(1), rs.getString(2), rs.getLong(3), rs.getString(4), rs.getString(5),
                rs.getTimestamp(6).toInstant(), reminder == null ? null : reminder.toInstant(),
                closed == null ? null : closed.toInstant());
    }
}
