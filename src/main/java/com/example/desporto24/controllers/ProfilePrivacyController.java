package com.example.desporto24.controllers;
import com.example.desporto24.service.ProfilePrivacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profile/privacy")
@RequiredArgsConstructor
public class ProfilePrivacyController {
  private final ProfilePrivacyService service;
  private final com.example.desporto24.service.ProfileMediaReadinessService mediaReadiness;
  public record SettingsRequest(boolean privateProfile) {}
  /** Audit only; never exposes paths or storage credentials. */
  @GetMapping("/me/media-readiness")
  public com.example.desporto24.service.ProfileMediaReadinessService.MediaReadiness mediaReadiness(Authentication auth) {
    return mediaReadiness.mine(auth.getName());
  }
  @GetMapping("/me")
  public ProfilePrivacyService.PrivacyStatus mine(Authentication auth) {
    return service.status(auth.getName(),auth.getName());
  }
  @PutMapping("/me")
  public ProfilePrivacyService.PrivacyStatus update(
      @RequestBody SettingsRequest request,Authentication auth) {
    return service.update(auth.getName(),request.privateProfile());
  }
  @GetMapping("/{username}")
  public ProfilePrivacyService.PrivacyStatus status(@PathVariable String username,Authentication auth) {
    return service.status(auth.getName(),username);
  }
  @PostMapping("/{username}/requests")
  public void request(@PathVariable String username,Authentication auth) {
    service.request(auth.getName(),username);
  }
  @DeleteMapping("/{username}/requests")
  public void cancel(@PathVariable String username,Authentication auth) {
    service.cancel(auth.getName(),username);
  }
  @GetMapping("/me/requests")
  public List<ProfilePrivacyService.PendingRequest> pending(Authentication auth) {
    return service.pending(auth.getName());
  }
  @PostMapping("/me/requests/{id}/accept")
  public void accept(@PathVariable long id,Authentication auth) {
    service.decide(auth.getName(),id,true);
  }
  @PostMapping("/me/requests/{id}/reject")
  public void reject(@PathVariable long id,Authentication auth) {
    service.decide(auth.getName(),id,false);
  }
}
