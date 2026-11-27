package com.example.desporto24.controllers;

import com.example.desporto24.repository.venue.VenueRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

/**
 * Public sitemap for search engines: the public pages plus every venue page.
 * Venues are the only content public by default; games, profiles and
 * communities stay out (they need sign-in or are not meant to be indexed).
 */
@RestController
public class SitemapController {

    /** The sitemap format allows 50 000 addresses per file. */
    static final int MAX_VENUES = 49_000;
    private static final List<String[]> STATIC_PAGES = List.of(
            new String[]{"/", "weekly", "1.0"},
            new String[]{"/for-venues", "monthly", "0.7"},
            new String[]{"/about", "monthly", "0.6"},
            new String[]{"/contact", "yearly", "0.4"},
            new String[]{"/privacy", "yearly", "0.3"},
            new String[]{"/terms", "yearly", "0.3"});

    private final VenueRepository venues;
    private final String siteUrl;

    public SitemapController(VenueRepository venues,
                             @Value("${desporto.public-site-url:https://jynlosports.com}") String siteUrl) {
        this.venues = venues;
        this.siteUrl = siteUrl.replaceAll("/+$", "");
    }

    @GetMapping(value = "/api/public/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        StringBuilder xml = new StringBuilder(64 * 1024)
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String[] page : STATIC_PAGES) {
            url(xml, siteUrl + page[0], page[1], page[2]);
        }
        for (Long id : venues.findAllIdsForSitemap(PageRequest.of(0, MAX_VENUES))) {
            url(xml, siteUrl + "/venues/" + id, "monthly", "0.5");
        }
        xml.append("</urlset>\n");
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(6)).cachePublic())
                .body(xml.toString());
    }

    /** What a pre-built venue page shows: public facts only (no contacts of people, no managers). */
    public record PrerenderVenue(Long id, String name, String city, String district, String address, String sports,
                                 String venueType, String surfaceType, String environment, Double latitude, Double longitude,
                                 String openingHours, String priceInfo, boolean parking, boolean lighting, String bookingUrl) {}

    /** Read at build time by the website to write one ready-made page per venue for search engines. */
    @GetMapping("/api/public/venues/prerender")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<PrerenderVenue>> prerenderVenues() {
        List<PrerenderVenue> list = venues.findAllForPrerender(PageRequest.of(0, MAX_VENUES)).stream()
                .map(v -> new PrerenderVenue(v.getId(), v.getName(), v.getCity(), v.getDistrict(), v.getAddress(), v.getSports(),
                        v.getVenueType(), v.getSurfaceType(), v.getEnvironmentType() == null ? null : v.getEnvironmentType().name(),
                        v.getLatitude(), v.getLongitude(), v.getOpeningHours(), v.getPriceInfo(), v.isParking(), v.isLighting(),
                        v.getBookingUrl()))
                .toList();
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic()).body(list);
    }

    private static void url(StringBuilder xml, String loc, String changefreq, String priority) {
        xml.append("  <url><loc>").append(loc).append("</loc><changefreq>").append(changefreq)
                .append("</changefreq><priority>").append(priority).append("</priority></url>\n");
    }
}
