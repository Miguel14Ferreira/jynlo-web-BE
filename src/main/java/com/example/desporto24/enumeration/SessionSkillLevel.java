package com.example.desporto24.enumeration;

/** Level a session is aimed at. A session without a level is open to all levels. */
public enum SessionSkillLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    /** Lenient parse for query parameters: unknown or blank values mean "no filter". */
    public static SessionSkillLevel parseOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
