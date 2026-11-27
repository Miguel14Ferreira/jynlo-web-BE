package com.example.desporto24.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlowRequestLogFilterTest {
    @Test
    void masksIdsAndNamesInPaths() {
        assertEquals("/api/sessions/{n}/payments", SlowRequestLogFilter.mask("/api/sessions/123/payments"));
        assertEquals("/api/chat/me/{x}", SlowRequestLogFilter.mask("/api/chat/me/Joao.Silva"));
        assertEquals("/api/chat/me/{x}", SlowRequestLogFilter.mask("/api/chat/me/miguel"));
        assertEquals("/api/venues/{n}/booking-offer", SlowRequestLogFilter.mask("/api/venues/5/booking-offer"));
    }
}
