package com.example.desporto24.controller;

import com.example.desporto24.service.SocialFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Creates the internal post behind a publicly visible attendance activity. */
@RestController
@RequestMapping("/api/feed/activities")
@RequiredArgsConstructor
public class SocialActivityInteractionController {
    private final SocialFeedService feedService;

    @PostMapping("/attendance/{sessionId}/{attendeeUsername}/post")
    public ResponseEntity<Long> attendancePost(
            @PathVariable Long sessionId,
            @PathVariable String attendeeUsername,
            Authentication authentication) {
        return ResponseEntity.ok(feedService.ensureAttendancePost(
                sessionId, attendeeUsername, authentication.getName()));
    }
}
