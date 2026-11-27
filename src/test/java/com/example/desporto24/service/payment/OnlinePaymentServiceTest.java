package com.example.desporto24.service.payment;

import com.example.desporto24.model.payment.SessionOnlinePayment;
import com.example.desporto24.model.session.SessionParticipantPayment;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.payment.SessionOnlinePaymentRepository;
import com.example.desporto24.repository.payment.StripeConnectedAccountRepository;
import com.example.desporto24.repository.session.SessionParticipantPaymentRepository;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.session.SessionPaymentSetupRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OnlinePaymentServiceTest {
    private final StripeClient stripe = mock(StripeClient.class);
    private final StripeConnectedAccountRepository accounts = mock(StripeConnectedAccountRepository.class);
    private final SessionOnlinePaymentRepository online = mock(SessionOnlinePaymentRepository.class);
    private final SessionParticipantPaymentRepository participantPayments = mock(SessionParticipantPaymentRepository.class);
    private final OnlinePaymentService service = new OnlinePaymentService(stripe, accounts, online,
            mock(SessionRepository.class), mock(SessionParticipantRepository.class), participantPayments,
            mock(SessionPaymentSetupRepository.class), mock(ProfileRepository.class),
            mock(VenueOnlinePaymentService.class), "https://jynlosports.com/");

    private SessionOnlinePayment pending() {
        SessionOnlinePayment row = new SessionOnlinePayment();
        row.setCheckoutSessionId("cs_1");
        row.setParticipantId(42L);
        row.setStripeAccountId("acct_org");
        row.setStatus(SessionOnlinePayment.Status.PENDING);
        return row;
    }

    @Test
    void aPaidCheckoutMarksThePlaceClaimedAndConfirmed() {
        when(online.findById("cs_1")).thenReturn(Optional.of(pending()));
        when(participantPayments.findById(42L)).thenReturn(Optional.empty());
        service.markPaid("cs_1", "acct_org");
        ArgumentCaptor<SessionParticipantPayment> saved = ArgumentCaptor.forClass(SessionParticipantPayment.class);
        verify(participantPayments).save(saved.capture());
        assertNotNull(saved.getValue().getClaimedAt());
        assertNotNull(saved.getValue().getConfirmedAt());
    }

    @Test
    void ignoresEventsFromAnotherAccountOrAlreadyPaid() {
        when(online.findById("cs_1")).thenReturn(Optional.of(pending()));
        service.markPaid("cs_1", "acct_someone_else");
        SessionOnlinePayment paid = pending();
        paid.setStatus(SessionOnlinePayment.Status.PAID);
        when(online.findById("cs_2")).thenReturn(Optional.of(paid));
        service.markPaid("cs_2", "acct_org");
        service.markPaid("cs_unknown", "acct_org");
        verify(participantPayments, never()).save(any());
    }

    @Test
    void statusIsUnavailableWithoutStripeKeys() {
        when(stripe.enabled()).thenReturn(false);
        assertFalse(service.status("ana").available());
        assertFalse(service.organiserCanReceive("ana"));
    }

    @Test
    void returnPathsStayOnTheSite() {
        assertEquals("/menu/events/5", OnlinePaymentService.safePath("/menu/events/5"));
        assertEquals("/menu/feed", OnlinePaymentService.safePath("https://evil.example"));
        assertEquals("/menu/feed", OnlinePaymentService.safePath("//evil.example"));
        assertEquals("/menu/feed", OnlinePaymentService.safePath(null));
    }

    @Test
    void pricesBecomeCents() {
        assertEquals(350, OnlinePaymentService.cents(new BigDecimal("3.5")));
        assertEquals(0, OnlinePaymentService.cents(null));
    }
}
