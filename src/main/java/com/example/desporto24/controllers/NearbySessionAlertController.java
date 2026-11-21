package com.example.desporto24.controllers;

import com.example.desporto24.service.nearby.NearbyAlertPreference;
import com.example.desporto24.service.nearby.NearbySessionAlertService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The signed-in person's "new game near you" settings. */
@RestController
@RequestMapping("/api/notifications/me/nearby-sessions")
public class NearbySessionAlertController {

    private final NearbySessionAlertService service;

    public NearbySessionAlertController(NearbySessionAlertService service) {
        this.service = service;
    }

    @GetMapping
    public NearbyAlertPreference get(Authentication authentication) {
        return service.get(authentication.getName());
    }

    @PutMapping
    public NearbyAlertPreference update(Authentication authentication, @RequestBody NearbyAlertPreference request) {
        return service.update(authentication.getName(), request);
    }
}
