package com.example.desporto24.service;

import com.example.desporto24.repository.team.SportsTeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

class SportsTeamDirectoryQueryTest {
    @Test
    void publicDirectoryFetchesTheOwnerWithoutPerCardLazyQueries() throws Exception {
        Method lookup = SportsTeamRepository.class.getMethod(
                "findByNameContainingIgnoreCaseOrderByIdDesc", String.class, Pageable.class);
        var graph = lookup.getAnnotation(EntityGraph.class);
        assertNotNull(graph, "Public team cards require owner details in one query");
        assertArrayEquals(new String[]{"owner"}, graph.attributePaths());
    }

    @Test
    void myTeamsDirectoryUsesTheSameOwnerFetchPlan() throws Exception {
        Method lookup = SportsTeamRepository.class.getMethod("findByOwnerIdOrderByIdDesc", Long.class);
        var graph = lookup.getAnnotation(EntityGraph.class);
        assertNotNull(graph, "Owned team cards must not create one SQL lookup per owner");
        assertArrayEquals(new String[]{"owner"}, graph.attributePaths());
    }
}
