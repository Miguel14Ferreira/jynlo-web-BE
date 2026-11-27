package com.example.desporto24.service;

import com.example.desporto24.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Venues meant for adults (snooker, billiards, darts, bars, pubs…) are only
 * shown to signed-in accounts aged 18 or over. Visitors without an account, or
 * an account without a valid adult birth date, never receive them from the API.
 */
@Component
@RequiredArgsConstructor
public class AdultVenueAccess {

    private static final Pattern ADULT_SPORTS =
            Pattern.compile("(?iU)\\b(snooker|bilhar|dardos|darts|pool|poker)\\b");
    private static final Pattern ADULT_VENUE_TYPES =
            Pattern.compile("(?iU)\\b(bar|pub|discoteca|casino|taberna|cervejaria)\\b");

    private final ProfileRepository profileRepository;

    /** Automatic classification from the sports and the venue type. */
    public static boolean detect(String sports, String venueType) {
        return (sports != null && ADULT_SPORTS.matcher(sports).find())
                || (venueType != null && ADULT_VENUE_TYPES.matcher(venueType).find());
    }

    /** True only for an authenticated account whose stored birth date is 18+ years ago. */
    public boolean viewerIsAdult() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getName() == null) {
            return false;
        }
        return profileRepository.findUserByUsername(authentication.getName())
                .map(profile -> isAdult(profile.getDateOfBirth(), LocalDate.now()))
                .orElse(false);
    }

    public static boolean isAdult(String dateOfBirth, LocalDate today) {
        if (dateOfBirth == null || dateOfBirth.isBlank()) return false;
        try {
            return !LocalDate.parse(dateOfBirth.trim()).isAfter(today.minusYears(18));
        } catch (DateTimeException exception) {
            return false;
        }
    }
}
