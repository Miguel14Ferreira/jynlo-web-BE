package com.example.desporto24.controllers;

import com.example.desporto24.service.availability.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** "Quem pode?" availability requests in private groups and communities (members only). */
@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @GetMapping
    public List<AvailabilityService.RequestView> open(@RequestParam String kind, @RequestParam Long targetId,
                                                      Authentication authentication) {
        return service.open(kind, targetId, authentication.getName());
    }

    @PostMapping
    public AvailabilityService.RequestView create(@RequestBody AvailabilityService.CreateRequest request,
                                                  Authentication authentication) {
        return service.create(request, authentication.getName());
    }

    @PutMapping("/{id}/slots/{slotId}/answer")
    public AvailabilityService.RequestView answer(@PathVariable Long id, @PathVariable Long slotId,
                                                  @RequestBody AvailabilityService.AnswerRequest body,
                                                  Authentication authentication) {
        return service.answer(id, slotId, body, authentication.getName());
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Void> close(@PathVariable Long id, Authentication authentication) {
        service.close(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
