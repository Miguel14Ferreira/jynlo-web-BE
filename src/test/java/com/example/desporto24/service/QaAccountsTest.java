package com.example.desporto24.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QaAccountsTest {
    @Test
    void testAccountsAreHiddenWhateverTheCase() {
        assertTrue(QaAccounts.isHidden("jynloteste01"));
        assertTrue(QaAccounts.isHidden(" JynloTeste02 "));
        assertFalse(QaAccounts.isHidden("miguel"));
        assertFalse(QaAccounts.isHidden(null));
    }
}
