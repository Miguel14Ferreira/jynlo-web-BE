package com.example.desporto24.model.dto.social;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class StoryResponseTeamTest {
    @Test void legacyStoriesRemainCompatibleAndNotAttributedToTeams() {
        StoryResponse legacy=new StoryResponse(1L,"player","Player",null,
            null,null,null,"texto","/media/test.jpg",false,
            Instant.parse("2026-11-20T12:00:00Z"),Instant.parse("2026-11-21T12:00:00Z"),
            true,0,false);
        assertNull(legacy.teamId());
        assertNull(legacy.teamName());
        assertNull(legacy.teamLogoUrl());
    }
    @Test void teamStoriesExposeRealEntityWithoutRewritingAuthorIdentity() {
        StoryResponse story=new StoryResponse(2L,"captain","Capitão",null,
            null,null,null,"Vitória","/media/team.jpg",false,
            Instant.parse("2026-11-20T12:00:00Z"),Instant.parse("2026-11-21T12:00:00Z"),
            true,2,false,"bottom","medium","pill",null,false,
            null,null,null,null,null,null,null,null,null,
            32L,"Colégio Pio FC","/teams/32/logo");
        assertEquals("captain",story.authorUsername());
        assertEquals(32L,story.teamId());
        assertEquals("Colégio Pio FC",story.teamName());
    }
}
