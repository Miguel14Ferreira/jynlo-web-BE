package com.example.desporto24.service;

import com.example.desporto24.enumeration.RegistrationOpeningMode;
import com.example.desporto24.enumeration.SessionRecurrence;
import com.example.desporto24.model.dto.session.CreateSessionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommunitySessionScheduleTest {
    private static final ZoneId LISBON = ZoneId.of("Europe/Lisbon");
    private static final Instant NOW = Instant.parse("2027-03-20T12:00:00Z");
    private static final Instant FIRST = Instant.parse("2027-03-24T20:00:00Z");
    private static final Instant SECOND = Instant.parse("2027-03-26T20:00:00Z");

    private CreateSessionRequest request(int weeks, List<Instant> extra,
                                         RegistrationOpeningMode openingMode) {
        return new CreateSessionRequest("Futebol", FIRST, "Lisboa", "Rua do Campo",
                10, BigDecimal.ZERO, false, null, null, null, 7L, null,
                SessionRecurrence.WEEKLY, weeks, 0, openingMode,
                openingMode == RegistrationOpeningMode.SCHEDULED
                        ? Instant.parse("2027-03-23T12:00:00Z") : null,
                extra, LISBON.getId());
    }

    @Test
    void twoPerWeekOverFourWeeksCreatesEightOrderedIndependentOccurrences() {
        var plan = CommunitySessionSchedule.expand(
                request(4, List.of(SECOND), RegistrationOpeningMode.MANUAL), NOW);
        assertEquals(8, plan.size());
        assertEquals(FIRST, plan.get(0).kickoff());
        assertEquals(SECOND, plan.get(1).kickoff());
        assertEquals("20:00", plan.get(2).kickoff().atZone(LISBON).toLocalTime().toString());
        assertEquals("2027-03-31", plan.get(2).kickoff()
                .atZone(LISBON).toLocalDate().toString());
        assertTrue(plan.stream().allMatch(slot -> slot.registrationOpensAt() == null));
    }

    @Test
    void threePerWeekCreatesTwelveAndRetainsLocalClockAcrossDst() {
        var plan = CommunitySessionSchedule.expand(request(4, List.of(
                Instant.parse("2027-03-25T20:00:00Z"), SECOND),
                RegistrationOpeningMode.IMMEDIATE), NOW);
        assertEquals(12, plan.size());
        assertEquals("20:00", plan.get(3).kickoff().atZone(LISBON).toLocalTime().toString());
        assertEquals("20:00", plan.get(4).kickoff().atZone(LISBON).toLocalTime().toString());
        assertEquals("20:00", plan.get(5).kickoff().atZone(LISBON).toLocalTime().toString());
    }

    @Test
    void scheduledOpeningIsRelativeToEachGameAndRespectsLocalClockAfterDst() {
        var plan = CommunitySessionSchedule.expand(request(2, List.of(SECOND),
                RegistrationOpeningMode.SCHEDULED), NOW);
        assertEquals(4, plan.size());
        assertEquals("2027-03-23T12:00:00Z", plan.get(0).registrationOpensAt().toString());
        assertEquals("2027-03-25T12:00:00Z", plan.get(1).registrationOpensAt().toString());
        assertEquals("12:00", plan.get(2).registrationOpensAt()
                .atZone(LISBON).toLocalTime().toString());
        assertTrue(plan.stream().allMatch(slot ->
                slot.registrationOpensAt().isBefore(slot.kickoff())));
    }

    @Test
    void scheduledCallUpUsesRealLeadTimeWhenSpringGapSkipsOpeningHour() {
        // On 28 March 2027 Lisbon skips 01:00-01:59.
        // A weekly 02:15 game has a 01:45 call-up in the first week.
        // The following week's 01:45 does not exist; it must not be
        // shifted to 02:45, after the 02:15 kickoff.
        Instant first = Instant.parse("2027-03-21T02:15:00Z");
        Instant opening = Instant.parse("2027-03-21T01:45:00Z");
        var request = new CreateSessionRequest(
                "Futebol", first, "Lisboa", "Rua do Campo",
                10, BigDecimal.ZERO, false, null, null, null, 7L, null,
                SessionRecurrence.WEEKLY, 3, 0,
                RegistrationOpeningMode.SCHEDULED, opening,
                List.of(), LISBON.getId());

        var plan = CommunitySessionSchedule.expand(
                request, Instant.parse("2027-03-20T12:00:00Z"));

        assertEquals(3, plan.size());
        assertEquals(first, plan.get(0).kickoff());
        assertEquals(opening, plan.get(0).registrationOpensAt());
        assertEquals(Instant.parse("2027-03-28T01:15:00Z"), plan.get(1).kickoff());
        assertEquals(Instant.parse("2027-03-28T00:45:00Z"), plan.get(1).registrationOpensAt());
        assertEquals(Instant.parse("2027-04-04T01:15:00Z"), plan.get(2).kickoff());
        assertEquals(Instant.parse("2027-04-04T00:45:00Z"), plan.get(2).registrationOpensAt());
        assertTrue(plan.stream().allMatch(slot -> slot.registrationOpensAt().isBefore(slot.kickoff())));
    }

    @Test
    void scheduledCallUpBeforeTheRepeatedHourRemainsValidAfterAutumnClockRollback() {
        // In Lisbon the clock goes from 01:59 WEST back to 01:00 WET.
        // The opening at 00:45Z is 30 minutes before the second 01:15,
        // even though its local clock shows 01:45 (after 01:15).
        Instant first = Instant.parse("2027-10-31T01:15:00Z");
        Instant opening = Instant.parse("2027-10-31T00:45:00Z");
        var request = new CreateSessionRequest(
                "Futebol", first, "Lisboa", "Rua do Campo",
                10, BigDecimal.ZERO, false, null, null, null, 7L, null,
                SessionRecurrence.WEEKLY, 2, 0,
                RegistrationOpeningMode.SCHEDULED, opening,
                List.of(), LISBON.getId());

        var plan = CommunitySessionSchedule.expand(
                request, Instant.parse("2027-10-30T12:00:00Z"));

        assertEquals(2, plan.size());
        assertEquals(opening, plan.getFirst().registrationOpensAt());
        assertEquals(first, plan.getFirst().kickoff());
        assertEquals(Instant.parse("2027-11-07T00:45:00Z"),
                plan.get(1).registrationOpensAt());
        assertEquals(Instant.parse("2027-11-07T01:15:00Z"),
                plan.get(1).kickoff());
    }

    @Test
    void acceptsLegacyOnePerWeekRequestWithoutNewFields() {
        var old = new CreateSessionRequest("Futebol", FIRST, "Lisboa", "Campo 1",
                10, BigDecimal.ZERO, false, null, null, null, 7L, null,
                SessionRecurrence.WEEKLY, 4, 0,
                RegistrationOpeningMode.IMMEDIATE, null);
        assertEquals(4, CommunitySessionSchedule.expand(old, NOW).size());
    }

    @Test
    void rejectsOverLimitBeforeAnyDatabaseWrite() {
        var error = assertThrows(IllegalArgumentException.class, () ->
                CommunitySessionSchedule.expand(request(27, List.of(SECOND),
                        RegistrationOpeningMode.IMMEDIATE), NOW));
        assertTrue(error.getMessage().contains("52 sessões"));
    }

    @Test
    void rejectsAdditionalSlotOutsideFirstWeekAndDuplicates() {
        var afterWeek = FIRST.plusSeconds(8 * 24 * 3600);
        assertThrows(IllegalArgumentException.class, () ->
                CommunitySessionSchedule.expand(request(2, List.of(afterWeek),
                        RegistrationOpeningMode.MANUAL), NOW));
        assertThrows(IllegalArgumentException.class, () ->
                CommunitySessionSchedule.expand(request(2, List.of(SECOND, SECOND),
                        RegistrationOpeningMode.MANUAL), NOW));
    }

    @Test
    void rejectsNonWeeklyExtrasAndBadTimezone() {
        var weekly = request(2, List.of(SECOND), RegistrationOpeningMode.MANUAL);
        var biweekly = new CreateSessionRequest(
                weekly.sport(), weekly.gameDate(), weekly.city(), weekly.address(),
                weekly.maxParticipants(), weekly.priceAmount(), weekly.privateGame(),
                weekly.password(), weekly.latitude(), weekly.longitude(),
                weekly.communityId(), weekly.venueId(), SessionRecurrence.BIWEEKLY,
                weekly.recurrenceOccurrences(), weekly.externalConfirmedParticipants(),
                weekly.registrationOpeningMode(), weekly.registrationOpensAt(),
                weekly.additionalWeeklyGameDates(), weekly.recurrenceTimeZone());
        assertThrows(IllegalArgumentException.class,
                () -> CommunitySessionSchedule.expand(biweekly, NOW));
        var invalidZone = new CreateSessionRequest(
                weekly.sport(), weekly.gameDate(), weekly.city(), weekly.address(),
                weekly.maxParticipants(), weekly.priceAmount(), weekly.privateGame(),
                weekly.password(), weekly.latitude(), weekly.longitude(),
                weekly.communityId(), weekly.venueId(), SessionRecurrence.WEEKLY,
                weekly.recurrenceOccurrences(), weekly.externalConfirmedParticipants(),
                weekly.registrationOpeningMode(), weekly.registrationOpensAt(),
                weekly.additionalWeeklyGameDates(), "Invalid/Zone");
        assertThrows(IllegalArgumentException.class,
                () -> CommunitySessionSchedule.expand(invalidZone, NOW));
    }
}
