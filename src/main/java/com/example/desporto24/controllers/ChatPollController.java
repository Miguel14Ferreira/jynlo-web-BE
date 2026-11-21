package com.example.desporto24.controllers;

import com.example.desporto24.service.ChatPollService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Group-chat polls: /api/messages/polls/{session|community}/{conversationId}. */
@RestController
@RequestMapping("/api/messages/polls")
@RequiredArgsConstructor
public class ChatPollController {
    private final ChatPollService service;

    public record VoteRequest(Integer option) {}

    @GetMapping("/{kind}/{conversationId}")
    public List<ChatPollService.PollResult> results(@PathVariable String kind,
            @PathVariable String conversationId, @RequestParam List<Long> ids, Authentication auth) {
        return service.results(auth.getName(), kind, conversationId, ids);
    }

    @PutMapping("/{kind}/{conversationId}/{messageId}")
    public ChatPollService.PollResult vote(@PathVariable String kind, @PathVariable String conversationId,
            @PathVariable Long messageId, @RequestBody VoteRequest request, Authentication auth) {
        if (request == null || request.option() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Missing option");
        }
        return service.vote(auth.getName(), kind, conversationId, messageId, request.option());
    }

    @DeleteMapping("/{kind}/{conversationId}/{messageId}")
    public ChatPollService.PollResult remove(@PathVariable String kind, @PathVariable String conversationId,
            @PathVariable Long messageId, Authentication auth) {
        return service.vote(auth.getName(), kind, conversationId, messageId, null);
    }
}
