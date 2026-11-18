package com.example.desporto24.configuration;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Static release guard for our 512 MiB Render service.
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
        assertEquals("${JYNLO_DB_POOL_MAX:4}",
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
        assertTrue(docker.contains("\"-Dserver.tomcat.threads.max=16\""));
        assertTrue(docker.contains("\"-Dserver.tomcat.threads.min-spare=2\""));
        assertTrue(docker.contains("\"-XX:MaxMetaspaceSize=200m\""));
        assertTrue(docker.contains("\"-Xmx160m\""));
        assertFalse(docker.contains("\"-XX:MaxMetaspaceSize=144m\""));
    }
}
