package com.example.desporto24.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionVenueSportValidationTest {

    @Test
    void acceptsExactSportsSeparatedByCommaOrSemicolon() {
        assertTrue(SessionServiceImpl.venueSupportsSport("Futsal, Padel; Ténis", "Padel"));
        assertTrue(SessionServiceImpl.venueSupportsSport("Futsal, Padel; Ténis", "ténis"));
        assertTrue(SessionServiceImpl.venueSupportsSport("Futsal;Padel", "Futsal"));
    }

    @Test
    void rejectsPartialSportsAndUnsupportedSports() {
        assertFalse(SessionServiceImpl.venueSupportsSport("Padel, Futebol Americano", "Futebol"));
        assertFalse(SessionServiceImpl.venueSupportsSport("Futsal; Ténis", "Padel"));
    }

    @Test
    void preservesUnrestrictedVenuesWithoutSpecifiedSports() {
        assertTrue(SessionServiceImpl.venueSupportsSport(null, "Padel"));
        assertTrue(SessionServiceImpl.venueSupportsSport("  ", "Futsal"));
    }
}
