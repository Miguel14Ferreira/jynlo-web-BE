package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunitySeason;
import java.util.List;
import java.util.Optional;

/**
 * Picks the season a new season game counts for. A season for the game's
 * sport wins; otherwise a shared (all-sports) season. When the community only
 * has seasons for other sports, nothing is picked and the caller starts a
 * season for this sport, instead of failing with several active seasons.
 */
public final class CommunitySessionSeasonSelector {
    private CommunitySessionSeasonSelector() {}

    public static Optional<CommunitySeason> pick(List<CommunitySeason> activeSeasons, String sport) {
        if (activeSeasons == null || activeSeasons.isEmpty()) return Optional.empty();
        Optional<CommunitySeason> tagged = activeSeasons.stream()
                .filter(s -> s.getSport() != null && !s.getSport().isBlank()
                        && CommunityRoundSport.belongsToSeason(s.getSport(), sport))
                .findFirst();
        if (tagged.isPresent()) return tagged;
        return activeSeasons.stream()
                .filter(s -> s.getSport() == null || s.getSport().isBlank())
                .findFirst();
    }

    /** True when a new season for this sport must be tagged with it. */
    public static boolean needsSportSeason(List<CommunitySeason> activeSeasons) {
        return activeSeasons != null && !activeSeasons.isEmpty();
    }
}
