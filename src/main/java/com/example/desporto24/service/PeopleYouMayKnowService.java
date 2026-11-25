package com.example.desporto24.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * "Pessoas para conhecer" with a personal reason, like Instagram: people followed by the people you follow.
 * Only people you already follow count as the reason, so nothing private is revealed: you could already
 * see whom they follow. Excludes you, people you follow, blocks in either direction and suspended accounts.
 */
@Service
@RequiredArgsConstructor
public class PeopleYouMayKnowService {

    public record Suggestion(String username, String fullName, String nickname, String profilePicUrl,
                             int followedByCount, List<String> followedBy) {}

    private final JdbcTemplate jdbc;

    public List<Suggestion> suggestions(String viewer, int limit) {
        int size = Math.max(1, Math.min(limit, 20));
        return jdbc.query("""
                with me as (select id from profile where lower(username) = lower(?)),
                     mine as (select f.following_id as id from profile_follow f, me where f.follower_id = me.id),
                     candidates as (
                       select f2.following_id as candidate_id, p.username as via
                         from profile_follow f2
                         join mine on mine.id = f2.follower_id
                         join profile p on p.id = f2.follower_id
                       )
                select t.username, t.full_name, t.nickname, t.profile_pic_url, count(*) as n,
                       string_agg(c.via, ',' order by c.via) as via
                  from candidates c
                  join profile t on t.id = c.candidate_id, me
                 where t.id <> me.id
                   and t.id not in (select id from mine)
                   and t.moderation_suspended_at is null
                   and coalesce(t.enabled, true)
                   and not exists (select 1 from user_block b
                                    where (b.blocker_profile_id = me.id and b.blocked_profile_id = t.id)
                                       or (b.blocker_profile_id = t.id and b.blocked_profile_id = me.id))
                 group by t.id, t.username, t.full_name, t.nickname, t.profile_pic_url
                 order by n desc, t.username
                 limit ?
                """, (rs, i) -> {
            String via = rs.getString(6);
            List<String> names = via == null ? List.of() : new ArrayList<>(Arrays.asList(via.split(",")));
            return new Suggestion(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getInt(5), names.subList(0, Math.min(2, names.size())));
        }, viewer, size);
    }
}
