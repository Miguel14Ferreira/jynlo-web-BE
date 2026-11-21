package com.example.desporto24.service.nearby;

import java.util.List;

/**
 * A person's choice for "new game near you" alerts. The position is a place
 * they picked (or their approximate position, rounded to about 1 km), never a
 * live location. Sports empty = every sport.
 */
public record NearbyAlertPreference(
        boolean enabled,
        Double latitude,
        Double longitude,
        String placeLabel,
        int radiusKm,
        List<String> sports) {

    public static final List<Integer> RADII_KM = List.of(10, 25, 50, 100);
    public static final int DEFAULT_RADIUS_KM = 25;

    public static NearbyAlertPreference off() {
        return new NearbyAlertPreference(false, null, null, null, DEFAULT_RADIUS_KM, List.of());
    }
}
