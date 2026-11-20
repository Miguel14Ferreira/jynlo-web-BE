package com.example.desporto24.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CommunityFeedRealtimeListener {
    private final ChatRealtimeGateway gateway;
    private final CommunityChatRecipientResolver resolver;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFeedChange(CommunityFeedChangedEvent event) {
        if (event == null || event.communityId() == null || event.communityId() <= 0) return;
        try {
            // Resolve current membership after commit. Never broadcast private post content.
            gateway.publishCommunityFeedInvalidated(resolver.currentRecipients(event.communityId()),
                    event.communityId());
        } catch (RuntimeException ignored) {
            // Access-controlled HTTP read on reconnect remains authoritative.
        }
    }
}
