package com.example.desporto24.controllers;

import com.example.desporto24.service.GooglePlaceLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class GooglePlacesController {
    private final GooglePlaceLookupService service;
    private String viewer(Authentication authentication) {
        if(authentication==null || !authentication.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return authentication.getName();
    }
    @GetMapping("/suggestions")
    public GooglePlaceLookupService.Suggestions suggestions(
        @RequestParam String q,@RequestParam(required=false) String sessionToken,
        Authentication authentication) {
        return service.suggest(q,sessionToken,viewer(authentication));
    }
    @GetMapping("/google/{placeId}")
    public GooglePlaceLookupService.Resolved details(
        @PathVariable String placeId,@RequestParam(required=false) String sessionToken,
        Authentication authentication) {
        return service.resolve(placeId,sessionToken,viewer(authentication));
    }
}
