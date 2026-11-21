package com.example.desporto24.controllers;

import com.example.desporto24.service.clienterror.ClientErrorReporter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Receives error reports from the app and the website. Open to visitors who are
 * not signed in, because errors on the login screens matter too. Always answers
 * 202 for a well-formed request, whether or not it was logged, so clients never
 * retry and a caller cannot probe the limits.
 */
@RestController
@RequestMapping("/api/client-errors")
public class ClientErrorController {

    static final long MAX_BODY_BYTES = 16 * 1024;

    private final ClientErrorReporter reporter;

    public ClientErrorController(ClientErrorReporter reporter) {
        this.reporter = reporter;
    }

    @PostMapping
    public ResponseEntity<Void> report(@RequestBody(required = false) ClientErrorReporter.Report report,
                                       HttpServletRequest request,
                                       Authentication authentication) {
        if (request.getContentLengthLong() > MAX_BODY_BYTES) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }
        boolean signedIn = authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        reporter.report(report, signedIn, signedIn ? authentication.getName() : null);
        return ResponseEntity.accepted().build();
    }
}
