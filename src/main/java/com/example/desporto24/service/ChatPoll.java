package com.example.desporto24.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * "Sondagem": a group-chat message with a question and 2–6 options. Stored as
 * ordinary chat text with a line-based envelope (no JSON to parse on clients):
 *
 *   [JYNLO:POLL:1]Que dia jogamos?
 *   Sábado
 *   Domingo
 *
 * Votes live in chat_poll_vote, one per member (they can change it).
 * Only session and community chats accept polls.
 */
public final class ChatPoll {
    public static final String PREFIX = "[JYNLO:POLL:1]";
    public static final int MAX_QUESTION = 140;
    public static final int MAX_OPTION = 60;
    public static final int MIN_OPTIONS = 2;
    public static final int MAX_OPTIONS = 6;

    public record Poll(String question, List<String> options) {}

    private ChatPoll() {}

    public static boolean isPoll(String text) {
        return text != null && text.trim().startsWith(PREFIX);
    }

    /** Parses a stored poll; empty when the text is not a well-formed poll. */
    public static Optional<Poll> parse(String text) {
        if (!isPoll(text)) return Optional.empty();
        String[] lines = text.trim().substring(PREFIX.length()).split("\\R", -1);
        if (lines.length < 1 + MIN_OPTIONS || lines.length > 1 + MAX_OPTIONS) return Optional.empty();
        String question = lines[0].strip();
        if (question.isEmpty() || question.length() > MAX_QUESTION || hasControl(question)) return Optional.empty();
        List<String> options = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i = 1; i < lines.length; i++) {
            String option = lines[i].strip();
            if (option.isEmpty() || option.length() > MAX_OPTION || hasControl(option)
                    || !seen.add(option.toLowerCase(Locale.ROOT))) return Optional.empty();
            options.add(option);
        }
        return Optional.of(new Poll(question, List.copyOf(options)));
    }

    /** Group chats: a poll envelope must be well formed. Other messages pass. */
    public static void validate(String text) {
        if (isPoll(text) && parse(text).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid poll");
        }
    }

    /** Direct chats, private groups and guests: polls are not offered there. */
    public static void reject(String text) {
        if (isPoll(text)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Polls are only available in group chats");
        }
    }

    public static String preview(String text) {
        return parse(text).map(p -> "📊 Sondagem: " + p.question()).orElse("📊 Sondagem");
    }

    private static boolean hasControl(String value) {
        return value.chars().anyMatch(c -> Character.isISOControl(c));
    }
}
