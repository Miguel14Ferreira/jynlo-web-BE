package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.session.CreateSessionRequest;
import com.example.desporto24.model.dto.session.MySessionsResponse;
import com.example.desporto24.model.dto.session.JoinSessionRequest;
import com.example.desporto24.model.dto.session.JoinSessionResponse;
import com.example.desporto24.model.dto.session.SessionDetailResponse;
import com.example.desporto24.model.dto.session.SessionPublicPreviewResponse;
import com.example.desporto24.model.dto.session.SessionSearchCriteria;
import com.example.desporto24.model.dto.session.SessionParticipantResponse;
import com.example.desporto24.model.dto.session.SessionSummaryResponse;
import com.example.desporto24.model.dto.session.SessionTeamSuggestionResponse;
import com.example.desporto24.model.dto.session.ReportSessionProblemRequest;
import com.example.desporto24.model.dto.session.UpdateSessionExternalConfirmationsRequest;
import com.example.desporto24.model.dto.session.UpdateSessionMeetingPointRequest;
import com.example.desporto24.service.interfaces.SessionService;
import com.example.desporto24.service.SessionTeamSuggestionService;
import com.example.desporto24.service.SessionOrganizerExclusionService;
import com.example.desporto24.model.dto.session.SessionOrganizerExclusionResponse;
import com.example.desporto24.model.dto.session.OrganizerExclusionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final SessionTeamSuggestionService sessionTeamSuggestionService;
    private final SessionOrganizerExclusionService exclusionService;

    @GetMapping
    public List<SessionSummaryResponse> discover(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String query,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) Boolean privateGame,
            @RequestParam(required = false) Boolean freeOnly,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            Authentication authentication) {
        SessionSearchCriteria criteria = new SessionSearchCriteria(
                sport, city, from, to, latitude, longitude, radiusKm,
                availability, privateGame, freeOnly, minPrice, maxPrice, sort, query
        );

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return sessionService.discover(criteria);
        }

        return sessionService.discover(criteria, authentication.getName());
    }

    @GetMapping("/mine")
    public MySessionsResponse mine(Authentication authentication) {
        return sessionService.mine(authentication.getName());
    }

    @GetMapping("/{id}/public-preview")
    public SessionPublicPreviewResponse publicPreview(@PathVariable Long id) {
        return sessionService.publicPreview(id);
    }

    @GetMapping("/{id}")
    public SessionDetailResponse detail(@PathVariable Long id, Authentication authentication) {
        return sessionService.getById(id, authentication.getName());
    }

    @GetMapping("/{id}/series")
    public List<SessionSummaryResponse> series(
            @PathVariable Long id,
            Authentication authentication) {
        return sessionService.series(id, authentication.getName());
    }

    @GetMapping("/{id}/participants")
    public List<SessionParticipantResponse> participants(
            @PathVariable Long id,
            Authentication authentication) {
        return sessionService.participants(id, authentication.getName());
    }

    @GetMapping("/{id}/teams/suggestion")
    public SessionTeamSuggestionResponse teamSuggestion(
            @PathVariable Long id,
            @RequestParam(defaultValue = "FORM") String source,
            Authentication authentication) {
        return sessionTeamSuggestionService.suggest(id, authentication.getName(), source);
    }

    @PostMapping("/{id}/teams/confirm")
    public SessionTeamSuggestionResponse confirmTeams(
            @PathVariable Long id,
            @RequestParam(defaultValue = "FORM") String source,
            @RequestBody(required = false) com.example.desporto24.model.dto.session.ConfirmRandomSessionTeamsRequest assignment,
            Authentication authentication) {
        return sessionTeamSuggestionService.confirm(id, authentication.getName(), source, assignment);
    }

    @PostMapping("/{id}/registration/open")
    public SessionDetailResponse openRegistration(
            @PathVariable Long id, Authentication authentication) {
        return sessionService.openRegistration(id, authentication.getName());
    }

    @PostMapping("/{id}/join")
    public JoinSessionResponse join(
            @PathVariable Long id,
            @RequestBody(required = false) JoinSessionRequest request,
            Authentication authentication) {
        JoinSessionRequest effectiveRequest = request == null ? new JoinSessionRequest(null) : request;
        return sessionService.join(id, effectiveRequest, authentication.getName());
    }

    /** The organizer may adjust private arrival instructions before kickoff.
     * Never modifies venue, coordinates or public session discovery.
     */
    /** Edit the principal details of a future session without changing its identity or membership. */
    public record EditSessionDetailsRequest(
            @jakarta.validation.constraints.NotBlank String sport,
            @jakarta.validation.constraints.NotNull java.time.Instant gameDate,
            @jakarta.validation.constraints.Min(2) @jakarta.validation.constraints.Max(100) int maxParticipants,
            @jakarta.validation.constraints.Min(2) @jakarta.validation.constraints.Max(100) int minParticipants,
            @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.DecimalMin("0.00")
            java.math.BigDecimal priceAmount,
            @jakarta.validation.constraints.NotBlank String city,
            @jakarta.validation.constraints.NotBlank String address,
            @jakarta.validation.constraints.Size(max = 240) String meetingPointInstructions,
            @jakarta.validation.constraints.Size(max = 1000) String description) {
        /** Source compatibility for callers not providing a description. */
        public EditSessionDetailsRequest(String sport, java.time.Instant gameDate,
                int maxParticipants, int minParticipants, java.math.BigDecimal priceAmount,
                String city, String address, String meetingPointInstructions) {
            this(sport, gameDate, maxParticipants, minParticipants, priceAmount,
                    city, address, meetingPointInstructions, null);
        }
    }

    @PatchMapping("/{id}/details")
    public SessionDetailResponse updateDetails(@PathVariable Long id,
            @RequestBody @Valid EditSessionDetailsRequest request, Authentication authentication) {
        return sessionService.updateDetails(id, request.sport(), request.gameDate(), request.maxParticipants(),
                request.minParticipants(), request.priceAmount(), request.city(), request.address(),
                request.meetingPointInstructions(), request.description(), authentication.getName());
    }

    /** Organizer-only capacity correction, validated against existing confirmed places. */
    @PatchMapping("/{id}/capacity")
    public SessionDetailResponse updateCapacity(
            @PathVariable Long id,
            @RequestParam int maxParticipants,
            Authentication authentication) {
        return sessionService.updateCapacity(id, maxParticipants, authentication.getName());
    }

    @PatchMapping("/{id}/meeting-point")
    public SessionDetailResponse updateMeetingPoint(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSessionMeetingPointRequest request,
            Authentication authentication) {
        return sessionService.updateMeetingPoint(id, request.meetingPointInstructions(),
                authentication.getName());
    }

    @PatchMapping("/{id}/external-confirmations")
    public SessionDetailResponse updateExternalConfirmations(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSessionExternalConfirmationsRequest request,
            Authentication authentication) {
        return sessionService.updateExternalConfirmations(
                id, request.externalConfirmedParticipants(), authentication.getName());
    }

    @PutMapping("/{id}/participants/{participantId}/future-exclusion")
    public SessionOrganizerExclusionResponse excludeFromFutureSessions(
            @PathVariable Long id, @PathVariable Long participantId,
            @RequestBody(required = false) @Valid OrganizerExclusionRequest request,
            Authentication authentication) {
        return new SessionOrganizerExclusionResponse(
                request == null
                    ? exclusionService.excludeAfterNoShow(id, participantId, authentication.getName())
                    : exclusionService.excludeForReason(id, participantId, authentication.getName(), request.reason()));
    }

    @GetMapping("/{id}/participants/{participantId}/future-exclusion")
    public SessionOrganizerExclusionResponse exclusionStatus(
            @PathVariable Long id, @PathVariable Long participantId,
            Authentication authentication) {
        return new SessionOrganizerExclusionResponse(
                exclusionService.status(id, participantId, authentication.getName()));
    }

    @DeleteMapping("/{id}/participants/{participantId}/future-exclusion")
    public SessionOrganizerExclusionResponse removeFutureExclusion(
            @PathVariable Long id, @PathVariable Long participantId,
            Authentication authentication) {
        return new SessionOrganizerExclusionResponse(
                exclusionService.remove(id, participantId, authentication.getName()));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leave(@PathVariable Long id, Authentication authentication) {
        sessionService.leave(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication authentication) {
        sessionService.cancel(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id, Authentication authentication) {
        sessionService.complete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/problems/not-occurred")
    public ResponseEntity<Void> reportDidNotOccur(
            @PathVariable Long id,
            Authentication authentication) {
        sessionService.reportDidNotOccur(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/problems")
    public ResponseEntity<Void> reportProblem(
            @PathVariable Long id,
            @RequestBody @Valid ReportSessionProblemRequest request,
            Authentication authentication) {
        sessionService.reportProblem(id, request.type(), request.details(), authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/participants/{participantId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Long id,
            @PathVariable Long participantId,
            Authentication authentication) {
        sessionService.removeParticipant(id, participantId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionDetailResponse> create(
            @RequestBody @Valid CreateSessionRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.create(request, authentication.getName()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SessionDetailResponse> createWithPhoto(
            @RequestPart("session") @Valid CreateSessionRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.create(request, photo, authentication.getName()));
    }
}
