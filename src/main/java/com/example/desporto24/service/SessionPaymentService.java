package com.example.desporto24.service;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.model.session.SessionParticipantPayment;
import com.example.desporto24.model.session.SessionPaymentSetup;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.session.SessionParticipantPaymentRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.session.SessionPaymentSetupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * "Quem já pagou?" for paid sessions. Players pay the organiser directly by
 * MB WAY (outside Jynlo); Jynlo only shows where to pay and keeps the list.
 * - the organiser's MB WAY number is visible only to the organiser and active players;
 * - a player can say "já paguei"; only the organiser confirms the money arrived;
 * - only the organiser sees everyone's status; players see their own and the totals.
 */
@Service
@RequiredArgsConstructor
public class SessionPaymentService {

    public record Mine(Instant claimedAt, Instant confirmedAt) {}
    public record Player(String username, String profilePicUrl, Instant claimedAt, Instant confirmedAt) {}
    public record Payments(BigDecimal priceAmount, boolean organiser, String method, String mbwayPhone,
                           Mine me, int confirmedCount, int playerCount, List<Player> players,
                           Instant lastReminderAt, Payee payee) {
        public Payments(BigDecimal priceAmount, boolean organiser, String method, String mbwayPhone,
                        Mine me, int confirmedCount, int playerCount, List<Player> players,
                        Instant lastReminderAt) {
            this(priceAmount, organiser, method, mbwayPhone, me, confirmedCount, playerCount,
                    players, lastReminderAt, null);
        }
    }
    /** Who receives the money, so players know who the MB WAY number belongs to. */
    public record Payee(String username, String fullName, String profilePicUrl) {}

    /** Who still owes the previous game, shown before the next list opens. */
    public record PreviousUnpaid(Long sessionId, Instant gameDate, String sport, List<String> unpaid) {}

    public static final String MBWAY = "MBWAY";
    public static final String IN_PERSON = "IN_PERSON";
    /** Paid inside Jynlo (MB WAY or card, through the organiser's Stripe account): marked paid on its own. */
    public static final String ONLINE = "ONLINE";
    static final java.time.Duration REMINDER_GAP = java.time.Duration.ofHours(12);

    private final SessionRepository sessions;
    private final ProfileRepository profiles;
    private final SessionParticipantRepository participants;
    private final SessionPaymentSetupRepository setups;
    private final SessionParticipantPaymentRepository payments;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.desporto24.repository.NotificationsRepository notifications;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private NotificationPreferencesService preferences;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private org.springframework.context.ApplicationEventPublisher pushEvents;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    @org.springframework.context.annotation.Lazy
    private com.example.desporto24.service.payment.OnlinePaymentService online;

    @Transactional(readOnly = true)
    public Payments get(Long sessionId, String username) {
        Access access = access(sessionId, username);
        return view(access);
    }

    /**
     * Organiser only: how players pay. MBWAY needs the organiser's number;
     * IN_PERSON means on the day (cash or MB WAY there). Null method clears it.
     */
    @Transactional
    public Payments setMethod(Long sessionId, String username, String method, String phone) {
        Access access = access(sessionId, username);
        if (!access.organiser()) throw forbidden("Só o organizador pode escolher como se paga");
        if (method == null || method.isBlank()) {
            if (setups.existsById(sessionId)) setups.deleteById(sessionId);
            return view(access);
        }
        if (!MBWAY.equals(method) && !IN_PERSON.equals(method) && !ONLINE.equals(method)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Forma de pagamento inválida");
        }
        if (ONLINE.equals(method) && (online == null || !online.organiserCanReceive(username))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ativa primeiro os pagamentos na app");
        }
        SessionPaymentSetup setup = setups.findById(sessionId).orElseGet(() -> {
            SessionPaymentSetup fresh = new SessionPaymentSetup();
            fresh.setSessionId(sessionId);
            return fresh;
        });
        setup.setMethod(method);
        setup.setMbwayPhone(MBWAY.equals(method) ? normalisePhone(phone == null ? "" : phone) : null);
        setup.setUpdatedAt(Instant.now());
        setups.save(setup);
        return view(access);
    }

