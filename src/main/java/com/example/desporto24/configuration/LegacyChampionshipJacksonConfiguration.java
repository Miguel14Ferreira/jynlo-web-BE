package com.example.desporto24.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4 configures Jackson 3 by default, while the championship
 * snapshot service still reads/writes persisted snapshots with Jackson 2.
 * Supply the legacy mapper explicitly until the snapshot format is migrated.
 */
@Configuration(proxyBeanMethods = false)
public class LegacyChampionshipJacksonConfiguration {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    ObjectMapper championshipSnapshotObjectMapper() {
        return new ObjectMapper();
    }
}
