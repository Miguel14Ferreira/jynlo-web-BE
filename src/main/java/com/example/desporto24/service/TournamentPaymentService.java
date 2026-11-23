package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.CommunityTournament;
import com.example.desporto24.model.community.CommunityTournamentParticipant;
import com.example.desporto24.model.community.CommunityTournamentTeamPlayer;
import com.example.desporto24.model.community.TournamentEntryPayment;
import com.example.desporto24.model.community.TournamentPaymentSetup;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

/**
 * Tournament entry fees, the same way as game payments: the organiser sets the fee and
 * whether players pay by MB WAY to their number or in person; players mark "Já paguei"
 * and an organiser confirms it arrived. Jynlo never holds or moves the money.
 */
@Service
@RequiredArgsConstructor
public class TournamentPaymentService {
    public record Mine(Instant claimedAt, Instant confirmedAt) {}
    public record Player(String username, String fullName, Instant claimedAt, Instant confirmedAt) {}
    public record Payments(Integer entryFeeCents, String method, String mbwayPhone, boolean organiser,
                           boolean participant, Mine mine, int confirmedCount, int playerCount, List<Player> players) {}

    private final CommunityTournamentRepository tournaments;
    private final CommunityMemberRepository members;
    private final CommunityTournamentParticipantRepository participants;
    private final CommunityTournamentTeamPlayerRepository teamPlayers;
    private final TournamentPaymentSetupRepository setups;
    private final TournamentEntryPaymentRepository payments;
    private final ProfileRepository profiles;

    @Autowired(required = false)
    private NotificationsRepository notifications;
    @Autowired(required = false)
    private NotificationPreferencesService preferences;
    @Autowired(required = false)
    private ApplicationEventPublisher pushEvents;

    private record Access(CommunityTournament tournament, Profile actor, boolean organiser, boolean participant) {}

    @Transactional(readOnly = true)
    public Payments get(Long communityId, Long tournamentId, String username) {
        return view(access(communityId, tournamentId, username));
    }

    /** Organiser: entry fee (cents, null = free) and how to pay. A null method clears the setup. */
    @Transactional
    public Payments setup(Long communityId, Long tournamentId, String username, Integer entryFeeCents, String method, String phone) {
        Access access = access(communityId, tournamentId, username);
        if (!access.organiser()) throw forbidden("Só quem organiza o torneio pode definir a inscrição.");
        if (method == null || method.isBlank()) {
            if (setups.existsById(tournamentId)) setups.deleteById(tournamentId);
            return view(access);
        }
        if (!SessionPaymentService.MBWAY.equals(method) && !SessionPaymentService.IN_PERSON.equals(method)) {
            throw bad("Forma de pagamento inválida");
        }
        if (entryFeeCents != null && (entryFeeCents < 0 || entryFeeCents > 100_000)) {
            throw bad("Indica uma inscrição entre 0 e 1000 €.");
        }
        TournamentPaymentSetup setup = setups.findById(tournamentId).orElseGet(() -> {
            TournamentPaymentSetup fresh = new TournamentPaymentSetup();
            fresh.setTournamentId(tournamentId);
            return fresh;
        });
        setup.setEntryFeeCents(entryFeeCents);
        setup.setMethod(method);
        setup.setMbwayPhone(SessionPaymentService.MBWAY.equals(method)
                ? SessionPaymentService.normalisePhone(phone == null ? "" : phone) : null);
        setup.setUpdatedAt(Instant.now());
        setups.save(setup);
        return view(access);
    }

    /** Player in the tournament: "Já paguei" (or undo it while not confirmed). */
    @Transactional
    public Payments claim(Long communityId, Long tournamentId, String username, boolean paid) {
        Access access = access(communityId, tournamentId, username);
        if (!access.participant()) throw forbidden("Só quem está inscrito no torneio pode marcar a inscrição como paga.");
        if (setups.findById(tournamentId).isEmpty()) throw conflict("Este torneio não tem inscrição paga.");
        TournamentEntryPayment row = row(tournamentId, access.actor().getId());
        if (row.getConfirmedAt() != null && !paid) throw conflict("O organizador já confirmou o teu pagamento.");
        row.setClaimedAt(paid ? Instant.now() : null);
        payments.save(row);
        if (paid) notifyOrganisers(access, access.actor().getUsername() + " diz que já pagou a inscrição em \""
                + access.tournament().getName() + "\". Confirma quando receberes.");
        return view(access);
    }

    /** Organiser: confirm (or undo) a player's payment. */
    @Transactional
    public Payments confirm(Long communityId, Long tournamentId, String username, String playerUsername, boolean paid) {
        Access access = access(communityId, tournamentId, username);
        if (!access.organiser()) throw forbidden("Só quem organiza o torneio pode confirmar pagamentos.");
        Profile player = profiles.findUserByUsername(playerUsername == null ? "" : playerUsername)
                .orElseThrow(() -> bad("Jogador não encontrado."));
        if (!isParticipant(tournamentId, player.getId())) throw bad("Este jogador não está inscrito no torneio.");
        TournamentEntryPayment row = row(tournamentId, player.getId());
        row.setConfirmedAt(paid ? Instant.now() : null);
        payments.save(row);
        return view(access);
    }

