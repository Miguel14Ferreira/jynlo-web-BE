package com.example.desporto24.service;

import com.example.desporto24.model.MobilePushReceipt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.desporto24.repository.MobilePushDeviceRepository;
import com.example.desporto24.repository.MobilePushReceiptRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MobilePushReceiptPollerTest {
    private final MobilePushReceiptRepository receipts=mock(MobilePushReceiptRepository.class);
    private final MobilePushDeviceRepository devices=mock(MobilePushDeviceRepository.class);
    private final MobilePushReceiptPoller poller=new MobilePushReceiptPoller(receipts,devices);

    @Test
    void remainsDisabledWithoutExplicitConfiguration() {
        poller.checkDueReceipts();
        verifyNoInteractions(receipts,devices);
    }

    @Test
    void missingExpoAccessTokenNeverQueriesProviderOrDevices() {
        ReflectionTestUtils.setField(poller,"enabled",true);
        ReflectionTestUtils.setField(poller,"accessToken","");
        poller.checkDueReceipts();
        verifyNoInteractions(receipts,devices);
    }

    @Test
    void staleTicketsArePrunedWithoutSendingAnotherPush() {
        ReflectionTestUtils.setField(poller,"enabled",true);
        ReflectionTestUtils.setField(poller,"accessToken","test-only");
        var expired=new MobilePushReceipt();
        expired.setTicketId("12345678-1234-4234-8234-123456789012");
        expired.setDeviceId("12345678-1234-4234-8234-123456789012");
        expired.setTokenHash("f".repeat(64));
        expired.setCreatedAt(Instant.now().minus(26,ChronoUnit.HOURS));
        expired.setNextCheckAt(Instant.now().minus(25,ChronoUnit.HOURS));
        when(receipts.findTop100ByNextCheckAtBeforeOrderByNextCheckAtAsc(any()))
            .thenReturn(List.of(expired));
        poller.checkDueReceipts();
        verify(receipts).delete(expired);
        verify(receipts,never()).save(any());
        verifyNoInteractions(devices);
    }

    @Test
    void aFailedReceiptDoesNotRescheduleAlreadySettledReceipts() throws Exception {
        var now=Instant.now();
        var first=new MobilePushReceipt();
        first.setTicketId("ticket_first");first.setCreatedAt(now);
        var broken=new MobilePushReceipt();
        broken.setTicketId("ticket_broken");broken.setCreatedAt(now);
        var last=new MobilePushReceipt();
        last.setTicketId("ticket_last");last.setCreatedAt(now);
        doThrow(new IllegalStateException("database write failed")).when(receipts).delete(broken);
        var response=new ObjectMapper().readTree("""
                {"ticket_first":{"status":"ok"},"ticket_broken":{"status":"ok"},
                 "ticket_last":{"status":"ok"}}
                """);

        poller.processReceipts(List.of(first,broken,last),response,now);

        verify(receipts).delete(first);
        verify(receipts).delete(broken);
        verify(receipts).delete(last);
        verify(receipts).save(broken);
        verify(receipts,never()).save(first);
        verify(receipts,never()).save(last);
        verifyNoInteractions(devices);
    }

    @Test
    void anOldInvalidTokenReceiptCannotRemoveRotatedDevice() throws Exception {
        var now=Instant.now();
        var old=new MobilePushReceipt();
        old.setTicketId("old_token_ticket");
        old.setDeviceId("131df783-4b8f-42da-8f58-382a08a395a1");
        old.setTokenHash(MobilePushReceiptPoller.fingerprint("ExponentPushToken[old_token_123456789]"));
        old.setCreatedAt(now);
        var rotated=new com.example.desporto24.model.MobilePushDevice();
        rotated.setDeviceId(old.getDeviceId());
        rotated.setPushToken("ExponentPushToken[new_token_123456789]");
        when(devices.findById(old.getDeviceId())).thenReturn(java.util.Optional.of(rotated));
        var response=new ObjectMapper().readTree("""
                {"old_token_ticket":{"status":"error","details":{"error":"DeviceNotRegistered"}}}
                """);

        poller.processReceipts(List.of(old),response,now);

        verify(devices,never()).delete(any());
        verify(receipts).delete(old);
        verify(receipts,never()).save(any());
    }

    @Test
    void tokenFingerprintIsDeterministicWithoutStoringRawToken() {
        String hash=MobilePushReceiptPoller.fingerprint("ExponentPushToken[abcdefghijkl12345678]");
        assertEquals(64,hash.length());
        assertEquals(hash,MobilePushReceiptPoller.fingerprint("ExponentPushToken[abcdefghijkl12345678]"));
        assertNotEquals(hash,MobilePushReceiptPoller.fingerprint("ExponentPushToken[other_token_123456789]"));
        assertFalse(hash.contains("ExponentPushToken"));
    }
}
