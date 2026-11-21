package com.example.desporto24.service.availability;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.community.CommunityMember;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.PrivateGroupMemberRepository;
import com.example.desporto24.repository.PrivateGroupRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.service.ConversationNotificationMuteService;
import com.example.desporto24.service.MobilePushDeliveryService;
import com.example.desporto24.service.NotificationPreferencesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * "Quem pode?": a member of a private group or a community proposes 1 to 5
 * dates and times; members answer yes, maybe or no for each. The other
 * members are alerted once when it is created, and once more after 24 hours
 * if they still have not answered. The best option can then become a game.
 *
 * Only members see or answer a request, and only its creator closes it.
 */
@Slf4j
@Service
public class AvailabilityService {

    public static final Set<String> KINDS = Set.of("private", "community");
    public static final Set<String> ANSWERS = Set.of("YES", "MAYBE", "NO");
    static final int MAX_SLOTS = 5;
    static final int MAX_OPEN_PER_TARGET = 3;
    static final int MAX_NOTE = 200;
    static final Duration MIN_AHEAD = Duration.ofHours(1);
    static final Duration MAX_AHEAD = Duration.ofDays(60);
    static final Duration REMIND_AFTER = Duration.ofHours(24);
    static final Duration REMIND_NOT_LATER_THAN = Duration.ofHours(3);
    private static final ZoneId LISBON = ZoneId.of("Europe/Lisbon");
    private static final DateTimeFormatter LABEL =
            DateTimeFormatter.ofPattern("EEE dd/MM HH:mm", Locale.forLanguageTag("pt-PT")).withZone(LISBON);

    public record CreateRequest(String kind, Long targetId, String note, List<Instant> slots) {}
    public record AnswerRequest(String answer) {}
    public record SlotView(long id, Instant startsAt, List<String> yes, List<String> maybe, int no, String mine) {}
    public record RequestView(long id, String kind, long targetId, String creator, String note, Instant createdAt,
                              boolean mine, int members, int answered, List<SlotView> slots) {}

    private final AvailabilityStore store;
    private final PrivateGroupRepository groups;
    private final PrivateGroupMemberRepository groupMembers;
    private final CommunityRepository communities;
    private final CommunityMemberRepository communityMembers;
    private final NotificationsRepository notifications;
    private final NotificationPreferencesService preferences;
    private final ConversationNotificationMuteService mutes;
    private final ApplicationEventPublisher events;
    Clock clock = Clock.systemUTC();

    public AvailabilityService(AvailabilityStore store, PrivateGroupRepository groups,
                               PrivateGroupMemberRepository groupMembers, CommunityRepository communities,
                               CommunityMemberRepository communityMembers, NotificationsRepository notifications,
                               NotificationPreferencesService preferences, ConversationNotificationMuteService mutes,
                               ApplicationEventPublisher events) {
        this.store = store;
        this.groups = groups;
        this.groupMembers = groupMembers;
        this.communities = communities;
        this.communityMembers = communityMembers;
        this.notifications = notifications;
        this.preferences = preferences;
        this.mutes = mutes;
        this.events = events;
    }