    private Payments view(Access access) {
        Long tournamentId = access.tournament().getId();
        Optional<TournamentPaymentSetup> setup = setups.findById(tournamentId);
        Map<Long, TournamentEntryPayment> byProfile = new HashMap<>();
        for (TournamentEntryPayment p : payments.findByTournamentId(tournamentId)) byProfile.put(p.getProfileId(), p);
        LinkedHashMap<Long, Profile> players = playerProfiles(tournamentId);
        int confirmed = 0;
        List<Player> list = new ArrayList<>();
        for (Profile p : players.values()) {
            TournamentEntryPayment pay = byProfile.get(p.getId());
            if (pay != null && pay.getConfirmedAt() != null) confirmed++;
            if (access.organiser()) {
                list.add(new Player(p.getUsername(), p.getFullName(),
                        pay == null ? null : pay.getClaimedAt(), pay == null ? null : pay.getConfirmedAt()));
            }
        }
        TournamentEntryPayment minePay = byProfile.get(access.actor().getId());
        Mine mine = access.participant() ? new Mine(minePay == null ? null : minePay.getClaimedAt(),
                minePay == null ? null : minePay.getConfirmedAt()) : null;
        boolean seesPhone = access.organiser() || access.participant();
        return new Payments(setup.map(TournamentPaymentSetup::getEntryFeeCents).orElse(null),
                setup.map(TournamentPaymentSetup::getMethod).orElse(null),
                seesPhone ? setup.map(TournamentPaymentSetup::getMbwayPhone).orElse(null) : null,
                access.organiser(), access.participant(), mine, confirmed, players.size(), list);
    }

    /** Individual participants and team players, each person once. */
    private LinkedHashMap<Long, Profile> playerProfiles(Long tournamentId) {
        LinkedHashMap<Long, Profile> result = new LinkedHashMap<>();
        for (CommunityTournamentParticipant p : participants.findByTournamentIdOrderByIdAsc(tournamentId)) {
            if (p.getProfile() != null) result.putIfAbsent(p.getProfile().getId(), p.getProfile());
        }
        for (CommunityTournamentTeamPlayer p : teamPlayers.findByTournamentIdOrderByTeamIdAscIdAsc(tournamentId)) {
            if (p.getProfile() != null) result.putIfAbsent(p.getProfile().getId(), p.getProfile());
        }
        return result;
    }

    private boolean isParticipant(Long tournamentId, Long profileId) {
        return participants.existsByTournamentIdAndProfileId(tournamentId, profileId)
                || teamPlayers.existsByTournamentIdAndProfileId(tournamentId, profileId);
    }

    private TournamentEntryPayment row(Long tournamentId, Long profileId) {
        return payments.findByTournamentIdAndProfileId(tournamentId, profileId).orElseGet(() -> {
            TournamentEntryPayment fresh = new TournamentEntryPayment();
            fresh.setTournamentId(tournamentId);
            fresh.setProfileId(profileId);
            return fresh;
        });
    }

    private Access access(Long communityId, Long tournamentId, String username) {
        Profile actor = profiles.findUserByUsername(username == null ? "" : username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida"));
        CommunityTournament tournament = tournaments.findById(tournamentId)
                .filter(t -> t.getCommunity() != null && Objects.equals(t.getCommunity().getId(), communityId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneio não encontrado"));
        var membership = members.findByCommunityIdAndProfileId(communityId, actor.getId()).orElse(null);
        if (membership == null) throw forbidden("Não pertences a esta comunidade");
        boolean organiser = membership.getRole() == CommunityRole.OWNER || membership.getRole() == CommunityRole.ADMIN;
        return new Access(tournament, actor, organiser, isParticipant(tournamentId, actor.getId()));
    }

    private void notifyOrganisers(Access access, String message) {
        if (notifications == null) return;
        Long communityId = access.tournament().getCommunity().getId();
        for (var m : members.findByCommunityIdOrderByJoinedAtAsc(communityId)) {
            if (m.getRole() != CommunityRole.OWNER && m.getRole() != CommunityRole.ADMIN) continue;
            if (m.getProfile() == null || m.getProfile().getUsername() == null) continue;
            if (m.getProfile().getId().equals(access.actor().getId())) continue;
            Notifications n = new Notifications();
            n.setUsername(m.getProfile().getUsername());
            n.setSubject("Inscrição paga");
            n.setMessage(message);
            n.setGreeting("");
            n.setSignature("");
            n.setCreatedAt(Instant.now().toString());
            n.setNormalMessage(true);
            n.setType(NotificationType.TOURNAMENT_RESULT);
            n.setCommunityId(communityId);
            n.setCompetitionTournamentId(access.tournament().getId());
            n.setRead(false);
            if (preferences != null && !preferences.allowsInApp(n.getUsername(), n)) continue;
            Notifications saved = notifications.save(n);
            if (pushEvents != null && saved != null && saved.getId() != null) {
                pushEvents.publishEvent(new MobilePushDeliveryService.StoredNotification(
                        saved.getUsername(), saved.getId(), NotificationType.TOURNAMENT_RESULT.name(), null, null, null, true));
            }
        }
    }

    private static ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private static ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
