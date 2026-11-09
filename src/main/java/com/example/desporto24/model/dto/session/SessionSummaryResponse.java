package com.example.desporto24.model.dto.session;

import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.enumeration.SessionAudience;

import java.math.BigDecimal;
import java.time.Instant;

public record SessionSummaryResponse(
        Long id,
        String sport,
        String city,
        Instant gameDate,
        Double distanceKm,
        int participantCount,
        int maxParticipants,
        BigDecimal priceAmount,
        boolean privateGame,
        SessionStatus status,
        SessionAudience audience,
        boolean joinedByViewer,
        boolean waitlistedByViewer,
        String recurrenceSeriesId,
        Long communityId,
        Long seasonCommunityId,
        com.example.desporto24.enumeration.AirsoftGameType airsoftGameType,
        Double latitude,
        Double longitude
) {
    /** Original full projection is retained for existing service and test consumers. */
    public SessionSummaryResponse(
            Long id, String sport, String city, Instant gameDate, Double distanceKm,
            int participantCount, int maxParticipants, BigDecimal priceAmount,
            boolean privateGame, SessionStatus status, SessionAudience audience,
            boolean joinedByViewer, boolean waitlistedByViewer, String recurrenceSeriesId,
            Long communityId, Long seasonCommunityId,
            com.example.desporto24.enumeration.AirsoftGameType airsoftGameType) {
        this(id, sport, city, gameDate, distanceKm, participantCount, maxParticipants,
                priceAmount, privateGame, status, audience, joinedByViewer,
                waitlistedByViewer, recurrenceSeriesId, communityId, seasonCommunityId,
                airsoftGameType, null, null);
    }

    /** Only discovery may attach coarse map coordinates, never exact GPS. */
    public SessionSummaryResponse withPublicMapPosition(Double publicLatitude, Double publicLongitude) {
        return new SessionSummaryResponse(id, sport, city, gameDate, distanceKm,
                participantCount, maxParticipants, priceAmount, privateGame, status,
                audience, joinedByViewer, waitlistedByViewer, recurrenceSeriesId,
                communityId, seasonCommunityId, airsoftGameType, publicLatitude, publicLongitude);
    }

    /** Backwards-compatible constructor for existing summary projections. */
    public SessionSummaryResponse(
            Long id,
            String sport,
            String city,
            Instant gameDate,
            Double distanceKm,
            int participantCount,
            int maxParticipants,
            BigDecimal priceAmount,
            boolean privateGame,
            SessionStatus status,
            SessionAudience audience,
            boolean joinedByViewer,
            boolean waitlistedByViewer,
            String recurrenceSeriesId,
            Long communityId,
            Long seasonCommunityId
    ) {
        this(id, sport, city, gameDate, distanceKm, participantCount, maxParticipants, priceAmount, privateGame, status, audience, joinedByViewer, waitlistedByViewer, recurrenceSeriesId, communityId, seasonCommunityId, null);
    }

    // Preserve existing response constructors.
    public SessionSummaryResponse(
            Long id, String sport, String city, Instant gameDate, Double distanceKm,
            int participantCount, int maxParticipants, BigDecimal priceAmount,
            boolean privateGame, SessionStatus status, SessionAudience audience,
            boolean joinedByViewer, boolean waitlistedByViewer,
            String recurrenceSeriesId, Long communityId) {
        this(id, sport, city, gameDate, distanceKm, participantCount, maxParticipants,
                priceAmount, privateGame, status, audience, joinedByViewer,
                waitlistedByViewer, recurrenceSeriesId, communityId, null);
    }
    // Preserve older 13-field constructors from services and tests.
    public SessionSummaryResponse(
            Long id, String sport, String city, Instant gameDate,
            Double distanceKm, int participantCount, int maxParticipants,
            BigDecimal priceAmount, boolean privateGame, SessionStatus status,
            SessionAudience audience, boolean joinedByViewer,
            boolean waitlistedByViewer, String recurrenceSeriesId) {
        this(id, sport, city, gameDate, distanceKm, participantCount, maxParticipants,
             priceAmount, privateGame, status, audience, joinedByViewer,
             waitlistedByViewer, recurrenceSeriesId, null);
    }
    public SessionSummaryResponse(
            Long id,
            String sport,
            String city,
            Instant gameDate,
            Double distanceKm,
            int participantCount,
            int maxParticipants,
            BigDecimal priceAmount,
            boolean privateGame,
            SessionStatus status,
            SessionAudience audience) {
        this(
                id,
                sport,
                city,
                gameDate,
                distanceKm,
                participantCount,
                maxParticipants,
                priceAmount,
                privateGame,
                status,
                audience,
                false,
                false,
                null
        );
    }

    public SessionSummaryResponse(
            Long id,
            String sport,
            String city,
            Instant gameDate,
            Double distanceKm,
            int participantCount,
            int maxParticipants,
            BigDecimal priceAmount,
            boolean privateGame,
            SessionStatus status) {
        this(
                id,
                sport,
                city,
                gameDate,
                distanceKm,
                participantCount,
                maxParticipants,
                priceAmount,
                privateGame,
                status,
                SessionAudience.PUBLIC,
                false,
                false,
                null
        );
    }
}
