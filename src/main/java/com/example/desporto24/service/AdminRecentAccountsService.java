package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Recent sign-ups with simple warning signs, so an administrator can spot
 * fake or automated accounts early. The signs are hints, never automatic bans.
 */
@Service
@RequiredArgsConstructor
public class AdminRecentAccountsService {
    /** A name followed by 4+ digits, e.g. freddieobrien.72965@gmail.com. */
    private static final Pattern GENERATED_EMAIL = Pattern.compile("^[a-z]+[._-]?[a-z]*[._-]?\\d{4,}@.*$");
    private static final int MAX = 200;
    private final ProfileRepository profiles;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private LoginLocationService loginLocations;

    public record RecentAccount(Long id, String username, String fullName, String email, Instant joinedAt,
                                Instant lastLoginAt, boolean emailConfirmed, boolean hasPhoto,
                                Instant suspendedAt, boolean locked, List<String> flags,
                                String loginCountry, String loginCity) {}

    @Transactional(readOnly = true)
    public List<RecentAccount> recent(int days) {
        int window = Math.max(1, Math.min(days, 60));
        Date since = Date.from(Instant.now().minus(window, ChronoUnit.DAYS));
        List<Profile> list = profiles.findByJoinDateAfterOrderByJoinDateDesc(since, PageRequest.of(0, MAX));
        List<RecentAccount> out = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Profile p = list.get(i);
            Instant joined = p.getJoinDate() == null ? null : p.getJoinDate().toInstant();
            List<String> flags = new ArrayList<>();
            String email = p.getEmail() == null ? "" : p.getEmail().trim().toLowerCase(Locale.ROOT);
            if (GENERATED_EMAIL.matcher(email).matches()) flags.add("EMAIL_PADRAO_ALEATORIO");
            if (!Boolean.TRUE.equals(p.getEnabled())) flags.add("EMAIL_NAO_CONFIRMADO");
            if (p.getProfilePicUrl() == null || p.getProfilePicUrl().isBlank()) flags.add("SEM_FOTO");
            if (p.getLastLoginDate() == null) flags.add("NUNCA_ENTROU");
            if (joined != null && burst(list, joined)) flags.add("VARIOS_REGISTOS_SEGUIDOS");
            LoginLocationService.Place place = loginLocations == null ? null : loginLocations.latest(p.getId());
            if (place != null && place.country() != null && !"PT".equals(place.country())) flags.add("ENTRA_DE_FORA_DE_PORTUGAL");
            out.add(new RecentAccount(p.getId(), p.getUsername(), p.getFullName(), p.getEmail(), joined,
                    p.getLastLoginDate() == null ? null : p.getLastLoginDate().toInstant(),
                    Boolean.TRUE.equals(p.getEnabled()), !flags.contains("SEM_FOTO"),
                    p.getModerationSuspendedAt(), Boolean.FALSE.equals(p.getNotLocked()), List.copyOf(flags),
                    place == null ? null : place.country(), place == null ? null : place.city()));
        }
        return out;
    }

    /** Three or more sign-ups within ten minutes of this one. */
    private static boolean burst(List<Profile> list, Instant joined) {
        int near = 0;
        for (Profile other : list) {
            if (other.getJoinDate() == null) continue;
            long gap = Math.abs(ChronoUnit.SECONDS.between(joined, other.getJoinDate().toInstant()));
            if (gap <= 600) near++;
        }
        return near >= 3;
    }
}
