package com.example.desporto24.service;

import com.example.desporto24.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessionMinimumKickoffJobTest {
    @Test
    void onlyLooksAtGamesThatStartedInTheLastThreeHours() {
        SessionRepository sessions = mock(SessionRepository.class);
        SessionMinimumClosureService closure = mock(SessionMinimumClosureService.class);
        when(sessions.findDueUnderMinimum(any(), any(), any(Pageable.class))).thenReturn(List.of(7L));

        new SessionMinimumKickoffJob(sessions, closure).checkKickoffs();

        ArgumentCaptor<Date> since = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> cutoff = ArgumentCaptor.forClass(Date.class);
        verify(sessions).findDueUnderMinimum(since.capture(), cutoff.capture(), any(Pageable.class));
        assertEquals(Duration.ofHours(3).toMillis(), cutoff.getValue().getTime() - since.getValue().getTime());
        verify(closure).closeIfUnderMinimum(eq(7L), any());
    }

    @Test
    void theThreeJobsAreCreatedAtStartupEvenThoughProductionBeansAreLazy() {
        for (Class<?> job : List.of(SessionMinimumKickoffJob.class, PostGamePaymentReminderJob.class, SessionInviteReminderService.class)) {
            Lazy lazy = job.getAnnotation(Lazy.class);
            assertNotNull(lazy, job.getSimpleName());
            assertFalse(lazy.value(), job.getSimpleName());
        }
    }
}
