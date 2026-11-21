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
    public record Payments(BigDecimal priceAmount, boolean organiser, String mbwayPhone,
                           Mine me, int confirmedCount, int playerCount, List<Player> players) {}

    private final SessionRepository sessions;
    private final ProfileRepository profiles;
    private final SessionParticipantRepository participants;
    private final SessionPaymentSetupRepository setups;
    private final SessionParticipantPaymentRepository payments;

    @Transactional(readOnly = true)
    public Payments get(Long sessionId, String username) {
        Access access = access(sessionId, username);
        return view(access);
    }

    /** Organiser only. A blank number removes MB WAY from the session. */
    @Transactional
    public Payments setMbway(Long sessionId, String username, String phone) {
        Access access = access(sessionId, username);
        if (!access.organiser()) throw forbidden("Só o organizador pode indicar o número MB WAY");
        if (phone == null || phone.isBlank()) {
            setups.deleteById(sessionId);
        } else {
            SessionPaymentSetup setup = setups.findById(sessionId).orElseGet(() -> {
                SessionPaymentSetup fresh = new SessionPaymentSetup();
                fresh.setSessionId(sessionId);
                return fresh;
            });
            setup.setMbwayPhone(normalisePhone(phone));
            setup.setUpdatedAt(Instant.now());
            setups.save(setup);
        }
        return view(access);
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
        row.setClaimedAt(paid ? (row.getClaimedAt() == null ? Instant.now() : row.getClaimedAt()) : null);
        save(row);
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

    /** Portuguese mobile numbers only: 9 digits starting with 91, 92, 93 or 96 (with or without +351). */
    static String normalisePhone(String raw) {
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
        String phone = setups.findById(sessionId).map(SessionPaymentSetup::getMbwayPhone).orElse(null);
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
        return new Payments(access.session().getPriceAmount(), access.organiser(), phone, mine,
                confirmed, players.size(), list);
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
}
