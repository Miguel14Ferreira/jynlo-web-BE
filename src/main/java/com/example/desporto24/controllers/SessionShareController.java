package com.example.desporto24.controllers;

import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.exception.SessionNotFound;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.dto.session.SessionPublicPreviewResponse;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.service.interfaces.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Link-preview pages for sessions, communities and profiles.
 *
 * WhatsApp, Instagram, Facebook and similar crawlers do not run JavaScript, so
 * they cannot read the Open Graph tags that the single-page app sets after it
 * loads. The frontend's nginx sends only those crawlers here; people always get
 * the normal app.
 *
 * It exposes nothing new: the data comes from {@link SessionService#publicPreview},
 * which already refuses private, community-only and disabled sessions. A session
 * that cannot be previewed gets the generic Jynlo card, so the response never
 * reveals whether a private session exists.
 */
@RestController
@RequestMapping("/api/share")
public class SessionShareController {

    /** Test hook: the repository is optional so older constructor-based tests keep working. */
    void setCommunities(CommunityRepository communities) {
        this.communities = communities;
    }

    private static final ZoneId LISBON = ZoneId.of("Europe/Lisbon");
    private static final DateTimeFormatter WHEN =
            DateTimeFormatter.ofPattern("EEE, d 'de' MMM 'às' HH:mm", Locale.forLanguageTag("pt-PT"));
    private static final String GENERIC_TITLE = "Jynlo — Join the game.";
    private static final String GENERIC_DESCRIPTION = "Jynlo — encontra onde, quando e com quem jogar.";

    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9._-]{1,40}");
    private static final int DESCRIPTION_LIMIT = 180;

    private final SessionService sessionService;
    private final String siteUrl;
    private final String apiUrl;
    @Autowired(required = false)
    private CommunityRepository communities;

    public SessionShareController(
            SessionService sessionService,
            @Value("${desporto.public-site-url:https://jynlosports.com}") String siteUrl,
            @Value("${desporto.public-api-url:https://api.jynlosports.com}") String apiUrl) {
        this.sessionService = sessionService;
        this.siteUrl = siteUrl.replaceAll("/+$", "");
        this.apiUrl = apiUrl.replaceAll("/+$", "");
    }

    @GetMapping(value = "/sessions/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> session(@PathVariable Long id) {
        Card card;
        try {
            card = cardFor(sessionService.publicPreview(id));
        } catch (SessionNotFound notPreviewable) {
            card = new Card(GENERIC_TITLE, GENERIC_DESCRIPTION);
        }
        return html(siteUrl + "/sessions/" + id, apiUrl + "/api/share/sessions/" + id, card, null);
    }

    /**
     * Communities are discoverable by anyone (public and private alike), so the
     * card only uses the same safe presentation: name, sport, description, photo.
     * Never members, chats or games.
     */
    @GetMapping(value = "/communities/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> community(@PathVariable Long id) {
        Community community = communities == null ? null : communities.findWithSportsById(id).orElse(null);
        Card card = community == null ? new Card(GENERIC_TITLE, GENERIC_DESCRIPTION) : cardFor(community);
        String image = community == null ? null : mediaUrl(community.getPhotoUrl());
        return html(siteUrl + "/menu/communities/" + id, apiUrl + "/api/share/communities/" + id, card, image);
    }

    /**
     * A profile card shows only the username, which is already in the link.
     * It never says whether the account exists, and never shows the name,
     * photo or city: those stay behind sign-in and the profile's privacy.
     */
    @GetMapping(value = "/profiles/{username}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> profile(@PathVariable String username) {
        String handle = username == null ? "" : username.trim();
        if (!USERNAME.matcher(handle).matches()) {
            return html(siteUrl + "/", null, new Card(GENERIC_TITLE, GENERIC_DESCRIPTION), null);
        }
        Card card = new Card("@" + handle + " na Jynlo",
                "Vê o perfil de @" + handle + " e combina um jogo na Jynlo.");
        return html(siteUrl + "/menu/profiles/" + handle, apiUrl + "/api/share/profiles/" + handle, card, null);
    }

    private ResponseEntity<String> html(String url, String shareUrl, Card card, String image) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                .header("X-Robots-Tag", "noindex")
                .body(page(url, shareUrl, card, image));
    }

    private static Card cardFor(Community community) {
        String name = clean(community.getName());
        String sport = clean(community.getSport());
        String title = (name == null ? "Comunidade" : name) + " · comunidade na Jynlo";
        String description = clean(community.getDescription());
        if (description == null) {
            description = (sport == null ? "Uma comunidade" : "Comunidade de " + sport)
                    + " na Jynlo. Junta-te e combina jogos.";
        } else {
            description = description.replaceAll("\\s+", " ");
            if (description.length() > DESCRIPTION_LIMIT) {
                description = description.substring(0, DESCRIPTION_LIMIT - 1).trim() + "…";
            }
        }
        return new Card(title, description);
    }

    /** Only our own media or an https address; anything else falls back to the default image. */
    private String mediaUrl(String photoUrl) {
        String url = clean(photoUrl);
        if (url == null) return null;
        if (url.startsWith("/media/")) return siteUrl + url;
        if (url.startsWith("https://")) return url;
        return null;
    }

    private static Card cardFor(SessionPublicPreviewResponse preview) {
        String sport = clean(preview.sport());
        String city = clean(preview.city());
        StringBuilder title = new StringBuilder(sport == null ? "Jogo" : sport);
        if (city != null) title.append(" em ").append(city);
        if (preview.gameDate() != null) {
            title.append(" · ").append(WHEN.format(preview.gameDate().atZone(LISBON)));
        }
        return new Card(title.toString(), descriptionFor(preview));
    }

    private static String descriptionFor(SessionPublicPreviewResponse preview) {
        SessionStatus status = preview.status();
        if (status == SessionStatus.CANCELLED) return "Este jogo foi cancelado.";
        if (status == SessionStatus.COMPLETED) return "Este jogo já terminou.";
        Integer max = preview.maxParticipants();
        Integer taken = preview.participantCount();
        if (status == SessionStatus.FULL || (max != null && taken != null && taken >= max)) {
            return "Jogo completo. Vê outros jogos perto de ti na Jynlo.";
        }
        if (max != null && taken != null) {
            int free = max - taken;
            return (free == 1 ? "1 vaga livre" : free + " vagas livres")
                    + ". Junta-te a este jogo na Jynlo.";
        }
        return "Junta-te a este jogo na Jynlo.";
    }

    /**
     * The card is read by WhatsApp, Facebook and others from this address, so og:url points here (Facebook
     * would otherwise re-read the site's generic page). People are sent straight on to the site, where the
     * app takes over through its universal/app links when it is installed.
     */
    private String page(String url, String shareUrl, Card card, String customImage) {
        String title = escape(card.title());
        String description = escape(card.description());
        String canonical = escape(url);
        String ogUrl = escape(shareUrl == null ? url : shareUrl);
        String image = escape(customImage == null ? siteUrl + "/assets/og-default.png" : customImage);
        // The default image's size is known; a community photo's is not, so it is not declared.
        String imageMeta = customImage == null
                ? "<meta property=\"og:image:type\" content=\"image/png\">\n<meta property=\"og:image:width\" content=\"1200\">\n<meta property=\"og:image:height\" content=\"630\">"
                : "";
        return """
                <!doctype html>
                <html lang="pt-PT">
                <head>
                <meta charset="utf-8">
                <title>%1$s</title>
                <meta name="robots" content="noindex">
                <meta name="description" content="%2$s">
                <link rel="canonical" href="%3$s">
                <meta property="og:site_name" content="Jynlo">
                <meta property="og:locale" content="pt_PT">
                <meta property="og:type" content="website">
                <meta property="og:title" content="%1$s">
                <meta property="og:description" content="%2$s">
                <meta property="og:url" content="%6$s">
                <meta http-equiv="refresh" content="0; url=%3$s">
                <meta property="og:image" content="%4$s">
                %5$s
                <meta name="twitter:card" content="summary_large_image">
                <meta name="twitter:title" content="%1$s">
                <meta name="twitter:description" content="%2$s">
                <meta name="twitter:image" content="%4$s">
                </head>
                <body>
                <p><a href="%3$s">Abrir na Jynlo</a></p>
                <script>location.replace(document.querySelector('link[rel=canonical]').href);</script>
                </body>
                </html>
                """.formatted(title, description, canonical, image, imageMeta, ogUrl);
    }

    /**
     * Escapes only the characters that can break out of HTML text or attributes.
     * The one-argument HtmlUtils.htmlEscape defaults to ISO-8859-1 and would also
     * turn accented letters and dashes into named entities.
     */
    private static String escape(String value) {
        return HtmlUtils.htmlEscape(value, "UTF-8");
    }

    private static String clean(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private record Card(String title, String description) {
    }
}
