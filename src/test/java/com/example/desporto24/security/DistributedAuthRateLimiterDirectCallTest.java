package com.example.desporto24.security;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DistributedAuthRateLimiterDirectCallTest {
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final DistributedAuthRateLimiter limiter =
            new DistributedAuthRateLimiter(jdbc, "test-secret-not-for-production");

    @Test
    void reservesGlobalAndPeerBudgetsInOrder() {
        when(jdbc.queryForObject(anyString(), eq(Integer.class),
                anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(1, 1);

        limiter.reserveDirectCall("ana", "ana:bea", 6, 3, Duration.ofMinutes(1));

        verify(jdbc, times(2)).queryForObject(anyString(), eq(Integer.class),
                anyString(), eq(60), anyInt(), eq(60));
    }

    @Test
    void rejectsWhenPeerBudgetIsExhausted() {
        when(jdbc.queryForObject(anyString(), eq(Integer.class),
                anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(1, 4);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> limiter.reserveDirectCall("ana", "ana:bea", 6, 3, Duration.ofMinutes(1)));

        assertEquals(429, error.getStatusCode().value());
        verify(jdbc, times(2)).queryForObject(anyString(), eq(Integer.class),
                anyString(), anyInt(), anyInt(), anyInt());
    }

    @Configuration
    @EnableTransactionManagement
    static class TransactionConfiguration {}

    @Test
    void springProxyRollsBackBothBudgetsWhenPeerIsRejected() {
        PlatformTransactionManager transactions = mock(PlatformTransactionManager.class);
        TransactionStatus status = mock(TransactionStatus.class);
        when(transactions.getTransaction(any())).thenReturn(status);
        when(jdbc.queryForObject(anyString(), eq(Integer.class),
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(1, 4);

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(TransactionConfiguration.class);
            context.registerBean(PlatformTransactionManager.class, () -> transactions);
            context.registerBean(DistributedAuthRateLimiter.class,
                    () -> new DistributedAuthRateLimiter(jdbc, "test-secret-not-for-production"));
            context.refresh();

            DistributedAuthRateLimiter proxied = context.getBean(DistributedAuthRateLimiter.class);
            assertEquals(429, assertThrows(ResponseStatusException.class,
                    () -> proxied.reserveDirectCall("ana", "ana:bea", 6, 3,
                            Duration.ofMinutes(1))).getStatusCode().value());
        }
        verify(transactions).rollback(status);
        verify(transactions, never()).commit(any());
    }

    @Test
    void propagatesDatabaseFailureOnSecondReservation() {
        when(jdbc.queryForObject(anyString(), eq(Integer.class),
                anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(1)
                .thenThrow(new IllegalStateException("PostgreSQL unavailable"));

        assertThrows(IllegalStateException.class,
                () -> limiter.reserveDirectCall("ana", "ana:bea", 6, 3, Duration.ofMinutes(1)));
        verify(jdbc, times(2)).queryForObject(anyString(), eq(Integer.class),
                anyString(), anyInt(), anyInt(), anyInt());
    }
}
