package com.example.desporto24.controllers;

import com.example.desporto24.repository.venue.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SitemapControllerTest {

    @Test
    void listsPublicPagesAndVenuesOnly() {
        VenueRepository venues = mock(VenueRepository.class);
        when(venues.findAllIdsForSitemap(PageRequest.of(0, SitemapController.MAX_VENUES))).thenReturn(List.of(3L, 7L));

        var response = new SitemapController(venues, "https://d24sports.com/").sitemap();
        String xml = response.getBody();

        assertTrue(xml.startsWith("<?xml"));
        assertTrue(xml.contains("<loc>https://d24sports.com/</loc>"));
        assertTrue(xml.contains("<loc>https://d24sports.com/privacy</loc>"));
        assertTrue(xml.contains("<loc>https://d24sports.com/venues/3</loc>"));
        assertTrue(xml.contains("<loc>https://d24sports.com/venues/7</loc>"));
        assertFalse(xml.contains("/menu/"));
        assertFalse(xml.contains("/sessions/"));
        assertTrue(xml.contains("<loc>https://d24sports.com/for-venues</loc>"));
        assertEquals(8, xml.split("<url>").length - 1);
        assertTrue(response.getHeaders().getCacheControl().contains("max-age=21600"));
    }

    @Test
    void prerenderListGivesPublicFactsOfEachVenue() {
        VenueRepository venues = mock(VenueRepository.class);
        com.example.desporto24.model.venue.Venue v = new com.example.desporto24.model.venue.Venue();
        v.setId(5L);
        v.setName("Clube Padel Lisboa");
        v.setCity("Lisboa");
        v.setSports("Padel");
        when(venues.findAllForPrerender(PageRequest.of(0, SitemapController.MAX_VENUES))).thenReturn(List.of(v));

        var list = new SitemapController(venues, "https://jynlosports.com").prerenderVenues().getBody();
        assertEquals(1, list.size());
        assertEquals("Clube Padel Lisboa", list.get(0).name());
        assertEquals("Padel", list.get(0).sports());
    }
}
