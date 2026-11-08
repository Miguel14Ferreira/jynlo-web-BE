package com.example.desporto24.model;

import com.example.desporto24.enumeration.AirsoftGameType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SessionAirsoftGameTypeTest {

    @Test
    void liquibaseMigrationIsRegisteredAndKeepsExistingSessionsCompatible() throws Exception {
        String masterPath = "db/changelog/db.changelog-master.yaml";
        String migrationPath = "db/changelog/changes/2026-11-08-session-airsoft-game-type.yaml";
        try (var masterStream = getClass().getClassLoader().getResourceAsStream(masterPath);
             var migrationStream = getClass().getClassLoader().getResourceAsStream(migrationPath)) {
            assertNotNull(masterStream, "Liquibase master changelog must exist");
            assertNotNull(migrationStream, "Airsoft migration must exist");
            String master = new String(masterStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            String migration = new String(migrationStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            assertTrue(master.contains(migrationPath));
            assertTrue(migration.contains("name: airsoft_game_type"));
            assertTrue(migration.contains("nullable: true"));
            assertTrue(migration.contains("tableName: session"));
        }
    }

    @Test
    void storesPublicGameTypeSeparatelyFromPrivateArrivalInstructions() throws Exception {
        Session session = new Session();
        session.setSport("Airsoft");
        session.setAirsoftGameType(AirsoftGameType.MILSIM);
        session.setMeetingPointInstructions("Entrada reservada");

        assertEquals(AirsoftGameType.MILSIM, session.getAirsoftGameType());
        assertEquals("Entrada reservada", session.getMeetingPointInstructions());

        Field publicType = Session.class.getDeclaredField("airsoftGameType");
        Field privateInstructions = Session.class.getDeclaredField("meetingPointInstructions");
        assertNotEquals(publicType.getName(), privateInstructions.getName());

        Enumerated enumMapping = publicType.getAnnotation(Enumerated.class);
        assertNotNull(enumMapping);
        assertEquals(EnumType.STRING, enumMapping.value());
        Column publicColumn = publicType.getAnnotation(Column.class);
        assertNotNull(publicColumn);
        assertEquals("airsoft_game_type", publicColumn.name());
    }
}
