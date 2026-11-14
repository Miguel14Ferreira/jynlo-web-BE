package com.example.desporto24.service;

import com.example.desporto24.storage.MediaStorage;
import com.example.desporto24.storage.PrivateProfileMediaStorage;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProfileMediaDeletionOutboxTest {
    private final JdbcTemplate jdbc=mock(JdbcTemplate.class);
    private final MediaStorage publicStorage=mock(MediaStorage.class);
    private final PrivateProfileMediaStorage privateStorage=mock(PrivateProfileMediaStorage.class);
    private final ProfileMediaDeletionOutbox service=new ProfileMediaDeletionOutbox(jdbc,publicStorage);

    private void setupPrivate() {
        ReflectionTestUtils.setField(service,"privateStorage",privateStorage);
    }

    @Test void queueRequiresRealTransactionAndNeverDeletesObjects() {
        assertThrows(IllegalStateException.class,
                ()->service.enqueue("private-profile-media/posts/19/photo.jpg"));
        verifyNoInteractions(jdbc,publicStorage,privateStorage);
        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            service.enqueue("private-profile-media/posts/19/photo.jpg");
            verify(jdbc).update(contains("INSERT INTO desporto24.profile_media_deletion_outbox"),
                    eq("private-profile-media/posts/19/photo.jpg"));
            verifyNoInteractions(publicStorage,privateStorage);
        } finally {
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }

    @Test void successfulRetryDeletesOnlyTheMatchingClaim() {
        setupPrivate();
        when(privateStorage.isConfigured()).thenReturn(true);
        var item=new ProfileMediaDeletionOutbox.Pending(12L,
                "private-profile-media/stories/19/photo.jpg",2);
        when(jdbc.query(contains("WITH due"),any(org.springframework.jdbc.core.RowMapper.class)))
                .thenReturn(List.of(item));
        service.processPending();
        verify(privateStorage).delete("private-profile-media/stories/19/photo.jpg");
        verify(publicStorage,never()).delete(anyString());
        verify(jdbc).update(contains("DELETE FROM desporto24.profile_media_deletion_outbox"),
                eq(12L),eq(2));
    }

    @Test void failureKeepsPendingRecordForRetry() {
        setupPrivate();
        when(privateStorage.isConfigured()).thenReturn(false);
        var item=new ProfileMediaDeletionOutbox.Pending(13L,
                "private-profile-media/stories/19/photo.jpg",3);
        when(jdbc.query(contains("WITH due"),any(org.springframework.jdbc.core.RowMapper.class)))
                .thenReturn(List.of(item));
        service.processPending();
        verify(jdbc).update(contains("SET next_attempt_at"),anyInt(),eq(13L),eq(3));
        verify(jdbc,never()).update(contains("DELETE FROM"),anyLong(),anyInt());
        verify(privateStorage,never()).delete(anyString());
    }

    @Test void invalidNamespaceNeverReachesPublicBucket() {
        var item=new ProfileMediaDeletionOutbox.Pending(18L,"../../etc/passwd",1);
        when(jdbc.query(contains("WITH due"),any(org.springframework.jdbc.core.RowMapper.class)))
                .thenReturn(List.of(item));
        service.processPending();
        verifyNoInteractions(publicStorage);
        verify(jdbc).update(contains("SET next_attempt_at"),anyInt(),eq(18L),eq(1));
    }
}
