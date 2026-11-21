package com.example.desporto24.service.profile;

import com.example.desporto24.constant.SportsCatalog;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * First steps after signing up: which sports the person plays and their city.
 * Changes only those two fields, so it never touches email, phone or security
 * settings the way the full profile form does.
 */
@Service
public class FirstStepsService {

    public static final int MAX_SPORTS = 10;
    public static final int MAX_LOCATION = 120;
    private static final int MAX_STORED = 255;

    public record FirstSteps(List<String> favoriteSports, String location, boolean needed) {
    }

    public record Update(List<String> favoriteSports, String location) {
    }

    private final ProfileRepository profiles;

    public FirstStepsService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Transactional(readOnly = true)
    public FirstSteps get(String username) {
        return view(load(username));
    }

    @Transactional
    public FirstSteps save(String username, Update update) {
        if (update == null || update.favoriteSports() == null) {
            throw bad("Escolhe pelo menos uma modalidade.");
        }
        LinkedHashSet<String> sports = new LinkedHashSet<>();
        for (String sport : update.favoriteSports()) {
            String value = sport == null ? "" : sport.trim();
            if (!value.isEmpty()) sports.add(value);
        }
        if (sports.isEmpty()) throw bad("Escolhe pelo menos uma modalidade.");
        if (sports.size() > MAX_SPORTS) throw bad("Escolhe no máximo " + MAX_SPORTS + " modalidades.");
        String joined = String.join(",", sports);
        if (joined.length() > MAX_STORED || !SportsCatalog.isValidSelection(joined)) {
            throw bad("Modalidade desconhecida.");
        }
        String location = update.location() == null ? "" : update.location().replaceAll("[\\p{Cntrl}]", " ").trim();
        if (location.length() > MAX_LOCATION) throw bad("A localidade é demasiado longa.");

        Profile profile = load(username);
        profile.setFavoriteSports(joined);
        // An empty city keeps whatever the person had before.
        if (!location.isEmpty()) profile.setLocation(location);
        return view(profiles.save(profile));
    }

    private Profile load(String username) {
        return profiles.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado."));
    }

    private static FirstSteps view(Profile profile) {
        String raw = profile.getFavoriteSports();
        List<String> sports = raw == null ? List.of() : Arrays.stream(raw.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        return new FirstSteps(sports, profile.getLocation(), sports.isEmpty());
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
