package com.example.desporto24.service;

import com.example.desporto24.storage.PrivateChatFileStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrivateChatAttachmentDeletionTest {
    private PrivateChatFileStorage storage;

    @BeforeEach void startTransaction() {
        storage = mock(PrivateChatFileStorage.class);
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
    }

    @AfterEach void clearTransaction() {
        TransactionSynchronizationManager.clearSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test void filesAreNotDeletedBeforeCommitAndAreDeletedAfterCommit() {
        PrivateChatAttachmentDeletion.afterCommit(storage, List.of("private-chat/a.pdf", "private-chat/b.pdf"));
        verifyNoInteractions(storage);
        TransactionSynchronizationManager.getSynchronizations().forEach(sync -> sync.afterCommit());
        verify(storage).delete("private-chat/a.pdf");
        verify(storage).delete("private-chat/b.pdf");
    }

    @Test void rollbackNeverDeletesPrivateFiles() {
        PrivateChatAttachmentDeletion.afterCommit(storage, List.of("private-chat/a.pdf"));
        TransactionSynchronizationManager.getSynchronizations().forEach(sync ->
                sync.afterCompletion(org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK));
        verifyNoInteractions(storage);
    }

    @Test void duplicateKeysAreDeletedOnlyOnceAndFailuresDoNotStopRemainingCleanup() {
        doThrow(new IllegalStateException("storage unavailable")).when(storage).delete("private-chat/a.pdf");
        PrivateChatAttachmentDeletion.afterCommit(storage,
                List.of("private-chat/a.pdf", "private-chat/a.pdf", "private-chat/b.pdf"));
        assertDoesNotThrow(() ->
                TransactionSynchronizationManager.getSynchronizations().forEach(sync -> sync.afterCommit()));
        verify(storage, times(1)).delete("private-chat/a.pdf");
        verify(storage).delete("private-chat/b.pdf");
    }
}
