package com.example.desporto24.controllers;

import com.example.desporto24.service.profile.FirstStepsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Sports and city chosen right after signing up (app and website). */
@RestController
@RequestMapping("/api/profile/me/first-steps")
public class FirstStepsController {

    private final FirstStepsService service;

    public FirstStepsController(FirstStepsService service) {
        this.service = service;
    }

    @GetMapping
    public FirstStepsService.FirstSteps get(Authentication authentication) {
        return service.get(authentication.getName());
    }

    @PutMapping
    public FirstStepsService.FirstSteps save(@RequestBody FirstStepsService.Update update, Authentication authentication) {
        return service.save(authentication.getName(), update);
    }
}
