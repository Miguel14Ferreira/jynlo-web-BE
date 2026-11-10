package com.example.desporto24.controllers;

import com.example.desporto24.enumeration.NotificationType;
import com.example.desporto24.register.Notifications.Notifications;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.repository.ProfileRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

/**
 * One-way, in-app administrative notices, not private chat messages.
 * Actor and destination are always resolved server-side.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/players")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminPlayerMessageController {
    private final ProfileRepository profiles;
    private final NotificationsRepository notifications;

    public record MessageRequest(@NotBlank @Size(max = 1000) String message) {}
    public record MessageReceipt(Long notificationId) {}

    @PostMapping("/{id}/message")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public MessageReceipt send(@PathVariable Long id, @Valid @RequestBody MessageRequest request,
                               Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem autorização");
        }
        var actor = profiles.findUserByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem autorização"));
        // Recheck the current database role, even if an older JWT still has admin claims.
        if (!"ROLE_ADMIN".equals(actor.getRole()) && !"ROLE_SUPER_ADMIN".equals(actor.getRole())
                || !Boolean.TRUE.equals(actor.getEnabled())
                || !Boolean.TRUE.equals(actor.getNotLocked())
                || actor.getModerationSuspendedAt() != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem autorização");
        }
        if (id == null || id <= 0 || id.equals(actor.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destinatário inválido");
        }
        var target = profiles.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado"));
        if (!Boolean.TRUE.equals(target.getEnabled()) || !Boolean.TRUE.equals(target.getNotLocked())
                || target.getModerationSuspendedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conta indisponível");
        }
        if (request == null || request.message() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem obrigatória");
        }
        String message = request.message().trim();
        if (message.isEmpty() || message.length() > 1000
                || message.chars().anyMatch(ch -> Character.isISOControl(ch))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem inválida");
        }

        var notification = new Notifications();
        notification.setSubject("Mensagem da Administração Jynlo");
        notification.setMessage(message);
        notification.setGreeting("Jynlo");
        notification.setSignature("Administração Jynlo");
        notification.setCreatedAt(Instant.now().toString());
        notification.setUsername(target.getUsername());
        notification.setRelatedUsername(actor.getUsername());
        notification.setType(NotificationType.ADMIN_MESSAGE);
        notification.setRead(false);
        return new MessageReceipt(notifications.save(notification).getId());
    }
}
