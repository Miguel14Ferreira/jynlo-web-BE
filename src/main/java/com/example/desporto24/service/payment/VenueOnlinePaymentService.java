package com.example.desporto24.service.payment;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.payment.VenueOnlinePayment;
import com.example.desporto24.model.payment.VenueStripeAccount;
import com.example.desporto24.model.venue.Venue;
import com.example.desporto24.model.venue.VenueBookingRequest;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.payment.VenueOnlinePaymentRepository;
import com.example.desporto24.repository.payment.VenueStripeAccountRepository;
import com.example.desporto24.repository.venue.VenueBookingRequestRepository;
import com.example.desporto24.repository.venue.VenuePaymentSetupRepository;
import com.example.desporto24.repository.venue.VenueRepository;
import com.example.desporto24.service.SessionPaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Optional automatic payment of court bookings. The venue connects its own Stripe account; the player pays in
 * Jynlo (MB WAY or card), the money goes straight to the venue, Stripe's fee is taken from it and Jynlo charges
 * nothing. Access checks (who manages the venue) are done by the caller.
 */
@Service
public class VenueOnlinePaymentService {
    public record Status(boolean available, boolean connected, boolean chargesEnabled) {}

    private final StripeClient stripe;
    private final VenueStripeAccountRepository accounts;
    private final VenueOnlinePaymentRepository payments;
    private final VenueBookingRequestRepository bookings;
    private final VenuePaymentSetupRepository setups;
    private final VenueRepository venues;
    private final ProfileRepository profiles;
    private final String siteUrl;

    public VenueOnlinePaymentService(StripeClient stripe, VenueStripeAccountRepository accounts,
                                     VenueOnlinePaymentRepository payments, VenueBookingRequestRepository bookings,
                                     VenuePaymentSetupRepository setups, VenueRepository venues, ProfileRepository profiles,
                                     @Value("${desporto.public-site-url:https://jynlosports.com}") String siteUrl) {
        this.stripe = stripe;
        this.accounts = accounts;
        this.payments = payments;
        this.bookings = bookings;
        this.setups = setups;
        this.venues = venues;
        this.profiles = profiles;
        this.siteUrl = siteUrl.replaceAll("/+$", "");
    }

    @Transactional
    public Status status(Long venueId) {
        if (!stripe.enabled()) return new Status(false, false, false);
        VenueStripeAccount account = accounts.findById(venueId).orElse(null);
        if (account == null) return new Status(true, false, false);
        if (!account.isChargesEnabled()) refresh(account);
        return new Status(true, true, account.isChargesEnabled());
    }

    /** Stripe's own sign-up page for the venue's account; the company's details and IBAN go to Stripe, not Jynlo. */
    @Transactional
    public OnlinePaymentService.Link onboarding(Long venueId, String username) {
        requireEnabled();
        Profile profile = profiles.findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta não encontrada"));
        VenueStripeAccount account = accounts.findById(venueId).orElseGet(() -> {
            VenueStripeAccount fresh = new VenueStripeAccount();
            fresh.setVenueId(venueId);
            fresh.setStripeAccountId(stripe.createAccount(profile.getEmail()));
            fresh.setChargesEnabled(false);
            fresh.setCreatedBy(profile.getUsername());
            fresh.setUpdatedAt(Instant.now());
            return accounts.save(fresh);
        });
        String back = siteUrl + "/menu/venues/" + venueId + "/manage";
        return new OnlinePaymentService.Link(stripe.onboardingLink(account.getStripeAccountId(), back, back));
    }

    public boolean venueCanReceive(Long venueId) {
        if (!stripe.enabled() || venueId == null) return false;
        return accounts.findById(venueId).map(VenueStripeAccount::isChargesEnabled).orElse(false);
    }

    /** Player: Stripe's payment page for a confirmed booking at a venue that takes payments in the app. */
    @Transactional
    public OnlinePaymentService.Link checkout(Long bookingId, String username) {
        requireEnabled();
        VenueBookingRequest booking = bookingId == null ? null : bookings.findById(bookingId).orElse(null);
        if (booking == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada");
        if (username == null || !booking.getRequesterUsername().equalsIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só quem fez a reserva a pode pagar.");
        }
        if (booking.getStatus() != VenueBookingRequest.Status.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Só reservas confirmadas se pagam.");
        }
        if (booking.getPaidConfirmedAt() != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta reserva já está paga.");
        var setup = setups.findById(booking.getVenueId()).orElse(null);
        if (setup == null || !SessionPaymentService.ONLINE.equals(setup.getMethod())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este recinto não recebe pagamentos na app.");
        }
        Integer cents = booking.getPriceCents();
        if (cents == null || cents < 50) throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta reserva não tem preço definido. Paga no recinto.");
        VenueStripeAccount account = accounts.findById(booking.getVenueId()).orElse(null);
        if (account == null || !account.isChargesEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O recinto ainda não ativou os pagamentos na app. Paga no recinto.");
        }
        String venueName = venues.findById(booking.getVenueId()).map(Venue::getName).orElse("recinto");
        String back = siteUrl + "/menu/venue-bookings";
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("jynlo_booking_id", String.valueOf(booking.getId()));
        StripeClient.CheckoutLink link = stripe.checkout(account.getStripeAccountId(), cents,
                "Reserva de campo em " + venueName, back + "?pagamento=ok", back + "?pagamento=cancelado", metadata);
        VenueOnlinePayment row = new VenueOnlinePayment();
        row.setCheckoutSessionId(link.id());
        row.setBookingId(booking.getId());
        row.setStripeAccountId(account.getStripeAccountId());
        row.setAmountCents(cents);
        row.setStatus(VenueOnlinePayment.Status.PENDING);
        row.setCreatedAt(Instant.now());
        payments.save(row);
        return new OnlinePaymentService.Link(link.url());
    }

    /** Webhook: true when the checkout belonged to a booking (paid or already handled). */
    boolean markPaid(String checkoutSessionId, String stripeAccount) {
        VenueOnlinePayment row = payments.findById(checkoutSessionId).orElse(null);
        if (row == null) return false;
        if (row.getStatus() == VenueOnlinePayment.Status.PAID) return true;
        if (stripeAccount != null && !stripeAccount.equals(row.getStripeAccountId())) return true;
        Instant now = Instant.now();
        row.setStatus(VenueOnlinePayment.Status.PAID);
        row.setPaidAt(now);
        payments.save(row);
        bookings.findById(row.getBookingId()).ifPresent(b -> {
            if (b.getPaidClaimedAt() == null) b.setPaidClaimedAt(now);
            if (b.getPaidConfirmedAt() == null) b.setPaidConfirmedAt(now);
            bookings.save(b);
        });
        return true;
    }

    void accountUpdated(String stripeAccountId, boolean chargesEnabled) {
        accounts.findByStripeAccountId(stripeAccountId).ifPresent(a -> {
            a.setChargesEnabled(chargesEnabled);
            a.setUpdatedAt(Instant.now());
            accounts.save(a);
        });
    }

    private void refresh(VenueStripeAccount account) {
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
}
