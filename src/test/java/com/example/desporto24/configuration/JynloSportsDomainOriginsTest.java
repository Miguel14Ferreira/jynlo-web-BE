package com.example.desporto24.configuration;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JynloSportsDomainOriginsTest {
    @Test
    void newDomainIsAllowedNextToTheConfiguredOrigins() throws Exception {
        Properties properties = new Properties();
        try (Reader in = Files.newBufferedReader(Path.of("src/main/resources/application.properties"))) {
            properties.load(in);
        }
        String origins = properties.getProperty("desporto.cors.allowed-origins");
        assertTrue(origins.startsWith("${CORS_ALLOWED_ORIGINS:${UI_APP_URL}},"));
        assertTrue(origins.contains("https://jynlosports.com"));
        assertTrue(origins.contains("https://www.jynlosports.com"));
    }
}