    // ---- Reading ---------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<RequestView> open(String rawKind, Long targetId, String username) {
        String kind = kind(rawKind);
        long target = target(targetId);
        List<String> members = requireMember(kind, target, username);
        return store.open(kind, target, clock.instant()).stream().map(r -> view(r, username, members)).toList();
    }

    // ---- Writing ---------------------------------------------------------------

    @Transactional
    public RequestView create(CreateRequest request, String username) {
        if (request == null) throw bad("Pedido inválido.");
        String kind = kind(request.kind());
        long target = target(request.targetId());
        List<String> members = requireMember(kind, target, username);
        Instant now = clock.instant();
        List<Instant> slots = slots(request.slots(), now);
        String note = note(request.note());
        if (store.openCount(kind, target, now) >= MAX_OPEN_PER_TARGET) {
            throw bad("Já há " + MAX_OPEN_PER_TARGET + " pedidos abertos aqui. Fecha um antes de criar outro.");
        }
        long id = store.create(kind, target, username, note, slots, now);
        String place = placeName(kind, target);
        String subject = "Quem pode? " + place;
        String message = username + " quer saber quem pode: " + labels(slots) + ".";
        for (String member : members) {
            if (member.equalsIgnoreCase(username)) continue;
            try {
                notify(member, kind, target, NotificationType.AVAILABILITY_REQUEST, subject, message, username);
            } catch (RuntimeException ignored) {
                // One member's alert must never block the others.
            }
        }
        return view(store.find(id).orElseThrow(), username, members);
    }

    @Transactional
    public RequestView answer(Long requestId, Long slotId, AnswerRequest body, String username) {
        AvailabilityStore.Request request = store.find(requestId == null ? -1 : requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<String> members = requireMember(request.kind(), request.targetId(), username);
        if (request.closedAt() != null) throw bad("Este pedido já foi fechado.");
        String answer = body == null || body.answer() == null ? "" : body.answer().strip().toUpperCase(Locale.ROOT);
        if (!ANSWERS.contains(answer)) throw bad("Resposta inválida.");
        Instant now = clock.instant();
        AvailabilityStore.Slot slot = store.slots(request.id()).stream()
                .filter(s -> Objects.equals(s.id(), slotId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!slot.startsAt().isAfter(now)) throw bad("Esta opção já passou.");
        store.answer(slot.id(), canonical(members, username), answer, now);
        return view(request, username, members);
    }

    @Transactional
    public void close(Long requestId, String username) {
        AvailabilityStore.Request request = store.find(requestId == null ? -1 : requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireMember(request.kind(), request.targetId(), username);
        if (!request.creator().equalsIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só quem criou o pedido o pode fechar.");
        }
        store.close(request.id(), clock.instant());
    }

    // ---- Reminder ----------------------------------------------------------------

    @Scheduled(fixedDelayString = "${jynlo.availability.reminder-check-ms:1800000}",
               initialDelayString = "${jynlo.availability.reminder-initial-delay-ms:240000}")
    public void remindLateResponders() {
        Instant now = clock.instant();
        for (Long id : store.dueForReminder(now.minus(REMIND_AFTER), now.plus(REMIND_NOT_LATER_THAN))) {
            try {
                remind(id, now);
            } catch (RuntimeException failed) {
                log.warn("Availability reminder failed for request {}", id, failed);
            }
        }
    }

    @Transactional
    public int remind(long requestId, Instant now) {
        if (!store.claimReminder(requestId, now)) return 0;
        AvailabilityStore.Request request = store.find(requestId).orElse(null);
        if (request == null || request.closedAt() != null) return 0;
        Set<String> answered = new HashSet<>();
        store.answers(requestId).forEach(a -> answered.add(a.username().toLowerCase(Locale.ROOT)));
        answered.add(request.creator().toLowerCase(Locale.ROOT));
        String place = placeName(request.kind(), request.targetId());
        int sent = 0;
        for (String member : members(request.kind(), request.targetId())) {
            if (answered.contains(member.toLowerCase(Locale.ROOT))) continue;
            try {
                if (notify(member, request.kind(), request.targetId(), NotificationType.AVAILABILITY_REMINDER,
                        "Ainda não respondeste: quem pode?",
                        request.creator() + " está à espera da tua resposta em " + place + ".", request.creator())) sent++;
            } catch (RuntimeException ignored) {
                // Keep reminding the others.
            }
        }
        return sent;
    }

    // ---- Helpers -----------------------------------------------------------------

    RequestView view(AvailabilityStore.Request request, String viewer, List<String> members) {
        List<AvailabilityStore.Answer> answers = store.answers(request.id());
        Set<String> memberKeys = new HashSet<>();
        members.forEach(m -> memberKeys.add(m.toLowerCase(Locale.ROOT)));
        Set<String> answeredBy = new TreeSet<>();
        Map<Long, List<AvailabilityStore.Answer>> bySlot = new LinkedHashMap<>();
        for (AvailabilityStore.Answer a : answers) {
            // People who left the group no longer count.
            if (!memberKeys.contains(a.username().toLowerCase(Locale.ROOT))) continue;
            bySlot.computeIfAbsent(a.slotId(), k -> new ArrayList<>()).add(a);
            answeredBy.add(a.username().toLowerCase(Locale.ROOT));
        }
        List<SlotView> slots = new ArrayList<>();
        for (AvailabilityStore.Slot slot : store.slots(request.id())) {
            List<String> yes = new ArrayList<>(), maybe = new ArrayList<>();
            int no = 0;
            String mine = null;
            for (AvailabilityStore.Answer a : bySlot.getOrDefault(slot.id(), List.of())) {
                switch (a.answer()) {
                    case "YES" -> yes.add(a.username());
                    case "MAYBE" -> maybe.add(a.username());
                    default -> no++;
                }
                if (a.username().equalsIgnoreCase(viewer)) mine = a.answer();
            }
            slots.add(new SlotView(slot.id(), slot.startsAt(), yes, maybe, no, mine));
        }
        return new RequestView(request.id(), request.kind(), request.targetId(), request.creator(), request.note(),
                request.createdAt(), request.creator().equalsIgnoreCase(viewer), members.size(), answeredBy.size(), slots);
    }

    List<String> members(String kind, long target) {
        if ("private".equals(kind)) {
            return groupMembers.findByGroupIdOrderByJoinedAtAsc(target).stream()
                    .map(m -> m.getUsername()).filter(Objects::nonNull).toList();
        }
        return communityMembers.findByCommunityIdOrderByJoinedAtAsc(target).stream()
                .map(CommunityMember::getProfile).filter(Objects::nonNull)
                .map(p -> p.getUsername()).filter(Objects::nonNull).toList();
    }

    private List<String> requireMember(String kind, long target, String username) {
        List<String> members = members(kind, target);
        if (username == null || members.stream().noneMatch(username::equalsIgnoreCase)) {
            // Same answer whether the group exists or not.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return members;
    }

    private static String canonical(List<String> members, String username) {
        return members.stream().filter(username::equalsIgnoreCase).findFirst().orElse(username);
    }

    private String placeName(String kind, long target) {
        String name = "private".equals(kind)
                ? groups.findById(target).map(g -> g.getTitle()).orElse(null)
                : communities.findById(target).map(c -> c.getName()).orElse(null);
        if (name == null || name.isBlank()) return "private".equals(kind) ? "no grupo" : "na comunidade";
        String cleaned = name.strip().replaceAll("[\\p{Cntrl}]+", " ");
        return cleaned.length() > 60 ? cleaned.substring(0, 60).strip() : cleaned;
    }

    private boolean notify(String member, String kind, long target, NotificationType type,
                           String subject, String message, String related) {
        if (mutes.muted(member.toLowerCase(Locale.ROOT), kind, Long.toString(target))) return false;
        Notifications n = new Notifications();
        n.setSubject(subject);
        n.setMessage(message);
        n.setGreeting("");
        n.setSignature("");
        n.setCreatedAt(clock.instant().toString());
        n.setUsername(member);
        n.setType(type);
        n.setRelatedUsername(related);
        if ("private".equals(kind)) n.setPrivateGroupId(target); else n.setCommunityId(target);
        n.setRead(false);
        if (!preferences.allowsInApp(member, n)) return false;
        Notifications saved = notifications.save(n);
        if (saved != null && saved.getId() != null) {
            events.publishEvent(new MobilePushDeliveryService.StoredNotification(
                    saved.getUsername(), saved.getId(), type.name(), null,
                    "community".equals(kind) ? target : null, related, true,
                    "private".equals(kind) ? target : null));
        }
        return true;
    }

    static List<Instant> slots(List<Instant> raw, Instant now) {
        if (raw == null || raw.isEmpty()) throw bad("Propõe pelo menos uma data e hora.");
        TreeSet<Instant> unique = new TreeSet<>();
        for (Instant start : raw) {
            if (start == null) throw bad("Data inválida.");
            if (start.isBefore(now.plus(MIN_AHEAD))) throw bad("As opções têm de ser daqui a pelo menos 1 hora.");
            if (start.isAfter(now.plus(MAX_AHEAD))) throw bad("As opções têm de ser nos próximos 60 dias.");
            unique.add(start.truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        }
        if (unique.size() > MAX_SLOTS) throw bad("No máximo " + MAX_SLOTS + " opções.");
        return List.copyOf(unique);
    }

    static String note(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String value = raw.strip();
        if (value.length() > MAX_NOTE || value.codePoints().anyMatch(c -> Character.isISOControl(c) && c != '\n')) {
            throw bad("Nota inválida (máximo " + MAX_NOTE + " caracteres).");
        }
        return value;
    }

    static String labels(List<Instant> slots) {
        List<String> parts = slots.stream().map(LABEL::format).toList();
        return String.join(", ", parts.size() > 3 ? parts.subList(0, 3) : parts) + (parts.size() > 3 ? "…" : "");
    }

    private static String kind(String raw) {
        String value = raw == null ? "" : raw.strip().toLowerCase(Locale.ROOT);
        if (!KINDS.contains(value)) throw bad("Tipo inválido.");
        return value;
    }

    private static long target(Long id) {
        if (id == null || id <= 0) throw bad("Grupo inválido.");
        return id;
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
