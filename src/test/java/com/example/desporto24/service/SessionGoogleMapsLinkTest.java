package com.example.desporto24.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionGoogleMapsLinkTest {
    @Test
    void acceptsExactSharedGoogleMapsDestinations() {
        String shortLink = "https://maps.app.goo.gl/2Qy87Din3ZYJz8VZA";
        String searchLink = "https://www.google.com/maps/search/?api=1&query=Avenida%20Professor%20An%C3%ADbal%20de%20Bettencourt%2C%20Campo%20Grande";
        assertEquals(shortLink, SessionGoogleMapsLink.normalize(shortLink));
        assertEquals(searchLink, SessionGoogleMapsLink.normalize(searchLink));
        assertNull(SessionGoogleMapsLink.normalize(null));
        assertNull(SessionGoogleMapsLink.normalize("  "));
    }

    @Test
    void rejectsRedirectsAndUntrustedSchemesOrDomains() {
        String[] malicious = {
            "http://maps.app.goo.gl/abcd", "https://maps.app.goo.gl.evil.example/abcd",
            "https://www.google.com.evil.example/maps", "javascript:alert(1)",
            "https://example.com/maps", "https://user@maps.app.goo.gl/path",
            "https://maps.app.goo.gl:8443/abcd", "https://www.google.com/search?q=test",
            "https://maps.app.goo.gl/", "https://www.google.com/maps/\nother"
        };
        for (String link : malicious) {
            assertThrows(IllegalArgumentException.class, () -> SessionGoogleMapsLink.normalize(link), link);
        }
    }
}
