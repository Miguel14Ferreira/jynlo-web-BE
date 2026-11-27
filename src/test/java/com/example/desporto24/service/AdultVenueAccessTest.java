package com.example.desporto24.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AdultVenueAccessTest {

    @Test
    void classifiesAdultVenuesFromSportsAndType() {
        assertThat(AdultVenueAccess.detect("Snooker;Bilhar", "Salão de jogos")).isTrue();
        assertThat(AdultVenueAccess.detect("Dardos", null)).isTrue();
        assertThat(AdultVenueAccess.detect("Futebol", "Bar desportivo")).isTrue();
        assertThat(AdultVenueAccess.detect(null, "Pub")).isTrue();
        // Words that only contain "bar" or "pool" are not adult venues.
        assertThat(AdultVenueAccess.detect("Padel", "Barracão")).isFalse();
        assertThat(AdultVenueAccess.detect("Natação", "Piscina municipal")).isFalse();
        assertThat(AdultVenueAccess.detect("Futebol;Ténis", "Campo desportivo")).isFalse();
        assertThat(AdultVenueAccess.detect(null, null)).isFalse();
    }

    @Test
    void onlyAValidBirthDateAtLeast18YearsAgoIsAdult() {
        LocalDate today = LocalDate.of(2026, 11, 27);
        assertThat(AdultVenueAccess.isAdult("2008-11-27", today)).isTrue();
        assertThat(AdultVenueAccess.isAdult("2008-11-28", today)).isFalse();
        assertThat(AdultVenueAccess.isAdult("2013-05-01", today)).isFalse();
        assertThat(AdultVenueAccess.isAdult("", today)).isFalse();
        assertThat(AdultVenueAccess.isAdult(null, today)).isFalse();
        assertThat(AdultVenueAccess.isAdult("01/05/1990", today)).isFalse();
    }
}
