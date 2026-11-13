package com.example.desporto24.controllers;

import com.example.desporto24.service.PersonalLiveService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Personal lives need no community membership. */
@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class PersonalLiveController {
    private final PersonalLiveService service;
    public record StartRequest(@NotBlank @Size(max=120) String title) {}

    @GetMapping
    public List<PersonalLiveService.Event> active(Authentication auth) {
        return service.active(auth.getName());
    }
    @PostMapping
    public PersonalLiveService.Event start(@RequestBody @Valid StartRequest request, Authentication auth) {
        return service.start(auth.getName(),request.title());
    }
    @PostMapping("/{id}/join")
    public PersonalLiveService.Connection join(@PathVariable Long id,Authentication auth) {
        return service.join(auth.getName(),id);
    }
    @PostMapping("/{id}/heartbeat")
    public void heartbeat(@PathVariable Long id,Authentication auth) {
        service.heartbeat(auth.getName(),id);
    }
    @PostMapping("/{id}/end")
    public PersonalLiveService.Event end(@PathVariable Long id,Authentication auth) {
        return service.end(auth.getName(),id);
    }
}
