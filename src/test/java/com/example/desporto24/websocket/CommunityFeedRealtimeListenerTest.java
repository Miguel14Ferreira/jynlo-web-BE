package com.example.desporto24.websocket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityFeedRealtimeListenerTest {
    @Mock ChatRealtimeGateway gateway;
    @Mock CommunityChatRecipientResolver resolver;
    @InjectMocks CommunityFeedRealtimeListener listener;

    @Test void notifiesOnlyCurrentlyAuthorizedMembersWithoutPublishingPostContent() {
        when(resolver.currentRecipients(7L)).thenReturn(Set.of("owner", "member"));
        listener.onFeedChange(new CommunityFeedChangedEvent(7L));
        verify(gateway).publishCommunityFeedInvalidated(Set.of("owner", "member"), 7L);
    }

    @Test void permissionLookupFailuresDoNotBroadcastOrRollbackPost() {
        when(resolver.currentRecipients(7L)).thenThrow(new IllegalStateException("offline"));
        assertDoesNotThrow(() -> listener.onFeedChange(new CommunityFeedChangedEvent(7L)));
        verifyNoInteractions(gateway);
    }

    @Test void invalidCommunityIdsNeverBroadcast() {
        listener.onFeedChange(new CommunityFeedChangedEvent(null));
        listener.onFeedChange(new CommunityFeedChangedEvent(0L));
        verifyNoInteractions(gateway, resolver);
    }

    @Test void notificationsOccurOnlyAfterCommit() throws Exception {
        var annotation=CommunityFeedRealtimeListener.class
                .getMethod("onFeedChange",CommunityFeedChangedEvent.class)
                .getAnnotation(TransactionalEventListener.class);
        assertNotNull(annotation);
        assertEquals(TransactionPhase.AFTER_COMMIT,annotation.phase());
    }
}
