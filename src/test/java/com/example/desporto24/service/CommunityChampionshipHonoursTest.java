package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunityChampionship;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CommunityChampionshipHonoursTest {
    private CommunityChampionship season(long id, String kind, long winner, String label, String closed) {
        return CommunityChampionship.builder().id(id).name("Campeonato")
                .sport("Futebol").seasonLabel("2025/26")
                .championEntryType(kind).championParticipantId(winner)
                .championDisplayName(label)
                .closedAt(closed == null ? null : Instant.parse(closed)).build();
    }

    @Test
    void aggregatesRepeatIndividualTitlesByStableProfileId() {
        var result = CommunityChampionshipHonours.calculate(7L, List.of(
                season(1L, "INDIVIDUAL", 22L, "Ana", "2025-06-01T12:00:00Z"),
                season(2L, "INDIVIDUAL", 22L, "Ana Nova", "2026-06-01T12:00:00Z"),
                season(3L, "INDIVIDUAL", 33L, "Rui", "2026-08-01T12:00:00Z")));
        assertEquals(3, result.completedSeasons());
        assertEquals(2, result.individualHonours().size());
        assertEquals(2, result.individualHonours().get(0).titles());
        assertEquals(22L, result.individualHonours().get(0).profileId());
        assertEquals(3L, result.titles().get(0).championshipId());
    }

    @Test
    void neverMergesTeamsAcrossSeasonsByNameOrCoincidentIds() {
        var result = CommunityChampionshipHonours.calculate(7L, List.of(
                season(5L, "TEAM", 20L, "Águias", "2025-06-01T12:00:00Z"),
                season(6L, "TEAM", 20L, "Águias", "2026-06-01T12:00:00Z")));
        assertEquals(2, result.completedSeasons());
        assertEquals(2, result.titles().size());
        assertTrue(result.individualHonours().isEmpty());
    }

    @Test
    void countsPermanentTeamTitlesOnlyWhenExplicitlyLinkedAcrossSeasons() {
        var seasons = List.of(
                season(5L, "TEAM", 20L, "Águias A", "2025-06-01T12:00:00Z"),
                season(6L, "TEAM", 25L, "Águias B", "2026-06-01T12:00:00Z"),
                season(7L, "TEAM", 30L, "Águias", "2026-07-01T12:00:00Z"));
        var linked = java.util.Map.of(
                5L,new CommunityChampionshipHonours.PermanentTeam(90L,"Águias FC"),
                6L,new CommunityChampionshipHonours.PermanentTeam(90L,"Águias FC"));
        var result = CommunityChampionshipHonours.calculate(7L,seasons,linked);
        assertEquals(3,result.completedSeasons());
        assertEquals(1,result.teamHonours().size());
        assertEquals(90L,result.teamHonours().get(0).permanentTeamId());
        assertEquals(2,result.teamHonours().get(0).titles());
        assertEquals(2,result.titles().stream().filter(t -> t.permanentTeamId() != null).count());
        assertNull(result.titles().get(0).permanentTeamId());
    }

    @Test
    void countsLeagueAndCupTrophiesOnlyFromArchivedExplicitlyLinkedWinners() {
        var seasonOne = season(11L, "TEAM", 301L, "Águias A", "2025-06-01T12:00:00Z");
        var seasonTwo = season(12L, "TEAM", 302L, "Águias B", "2026-06-01T12:00:00Z");
        var permanent = new CommunityChampionshipHonours.PermanentTeam(90L, "Águias FC");
        var result = CommunityChampionshipHonours.calculate(7L,
                List.of(seasonOne, seasonTwo),
                java.util.Map.of(11L, permanent, 12L, permanent),
                java.util.Map.of(301L, permanent, 302L, permanent),
                java.util.Map.of(
                        11L, List.of(
                                new com.example.desporto24.model.dto.community.CommunityChampionshipTrophySnapshot(
                                        41L, "Liga", "LEAGUE", "TEAM", null, 301L, "Águias A"),
                                new com.example.desporto24.model.dto.community.CommunityChampionshipTrophySnapshot(
                                        42L, "Taça", "KNOCKOUT", "TEAM", null, 301L, "Águias A")),
                        12L, List.of(
                                new com.example.desporto24.model.dto.community.CommunityChampionshipTrophySnapshot(
                                        43L, "Taça", "GROUP_KNOCKOUT", "TEAM", null, 302L, "Águias B"),
                                new com.example.desporto24.model.dto.community.CommunityChampionshipTrophySnapshot(
                                        44L, "Taça sem associação", "KNOCKOUT", "TEAM", null, null, "Leões"))
                ));
        assertEquals(2, result.teamHonours().get(0).titles());
        assertEquals(1, result.teamHonours().get(0).leagues());
        assertEquals(2, result.teamHonours().get(0).cups());
        assertEquals(4, result.competitionTitles().size());
        assertEquals(1, result.competitionTitles().stream()
                .filter(t -> t.permanentTeamId() == null).count());
    }

    @Test
    void independentSeasonStillDisplaysLeagueAndCupTrophiesWithoutOverallChampion() {
        var independent = CommunityChampionship.builder().id(15L).name("Época 2026/27")
                .sport("Futebol").seasonLabel("2026/27").standingsMode("INDEPENDENT")
                .closedAt(Instant.parse("2026-11-11T12:00:00Z")).build();
        var permanent = new CommunityChampionshipHonours.PermanentTeam(99L, "Águias FC");
        var result = CommunityChampionshipHonours.calculate(7L,
                List.of(independent), java.util.Map.of(), java.util.Map.of(30L, permanent),
                java.util.Map.of(15L, List.of(
                        new com.example.desporto24.model.dto.community.CommunityChampionshipTrophySnapshot(
                                null, "Liga", "LEAGUE", "TEAM", null, 30L, "Águias", 100L),
                        new com.example.desporto24.model.dto.community.CommunityChampionshipTrophySnapshot(
                                null, "Taça", "KNOCKOUT", "TEAM", null, 30L, "Águias", 101L))));
        assertTrue(result.titles().isEmpty());
        assertEquals(1, result.completedSeasons());
        assertEquals(2, result.competitionTitles().size());
        assertEquals(1, result.teamHonours().get(0).leagues());
        assertEquals(1, result.teamHonours().get(0).cups());
        assertEquals(100L, result.competitionTitles().get(0).ownedEventId());
        assertNull(result.competitionTitles().get(0).tournamentId());
    }

    @Test
    void ignoresUnfinishedAndInvalidChampionSnapshots() {
        var result = CommunityChampionshipHonours.calculate(7L, List.of(
                season(1L, "TEAM", 11L, "Águias", null),
                season(2L, "UNKNOWN", 10L, "Desconhecido", "2026-06-01T12:00:00Z"),
                season(3L, "INDIVIDUAL", 12L, "Ana", "2026-06-01T12:00:00Z")));
        assertEquals(1, result.completedSeasons());
        assertEquals("Ana", result.titles().get(0).championName());
    }
}
