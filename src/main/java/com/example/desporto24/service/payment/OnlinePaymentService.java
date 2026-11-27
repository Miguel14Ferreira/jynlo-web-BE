package com.example.desporto24.service.payment;

import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.payment.SessionOnlinePayment;
import com.example.desporto24.model.payment.StripeConnectedAccount;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.model.session.SessionParticipantPayment;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.payment.SessionOnlinePaymentRepository;
import com.example.desporto24.repository.payment.StripeConnectedAccountRepository;
import com.example.desporto24.repository.session.SessionParticipantPaymentRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.session.SessionPaymentSetupRepository;
import com.example.desporto24.service.SessionPaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Paying a game inside Jynlo (MB WAY or card) so it is marked paid on its own, with no "Já paguei" and no
 * organiser confirmation. The money goes straight to the organiser's own Stripe account.
 */
@Service
public class OnlinePaymentService {
    public record Status(boolean available, boolean connected, boolean chargesEnabled) {}
    public record Link(String url) {}

    private final StripeClient stripe;
    private final StripeConnectedAccountRepository accounts;
    private final SessionOnlinePaymentRepository onlinePayments;
    private final SessionRepository sessions;
    private final SessionParticipantRepository participants;
    private final SessionParticipantPaymentRepository participantPayments;
    private final SessionPaymentSetupRepository setups;
    private final ProfileRepository profiles;
    private final VenueOnlinePaymentService venuePayments;
    private final String siteUrl;

    public OnlinePaymentService(StripeClient stripe, StripeConnectedAccountRepository accounts,
                                SessionOnlinePaymentRepository onlinePayments, SessionRepository sessions,
                                SessionParticipantRepository participants,
                                SessionParticipantPaymentRepository participantPayments,
                                SessionPaymentSetupRepository setups, ProfileRepository profiles,
                                VenueOnlinePaymentService venuePayments,
                                @Value("${desporto.public-site-url:https://jynlosports.com}") String siteUrl) {
        this.stripe = stripe;
        this.accounts = accounts;
        this.onlinePayments = onlinePayments;
        this.sessions = sessions;
        this.participants = participants;
        this.participantPayments = participantPayments;
        this.setups = setups;
        this.profiles = profiles;
        this.venuePayments = venuePayments;
        this.siteUrl = siteUrl.replaceAll("/+$", "");
    }

    @Transactional
    public Status status(String username) {
        if (!stripe.enabled()) return new Status(false, false, false);
        StripeConnectedAccount account = accounts.findByUsernameIgnoreCase(username).orElse(null);
        if (account == null) return new Status(true, false, false);
        if (!account.isChargesEnabled()) refresh(account);
        return new Status(true, true, account.isChargesEnabled());
    }

    /** Stripe's own sign-up page for the organiser's account (identity and IBAN are given to Stripe, not Jynlo). */
    @Transactional
    public Link onboarding(String username, String returnPath) {
        requireEnabled();
        Profile profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta não encontrada"));
        StripeConnectedAccount account = accounts.findByUsernameIgnoreCase(username).orElseGet(() -> {
            StripeConnectedAccount fresh = new StripeConnectedAccount();
            fresh.setUsername(profile.getUsername());
            fresh.setStripeAccountId(stripe.createAccount(profile.getEmail()));
            fresh.setChargesEnabled(false);
            fresh.setUpdatedAt(Instant.now());
            return accounts.save(fresh);
        });
        String back = siteUrl + safePath(returnPath);
        return new Link(stripe.onboardingLink(account.getStripeAccountId(), back, back));
    }

