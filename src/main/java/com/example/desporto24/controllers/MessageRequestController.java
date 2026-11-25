package com.example.desporto24.controllers;

import com.example.desporto24.Chat.ChatRepository;
import com.example.desporto24.model.Chat;
import com.example.desporto24.service.MessageRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Message requests: write to someone who is not a friend yet (Instagram style). */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages/requests")
public class MessageRequestController {
    private final MessageRequestService requests;
    private final ChatRepository chats;

    public record SendRequest(String username, String text) {}
    public record PolicyRequest(MessageRequestService.Policy policy) {}

    @GetMapping("/availability/{username}")
    public MessageRequestService.Availability availability(@PathVariable String username, Authentication auth) {
        return requests.availability(auth.getName(), username);
    }

    @PostMapping
    public ResponseEntity<Void> send(@RequestBody SendRequest body, Authentication auth) {
        requests.send(auth.getName(), body == null ? null : body.username(), body == null ? null : body.text());
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public List<MessageRequestService.IncomingRequest> incoming(Authentication auth) {
        return requests.incoming(auth.getName());
    }

    @GetMapping("/sent")
    public List<MessageRequestService.IncomingRequest> sent(Authentication auth) {
        return requests.sent(auth.getName());
    }

    @GetMapping("/count")
    public Map<String, Integer> count(Authentication auth) {
        return Map.of("pending", requests.pendingCount(auth.getName()));
    }

    @PostMapping("/{id}/accept")
    @Transactional
    public ResponseEntity<Void> accept(@PathVariable Long id, Authentication auth) {
        Chat first = requests.decide(id, auth.getName(), true);
        if (first != null) {
            first.setDataDisplay(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(first.getDate()));
            chats.save(first);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<Void> decline(@PathVariable Long id, Authentication auth) {
        requests.decide(id, auth.getName(), false);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/policy")
    public Map<String, String> policy(Authentication auth) {
        return Map.of("policy", requests.myPolicy(auth.getName()).name());
    }

    @PutMapping("/policy")
    public Map<String, String> setPolicy(@RequestBody PolicyRequest body, Authentication auth) {
        var policy = body == null || body.policy() == null ? MessageRequestService.Policy.EVERYONE : body.policy();
        return Map.of("policy", requests.setPolicy(auth.getName(), policy).name());
    }
}
