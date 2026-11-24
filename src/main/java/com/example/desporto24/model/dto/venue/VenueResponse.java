package com.example.desporto24.model.dto.venue;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;

public record VenueResponse(
        Long id,
        String name,
        String city,
        String address,
        String sports,
        String venueType,
        String ownershipType,
        String surfaceType,
        String environmentType,
        boolean indoor,
        boolean changingRooms,
        String changingRoomTypes,
        Boolean toilets,
        Boolean lockers,
        Boolean showers,
        Boolean stands,
        Boolean drinkingFountain,
        Boolean accessible,
        Boolean firstAid,
        Boolean aed,
        Boolean medicalStation,
        Boolean lifeguard,
        Boolean staffOnSite,
        Boolean securitySurveillance,
        Boolean cafe,
        Boolean restaurant,
        Boolean vendingMachines,
        Boolean sportsShop,
        Boolean wifi,
        Boolean phoneCharging,
        Integer nearbyRadiusMeters,
        Boolean nearbyPharmacy,
        Boolean nearbyAtm,
        String services,
        String transport,
        String openingHours,
        String priceInfo,
        String website,
        String contact,
        boolean parking,
        Boolean freeParking,
        boolean lighting,
        Double latitude,
        Double longitude,
        String informationSource,
        String createdByUsername,
        Instant createdAt,
        String district,
        String bookingUrl,
        Boolean freeAccess,
        @com.fasterxml.jackson.annotation.JsonIgnore String sourceReference
) {
    /** Who to credit for imported open data (licences require attribution). Null for community entries. */
    @JsonProperty("dataCredit")
    public String dataCredit() {
        String ref = sourceReference == null ? "" : sourceReference;
        if (ref.startsWith("SNID:")) return "IPDJ – Sistema Nacional de Informação Desportiva (CC BY 4.0)";
        if (ref.startsWith("CML:")) return "Câmara Municipal de Lisboa – dados abertos";
        if (ref.startsWith("CMC:")) return "Câmara Municipal de Cascais – dados abertos (CC BY)";
        if ("OPENSTREETMAP".equals(informationSource)) return "© OpenStreetMap contributors (ODbL)";
        return null;
    }

    /** Additional read-only location information, never proof of public access. */
    @JsonProperty("locationStatus")
    public String locationStatus() {
        return VenueLocationStatus.from(address, latitude, longitude);
    }

    /** Previous canonical signature (before booking link and free access). */
    public VenueResponse(Long id, String name, String city, String address, String sports, String venueType, String ownershipType, String surfaceType, String environmentType, boolean indoor, boolean changingRooms, String changingRoomTypes, Boolean toilets, Boolean lockers, Boolean showers, Boolean stands, Boolean drinkingFountain, Boolean accessible, Boolean firstAid, Boolean aed, Boolean medicalStation, Boolean lifeguard, Boolean staffOnSite, Boolean securitySurveillance, Boolean cafe, Boolean restaurant, Boolean vendingMachines, Boolean sportsShop, Boolean wifi, Boolean phoneCharging, Integer nearbyRadiusMeters, Boolean nearbyPharmacy, Boolean nearbyAtm, String services, String transport, String openingHours, String priceInfo, String website, String contact, boolean parking, Boolean freeParking, boolean lighting, Double latitude, Double longitude, String informationSource, String createdByUsername, Instant createdAt, String district) {
        this(id, name, city, address, sports, venueType, ownershipType, surfaceType, environmentType, indoor, changingRooms, changingRoomTypes, toilets, lockers, showers, stands, drinkingFountain, accessible, firstAid, aed, medicalStation, lifeguard, staffOnSite, securitySurveillance, cafe, restaurant, vendingMachines, sportsShop, wifi, phoneCharging, nearbyRadiusMeters, nearbyPharmacy, nearbyAtm, services, transport, openingHours, priceInfo, website, contact, parking, freeParking, lighting, latitude, longitude, informationSource, createdByUsername, createdAt, district, null, null, null);
    }

    // Backwards-compatible constructor for existing Java tests/callers that
    // do not yet supply district; public API now exposes the real DB value.
    public VenueResponse(
            Long id,
            String name,
            String city,
            String address,
            String sports,
            String venueType,
            String ownershipType,
            String surfaceType,
            String environmentType,
            boolean indoor,
            boolean changingRooms,
            String changingRoomTypes,
            Boolean toilets,
            Boolean lockers,
            Boolean showers,
            Boolean stands,
            Boolean drinkingFountain,
            Boolean accessible,
            Boolean firstAid,
            Boolean aed,
            Boolean medicalStation,
            Boolean lifeguard,
            Boolean staffOnSite,
            Boolean securitySurveillance,
            Boolean cafe,
            Boolean restaurant,
            Boolean vendingMachines,
            Boolean sportsShop,
            Boolean wifi,
            Boolean phoneCharging,
            Integer nearbyRadiusMeters,
            Boolean nearbyPharmacy,
            Boolean nearbyAtm,
            String services,
            String transport,
            String openingHours,
            String priceInfo,
            String website,
            String contact,
            boolean parking,
            Boolean freeParking,
            boolean lighting,
            Double latitude,
            Double longitude,
            String informationSource,
            String createdByUsername,
            Instant createdAt
    ) {
        this(id, name, city, address, sports, venueType, ownershipType, surfaceType, environmentType, indoor, changingRooms, changingRoomTypes, toilets, lockers, showers, stands, drinkingFountain, accessible, firstAid, aed, medicalStation, lifeguard, staffOnSite, securitySurveillance, cafe, restaurant, vendingMachines, sportsShop, wifi, phoneCharging, nearbyRadiusMeters, nearbyPharmacy, nearbyAtm, services, transport, openingHours, priceInfo, website, contact, parking, freeParking, lighting, latitude, longitude, informationSource, createdByUsername, createdAt, null);
    }
}
