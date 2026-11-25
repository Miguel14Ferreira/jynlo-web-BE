package com.example.desporto24.service;

import com.example.desporto24.model.Chat;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

/**
 * Instagram-style message requests. People who are not friends can write once;
 * the message waits in the recipient's "Pedidos de mensagem" until accepted.
 * Some relations open the conversation directly (the recipient follows the
 * sender, or both share a community), unless the recipient restricted it.
 */
@Service
@RequiredArgsConstructor
public class MessageRequestService {
    public enum Policy { EVERYONE, FOLLOWING, COMMUNITIES }
    public enum Mode { DIRECT, REQUEST, PENDING, RECEIVED, NOT_ALLOWED }
    public record Availability(Mode mode, String reason) {}
    public record IncomingRequest(Long id, String username, String fullName, String profilePicUrl, String text, Instant createdAt) {}

    private static final Set<String> FINAL = Set.of("DECLINED");
    private final JdbcTemplate jdbc;
    private final ProfileRepository profiles;
    private final UserBlockService blocks;
    private final com.example.desporto24.repository.FriendRepository friends;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private SessionContactAccessService sessionContacts;

    private Profile profile(String username) {
        return profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
    }

    public Policy policy(Long profileId) {
        List<String> rows = jdbc.queryForList("select policy from message_privacy where profile_id = ?", String.class, profileId);
        try { return rows.isEmpty() ? Policy.EVERYONE : Policy.valueOf(rows.get(0)); }
        catch (IllegalArgumentException e) { return Policy.EVERYONE; }
    }

    @Transactional
    public Policy setPolicy(String username, Policy policy) {
        Profile me = profile(username);
        jdbc.update("""
                insert into message_privacy(profile_id, policy) values (?, ?)
                on conflict (profile_id) do update set policy = excluded.policy
                """, me.getId(), policy.name());
        return policy;
    }

    public Policy myPolicy(String username) { return policy(profile(username).getId()); }

    private boolean follows(Long followerId, Long followingId) {
        Integer n = jdbc.queryForObject("select count(*) from profile_follow where follower_id = ? and following_id = ?",
                Integer.class, followerId, followingId);
        return n != null && n > 0;
    }

    private boolean shareCommunity(Long a, Long b) {
        Integer n = jdbc.queryForObject("""
                select count(*) from community c
                 where (c.owner_profile_id = ? or exists (select 1 from community_member m where m.community_id = c.id and m.profile_id = ?))
                   and (c.owner_profile_id = ? or exists (select 1 from community_member m where m.community_id = c.id and m.profile_id = ?))
                """, Integer.class, a, a, b, b);
        return n != null && n > 0;
    }

    private boolean acceptedBetween(Long a, Long b) {
        Integer n = jdbc.queryForObject("""
                select count(*) from message_request where status = 'ACCEPTED'
                   and ((sender_id = ? and recipient_id = ?) or (sender_id = ? and recipient_id = ?))
                """, Integer.class, a, b, b, a);
        return n != null && n > 0;
    }

    /**
     * Whether a conversation between these two is open without a pending request
     * (beyond friendship and session contacts, which ChatService checks first).
     */
    public boolean directlyAllowed(Profile a, Profile b) {
        if (a == null || b == null || a.getId() == null || b.getId() == null) return false;
        if (acceptedBetween(a.getId(), b.getId())) return true;
        // Opening a conversation directly needs a link the recipient chose or shares.
        return relationOpens(a, b) || relationOpens(b, a);
    }

    private boolean relationOpens(Profile sender, Profile recipient) {
        Policy policy = policy(recipient.getId());
        if (follows(recipient.getId(), sender.getId())) return true;
        return policy != Policy.FOLLOWING && shareCommunity(sender.getId(), recipient.getId());
    }

