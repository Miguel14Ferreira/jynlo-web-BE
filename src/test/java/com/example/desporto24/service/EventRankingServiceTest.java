package com.example.desporto24.service;

import com.example.desporto24.model.community.CommunityTournamentRankingResult;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventRankingServiceTest {
    private static final List<Integer> POINTS = List.of(25, 18, 15, 12, 10);
    private static final Map<Long, String> NAMES = Map.of(1L, "Ana", 2L, "Rui", 3L, "Bea", 4L, "Tiago");

    private static CommunityTournamentRankingResult result(long participant, String value, Integer position, boolean dnf) {
        CommunityTournamentRankingResult r = new CommunityTournamentRankingResult();
        r.setParticipantId(participant);
        r.setValue(value == null ? null : new BigDecimal(value));
        r.setPosition(position);
        r.setDidNotFinish(dnf);
        return r;
    }

    @Test
    void timeLowestWinsAndTiesSharePlaceAndPoints() {
        var placed = EventRankingService.place(List.of(
                result(1, "62.40", null, false), result(2, "61.90", null, false),
                result(3, "62.40", null, false), result(4, null, null, true)), "TIME", POINTS, NAMES);
        assertEquals("Rui", placed.get(0).name());
        assertEquals(1, placed.get(0).place());
        assertEquals(2, placed.get(1).place());
        assertEquals(2, placed.get(2).place(), "equal times share 2nd place");
        assertEquals(18, placed.get(2).points());
        assertTrue(placed.get(3).didNotFinish());
        assertEquals(0, placed.get(3).points());
    }

    @Test
    void scoreHighestWins() {
        var placed = EventRankingService.place(List.of(
                result(1, "8.75", null, false), result(2, "9.10", null, false)), "SCORE", POINTS, NAMES);
        assertEquals("Rui", placed.getFirst().name());
        assertEquals(25, placed.getFirst().points());
    }

    @Test
    void positionsAreTakenAsEntered() {
        var placed = EventRankingService.place(List.of(
                result(1, null, 2, false), result(2, null, 1, false)), "POSITION", POINTS, NAMES);
        assertEquals(List.of("Rui", "Ana"), placed.stream().map(EventRankingService.Placed::name).toList());
    }

    @Test
    void tableSumsEventsAndBreaksTiesByWins() {
        var e1 = new EventRankingService.Event(1L, "Prova 1", LocalDate.now(), EventRankingService.place(List.of(
                result(1, null, 1, false), result(2, null, 2, false)), "POSITION", POINTS, NAMES));
        var e2 = new EventRankingService.Event(2L, "Prova 2", LocalDate.now(), EventRankingService.place(List.of(
                result(1, null, 2, false), result(2, null, 1, false), result(3, null, 3, false)), "POSITION", POINTS, NAMES));
        var table = EventRankingService.table(List.of(e1, e2), "POSITION", NAMES);
        assertEquals(43, table.get(0).points());
        assertEquals(1, table.get(0).position());
        assertEquals(1, table.get(1).position(), "equal points, wins and podiums share first place");
        assertEquals(3, table.get(2).position());
        assertEquals(15, table.get(2).points());
    }

    @Test
    void pointsMustBeNumbersThatNeverIncrease() {
        assertEquals(List.of(10, 6, 4), EventRankingService.parsePoints(" 10, 6,4 "));
        assertThrows(ResponseStatusException.class, () -> EventRankingService.parsePoints("10,12"));
        assertThrows(ResponseStatusException.class, () -> EventRankingService.parsePoints("dez"));
        assertTrue(EventRankingService.supports("Tiro com Arco"));
        assertFalse(EventRankingService.supports("Yoga"));
    }
}
