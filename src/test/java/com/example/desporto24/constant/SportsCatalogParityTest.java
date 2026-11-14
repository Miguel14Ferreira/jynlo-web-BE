package com.example.desporto24.constant;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class SportsCatalogParityTest {
    @Test void everySportAvailableInTheClientIsAllowedByTheBackend() {
        assertEquals(122, SportsCatalog.ALLOWED.size(),
                "Sync this curated count with src/app/shared/sports-catalog.ts when the catalogue changes");
        for (String sport : Set.of(
                "Bowling", "Corrida de obstáculos", "Hipismo", "Teqball", "Ténis de praia")) {
            assertTrue(SportsCatalog.ALLOWED.contains(sport), sport);
            assertTrue(SportsCatalog.isValidSelection(sport), sport);
        }
    }

    @Test void commaSeparatedSelectionsRemainStrictAndLegacySportsWork() {
        assertTrue(SportsCatalog.isValidSelection("Futebol, Bowling, Ténis de praia"));
        assertTrue(SportsCatalog.isValidSelection("Natação Artística, Ginástica Rítmica"));
        assertTrue(SportsCatalog.isValidSelection(null));
        assertTrue(SportsCatalog.isValidSelection(""));
        assertFalse(SportsCatalog.isValidSelection("Bowling, modalidade-inventada"));
        assertFalse(SportsCatalog.isValidSelection("Ténis de praia, Teqball, ??"));
    }

    @Test void customSportNamesAreAcceptedOnlyForSessions() {
        assertTrue(SportsCatalog.isCustomSessionSport("Bossaball"));
        assertTrue(SportsCatalog.isAllowedSessionSport("Bossaball"));
        assertTrue(SportsCatalog.isAllowedSessionSport("Futebol"));
        assertFalse(SportsCatalog.isCustomSessionSport("Futebol"));
        assertFalse(SportsCatalog.isCustomSessionSport("FUTEBOL"));
        assertFalse(SportsCatalog.isCustomSessionSport("Tenis de praia"));
        assertFalse(SportsCatalog.isCustomSessionSport("A"));
        assertFalse(SportsCatalog.isCustomSessionSport("<script>"));
        assertFalse(SportsCatalog.isCustomSessionSport(" Desporto"));
        assertFalse(SportsCatalog.isValidSelection("Bossaball"),
                "The profile/community catalog must remain official-only");
    }
}
