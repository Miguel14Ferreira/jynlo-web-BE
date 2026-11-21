package com.example.desporto24.service.nearby;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Plain SQL for the alert preferences and the record of alerts sent. */
@Repository
public class NearbyAlertStore {

    public record Candidate(long profileId, double latitude, double longitude, int radiusKm, List<String> sports) {}

    private final JdbcTemplate jdbc;

    public NearbyAlertStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<NearbyAlertPreference> find(long profileId) {
        return jdbc.query("""
                select enabled, latitude, longitude, place_label, radius_km, sports
                from nearby_session_alert_preference where profile_id = ?
                """, (rs, row) -> new NearbyAlertPreference(
                        rs.getBoolean(1),
                        (Double) rs.getObject(2),
                        (Double) rs.getObject(3),
                        rs.getString(4),
                        rs.getInt(5),
                        splitSports(rs.getString(6))), profileId).stream().findFirst();
    }

    public void save(long profileId, NearbyAlertPreference preference, Instant now) {
        jdbc.update("""
                insert into nearby_session_alert_preference
                  (profile_id, enabled, latitude, longitude, place_label, radius_km, sports, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (profile_id) do update set
                  enabled = excluded.enabled, latitude = excluded.latitude, longitude = excluded.longitude,
                  place_label = excluded.place_label, radius_km = excluded.radius_km,
                  sports = excluded.sports, updated_at = excluded.updated_at
                """, profileId, preference.enabled(), preference.latitude(), preference.longitude(),
                preference.placeLabel(), preference.radiusKm(), String.join(",", preference.sports()),
                Timestamp.from(now));
    }

    /** Enabled preferences whose position lies in the given box (a first, coarse filter). */
    public List<Candidate> candidatesInBox(double minLat, double maxLat, double minLng, double maxLng) {
        return jdbc.query("""
                select profile_id, latitude, longitude, radius_km, sports
                from nearby_session_alert_preference
                where enabled = true
                  and latitude between ? and ? and longitude between ? and ?
                limit 5000
                """, (rs, row) -> new Candidate(rs.getLong(1), rs.getDouble(2), rs.getDouble(3),
                        rs.getInt(4), splitSports(rs.getString(5))),
                minLat, maxLat, minLng, maxLng);
    }

    public int sentSince(long profileId, Instant since) {
        Integer count = jdbc.queryForObject(
                "select count(*) from nearby_session_alert_delivery where profile_id = ? and sent_at > ?",
                Integer.class, profileId, Timestamp.from(since));
        return count == null ? 0 : count;
    }

    /** @return true when this is the first alert for this person and session. */
    public boolean recordDelivery(long profileId, long sessionId, Instant now) {
        return jdbc.update("""
                insert into nearby_session_alert_delivery (profile_id, session_id, sent_at)
                values (?, ?, ?) on conflict do nothing
                """, profileId, sessionId, Timestamp.from(now)) == 1;
    }

    public void deleteFor(long profileId) {
        jdbc.update("delete from nearby_session_alert_delivery where profile_id = ?", profileId);
        jdbc.update("delete from nearby_session_alert_preference where profile_id = ?", profileId);
    }

    static List<String> splitSports(String value) {
        if (value == null || value.isBlank()) return List.of();
        return Arrays.stream(value.split(",")).map(String::strip).filter(s -> !s.isEmpty()).toList();
    }
}
