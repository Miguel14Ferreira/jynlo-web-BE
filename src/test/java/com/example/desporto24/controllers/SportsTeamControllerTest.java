package com.example.desporto24.controllers;

import com.example.desporto24.service.SportsTeamService;
import com.example.desporto24.service.SportsTeamDeletionService;
import com.example.desporto24.service.SportsTeamChampionshipHonoursService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsTeamControllerTest {
    @Mock SportsTeamService service;
    @Mock SportsTeamChampionshipHonoursService honours;
    @Mock SportsTeamDeletionService deletion;
    @InjectMocks SportsTeamController controller;

    @Test
    void onlyAuthenticatedUsersMayReachTheSafeDeletionEndpoint() throws Exception {
        Method action=SportsTeamController.class.getMethod("deleteEmpty",Long.class,Authentication.class);
        var mapping=action.getAnnotation(DeleteMapping.class);
        var security=action.getAnnotation(PreAuthorize.class);
        assertNotNull(mapping);
        assertArrayEquals(new String[]{"/{id}"},mapping.value());
        assertNotNull(security);
        assertEquals("isAuthenticated()",security.value());
    }

    @Test
    void confirmedDeleteDelegatesToHistoryProtectingServiceAndReturnsNoContent() {
        Authentication actor=mock(Authentication.class);
        when(actor.getName()).thenReturn("owner");
        var response=controller.deleteEmpty(42L,actor);
        assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());
        verify(deletion).deleteEmpty(42L,"owner");
        verifyNoInteractions(service,honours);
    }
}
