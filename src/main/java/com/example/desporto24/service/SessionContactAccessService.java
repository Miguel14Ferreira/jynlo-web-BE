package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * Additional, short-lived contact permission created by a shared upcoming
 * sports session. Not a friendship and never an unrestricted inbox ACL.
 *
 * Both accounts must be active participants (or the session creator), and
 * block rules still apply. All callers must check this on each operation,
 * including call accept/invite and private conversation reads.
 */
@Service
@RequiredArgsConstructor
public class SessionContactAccessService {
    private final ProfileRepository profiles;
    private final SessionRepository sessions;
    private final UserBlockService blocks;

    @Transactional(readOnly = true)
    public boolean mayContact(String sender, String recipient) {
        if (sender == null || recipient == null || sender.isBlank() || recipient.isBlank()
                || sender.equalsIgnoreCase(recipient)) return false;
        Profile first = profiles.findUserByUsernameIgnoreCase(sender).orElse(null);
        Profile second = profiles.findUserByUsernameIgnoreCase(recipient).orElse(null);
        if (first == null || second == null || Objects.equals(first.getId(), second.getId())) {
            return false;
        }
        if (blocks.isBlockedBetween(first, second)) return false;
        return sessions.existsSharedUpcomingContactSession(
                first.getId(), second.getId(), Date.from(Instant.now()));
    }

    @Transactional(readOnly = true)
    public void requireContact(String sender, String recipient) {
        if (!mayContact(sender, recipient)) {
            throw new AccessDeniedException("No shared active session contact permission");
        }
    }
}
