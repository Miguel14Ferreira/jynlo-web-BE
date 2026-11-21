package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.TournamentEntryType;
import com.example.desporto24.enumeration.TournamentParticipantStatus;
import com.example.desporto24.enumeration.TournamentScoringMode;
import com.example.desporto24.enumeration.TournamentStatus;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.community.CommunityTournamentParticipant;
import com.example.desporto24.model.community.CommunityTournamentRankingEvent;
import com.example.desporto24.model.community.CommunityTournamentRankingResult;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.community.CommunityTournamentParticipantRepository;
import com.example.desporto24.repository.community.CommunityTournamentRankingEventRepository;
import com.example.desporto24.repository.community.CommunityTournamentRankingResultRepository;
import com.example.desporto24.repository.community.CommunityTournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * "Classificação por provas": one engine for every individual sport without a
 * match format. Each event ranks participants by TIME (lowest wins), SCORE
 * (highest wins: points, marks, judges' scores) or entered POSITION; places earn
 * points (25, 18, 15… by default) and the table is the sum over all events.
 * Equal results share the place and its points (1, 2, 2, 4…). DNF earns nothing.
 */
@Service
@RequiredArgsConstructor
public class EventRankingService {
    public static final String TIME = "TIME";
    public static final String SCORE = "SCORE";
    public static final String POSITION = "POSITION";
    public static final String DEFAULT_POINTS = "25,18,15,12,10,8,6,4,2,1";
    static final int MAX_EVENTS = 100;
    static final int MAX_PARTICIPANTS = 200;

    public record ResultInput(Long participantId, BigDecimal value, Integer position, Boolean didNotFinish) {}
    public record EventInput(String name, LocalDate heldOn, List<ResultInput> results) {}
    public record Participant(Long id, String name) {}
    public record Placed(Long participantId, String name, BigDecimal value, Integer place, int points, boolean didNotFinish) {}
    public record Event(Long id, String name, LocalDate heldOn, List<Placed> results) {}
    public record Row(int position, Long participantId, String name, int points, int events, int wins,
                      int podiums, BigDecimal best) {}
    public record Overview(Long tournamentId, TournamentStatus status, String valueKind, List<Integer> pointsByPlace,
                           boolean canManage, List<Participant> participants, List<Event> events, List<Row> table) {}

    private final ProfileRepository profiles;
    private final CommunityMemberRepository members;
    private final CommunityTournamentRepository tournaments;
    private final CommunityTournamentParticipantRepository participants;
    private final CommunityTournamentRankingEventRepository events;
    private final CommunityTournamentRankingResultRepository results;

    /** Sports with no competition to rank; sessions are enough for them. */
    static final Set<String> NOT_COMPETITIVE = Set.of("airsoft", "caminhada", "campismo", "ginásio", "montanhismo",
            "musculação", "pilates", "yoga");

    public static boolean supports(String sport) {
        return sport != null && !sport.isBlank() && !NOT_COMPETITIVE.contains(sport.trim().toLowerCase(Locale.ROOT));
    }

    @Transactional(readOnly = true)
    public Overview overview(Long communityId, Long tournamentId, String username) {
        Access access = access(communityId, tournamentId, username, false);
        return build(access.tournament(), access.manager());
    }

    /** Table only, for the opt-in public page. */
    @Transactional(readOnly = true)
    public List<Row> publicTable(CommunityTournament tournament) {
        return build(tournament, false, true).table();
    }

    @Transactional
    public Overview setValueKind(Long communityId, Long tournamentId, String username, String kind) {
        Access access = access(communityId, tournamentId, username, true);
        if (!TIME.equals(kind) && !SCORE.equals(kind) && !POSITION.equals(kind)) {
            throw bad("Escolhe tempo, pontuação ou posição");
        }
        if (events.countByTournamentId(tournamentId) > 0 && !kind.equals(access.tournament().getRankingValueKind())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já há provas registadas: a forma de classificar não pode mudar");
        }
        access.tournament().setRankingValueKind(kind);
        tournaments.save(access.tournament());
        return build(access.tournament(), true);
    }

    @Transactional
    public Overview setPoints(Long communityId, Long tournamentId, String username, String points) {
        Access access = access(communityId, tournamentId, username, true);
        requireEditable(access.tournament());
        access.tournament().setRacePointsByPlace(serialise(parsePoints(points)));
        tournaments.save(access.tournament());
        return build(access.tournament(), true);
    }

    @Transactional
    public Overview saveEvent(Long communityId, Long tournamentId, Long eventId, String username, EventInput input) {
        Access access = access(communityId, tournamentId, username, true);
        CommunityTournament tournament = access.tournament();
        requireEditable(tournament);
        if (input == null || input.name() == null || input.name().isBlank() || input.name().strip().length() > 80) {
            throw bad("Dá um nome à prova (até 80 caracteres)");
        }
        String kind = kind(tournament);
        Map<Long, CommunityTournamentParticipant> confirmed = confirmed(tournamentId).stream()
                .collect(Collectors.toMap(CommunityTournamentParticipant::getId, p -> p));
        List<ResultInput> rows = input.results() == null ? List.of() : input.results();
        if (rows.isEmpty()) throw bad("Indica o resultado de pelo menos um participante");
        Set<Long> seen = new HashSet<>();
        for (ResultInput row : rows) {
            if (row == null || row.participantId() == null || !confirmed.containsKey(row.participantId())) {
                throw bad("Só participantes confirmados do torneio podem ter resultados");
            }
            if (!seen.add(row.participantId())) throw bad("Cada participante só pode ter um resultado por prova");
            boolean dnf = Boolean.TRUE.equals(row.didNotFinish());
            if (dnf) continue;
            if (POSITION.equals(kind)) {
                if (row.position() == null || row.position() < 1 || row.position() > MAX_PARTICIPANTS) {
                    throw bad("Indica a posição (1.º, 2.º…) ou marca como não terminou");
                }
            } else if (row.value() == null || row.value().signum() < 0 || row.value().compareTo(new BigDecimal("999999")) > 0) {
                throw bad(TIME.equals(kind) ? "Indica o tempo em segundos ou marca como não terminou"
                        : "Indica a pontuação ou marca, ou marca como não terminou");
            }
        }
        CommunityTournamentRankingEvent event;
        if (eventId == null) {
            if (events.countByTournamentId(tournamentId) >= MAX_EVENTS) throw bad("Máximo de " + MAX_EVENTS + " provas");
            event = new CommunityTournamentRankingEvent();
            event.setTournamentId(tournamentId);
            event.setCreatedAt(Instant.now());
        } else {
            event = events.findById(eventId).filter(e -> e.getTournamentId().equals(tournamentId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada"));
            results.deleteByEventId(eventId);
            results.flush();
        }
        event.setName(input.name().strip());
        event.setHeldOn(input.heldOn());
        event = events.save(event);
        for (ResultInput row : rows) {
            CommunityTournamentRankingResult result = new CommunityTournamentRankingResult();
            result.setEventId(event.getId());
            result.setParticipantId(row.participantId());
            boolean dnf = Boolean.TRUE.equals(row.didNotFinish());
            result.setDidNotFinish(dnf);
            result.setValue(dnf || POSITION.equals(kind) ? null : row.value());
            result.setPosition(dnf || !POSITION.equals(kind) ? null : row.position());
            results.save(result);
        }
        if (tournament.getStatus() == TournamentStatus.DRAFT) {
            tournament.setStatus(TournamentStatus.IN_PROGRESS);
            tournament.setStartedAt(Instant.now());
            tournaments.save(tournament);
        }
        return build(tournament, true);
    }

    @Transactional
    public Overview deleteEvent(Long communityId, Long tournamentId, Long eventId, String username) {
        Access access = access(communityId, tournamentId, username, true);
        requireEditable(access.tournament());
        CommunityTournamentRankingEvent event = events.findById(eventId)
                .filter(e -> e.getTournamentId().equals(tournamentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada"));
        events.delete(event);
        return build(access.tournament(), true);
    }

    /** Closes the tournament and stores the champion snapshot (none when first place is shared). */
    @Transactional
    public Overview complete(Long communityId, Long tournamentId, String username) {
        Access access = access(communityId, tournamentId, username, true);
        CommunityTournament tournament = access.tournament();
        requireEditable(tournament);
        Overview overview = build(tournament, true);
        if (overview.events().isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Regista pelo menos uma prova");
        List<Row> table = overview.table();
        boolean tied = table.size() > 1 && table.get(0).points() == table.get(1).points()
                && table.get(0).wins() == table.get(1).wins() && table.get(0).podiums() == table.get(1).podiums();
        tournament.setStatus(TournamentStatus.COMPLETED);
        tournament.setCompletedAt(Instant.now());
        if (!table.isEmpty() && !tied) {
            CommunityTournamentParticipant winner = participants.findById(table.getFirst().participantId()).orElse(null);
            Profile profile = winner == null ? null : winner.getProfile();
            tournament.setChampionProfileId(profile == null ? null : profile.getId());
            tournament.setChampionUsername(profile == null ? null : profile.getUsername());
            tournament.setChampionDisplayName(table.getFirst().name());
            tournament.setChampionPoints((long) table.getFirst().points());
            tournament.setChampionTied(false);
        } else {
            tournament.setChampionTied(tied);
        }
        tournaments.save(tournament);
        return build(tournament, true);
    }

    // ---- calculation -------------------------------------------------------------------------

    private Overview build(CommunityTournament tournament, boolean manager) {
        return build(tournament, manager, false);
    }

    private Overview build(CommunityTournament tournament, boolean manager, boolean publicNames) {
        String kind = kind(tournament);
        List<Integer> points = parsePoints(tournament.getRacePointsByPlace() == null
                ? DEFAULT_POINTS : tournament.getRacePointsByPlace());
        List<CommunityTournamentParticipant> roster = confirmed(tournament.getId());
        Map<Long, String> names = new LinkedHashMap<>();
        roster.forEach(p -> names.put(p.getId(), publicNames ? publicDisplay(p.getProfile()) : display(p.getProfile())));
        List<CommunityTournamentRankingEvent> list = events.findByTournamentIdOrderByHeldOnAscIdAsc(tournament.getId());
        Map<Long, List<CommunityTournamentRankingResult>> byEvent = list.isEmpty() ? Map.of()
                : results.findByEventIdIn(list.stream().map(CommunityTournamentRankingEvent::getId).toList())
                    .stream().collect(Collectors.groupingBy(CommunityTournamentRankingResult::getEventId));
        List<Event> placedEvents = new ArrayList<>();
        for (CommunityTournamentRankingEvent event : list) {
            placedEvents.add(new Event(event.getId(), event.getName(), event.getHeldOn(),
                    place(byEvent.getOrDefault(event.getId(), List.of()), kind, points, names)));
        }
        return new Overview(tournament.getId(), tournament.getStatus(), kind, points, manager,
                names.entrySet().stream().map(e -> new Participant(e.getKey(), e.getValue())).toList(),
                placedEvents, table(placedEvents, kind, names));
    }

    /** Ranks one event. Equal results share the place and its points; DNF has no place. */
    static List<Placed> place(List<CommunityTournamentRankingResult> rows, String kind, List<Integer> points,
                              Map<Long, String> names) {
        Comparator<CommunityTournamentRankingResult> order = POSITION.equals(kind)
                ? Comparator.comparing(CommunityTournamentRankingResult::getPosition)
                : TIME.equals(kind) ? Comparator.comparing(CommunityTournamentRankingResult::getValue)
                : Comparator.comparing(CommunityTournamentRankingResult::getValue).reversed();
        List<CommunityTournamentRankingResult> finished = rows.stream()
                .filter(r -> !r.isDidNotFinish() && (POSITION.equals(kind) ? r.getPosition() != null : r.getValue() != null))
                .sorted(order).toList();
        List<Placed> placed = new ArrayList<>();
        int place = 0;
        for (int i = 0; i < finished.size(); i++) {
            CommunityTournamentRankingResult r = finished.get(i);
            if (i == 0 || order.compare(finished.get(i - 1), r) != 0) place = i + 1;
            int earned = place <= points.size() ? points.get(place - 1) : 0;
            placed.add(new Placed(r.getParticipantId(), names.getOrDefault(r.getParticipantId(), "?"),
                    POSITION.equals(kind) ? null : r.getValue(), place, earned, false));
        }
        rows.stream().filter(CommunityTournamentRankingResult::isDidNotFinish).forEach(r ->
                placed.add(new Placed(r.getParticipantId(), names.getOrDefault(r.getParticipantId(), "?"), null, null, 0, true)));
        return placed;
    }

    static List<Row> table(List<Event> events, String kind, Map<Long, String> names) {
        Map<Long, int[]> totals = new LinkedHashMap<>(); // points, events, wins, podiums
        Map<Long, BigDecimal> best = new HashMap<>();
        for (Event event : events) {
            for (Placed p : event.results()) {
                int[] t = totals.computeIfAbsent(p.participantId(), id -> new int[4]);
                t[0] += p.points();
                t[1]++;
                if (p.place() != null && p.place() == 1) t[2]++;
                if (p.place() != null && p.place() <= 3) t[3]++;
                if (p.value() != null) {
                    best.merge(p.participantId(), p.value(), (a, b) -> TIME.equals(kind)
                            ? (a.compareTo(b) <= 0 ? a : b) : (a.compareTo(b) >= 0 ? a : b));
                }
            }
        }
        List<Long> order = new ArrayList<>(totals.keySet());
        order.sort(Comparator.<Long>comparingInt(id -> totals.get(id)[0]).reversed()
                .thenComparing(Comparator.<Long>comparingInt(id -> totals.get(id)[2]).reversed())
                .thenComparing(Comparator.<Long>comparingInt(id -> totals.get(id)[3]).reversed())
                .thenComparing(id -> names.getOrDefault(id, "").toLowerCase(Locale.ROOT)));
        List<Row> rows = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < order.size(); i++) {
            int[] t = totals.get(order.get(i));
            if (i == 0) position = 1;
            else {
                int[] prev = totals.get(order.get(i - 1));
                if (prev[0] != t[0] || prev[2] != t[2] || prev[3] != t[3]) position = i + 1;
            }
            rows.add(new Row(position, order.get(i), names.getOrDefault(order.get(i), "?"), t[0], t[1], t[2], t[3],
                    best.get(order.get(i))));
        }
        return rows;
    }

    static List<Integer> parsePoints(String raw) {
        if (raw == null || raw.isBlank()) throw bad("Indica os pontos por lugar, por exemplo 25,18,15");
        String[] parts = raw.split(",");
        if (parts.length > 50) throw bad("No máximo 50 lugares pontuáveis");
        List<Integer> points = new ArrayList<>();
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part.trim());
                if (value < 0 || value > 1000) throw bad("Pontos entre 0 e 1000");
                if (!points.isEmpty() && value > points.getLast()) throw bad("Os pontos não podem subir com o lugar");
                points.add(value);
            } catch (NumberFormatException invalid) {
                throw bad("Pontos inválidos: usa números separados por vírgulas");
            }
        }
        return List.copyOf(points);
    }

    private static String serialise(List<Integer> points) {
        return points.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    // ---- access ------------------------------------------------------------------------------

    private record Access(CommunityTournament tournament, boolean manager) {}

    private Access access(Long communityId, Long tournamentId, String username, boolean write) {
        Profile actor = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta não encontrada"));
        var membership = members.findByCommunityIdAndProfileId(communityId, actor.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Não pertences a esta comunidade"));
        boolean manager = membership.getRole() == CommunityRole.OWNER || membership.getRole() == CommunityRole.ADMIN;
        if (write && !manager) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só Owner/Admin pode registar provas");
        CommunityTournament tournament = tournaments.findById(tournamentId)
                .filter(t -> t.getCommunity().getId().equals(communityId))
                .filter(t -> t.getScoringMode() == TournamentScoringMode.EVENT_RANKING)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneio por provas não encontrado"));
        return new Access(tournament, manager);
    }

    private List<CommunityTournamentParticipant> confirmed(Long tournamentId) {
        return participants.findByTournamentIdAndStatus(tournamentId, TournamentParticipantStatus.CONFIRMED,
                PageRequest.of(0, MAX_PARTICIPANTS, Sort.by("id"))).getContent();
    }

    private static String kind(CommunityTournament tournament) {
        String kind = tournament.getRankingValueKind();
        return kind == null ? POSITION : kind;
    }

    private static void requireEditable(CommunityTournament tournament) {
        if (tournament.getStatus() == TournamentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Torneio já concluído");
        }
    }

    private static String display(Profile profile) {
        if (profile == null) return "?";
        String nickname = profile.getNickname(), full = profile.getFullName();
        return nickname != null && !nickname.isBlank() ? nickname
                : full != null && !full.isBlank() ? full : profile.getUsername();
    }

    /** Public pages: nickname or handle, never the full real name. */
    private static String publicDisplay(Profile profile) {
        if (profile == null) return "?";
        String nickname = profile.getNickname();
        return nickname != null && !nickname.isBlank() ? nickname : profile.getUsername();
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    static boolean isIndividual(TournamentEntryType type) { return type == TournamentEntryType.INDIVIDUAL; }
}
