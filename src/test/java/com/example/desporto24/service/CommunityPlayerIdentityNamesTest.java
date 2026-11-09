package com.example.desporto24.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommunityPlayerIdentityNamesTest {

    @Test
    void keepsOriginalNameWhenNotUsed() {
        var value = CommunityPlayerIdentityNames.allocate(
                "Pessoa Exemplo", "player1", 21L, ignored -> false);
        assertEquals("Pessoa Exemplo", value.display());
        assertEquals("pessoa exemplo", value.normalized());
    }

    @Test
    void sameFullNameCreatesDistinctProfileLabelsWithoutMerging() {
        Set<String> occupied = new HashSet<>();
        var first = CommunityPlayerIdentityNames.allocate(
                "Pessoa Exemplo", "player1", 21L, occupied::contains);
        occupied.add(first.normalized());
        var second = CommunityPlayerIdentityNames.allocate(
                "Pessoa Exemplo", "player2", 22L, occupied::contains);
        assertEquals("Pessoa Exemplo (@player2)", second.display());
        assertNotEquals(first.normalized(), second.normalized());
    }

    @Test
    void historicalImportedNameIsPreservedAndProfileUsesHandle() {
        Set<String> imported = Set.of("pessoa exemplo");
        var value = CommunityPlayerIdentityNames.allocate(
                "Pessoa Exemplo", "newmember", 45L, imported::contains);
        assertEquals("Pessoa Exemplo (@newmember)", value.display());
        assertEquals("pessoa exemplo (@newmember)", value.normalized());
    }

    @Test
    void fallsBackToStableProfileIdentifierIfHandleAlsoConflicts() {
        Set<String> occupied = Set.of("pessoa exemplo", "pessoa exemplo (@member)");
        var value = CommunityPlayerIdentityNames.allocate(
                "Pessoa Exemplo", "member", 77L, occupied::contains);
        assertEquals("Pessoa Exemplo (#77)", value.display());
    }

    @Test
    void handlesLengthLimitWithoutTruncatingUniqueSuffix() {
        String longName = "A".repeat(120);
        var value = CommunityPlayerIdentityNames.allocate(
                longName, "member", 77L, key -> key.equals(longName.toLowerCase()));
        assertTrue(value.display().length() <= 120);
        assertTrue(value.display().endsWith("(@member)"));
    }

    @Test
    void longHandleFallsBackToStableProfileIdentifier() {
        String longHandle = "x".repeat(130);
        var value = CommunityPlayerIdentityNames.allocate(
                "Pessoa Exemplo", longHandle, 77L,
                key -> key.equals("pessoa exemplo"));
        assertEquals("Pessoa Exemplo (#77)", value.display());
        assertEquals("pessoa exemplo (#77)", value.normalized());
    }

    @Test
    void irresolvableConflictReturns409InsteadOfClaimingOtherIdentity() {
        Set<String> occupied = Set.of("pessoa exemplo",
                "pessoa exemplo (@member)", "pessoa exemplo (#77)");
        var error = assertThrows(ResponseStatusException.class,
                () -> CommunityPlayerIdentityNames.allocate(
                        "Pessoa Exemplo", "member", 77L, occupied::contains));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }
}
