package com.example.desporto24.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/** Never allow arbitrary external links in an organiser-owned map destination. */
public final class SessionGoogleMapsLink {
    private SessionGoogleMapsLink() {}

    public static String normalize(String value) {
        if (value == null || value.isBlank()) return null;
        String url = value.trim();
        if (url.length() > 512 || url.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Link Google Maps inválido");
        }
        try {
            URI parsed = new URI(url);
            String host = parsed.getHost() == null ? "" : parsed.getHost().toLowerCase(Locale.ROOT);
            boolean googleMaps = (host.equals("www.google.com") || host.equals("google.com"))
                    && parsed.getPath() != null && parsed.getPath().startsWith("/maps");
            boolean shortMaps = host.equals("maps.app.goo.gl")
                    && parsed.getPath() != null && parsed.getPath().length() > 1;
            if (!"https".equalsIgnoreCase(parsed.getScheme()) || !(googleMaps || shortMaps)
                    || parsed.getUserInfo() != null || parsed.getPort() != -1) {
                throw new IllegalArgumentException("Link Google Maps inválido");
            }
            return url;
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Link Google Maps inválido", ex);
        }
    }
}
