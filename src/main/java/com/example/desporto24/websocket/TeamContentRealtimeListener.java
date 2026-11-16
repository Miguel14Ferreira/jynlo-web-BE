package com.example.desporto24.websocket;

import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.team.SportsTeamRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TeamContentRealtimeListener {
    private final SportsTeamRepository teams;
    private final SportsTeamMemberRepository members;
    private final ChatRealtimeGateway gateway;
    private final PlatformTransactionManager transactionManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChange(TeamContentChangedEvent event) {
        if (event == null || event.teamId() == null
                || !("chat".equals(event.kind()) || "feed".equals(event.kind()))) return;
        try {
            var tx = new TransactionTemplate(transactionManager);
            tx.setReadOnly(true);
            tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            tx.executeWithoutResult(ignored -> notifyCurrentMembers(event.teamId(), event.kind()));
        } catch (RuntimeException ignored) {
            // Fail closed; private HTTP history remains the recovery path.
        }
    }

    /** No private message is sent. The removed player receives a revalidation hint only. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMembershipRevoked(TeamMembershipRevokedEvent event) {
        if (event == null || event.teamId() == null || event.teamId() <= 0
                || event.username() == null || event.username().isBlank()) return;
        try {
            // The former member is intentionally included: their next authenticated
            // HTTP read returns 403 and clears any locally cached private messages.
            gateway.publishTeamContentInvalidated(Set.of(event.username()), event.teamId(), "chat");
            gateway.publishTeamContentInvalidated(Set.of(event.username()), event.teamId(), "feed");
        } catch (RuntimeException ignored) {
            // No private content is included; periodic client revalidation is fallback.
        }
    }

    public void notifyCurrentMembers(Long teamId, String kind) {
        var team = teams.findById(teamId).orElse(null);
        if (team == null || ("chat".equals(kind) ? !team.isChatEnabled() : !team.isFeedEnabled())) return;
        Set<String> recipients = new HashSet<>();
        recipients.add(team.getOwner().getUsername());
        members.findByTeamIdAndStatusOrderByIdAsc(teamId, SportsTeamMember.Status.ACTIVE)
                .forEach(m -> recipients.add(m.getProfile().getUsername()));
        gateway.publishTeamContentInvalidated(recipients, teamId, kind);
    }
}