    private String status(Long senderId, Long recipientId) {
        List<String> rows = jdbc.queryForList("select status from message_request where sender_id = ? and recipient_id = ?",
                String.class, senderId, recipientId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public Availability availability(String actor, String target) {
        Profile me = profile(actor), other = profile(target);
        if (me.getId().equals(other.getId())) return new Availability(Mode.NOT_ALLOWED, "SELF");
        try { blocks.ensureInteractionAllowed(me, other); }
        catch (RuntimeException blocked) { return new Availability(Mode.NOT_ALLOWED, "BLOCKED"); }
        if (friends.findConnection(me.getId(), other.getId()).isPresent()
                || (sessionContacts != null && sessionContacts.mayContact(me.getUsername(), other.getUsername()))
                || directlyAllowed(me, other)) return new Availability(Mode.DIRECT, null);
        String mine = status(me.getId(), other.getId());
        if ("PENDING".equals(mine)) return new Availability(Mode.PENDING, null);
        if (mine != null && FINAL.contains(mine)) return new Availability(Mode.NOT_ALLOWED, "DECLINED");
        // Someone who already wrote to me: answering accepts their request.
        if ("PENDING".equals(status(other.getId(), me.getId()))) return new Availability(Mode.RECEIVED, null);
        Policy policy = policy(other.getId());
        if (policy == Policy.FOLLOWING) return new Availability(Mode.NOT_ALLOWED, "ONLY_FOLLOWING");
        if (policy == Policy.COMMUNITIES) return new Availability(Mode.NOT_ALLOWED, "ONLY_COMMUNITIES");
        return new Availability(Mode.REQUEST, null);
    }

    @Transactional
    public void send(String actor, String target, String text) {
        String body = text == null ? "" : text.trim();
        if (body.isEmpty() || body.length() > 1000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escreve uma mensagem até 1000 caracteres.");
        Availability availability = availability(actor, target);
        if (availability.mode() == Mode.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já enviaste um pedido de mensagem a esta pessoa.");
        if (availability.mode() == Mode.DIRECT)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já podes enviar mensagens diretamente a esta pessoa.");
        if (availability.mode() != Mode.REQUEST)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta pessoa não aceita pedidos de mensagem.");
        Profile me = profiles.lockForSessionChatQuota(actor).orElseGet(() -> profile(actor));
        Profile other = profile(target);
        // New or unconfirmed accounts can only open a few conversations a day.
        boolean young = !Boolean.TRUE.equals(me.getEnabled()) || me.getJoinDate() == null
                || me.getJoinDate().toInstant().isAfter(Instant.now().minus(24, ChronoUnit.HOURS));
        Integer today = jdbc.queryForObject("select count(*) from message_request where sender_id = ? and created_at > ?",
                Integer.class, me.getId(), Timestamp.from(Instant.now().minus(24, ChronoUnit.HOURS)));
        if (today != null && today >= (young ? 5 : 30))
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Chegaste ao limite de pedidos de mensagem por hoje.");
        jdbc.update("""
                insert into message_request(sender_id, recipient_id, first_text, status, created_at)
                values (?, ?, ?, 'PENDING', ?)
                """, me.getId(), other.getId(), body, Timestamp.from(Instant.now()));
    }

    public List<IncomingRequest> incoming(String username) {
        Profile me = profile(username);
        return jdbc.query("""
                select r.id, p.username, p.full_name, p.profile_pic_url, r.first_text, r.created_at
                  from message_request r join profile p on p.id = r.sender_id
                 where r.recipient_id = ? and r.status = 'PENDING'
                 order by r.created_at desc limit 100
                """, (rs, i) -> new IncomingRequest(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
                rs.getString(5), rs.getTimestamp(6).toInstant()), me.getId());
    }

    /** Requests this account sent that are still waiting, so they show in its inbox like Instagram. */
    public List<IncomingRequest> sent(String username) {
        Profile me = profile(username);
        return jdbc.query("""
                select r.id, p.username, p.full_name, p.profile_pic_url, r.first_text, r.created_at
                  from message_request r join profile p on p.id = r.recipient_id
                 where r.sender_id = ? and r.status = 'PENDING'
                 order by r.created_at desc limit 100
                """, (rs, i) -> new IncomingRequest(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
                rs.getString(5), rs.getTimestamp(6).toInstant()), me.getId());
    }

    public int pendingCount(String username) {
        Integer n = jdbc.queryForObject("select count(*) from message_request where recipient_id = ? and status = 'PENDING'",
                Integer.class, profile(username).getId());
        return n == null ? 0 : n;
    }

    /** Returns the first message (to be stored in the conversation) when accepted. */
    @Transactional
    public Chat decide(Long requestId, String username, boolean accept) {
        Profile me = profile(username);
        var rows = jdbc.queryForList("""
                select r.sender_id, r.first_text, r.created_at, p.username from message_request r join profile p on p.id = r.sender_id
                 where r.id = ? and r.recipient_id = ? and r.status = 'PENDING' for update
                """, requestId, me.getId());
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado ou já respondido.");
        var row = rows.get(0);
        jdbc.update("update message_request set status = ?, decided_at = ? where id = ?",
                accept ? "ACCEPTED" : "DECLINED", Timestamp.from(Instant.now()), requestId);
        if (!accept) return null;
        Timestamp created = (Timestamp) row.get("created_at");
        Chat first = new Chat((String) row.get("username"), me.getUsername(), (String) row.get("first_text"),
                null, new java.util.Date(created.getTime()));
        return first;
    }
}
