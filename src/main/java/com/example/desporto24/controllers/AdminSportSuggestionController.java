package com.example.desporto24.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/** Review queue only: approval to the official rules catalog remains a code change. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/sport-suggestions")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminSportSuggestionController {
    private final JdbcTemplate jdbc;

    public record Suggestion(long id, String name, int usageCount, String status,
                             OffsetDateTime firstRequestedAt, OffsetDateTime lastRequestedAt) {}

    @GetMapping
    public List<Suggestion> list(@RequestParam(defaultValue = "PENDING") String status) {
        if (!List.of("PENDING", "REVIEWED", "REJECTED", "ALL").contains(status)) {
            throw new IllegalArgumentException("Estado inválido");
        }
        String sql = """
                SELECT id, name, usage_count, status, first_requested_at, last_requested_at
                FROM custom_sport_suggestion
                """ + ("ALL".equals(status) ? "" : " WHERE status = ?") +
                " ORDER BY usage_count DESC, last_requested_at DESC LIMIT 200";
        org.springframework.jdbc.core.RowMapper<Suggestion> mapper = (rs, row) -> new Suggestion(
                rs.getLong("id"), rs.getString("name"), rs.getInt("usage_count"),
                rs.getString("status"), rs.getObject("first_requested_at", OffsetDateTime.class),
                rs.getObject("last_requested_at", OffsetDateTime.class));
        return "ALL".equals(status) ? jdbc.query(sql, mapper) : jdbc.query(sql, mapper, status);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> review(@PathVariable long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        if (!"REVIEWED".equals(status) && !"REJECTED".equals(status)
                && !"PENDING".equals(status)) {
            throw new IllegalArgumentException("Estado inválido");
        }
        int updated = jdbc.update("UPDATE custom_sport_suggestion SET status = ? WHERE id = ?",
                status, id);
        return updated == 0 ? ResponseEntity.notFound().build() : ResponseEntity.noContent().build();
    }
}