    /** Player: opens Stripe's payment page (MB WAY or card) for their place in the game. */
    @Transactional
    public Link checkout(Long sessionId, String username) {
        requireEnabled();
        Session session = sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada"));
        var setup = setups.findById(sessionId).orElse(null);
        if (setup == null || !SessionPaymentService.ONLINE.equals(setup.getMethod())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este jogo não recebe pagamentos na app.");
        }
        int cents = cents(session.getPriceAmount());
        if (cents < 50) throw new ResponseStatusException(HttpStatus.CONFLICT, "Este jogo não tem preço definido.");
        Profile actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta não encontrada"));
        SessionParticipant mine = participants
                .findBySessionIdAndProfileIdAndState(sessionId, actor.getId(), ParticipantState.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Só os jogadores inscritos podem pagar."));
        if (participantPayments.findById(mine.getId()).map(SessionParticipantPayment::getConfirmedAt).orElse(null) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já pagaste este jogo.");
        }
        Profile organiser = session.getProfile();
        StripeConnectedAccount account = organiser == null ? null
                : accounts.findByUsernameIgnoreCase(organiser.getUsername()).orElse(null);
        if (account == null || !account.isChargesEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O organizador ainda não ativou os pagamentos na app.");
        }
        String back = siteUrl + "/menu/events/" + sessionId;
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("jynlo_session_id", String.valueOf(sessionId));
        metadata.put("jynlo_participant_id", String.valueOf(mine.getId()));
        StripeClient.CheckoutLink link = stripe.checkout(account.getStripeAccountId(), cents,
                "Jogo de " + sport(session) + " na Jynlo", back + "?pagamento=ok", back + "?pagamento=cancelado", metadata);
        SessionOnlinePayment row = new SessionOnlinePayment();
        row.setCheckoutSessionId(link.id());
        row.setSessionId(sessionId);
        row.setParticipantId(mine.getId());
        row.setStripeAccountId(account.getStripeAccountId());
        row.setAmountCents(cents);
        row.setStatus(SessionOnlinePayment.Status.PENDING);
        row.setCreatedAt(Instant.now());
        onlinePayments.save(row);
        return new Link(link.url());
    }

    /** Stripe → Jynlo. Marks the place paid as soon as Stripe says the money arrived. Idempotent. */
    @Transactional
    public void handleWebhook(String payload, String signature) {
        JsonNode event = stripe.verifiedEvent(payload, signature);
        String type = event.path("type").asText();
        JsonNode object = event.path("data").path("object");
        switch (type) {
            case "checkout.session.completed", "checkout.session.async_payment_succeeded" -> {
                if (!"paid".equals(object.path("payment_status").asText())) return;
                String checkoutId = object.path("id").asText();
                String account = event.path("account").asText(null);
                if (!venuePayments.markPaid(checkoutId, account)) markPaid(checkoutId, account);
            }
            case "account.updated" -> {
                boolean enabled = object.path("charges_enabled").asBoolean(false);
                accounts.findByStripeAccountId(object.path("id").asText()).ifPresent(a -> {
                    a.setChargesEnabled(enabled);
                    a.setUpdatedAt(Instant.now());
                    accounts.save(a);
                });
                venuePayments.accountUpdated(object.path("id").asText(), enabled);
            }
            default -> { /* not used */ }
        }
    }

    void markPaid(String checkoutSessionId, String stripeAccount) {
        SessionOnlinePayment row = onlinePayments.findById(checkoutSessionId).orElse(null);
        if (row == null || row.getStatus() == SessionOnlinePayment.Status.PAID) return;
        // The event must come from the same organiser account the payment was created on.
        if (stripeAccount != null && !stripeAccount.equals(row.getStripeAccountId())) return;
        Instant now = Instant.now();
        row.setStatus(SessionOnlinePayment.Status.PAID);
        row.setPaidAt(now);
        onlinePayments.save(row);
        SessionParticipantPayment payment = participantPayments.findById(row.getParticipantId()).orElseGet(() -> {
            SessionParticipantPayment fresh = new SessionParticipantPayment();
            fresh.setParticipantId(row.getParticipantId());
            return fresh;
        });
        if (payment.getClaimedAt() == null) payment.setClaimedAt(now);
        if (payment.getConfirmedAt() == null) payment.setConfirmedAt(now);
        participantPayments.save(payment);
    }

    public boolean organiserCanReceive(String username) {
        if (!stripe.enabled() || username == null) return false;
        return accounts.findByUsernameIgnoreCase(username).map(StripeConnectedAccount::isChargesEnabled).orElse(false);
    }

    private void refresh(StripeConnectedAccount account) {
        try {
            boolean enabled = stripe.chargesEnabled(account.getStripeAccountId());
            if (enabled != account.isChargesEnabled()) {
                account.setChargesEnabled(enabled);
                account.setUpdatedAt(Instant.now());
                accounts.save(account);
            }
        } catch (ResponseStatusException ignored) {
            // Keep the last known state if Stripe cannot be reached.
        }
    }

    private void requireEnabled() {
        if (!stripe.enabled()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Os pagamentos na app ainda não estão ativos.");
    }

    static String safePath(String path) {
        if (path == null || !path.startsWith("/") || path.startsWith("//") || path.contains("\\")
                || path.length() > 200 || !path.matches("[A-Za-z0-9/_\\-?=&.]*")) return "/menu/feed";
        return path;
    }

    static int cents(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) return 0;
        return amount.setScale(2, RoundingMode.HALF_UP).movePointRight(2).intValueExact();
    }

    private static String sport(Session session) {
        String sport = session.getSport();
        return sport == null || sport.isBlank() ? "desporto" : sport.strip();
    }
}
