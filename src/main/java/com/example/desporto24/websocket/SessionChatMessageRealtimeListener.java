package com.example.desporto24.websocket;

import com.example.desporto24.service.SessionChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SessionChatMessageRealtimeListener {

    private final ChatRealtimeGateway chatRealtimeGateway;
    private final SessionChatService sessionChatService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSessionChatMessageCreated(SessionChatMessageCreatedEvent event) {
        if (event == null || event.message() == null || event.recipients() == null) return;
        try {
            // Members may have left or been removed since the message was stored.
            // Intersect the original audience with current committed membership,
            // and stop delivery if the session has closed or kickoff has passed.
            Set<String> recipients = new HashSet<>(
                    sessionChatService.currentRealtimeRecipients(event.message().sessionId()));
            recipients.retainAll(event.recipients());
            if (!recipients.isEmpty()) {
                chatRealtimeGateway.publishSessionChatMessage(recipients, event.message());
            }
        } catch (RuntimeException ignored) {
            // Fail closed on membership lookup failures. Authorized HTTP history
            // remains the source of truth for committed messages.
        }
    }
}
