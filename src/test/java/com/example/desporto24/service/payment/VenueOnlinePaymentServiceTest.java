package com.example.desporto24.service.payment;

import com.example.desporto24.model.payment.VenueOnlinePayment;
import com.example.desporto24.model.venue.VenueBookingRequest;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.payment.VenueOnlinePaymentRepository;
import com.example.desporto24.repository.payment.VenueStripeAccountRepository;
import com.example.desporto24.repository.venue.VenueBookingRequestRepository;
import com.example.desporto24.repository.venue.VenuePaymentSetupRepository;
import com.example.desporto24.repository.venue.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VenueOnlinePaymentServiceTest {
    private final StripeClient stripe = mock(StripeClient.class);
    private final VenueStripeAccountRepository accounts = mock(VenueStripeAccountRepository.class);
    private final VenueOnlinePaymentRepository payments = mock(VenueOnlinePaymentRepository.class);
    private final VenueBookingRequestRepository bookings = mock(VenueBookingRequestRepository.class);
    private final VenueOnlinePaymentService service = new VenueOnlinePaymentService(stripe, accounts, payments, bookings,
            mock(VenuePaymentSetupRepository.class), mock(VenueRepository.class), mock(ProfileRepository.class),
            "https://jynlosports.com");

    private VenueOnlinePayment pending() {
        VenueOnlinePayment row = new VenueOnlinePayment();
        row.setCheckoutSessionId("cs_v");
        row.setBookingId(9L);
        row.setStripeAccountId("acct_venue");
        row.setStatus(VenueOnlinePayment.Status.PENDING);
        return row;
    }

    @Test
    void aPaidCheckoutMarksTheBookingPaidWithoutTheManager() {
        when(payments.findById("cs_v")).thenReturn(Optional.of(pending()));
        VenueBookingRequest booking = new VenueBookingRequest();
        booking.setId(9L);
        when(bookings.findById(9L)).thenReturn(Optional.of(booking));
        assertTrue(service.markPaid("cs_v", "acct_venue"));
        assertNotNull(booking.getPaidClaimedAt());
        assertNotNull(booking.getPaidConfirmedAt());
    }

    @Test
    void unknownCheckoutsAreLeftToGamesAndOtherAccountsAreIgnored() {
        assertFalse(service.markPaid("cs_game", "acct_org"));
        when(payments.findById("cs_v")).thenReturn(Optional.of(pending()));
        assertTrue(service.markPaid("cs_v", "acct_someone_else"));
        verify(bookings, never()).save(any());
    }

    @Test
    void nothingWorksWithoutStripeKeys() {
        when(stripe.enabled()).thenReturn(false);
        assertFalse(service.status(5L).available());
        assertFalse(service.venueCanReceive(5L));
        assertThrows(ResponseStatusException.class, () -> service.checkout(9L, "ana"));
    }
}
