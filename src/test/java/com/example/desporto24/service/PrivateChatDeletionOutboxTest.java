package com.example.desporto24.service;

import com.example.desporto24.storage.PrivateChatFileStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PrivateChatDeletionOutboxTest {
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final PrivateChatFileStorage storage = mock(PrivateChatFileStorage.class);
    private final PrivateChatDeletionOutbox outbox = new PrivateChatDeletionOutbox(jdbc, storage);
    private static final String KEY = "private-chat/11111111-1111-1111-1111-111111111111.pdf";

    @AfterEach void clear() {
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test void enqueueRequiresActiveTransaction() {
        assertThrows(IllegalStateException.class, () -> outbox.enqueueAll(List.of(KEY)));
        verifyNoInteractions(jdbc, storage);
    }

    @Test void enqueueValidatesAndDeduplicatesKeys() {
        TransactionSynchronizationManager.setActualTransactionActive(true);
        outbox.enqueueAll(List.of(KEY, KEY));
        verify(jdbc, times(1)).update(anyString(), eq(KEY));
        assertThrows(IllegalArgumentException.class,
                () -> outbox.enqueueAll(List.of("posts/12/public.jpg")));
        verifyNoInteractions(storage);
    }

    @Test void successfulCleanupDeletesObjectAndQueueEntry() {
        var row = new PrivateChatDeletionOutbox.Pending(3L, KEY, 1);
        var spyOutbox = spy(outbox);
        doReturn(List.of(row)).when(spyOutbox).claimBatch();
        spyOutbox.processPending();
        verify(storage).delete(KEY);
        verify(jdbc).update(contains("DELETE FROM desporto24.private_chat_deletion_outbox"), eq(3L), eq(1));
    }

    @Test void storageFailureLeavesQueueEntryAndSchedulesRetry() {
        var row = new PrivateChatDeletionOutbox.Pending(4L, KEY, 2);
        var spyOutbox = spy(outbox);
        doReturn(List.of(row)).when(spyOutbox).claimBatch();
        doThrow(new IllegalStateException("R2 unavailable")).when(storage).delete(KEY);
        spyOutbox.processPending();
        verify(jdbc, never()).update(contains("DELETE FROM desporto24.private_chat_deletion_outbox"), any(), any());
        verify(jdbc).update(contains("UPDATE desporto24.private_chat_deletion_outbox"), eq(4), eq(4L), eq(2));
    }
}
