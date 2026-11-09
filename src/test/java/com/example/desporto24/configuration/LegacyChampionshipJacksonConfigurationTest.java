package com.example.desporto24.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LegacyChampionshipJacksonConfigurationTest {

    @Test
    void suppliesJacksonTwoMapperForPersistedChampionshipSnapshots() throws Exception {
        try (var context = new AnnotationConfigApplicationContext(
                LegacyChampionshipJacksonConfiguration.class)) {
            ObjectMapper mapper = context.getBean(ObjectMapper.class);
            var snapshot = Map.of("name", "Taça", "points", 3);
            String json = mapper.writeValueAsString(snapshot);
            assertEquals("Taça", mapper.readTree(json).get("name").asText());
            assertEquals(3, mapper.readTree(json).get("points").asInt());
            assertEquals(1, context.getBeansOfType(ObjectMapper.class).size());
        }
    }

    @Test
    void preservesExistingJacksonTwoMapperWhenAlreadyConfigured() {
        var provided = new ObjectMapper();
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean("existingObjectMapper", ObjectMapper.class, () -> provided);
            context.register(LegacyChampionshipJacksonConfiguration.class);
            context.refresh();
            assertSame(provided, context.getBean(ObjectMapper.class));
            assertEquals(1, context.getBeansOfType(ObjectMapper.class).size());
        }
    }
}
