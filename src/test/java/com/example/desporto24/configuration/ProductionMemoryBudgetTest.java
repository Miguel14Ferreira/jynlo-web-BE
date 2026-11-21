package com.example.desporto24.configuration;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Static release guard for the 2 GiB Render plan (sized on 2026-11-20).
 * These checks do not replace runtime RSS profiling or a real load test.
 */
class ProductionMemoryBudgetTest {
    @Test
    void productionDatasourcePoolIsExplicitlyBoundedAndOverridable() throws Exception {
        Properties properties = new Properties();
        try (var in = getClass().getClassLoader().getResourceAsStream("application-prod.properties")) {
            assertNotNull(in, "Production configuration must be packaged");
            properties.load(in);
        }
        assertEquals("${JYNLO_DB_POOL_MAX:10}",
                properties.getProperty("spring.datasource.hikari.maximum-pool-size"));
        assertEquals("${JYNLO_DB_POOL_MIN_IDLE:1}",
                properties.getProperty("spring.datasource.hikari.minimum-idle"));
        assertEquals("30000", properties.getProperty("spring.datasource.hikari.idle-timeout"));
        assertEquals("true", properties.getProperty("spring.main.lazy-initialization"));
    }

    @Test
    void containerBoundsAllocatorAndWebWorkerPressureWithoutReduccingMetaspaceToKnownUnsafeLevel() throws Exception {
        String docker = Files.readString(Path.of("Dockerfile"), StandardCharsets.UTF_8);
        assertTrue(docker.contains("ENV MALLOC_ARENA_MAX=2"));
        // Heap follows the container limit instead of a fixed cap.
        assertTrue(docker.contains("\"-XX:MaxRAMPercentage=55\""));
        assertFalse(docker.contains("\"-Xmx160m\""));
        assertTrue(docker.contains("\"-XX:+ExitOnOutOfMemoryError\""));
        assertTrue(docker.contains("\"-Dserver.tomcat.threads.max=40\""));
        assertTrue(docker.contains("\"-Dserver.tomcat.threads.min-spare=4\""));
        assertTrue(docker.contains("\"-XX:MaxMetaspaceSize=256m\""));
        assertFalse(docker.contains("\"-XX:MaxMetaspaceSize=144m\""));
    }
}
