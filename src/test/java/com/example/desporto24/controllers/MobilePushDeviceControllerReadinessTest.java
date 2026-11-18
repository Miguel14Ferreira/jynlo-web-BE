package com.example.desporto24.controllers;

import com.example.desporto24.service.MobilePushDeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MobilePushDeviceControllerReadinessTest {
    private final MobilePushDeviceController controller =
            new MobilePushDeviceController(mock(MobilePushDeviceService.class));
    private final Authentication authentication = mock(Authentication.class);

    @Test
    void disabledPushIsNotReportedAsAvailable() {
        when(authentication.getName()).thenReturn("player");
        ReflectionTestUtils.setField(controller,"pushEnabled",false);
        ReflectionTestUtils.setField(controller,"pushAccessToken","test-only");
        assertFalse(controller.deliveryStatus(authentication).ready());
    }

    @Test
    void missingSecretIsNotReportedAsAvailable() {
        when(authentication.getName()).thenReturn("player");
        ReflectionTestUtils.setField(controller,"pushEnabled",true);
        ReflectionTestUtils.setField(controller,"pushAccessToken","");
        assertFalse(controller.deliveryStatus(authentication).ready());
    }

    @Test
    void configuredSenderReportsReadinessWithoutReturningAnyCredentials() {
        when(authentication.getName()).thenReturn("player");
        ReflectionTestUtils.setField(controller,"pushEnabled",true);
        ReflectionTestUtils.setField(controller,"pushAccessToken","test-only");
        var result=controller.deliveryStatus(authentication);
        assertTrue(result.ready());
        assertEquals(1,result.getClass().getRecordComponents().length);
        assertEquals("ready",result.getClass().getRecordComponents()[0].getName());
        verify(authentication).getName();
    }
}
