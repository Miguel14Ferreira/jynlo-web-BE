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
    private final com.example.desporto24.service.PersonalLiveInteractionService interaction;
    public record ChatRequest(@NotBlank @Size(max=500) String message) {}
    public record ChatSettings(boolean enabled) {}
    public record SlowModeSettings(int seconds) {}
    public record MuteSettings(boolean muted) {}
    @GetMapping("/{id}/messages")
    public List<com.example.desporto24.service.PersonalLiveInteractionService.ChatMessage> messages(
        @PathVariable long id,Authentication auth) {
        return interaction.chat(auth.getName(),id);
    }
    @PostMapping("/{id}/messages")
    public com.example.desporto24.service.PersonalLiveInteractionService.ChatMessage message(
        @PathVariable long id,@Valid @RequestBody ChatRequest request,Authentication auth) {
        return interaction.send(auth.getName(),id,request.message());
    }
    @GetMapping("/{id}/controls")
    public com.example.desporto24.service.PersonalLiveInteractionService.LiveControls controls(
        @PathVariable long id,Authentication auth) {
        return interaction.controls(auth.getName(),id);
    }
    @PutMapping("/{id}/chat")
    public com.example.desporto24.service.PersonalLiveInteractionService.LiveControls chatSettings(
        @PathVariable long id,@RequestBody ChatSettings request,Authentication auth) {
        return interaction.toggleChat(auth.getName(),id,request.enabled());
    }
    @PutMapping("/{id}/slow-mode")
    public com.example.desporto24.service.PersonalLiveInteractionService.LiveControls slowMode(
        @PathVariable long id,@RequestBody SlowModeSettings request,Authentication auth) {
        return interaction.setSlowMode(auth.getName(),id,request.seconds());
    }
    @PutMapping("/{id}/mute/{username}")
    public com.example.desporto24.service.PersonalLiveInteractionService.LiveControls mute(
        @PathVariable long id,@PathVariable String username,@RequestBody MuteSettings request,
        Authentication auth) {
        return interaction.mute(auth.getName(),id,username,request.muted());
    }
    @PostMapping("/{id}/guest-requests")
    public com.example.desporto24.service.PersonalLiveInteractionService.GuestRequest requestGuest(
        @PathVariable long id,Authentication auth) {
        return interaction.askToJoin(auth.getName(),id);
    }
    @PostMapping("/{id}/guest-requests/{requestId}/approve")
    public com.example.desporto24.service.PersonalLiveInteractionService.GuestRequest approveGuest(
        @PathVariable long id,@PathVariable long requestId,Authentication auth) {
        return interaction.decide(auth.getName(),id,requestId,true);
    }
    @PostMapping("/{id}/guest-requests/{requestId}/decline")
    public com.example.desporto24.service.PersonalLiveInteractionService.GuestRequest declineGuest(
        @PathVariable long id,@PathVariable long requestId,Authentication auth) {
        return interaction.decide(auth.getName(),id,requestId,false);
    }
    @DeleteMapping("/{id}/guest")
    public void removeGuest(@PathVariable long id,Authentication auth) {
        interaction.removeGuest(auth.getName(),id);
    }
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