    /**
     * Organiser only: in-app (and push) reminder to every player not yet confirmed.
     * At most once every 12 hours per session, so it never turns into spam.
     */
    @Transactional
    public Payments remindUnpaid(Long sessionId, String username) {
        Access access = access(sessionId, username);
        if (!access.organiser()) throw forbidden("Só o organizador pode lembrar os pagamentos");
        SessionPaymentSetup setup = setups.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Escolhe primeiro como se paga"));
        Instant now = Instant.now();
        if (setup.getLastReminderAt() != null && setup.getLastReminderAt().plus(REMINDER_GAP).isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Já enviaste um lembrete há menos de 12 horas");
        }
        List<SessionParticipant> players = active(sessionId).stream()
                .filter(p -> p.getProfile() != null && !isOrganiser(access.session(), p.getProfile()))
                .toList();
        Map<Long, SessionParticipantPayment> rows = payments
                .findAllById(players.stream().map(SessionParticipant::getId).toList())
                .stream().collect(Collectors.toMap(SessionParticipantPayment::getParticipantId, Function.identity()));
        String amount = amountLabel(access.session().getPriceAmount());
        String how = ONLINE.equals(setup.getMethod()) ? " na app, por MB WAY ou cartão"
                : MBWAY.equals(setup.getMethod()) && setup.getMbwayPhone() != null
                ? " por MB WAY para " + setup.getMbwayPhone()
                : " ao organizador";
        for (SessionParticipant player : players) {
            SessionParticipantPayment row = rows.get(player.getId());
            if (row != null && row.getConfirmedAt() != null) continue;
            notify(player.getProfile(), access.session(), "Falta pagar o jogo de " + sport(access.session()),
                    "Paga " + amount + how + (ONLINE.equals(setup.getMethod())
                            ? ". Fica marcado como pago automaticamente."
                            : ". Quando pagares, marca \"Já paguei\" na sessão."));
        }
        setup.setLastReminderAt(now);
        setups.save(setup);
        return view(access);
    }

    /**
     * After the game: one notification to every player who has not paid yet, pointing to the
     * organiser's MB WAY number. Sent once per game (postGameReminderAt).
     */
    @Transactional
    public int sendPostGameReminder(Long sessionId, Instant now) {
        SessionPaymentSetup setup = setups.findById(sessionId).orElse(null);
        Session session = sessions.findById(sessionId).orElse(null);
        if (setup == null || session == null || setup.getPostGameReminderAt() != null) return 0;
        setup.setPostGameReminderAt(now);
        setups.save(setup);
        if (!MBWAY.equals(setup.getMethod()) || setup.getMbwayPhone() == null) return 0;
        List<SessionParticipant> players = active(sessionId).stream()
                .filter(p -> p.getProfile() != null && !isOrganiser(session, p.getProfile()))
                .toList();
        Map<Long, SessionParticipantPayment> rows = payments
                .findAllById(players.stream().map(SessionParticipant::getId).toList())
                .stream().collect(Collectors.toMap(SessionParticipantPayment::getParticipantId, Function.identity()));
        int sent = 0;
        for (SessionParticipant player : players) {
            SessionParticipantPayment row = rows.get(player.getId());
            if (row != null && (row.getClaimedAt() != null || row.getConfirmedAt() != null)) continue;
            notify(player.getProfile(), session, "Falta pagar o jogo de " + sport(session),
                    "Toca aqui para pagar " + amountLabel(session.getPriceAmount()) + " por MB WAY ao organizador.");
            sent++;
        }
        return sent;
    }

    /** Player: "já paguei" (true) or undo (false). Never confirms the payment. */
    @Transactional
    public Payments claim(Long sessionId, String username, boolean paid) {
        Access access = access(sessionId, username);
        if (access.mine() == null) throw forbidden("Só os jogadores inscritos podem indicar o pagamento");
        SessionParticipantPayment row = payments.findById(access.mine().getId()).orElseGet(() -> {
            SessionParticipantPayment fresh = new SessionParticipantPayment();
            fresh.setParticipantId(access.mine().getId());
            return fresh;
        });
        boolean newClaim = paid && row.getClaimedAt() == null && row.getConfirmedAt() == null;
        row.setClaimedAt(paid ? (row.getClaimedAt() == null ? Instant.now() : row.getClaimedAt()) : null);
        save(row);
        // Tell the organiser right away, so confirming is one tap from the notification.
        if (newClaim && access.session().getProfile() != null) {
            notify(access.session().getProfile(), access.session(),
                    "@" + access.actor().getUsername() + " diz que pagou",
                    "@" + access.actor().getUsername() + " diz que já pagou " + amountLabel(access.session().getPriceAmount())
                            + " do jogo de " + sport(access.session()) + ". Confirma quando vires o dinheiro.");
        }
        return view(access);
    }

