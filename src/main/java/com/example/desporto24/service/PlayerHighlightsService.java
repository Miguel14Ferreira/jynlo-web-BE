package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityMatchResult;
import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunityRoundEntry;
import com.example.desporto24.model.community.CommunityTimelineEntry;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.repository.community.CommunityRoundEntryRepository;
import com.example.desporto24.repository.social.ProfileFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

/**
 * What keeps players coming back (like Wady Le3b): their own numbers on the profile,
 * the month's top players in each community and a "Jogador do mês" vote in the community chat.
 * Built on the results the community already records (goals, assists, MVP).
 */
@Service
@RequiredArgsConstructor
public class PlayerHighlightsService {

    public record ProfileStats(long games, long wins, long goals, long assists, long mvps) {}
    public record MonthLeader(String name, String username, long games, long goals, long assists, long mvps, long score) {}
    public record MonthLeaders(String month, List<MonthLeader> leaders, boolean enabled, boolean canManage) {}

    private static final ZoneId LISBON = ZoneId.of("Europe/Lisbon");
    private static final Locale PT = Locale.forLanguageTag("pt-PT");

    private final ProfileRepository profiles;
    private final CommunityRepository communities;
    private final CommunityMemberRepository members;
    private final CommunityRoundEntryRepository entries;
    private final ProfileFollowRepository follows;
    private final CommunityTimelineService timeline;

    @Transactional(readOnly = true)
    public ProfileStats profileStats(String username, String viewer) {
        Profile target = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
        if (target.isPrivateProfile() && (viewer == null || !viewer.equalsIgnoreCase(target.getUsername()))) {
            Profile me = viewer == null ? null : profiles.findUserByUsernameIgnoreCase(viewer).orElse(null);
            if (me == null || !follows.existsByFollowerIdAndFollowingId(me.getId(), target.getId())) {
                return new ProfileStats(0, 0, 0, 0, 0);
            }
        }
        var list = entries.findEntriesForProfile(target.getId());
        long wins = list.stream().filter(e -> e.getResult() == CommunityMatchResult.WIN).count();
        return new ProfileStats(list.size(), wins, list.stream().mapToLong(CommunityRoundEntry::getGoals).sum(),
                list.stream().mapToLong(CommunityRoundEntry::getAssists).sum(),
                list.stream().filter(CommunityRoundEntry::isMvp).count());
    }

    @Transactional(readOnly = true)
    public MonthLeaders monthLeaders(Long communityId, String viewer, String month) {
        Community community = readable(communityId, viewer);
        YearMonth ym = parseMonth(month);
        boolean manager = isManager(community, viewer);
        // Off unless the community turned it on; managers still see the switch.
        if (!community.isMonthHighlightsEnabled())
            return new MonthLeaders(ym.toString(), List.of(), false, manager);
        return new MonthLeaders(ym.toString(), leaders(community.getId(), ym, 5), true, manager);
    }

    /** Owner/Admin: turn "Destaques do mês" and the "Jogador do mês" vote on or off for the community. */
    @Transactional
    public MonthLeaders setEnabled(Long communityId, String admin, boolean enabled) {
        Community community = readable(communityId, admin);
        if (!isManager(community, admin))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só os administradores da comunidade podem mudar isto.");
        community.setMonthHighlightsEnabled(enabled);
        communities.save(community);
        return monthLeaders(communityId, admin, null);
    }

    private boolean isManager(Community community, String username) {
        Profile me = username == null ? null : profiles.findUserByUsernameIgnoreCase(username).orElse(null);
        if (me == null) return false;
        return community.getOwner() != null && community.getOwner().getId().equals(me.getId())
                || members.findByCommunityIdAndProfileId(community.getId(), me.getId())
                   .map(m -> m.getRole() == CommunityRole.ADMIN || m.getRole() == CommunityRole.OWNER).orElse(false);
    }

    /** Community admins: posts a "Jogador do mês" poll with the month's top players in the community chat. */
    @Transactional
    public void playerOfMonthPoll(Long communityId, String admin, String month) {
        Community community = readable(communityId, admin);
        if (!community.isMonthHighlightsEnabled())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ativa primeiro os destaques do mês nesta comunidade.");
        if (!isManager(community, admin)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só os administradores da comunidade podem criar esta votação.");
        YearMonth ym = parseMonth(month);
        List<MonthLeader> top = leaders(communityId, ym, ChatPoll.MAX_OPTIONS);
        if (top.size() < ChatPoll.MIN_OPTIONS)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ainda não há resultados suficientes este mês para uma votação.");
        String monthName = ym.getMonth().getDisplayName(TextStyle.FULL, PT);
        StringBuilder body = new StringBuilder(ChatPoll.PREFIX).append("🏆 Jogador do mês de ").append(monthName).append('?');
        Set<String> seen = new HashSet<>();
        for (MonthLeader leader : top) {
            String option = (leader.username() != null ? "@" + leader.username() : leader.name());
            option = option.length() > ChatPoll.MAX_OPTION ? option.substring(0, ChatPoll.MAX_OPTION) : option;
            if (seen.add(option.toLowerCase(Locale.ROOT))) body.append('\n').append(option);
        }
        timeline.post(communityId, CommunityTimelineEntry.Kind.CHAT, body.toString(), admin);
    }

    private List<MonthLeader> leaders(Long communityId, YearMonth ym, int limit) {
        var from = ym.atDay(1).atStartOfDay(LISBON).toInstant();
        var to = ym.plusMonths(1).atDay(1).atStartOfDay(LISBON).toInstant();
        Map<Long, long[]> totals = new LinkedHashMap<>();
        Map<Long, String[]> names = new HashMap<>();
        for (CommunityRoundEntry e : entries.findCommunityEntriesBetween(communityId, from, to)) {
            var player = e.getCommunityPlayer();
            if (player == null) continue;
            long[] row = totals.computeIfAbsent(player.getId(), k -> new long[4]);
            row[0]++; row[1] += e.getGoals(); row[2] += e.getAssists(); if (e.isMvp()) row[3]++;
            Profile linked = e.getProfile() != null ? e.getProfile() : player.getProfile();
            names.putIfAbsent(player.getId(), new String[]{player.getDisplayName(), linked == null ? null : linked.getUsername()});
        }
        return totals.entrySet().stream()
                .map(en -> {
                    long[] r = en.getValue(); String[] n = names.get(en.getKey());
                    return new MonthLeader(n[0], n[1], r[0], r[1], r[2], r[3], r[3] * 3 + r[1] * 2 + r[2]);
                })
                .filter(l -> l.score() > 0)
                .sorted(Comparator.comparingLong(MonthLeader::score).reversed().thenComparing(MonthLeader::goals, Comparator.reverseOrder()))
                .limit(limit).toList();
    }

    private Community readable(Long communityId, String viewer) {
        Community community = communities.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidade não encontrada"));
        if (community.isPublicRankingEnabled()) return community;
        Profile me = viewer == null ? null : profiles.findUserByUsernameIgnoreCase(viewer).orElse(null);
        boolean member = me != null && ((community.getOwner() != null && community.getOwner().getId().equals(me.getId()))
                || members.findByCommunityIdAndProfileId(communityId, me.getId()).isPresent());
        if (!member) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só os membros veem estes resultados.");
        return community;
    }

    private static YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) return YearMonth.now(LISBON);
        try { return YearMonth.parse(month.strip()); }
        catch (RuntimeException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mês inválido (AAAA-MM)."); }
    }
}
