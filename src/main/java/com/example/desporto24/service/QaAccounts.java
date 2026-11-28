package com.example.desporto24.service;

import java.util.Locale;
import java.util.Set;

/**
 * Accounts kept for end-to-end testing (and store review). They can sign in and be opened by link,
 * but never appear in any search, mention list or suggestion, so other people cannot find them.
 */
public final class QaAccounts {
    private static final Set<String> HIDDEN = Set.of("jynloteste01", "jynloteste02");

    private QaAccounts() {}

    public static boolean isHidden(String username) {
        return username != null && HIDDEN.contains(username.trim().toLowerCase(Locale.ROOT));
    }
}
