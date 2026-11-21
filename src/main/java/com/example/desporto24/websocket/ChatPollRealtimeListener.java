package com.example.desporto24.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ChatPollRealtimeListener {
    private final ChatRealtimeGateway gateway;
    private final CommunityChatRecipientResolver communityRecipients;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChanged(ChatPollChangedEvent event) {
        try {
            Set<String> recipients = new HashSet<>(event.recipients());
            if ("community".equals(event.kind())) {
                recipients.retainAll(communityRecipients.currentRecipients(Long.parseLong(event.conversationId())));
            }
            gateway.publishChatPoll(recipients, event);
        } catch (RuntimeException ignored) {
            // A failed realtime update must never undo a committed vote.
        }
    }
}
