package com.example.desporto24.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirsoftGameTypeTest {
    @Test
    void supportsExactlyTheThreePublicFormats() {
        assertArrayEquals(
            new AirsoftGameType[] {
                AirsoftGameType.SKIRMISH,
                AirsoftGameType.MILSIM,
                AirsoftGameType.CAPTURE_THE_FLAG
            },
            AirsoftGameType.values()
        );
    }

    @Test
    void rejectsUnknownFormats() {
        assertThrows(IllegalArgumentException.class,
            () -> AirsoftGameType.valueOf("UNRECOGNIZED"));
    }
}
