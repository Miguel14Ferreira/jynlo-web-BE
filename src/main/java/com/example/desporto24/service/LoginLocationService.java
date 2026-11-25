package com.example.desporto24.service;

import com.maxmind.geoip2.DatabaseReader;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Approximate place of each sign-in (country + city), kept 90 days to help spot
 * fake accounts. The IP is only used for the local lookup and is never stored.
 * Location data: DB-IP Lite (CC BY 4.0). Without the database file this is a no-op.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLocationService {
    public record Place(String country, String city, Instant at) {}

    private final JdbcTemplate jdbc;
    @Value("${jynlo.geoip.path:/app/geo/dbip-city-lite.mmdb}")
    private String databasePath;
    private DatabaseReader reader;

    @PostConstruct
    void open() {
        try {
            File file = new File(databasePath);
            if (file.isFile()) reader = new DatabaseReader.Builder(file).build();
            else log.info("Login location disabled: no GeoIP database at {}", databasePath);
        } catch (Exception e) {
            log.warn("Login location disabled: GeoIP database could not be opened");
        }
    }

    /** Client address as seen behind Cloudflare/Render; the rightmost forwarded hop is the one Render added. */
    static String clientAddress(HttpServletRequest request) {
        String cf = request.getHeader("CF-Connecting-IP");
        if (cf != null && !cf.isBlank()) return cf.trim();
        String trueClient = request.getHeader("True-Client-IP");
        if (trueClient != null && !trueClient.isBlank()) return trueClient.trim();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            String[] hops = forwarded.split(",");
            return hops[hops.length - 1].trim();
        }
        return request.getRemoteAddr();
    }

    /** Never breaks a sign-in: any failure just skips the record. */
    public void recordCurrentLogin(Long profileId) {
        if (reader == null || profileId == null) return;
        try {
            var attributes = RequestContextHolder.getRequestAttributes();
            if (!(attributes instanceof ServletRequestAttributes servlet)) return;
            String ip = clientAddress(servlet.getRequest());
            if (ip == null || ip.isBlank()) return;
            var city = reader.city(InetAddress.getByName(ip));
            String country = city.getCountry() == null ? null : city.getCountry().getIsoCode();
            String name = city.getCity() == null ? null : city.getCity().getName();
            jdbc.update("insert into login_location(profile_id, country, city, created_at) values (?, ?, ?, ?)",
                    profileId, country, name == null ? null : name.substring(0, Math.min(120, name.length())),
                    Timestamp.from(Instant.now()));
            jdbc.update("delete from login_location where created_at < ?", Timestamp.from(Instant.now().minus(90, ChronoUnit.DAYS)));
        } catch (Exception ignored) {
            // Private, reserved or unknown addresses have no location.
        }
    }

    public Place latest(Long profileId) {
        List<Place> rows = jdbc.query("""
                select country, city, created_at from login_location where profile_id = ? order by created_at desc limit 1
                """, (rs, i) -> new Place(rs.getString(1), rs.getString(2), rs.getTimestamp(3).toInstant()), profileId);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
