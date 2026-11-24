package com.example.desporto24.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Likes on community feed posts (members only, one per person and post). */
@Service
@RequiredArgsConstructor
public class CommunityPostLikeService {
    private final CommunityTimelineService timeline;
    private final JdbcTemplate jdbc;

    public record Summary(long likeCount, boolean likedByMe) {}

    @Transactional(readOnly = true)
    public Map<Long, Summary> summaries(Long communityId, String username) {
        Long me = timeline.requirePostLikeAccess(communityId, null, username);
        Map<Long, Summary> out = new LinkedHashMap<>();
        jdbc.query("""
                select l.entry_id, count(*) as total, max(case when l.profile_id = ? then 1 else 0 end) as mine
                  from community_post_like l
                  join community_timeline_entry e on e.id = l.entry_id
                 where e.community_id = ?
                 group by l.entry_id
                """, rs -> {
            out.put(rs.getLong("entry_id"), new Summary(rs.getLong("total"), rs.getInt("mine") == 1));
        }, me, communityId);
        return out;
    }

    @Transactional
    public Summary like(Long communityId, Long entryId, String username) {
        Long me = timeline.requirePostLikeAccess(communityId, entryId, username);
        Integer existing = jdbc.queryForObject(
                "select count(*) from community_post_like where entry_id = ? and profile_id = ?", Integer.class, entryId, me);
        if (existing == null || existing == 0) {
            try {
                jdbc.update("insert into community_post_like (entry_id, profile_id, created_at) values (?, ?, ?)",
                        entryId, me, Timestamp.from(Instant.now()));
            } catch (DataIntegrityViolationException alreadyLiked) {
                // A double tap raced another request: the like is already there.
            }
        }
        return summary(entryId, me);
    }

    @Transactional
    public Summary unlike(Long communityId, Long entryId, String username) {
        Long me = timeline.requirePostLikeAccess(communityId, entryId, username);
        jdbc.update("delete from community_post_like where entry_id = ? and profile_id = ?", entryId, me);
        return summary(entryId, me);
    }

    private Summary summary(Long entryId, Long me) {
        Long total = jdbc.queryForObject("select count(*) from community_post_like where entry_id = ?", Long.class, entryId);
        Integer mine = jdbc.queryForObject(
                "select count(*) from community_post_like where entry_id = ? and profile_id = ?", Integer.class, entryId, me);
        return new Summary(total == null ? 0 : total, mine != null && mine > 0);
    }
}
