package com.example.desporto24.service;

import com.example.desporto24.enumeration.NotificationType;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Guard against enum/schema drift: inserts must not fail after a session note
 * changes and roll the organizer's edit back with a constraint exception.
 */
class MeetingPointNotificationTypeConstraintTest {
    @Test
    void currentNotificationTypesAreAllowedByLatestPostgresConstraint() throws Exception {
        // The newest migration (in master order) that recreates the check is the one in force.
        String master;
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("db/changelog/db.changelog-master.yaml")) {
            assertNotNull(in);
            master = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        String latest = null;
        String latestPath = null;
        java.util.regex.Matcher files = java.util.regex.Pattern.compile("file:\\s*(\\S+)").matcher(master);
        while (files.find()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(files.group(1))) {
                if (in == null) continue;
                String changelog = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                if (changelog.contains("ADD CONSTRAINT notifications_type_check")) {
                    latest = changelog;
                    latestPath = files.group(1);
                }
            }
        }
        assertNotNull(latest, "No migration recreates notifications_type_check");
        final String constraint = latest;
        final String path = latestPath;
        for (NotificationType type : NotificationType.values()) {
            assertTrue(constraint.contains("'" + type.name() + "'"),
                    () -> "PostgreSQL check in " + path + " is missing notification type " + type.name());
        }
    }

    @Test
    void masterChangelogIncludesConstraintUpgrade() throws Exception {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("db/changelog/db.changelog-master.yaml")) {
            assertNotNull(in);
            String master = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(master.contains("db/changelog/changes/2026-10-28-session-meeting-point-notification-type.yaml"));
        }
    }
}
