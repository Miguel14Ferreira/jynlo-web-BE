package com.example.desporto24.controllers;

import com.example.desporto24.service.GooglePlaceLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class GooglePlacesController {
    private final GooglePlaceLookupService service;
    @GetMapping("/suggestions")
    public GooglePlaceLookupService.Suggestions suggestions(
        @RequestParam String q,@RequestParam(required=false) String sessionToken,
        Authentication authentication) {
        return service.suggest(q,sessionToken,authentication.getName());
    }
    @GetMapping("/google/{placeId}")
    public GooglePlaceLookupService.Resolved details(
        @PathVariable String placeId,@RequestParam(required=false) String sessionToken,
        Authentication authentication) {
        return service.resolve(placeId,sessionToken,authentication.getName());
    }
}
