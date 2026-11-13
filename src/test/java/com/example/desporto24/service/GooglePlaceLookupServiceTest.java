package com.example.desporto24.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class GooglePlaceLookupServiceTest {
    @Test void withoutServerKeyProviderIsUnavailableWithoutGuessingLocations() {
        GooglePlaceLookupService service=new GooglePlaceLookupService();
        ReflectionTestUtils.setField(service,"apiKey","");
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,
            ()->service.suggest("Cacém","a-valid-session"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,exception.getStatusCode());
    }
    @Test void placeIdentifiersAreValidatedBeforeProviderRequest() {
        GooglePlaceLookupService service=new GooglePlaceLookupService();
        ReflectionTestUtils.setField(service,"apiKey","test-value");
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,
            ()->service.resolve("../admin","valid-session"));
        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatusCode());
    }
}
