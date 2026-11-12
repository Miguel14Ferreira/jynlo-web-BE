package com.example.desporto24.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VenuePhotoUploadAccessTest {
    @Test
    void uploadingPhotosMustRemainRestrictedUntilModerationExists() {
        Method endpoint = Arrays.stream(VenueController.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PostMapping.class))
                .filter(method -> Arrays.asList(method.getAnnotation(PostMapping.class).value())
                        .contains("/{id}/photos"))
                .findFirst().orElseThrow();
        PreAuthorize guard = endpoint.getAnnotation(PreAuthorize.class);
        assertNotNull(guard, "Public direct uploads would bypass photo moderation");
        assertTrue(guard.value().contains("ADMIN"));
        assertTrue(guard.value().contains("SUPER_ADMIN"));
    }
}
