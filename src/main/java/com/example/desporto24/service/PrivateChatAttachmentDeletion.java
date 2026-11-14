package com.example.desporto24.service;

import com.example.desporto24.storage.PrivateChatFileStorage;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * Delete private files only after the message tombstone has committed.
 * Keeping a file on rollback is safer than deleting an attachment referenced
 * by a message whose deletion did not commit.
 */
public final class PrivateChatAttachmentDeletion {
    private PrivateChatAttachmentDeletion() {}

    public static void afterCommit(PrivateChatFileStorage storage, List<String> keys) {
        if (storage == null || keys == null || keys.isEmpty()) return;
        if (!TransactionSynchronizationManager.isSynchronizationActive()
                || !TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("Private attachment cleanup requires an active transaction");
        }
        List<String> snapshot = keys.stream()
                .filter(key -> key != null && !key.isBlank()).distinct().toList();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                for (String key : snapshot) {
                    try {
                        storage.delete(key);
                    } catch (RuntimeException ignored) {
                        // Do not fail an already committed deletion or expose
                        // storage details to clients. Durable retries require a
                        // separate orphan-cleanup process.
                    }
                }
            }
        });
    }
}
