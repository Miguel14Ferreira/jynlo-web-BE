package com.example.desporto24.controllers;

import com.example.desporto24.enumeration.SessionStatus;
import com.example.desporto24.exception.SessionNotFound;
import com.example.desporto24.model.dto.session.SessionPublicPreviewResponse;
import com.example.desporto24.service.interfaces.SessionService;
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

/**
 * Link-preview page for a session.
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

    private static final ZoneId LISBON = ZoneId.of("Europe/Lisbon");
    private static final DateTimeFormatter WHEN =
            DateTimeFormatter.ofPattern("EEE, d 'de' MMM 'às' HH:mm", Locale.forLanguageTag("pt-PT"));
    private static final String GENERIC_TITLE = "Jynlo — Join the game.";
    private static final String GENERIC_DESCRIPTION = "Jynlo — encontra onde, quando e com quem jogar.";

    private final SessionService sessionService;
    private final String siteUrl;

    public SessionShareController(
            SessionService sessionService,
            @Value("${desporto.public-site-url:https://d24sports.com}") String siteUrl) {
        this.sessionService = sessionService;
        this.siteUrl = siteUrl.replaceAll("/+$", "");
    }

    @GetMapping(value = "/sessions/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> session(@PathVariable Long id) {
        Card card;
        try {
            card = cardFor(sessionService.publicPreview(id));
        } catch (SessionNotFound notPreviewable) {
            card = new Card(GENERIC_TITLE, GENERIC_DESCRIPTION);
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                .header("X-Robots-Tag", "noindex")
                .body(page(siteUrl + "/sessions/" + id, card));
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

    private String page(String url, Card card) {
        String title = escape(card.title());
        String description = escape(card.description());
        String canonical = escape(url);
        String image = escape(siteUrl + "/assets/og-default.png");
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
                <meta property="og:url" content="%3$s">
                <meta property="og:image" content="%4$s">
                <meta property="og:image:type" content="image/png">
                <meta property="og:image:width" content="1200">
                <meta property="og:image:height" content="630">
                <meta name="twitter:card" content="summary_large_image">
                <meta name="twitter:title" content="%1$s">
                <meta name="twitter:description" content="%2$s">
                <meta name="twitter:image" content="%4$s">
                </head>
                <body>
                <p><a href="%3$s">Abrir na Jynlo</a></p>
                </body>
                </html>
                """.formatted(title, description, canonical, image);
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
