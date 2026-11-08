package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunityTournament;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ChampionshipCustomScoringPersistenceContractTest {
    @Test
    void optionalVersionedSnapshotDoesNotChangeLegacyTournamentDefaults() throws Exception {
        CommunityTournament tournament = new CommunityTournament();
        assertNull(tournament.getChampionshipCustomRulesJson());
        assertNull(tournament.getChampionshipCustomRulesVersion());

        Column json = CommunityTournament.class.getDeclaredField("championshipCustomRulesJson")
                .getAnnotation(Column.class);
        Column version = CommunityTournament.class.getDeclaredField("championshipCustomRulesVersion")
                .getAnnotation(Column.class);
        assertEquals("championship_custom_rules_json", json.name());
        assertEquals("championship_custom_rules_version", version.name());
        assertTrue(json.nullable());
        assertTrue(version.nullable());
    }

    @Test
    void additiveMigrationIsRegisteredAndRollbackDropsBothColumns() throws Exception {
        String master = Files.readString(Path.of("src/main/resources/db/changelog/db.changelog-master.yaml"),
                StandardCharsets.UTF_8);
        String migration = Files.readString(Path.of(
                "src/main/resources/db/changelog/changes/2026-11-08-community-championship-custom-rules.yaml"),
                StandardCharsets.UTF_8);
        assertTrue(master.contains("2026-11-08-community-championship-custom-rules.yaml"));
        assertTrue(migration.contains("tableName: community_tournament"));
        assertTrue(migration.contains("championship_custom_rules_json"));
        assertTrue(migration.contains("championship_custom_rules_version"));
        assertTrue(migration.contains("nullable: true"));
        assertTrue(migration.contains("rollback:"));
    }
}
