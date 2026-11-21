package com.example.desporto24.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatPollTest {
    @Test
    void parsesQuestionAndOptions() {
        var poll = ChatPoll.parse("[JYNLO:POLL:1]Que dia jogamos?\nSábado\n Domingo ").orElseThrow();
        assertEquals("Que dia jogamos?", poll.question());
        assertEquals(List.of("Sábado", "Domingo"), poll.options());
        assertEquals("📊 Sondagem: Que dia jogamos?", ChatMessagePreview.forText("[JYNLO:POLL:1]Que dia jogamos?\nSábado\nDomingo"));
    }

    @Test
    void rejectsMalformedPolls() {
        assertTrue(ChatPoll.parse("[JYNLO:POLL:1]Só uma?\nSim").isEmpty(), "needs two options");
        assertTrue(ChatPoll.parse("[JYNLO:POLL:1]Q\na\nb\nc\nd\ne\nf\ng").isEmpty(), "max six options");
        assertTrue(ChatPoll.parse("[JYNLO:POLL:1]Q\nSim\nsim").isEmpty(), "duplicate options");
        assertTrue(ChatPoll.parse("[JYNLO:POLL:1]\nSim\nNão").isEmpty(), "empty question");
        assertTrue(ChatPoll.parse("[JYNLO:POLL:1]Q\nSim\n\nNão").isEmpty(), "empty option");
        assertTrue(ChatPoll.parse("[JYNLO:POLL:1]" + "x".repeat(141) + "\nSim\nNão").isEmpty());
        assertThrows(ResponseStatusException.class, () -> ChatPoll.validate("[JYNLO:POLL:1]Q\nSim"));
        assertDoesNotThrow(() -> ChatPoll.validate("olá a todos"));
    }

    @Test
    void directChatsRejectPollsButKeepNormalText() {
        assertThrows(ResponseStatusException.class, () -> ChatPoll.reject("[JYNLO:POLL:1]Q\nSim\nNão"));
        assertDoesNotThrow(() -> ChatPoll.reject("Sábado ou domingo?"));
    }

    @Test
    void pollsCannotBeEdited() {
        var now = java.time.Instant.now();
        assertThrows(ResponseStatusException.class, () -> ChatMessageMutationPolicy.editText(
                "[JYNLO:POLL:1]Q\nSim\nNão", now, now, null, false));
    }
}