    /** Organiser: the money arrived (true) or not (false) for one player. */
    @Transactional
    public Payments confirm(Long sessionId, String username, String playerUsername, boolean paid) {
        Access access = access(sessionId, username);
        if (!access.organiser()) throw forbidden("Só o organizador pode confirmar pagamentos");
        SessionParticipant player = active(sessionId).stream()
                .filter(p -> p.getProfile() != null && p.getProfile().getUsername() != null
                        && p.getProfile().getUsername().equalsIgnoreCase(playerUsername))
                .filter(p -> !isOrganiser(access.session(), p.getProfile()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não inscrito nesta sessão"));
        SessionParticipantPayment row = payments.findById(player.getId()).orElseGet(() -> {
            SessionParticipantPayment fresh = new SessionParticipantPayment();
            fresh.setParticipantId(player.getId());
            return fresh;
        });
        row.setConfirmedAt(paid ? (row.getConfirmedAt() == null ? Instant.now() : row.getConfirmedAt()) : null);
        save(row);
        return view(access);
    }

    /**
     * Organiser only. The previous game is the one before this in the same
     * recurring series or, without a series, the organiser's previous game in
     * the same community. Only paid games count; empty when nothing is owed.
     */
    @Transactional(readOnly = true)
    public PreviousUnpaid previousUnpaid(Long sessionId, String username) {
        Access access = access(sessionId, username);
        if (!access.organiser()) throw forbidden("Só o organizador vê os pagamentos em falta");
        Session current = access.session();
        Session previous = previousGame(current);
        if (previous == null || previous.getPriceAmount() == null
                || previous.getPriceAmount().signum() <= 0) {
            return new PreviousUnpaid(null, null, null, List.of());
        }
        List<SessionParticipant> players = active(previous.getId()).stream()
                .filter(p -> p.getProfile() != null && !isOrganiser(previous, p.getProfile()))
                .toList();
        Map<Long, SessionParticipantPayment> rows = payments
                .findAllById(players.stream().map(SessionParticipant::getId).toList())
                .stream().collect(Collectors.toMap(SessionParticipantPayment::getParticipantId, Function.identity()));
        List<String> unpaid = players.stream()
                .filter(p -> rows.get(p.getId()) == null || rows.get(p.getId()).getConfirmedAt() == null)
                .map(p -> p.getProfile().getUsername())
                .toList();
        return new PreviousUnpaid(previous.getId(),
                previous.getGameDate() == null ? null : previous.getGameDate().toInstant(),
                previous.getSport(), unpaid);
    }

    private Session previousGame(Session current) {
        if (current.getGameDate() == null) return null;
        String series = current.getRecurrenceSeriesId();
        if (series != null && !series.isBlank()) {
            Session previous = null;
            for (Session game : sessions.findByRecurrenceSeriesIdOrderByGameDateAsc(series)) {
                if (game.getGameDate() != null && game.getGameDate().before(current.getGameDate())) previous = game;
            }
            if (previous != null) return previous;
        }
        if (current.getCommunity() == null || current.getProfile() == null) return null;
        return sessions.findFirstByCommunity_IdAndProfile_IdAndGameDateBeforeOrderByGameDateDesc(
                current.getCommunity().getId(), current.getProfile().getId(), current.getGameDate()).orElse(null);
    }

    /** Portuguese mobile numbers only: 9 digits starting with 91, 92, 93 or 96 (with or without +351). */
    public static String normalisePhone(String raw) {
        String digits = raw.replaceAll("[\\s.-]", "");
        if (digits.startsWith("+351")) digits = digits.substring(4);
        else if (digits.startsWith("00351")) digits = digits.substring(5);
        if (!digits.matches("9[1236][0-9]{7}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Indica um número de telemóvel português (9 dígitos, começa por 91, 92, 93 ou 96)");
        }
        return digits;
    }

    private record Access(Session session, Profile actor, boolean organiser, SessionParticipant mine) {}

    private Access access(Long sessionId, String username) {
        Session session = sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada"));
        Profile actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> forbidden("Conta não encontrada"));
        boolean organiser = isOrganiser(session, actor);
        SessionParticipant mine = organiser ? null : participants
                .findBySessionIdAndProfileIdAndState(sessionId, actor.getId(), ParticipantState.ACTIVE)
                .orElse(null);
        if (!organiser && mine == null) throw forbidden("Só quem está inscrito vê os pagamentos desta sessão");
        return new Access(session, actor, organiser, mine);
    }

    private Payments view(Access access) {
        Long sessionId = access.session().getId();
        List<SessionParticipant> players = active(sessionId).stream()
                .filter(p -> p.getProfile() != null && !isOrganiser(access.session(), p.getProfile()))
                .toList();
        Map<Long, SessionParticipantPayment> rows = payments
                .findAllById(players.stream().map(SessionParticipant::getId).toList())
                .stream().collect(Collectors.toMap(SessionParticipantPayment::getParticipantId, Function.identity()));
        int confirmed = (int) players.stream()
                .filter(p -> rows.containsKey(p.getId()) && rows.get(p.getId()).getConfirmedAt() != null).count();
        var setup = setups.findById(sessionId).orElse(null);
        String phone = setup == null ? null : setup.getMbwayPhone();
        Mine mine = null;
        if (access.mine() != null) {
            SessionParticipantPayment row = rows.get(access.mine().getId());
            mine = new Mine(row == null ? null : row.getClaimedAt(), row == null ? null : row.getConfirmedAt());
        }
        List<Player> list = !access.organiser() ? List.of() : players.stream().map(p -> {
            SessionParticipantPayment row = rows.get(p.getId());
            return new Player(p.getProfile().getUsername(), p.getProfile().getProfilePicUrl(),
                    row == null ? null : row.getClaimedAt(), row == null ? null : row.getConfirmedAt());
        }).toList();
        return new Payments(access.session().getPriceAmount(), access.organiser(),
                setup == null ? null : setup.getMethod(), phone, mine, confirmed, players.size(), list,
                access.organiser() && setup != null ? setup.getLastReminderAt() : null,
                payee(access.session()));
    }

    private void notify(Profile recipient, Session session, String subject, String message) {
        if (notifications == null || recipient == null || recipient.getUsername() == null) return;
        var notification = new com.example.desporto24.register.Notifications.Notifications();
        notification.setUsername(recipient.getUsername());
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setGreeting("");
        notification.setSignature("");
        notification.setCreatedAt(Instant.now().toString());
        notification.setFriendRequest(false);
        notification.setUpdateProfile(false);
        notification.setSessionInvite(false);
        notification.setNormalMessage(false);
        notification.setToken(null);
        notification.setType(com.example.desporto24.enumeration.NotificationType.PAYMENT_REMINDER);
        notification.setSessionId(session.getId());
        notification.setRead(false);
        if (preferences != null && !preferences.allowsInApp(recipient.getUsername(), notification)) return;
        var saved = notifications.save(notification);
        if (pushEvents != null && saved != null && saved.getId() != null) {
            pushEvents.publishEvent(new MobilePushDeliveryService.StoredNotification(
                    saved.getUsername(), saved.getId(), NotificationPreferencesService.category(saved),
                    saved.getSessionId(), null, null, true));
        }
    }

    private static String amountLabel(BigDecimal amount) {
        if (amount == null) return "a tua parte";
        return amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString().replace('.', ',') + " €";
    }

    private static String sport(Session session) {
        String sport = session.getSport();
        return sport == null || sport.isBlank() ? "jogo" : sport.strip();
    }

    private List<SessionParticipant> active(Long sessionId) {
        return participants.findBySessionIdAndStateOrderByJoinedAtAsc(sessionId, ParticipantState.ACTIVE);
    }

    private void save(SessionParticipantPayment row) {
        if (row.getClaimedAt() == null && row.getConfirmedAt() == null) {
            if (payments.existsById(row.getParticipantId())) payments.deleteById(row.getParticipantId());
        } else {
            payments.save(row);
        }
    }

    private static boolean isOrganiser(Session session, Profile profile) {
        return session.getProfile() != null && profile != null
                && session.getProfile().getId() != null && session.getProfile().getId().equals(profile.getId());
    }

    private static ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    private static Payee payee(Session session) {
        Profile organiser = session.getProfile();
        if (organiser == null) return null;
        // A private profile keeps its name and photo private: players see the username only.
        if (organiser.isPrivateProfile()) return new Payee(organiser.getUsername(), null, null);
        String name = organiser.getFullName() != null && !organiser.getFullName().isBlank()
                ? organiser.getFullName() : organiser.getNickname();
        return new Payee(organiser.getUsername(), name, organiser.getProfilePicUrl());
    }
}
