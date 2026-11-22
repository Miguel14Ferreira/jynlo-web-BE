package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.moderation.UserBlockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * "Novo jogo na comunidade": when a game is created for one or more communities,
 * every member is told once (in the inbox and, when enabled, on the phone).
 * The creator, blocked people and inactive accounts are skipped; each member can
 * turn these alerts off in the notification preferences.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunitySessionAnnouncementService {

    private final CommunityMemberRepository members;
    private final UserBlockRepository blocks;
    private final SessionNotificationService notifications;

    public int announce(Session session, Profile creator, List<Community> communities,
                        boolean recurring, Instant now) {
        if (session == null || session.getId() == null || communities == null || communities.isEmpty()) return 0;
        Map<Long, Recipient> recipients = recipients(creator, communities);
        int sent = 0;
        for (Recipient recipient : recipients.values()) {
            String[] personal = text(session, creator, recipient.community(), recurring, now);
            try {
                notifications.notifyCommunitySession(session, recipient.profile(), personal[0], personal[1]);
                sent++;
            } catch (RuntimeException failed) {
                // One recipient must never prevent the game from being created.
                log.warn("Community game alert failed for session {}", session.getId(), failed);
            }
        }
        log.info("community_session_alerts session={} communities={} recipients={}",
                session.getId(), communities.size(), sent);
        return sent;
    }

    record Recipient(Profile profile, Community community) {}

    /** One alert per person, naming the first community they share with the game. */
    Map<Long, Recipient> recipients(Profile creator, List<Community> communities) {
        Long creatorId = creator == null ? null : creator.getId();
        Map<Long, Recipient> result = new LinkedHashMap<>();
        for (Community community : communities) {
            if (community == null || community.getId() == null) continue;
            for (CommunityMember member : members.findByCommunityIdOrderByJoinedAtAsc(community.getId())) {
                Profile profile = member.getProfile();
                if (profile == null || profile.getId() == null || result.containsKey(profile.getId())) continue;
                if (Objects.equals(profile.getId(), creatorId)) continue;
                if (profile.getUsername() == null || profile.getUsername().isBlank()
                        || !Boolean.TRUE.equals(profile.getEnabled())
                        || Boolean.FALSE.equals(profile.getNotLocked())
                        || profile.getModerationSuspendedAt() != null) continue;
                if (creatorId != null && blocks.existsBetween(profile.getId(), creatorId)) continue;
                result.put(profile.getId(), new Recipient(profile, community));
            }
        }
        return result;
    }

    static String[] text(Session session, Profile creator, Community community, boolean recurring, Instant now) {
        String communityName = label(community == null ? null : community.getName(), "a tua comunidade");
        String sport = label(session.getSport(), "jogo");
        String who = label(creator == null ? null : creator.getUsername(), "Alguém");
        String when = session.getGameDate() == null ? null
                : SessionReminderService.whenLabel(session.getGameDate().toInstant(), now);
        StringBuilder message = new StringBuilder(who).append(" marcou ").append(sport);
        if (when != null) {
            message.append(", ").append(when.substring(0, 1).toLowerCase(Locale.ROOT)).append(when.substring(1));
        }
        if (recurring) message.append(" (jogo recorrente)");
        message.append(". Inscreve-te na Jynlo.");
        return new String[]{"Novo jogo em " + communityName, message.toString()};
    }

    private static String label(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        String cleaned = value.strip().replaceAll("[\\p{Cntrl}]+", " ");
        return cleaned.length() > 60 ? cleaned.substring(0, 60).strip() : cleaned;
    }
}
