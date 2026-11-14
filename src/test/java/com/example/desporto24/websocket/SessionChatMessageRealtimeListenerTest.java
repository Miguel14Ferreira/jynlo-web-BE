package com.example.desporto24.websocket;

import com.example.desporto24.model.dto.session.SessionMessageResponse;
import com.example.desporto24.service.SessionChatService;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Set;
import static org.mockito.Mockito.*;

class SessionChatMessageRealtimeListenerTest {
    private final ChatRealtimeGateway gateway = mock(ChatRealtimeGateway.class);
    private final SessionChatService chat = mock(SessionChatService.class);
    private final SessionChatMessageRealtimeListener listener =
            new SessionChatMessageRealtimeListener(gateway, chat);

    private SessionChatMessageCreatedEvent event() {
        return new SessionChatMessageCreatedEvent(
            new SessionMessageResponse(10L, 20L, "author", "hello", Instant.now()),
            Set.of("author", "removed", "participant"));
    }

    @Test void removedPlayerDoesNotReceiveCommittedMessage() {
        when(chat.currentRealtimeRecipients(20L)).thenReturn(Set.of("author", "participant", "newJoiner"));
        var event=event();
        listener.onSessionChatMessageCreated(event);
        verify(gateway).publishSessionChatMessage(Set.of("author","participant"),event.message());
    }

    @Test void closedSessionNeverReceivesDelayedRealtimeDelivery() {
        when(chat.currentRealtimeRecipients(20L)).thenReturn(Set.of());
        listener.onSessionChatMessageCreated(event());
        verifyNoInteractions(gateway);
    }

    @Test void lookupFailureDoesNotLeakPrivateMessages() {
        when(chat.currentRealtimeRecipients(20L)).thenThrow(new IllegalStateException("storage unavailable"));
        listener.onSessionChatMessageCreated(event());
        verifyNoInteractions(gateway);
    }
}
