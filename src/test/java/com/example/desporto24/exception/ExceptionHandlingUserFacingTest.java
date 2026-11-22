package com.example.desporto24.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionHandlingUserFacingTest {
    @Test
    void showsBusinessReasonsButNeverTechnicalDetails() {
        assertEquals("Já existe uma época ativa para esta modalidade",
                ExceptionHandling.userFacing("Já existe uma época ativa para esta modalidade", "x"));
        assertEquals("x", ExceptionHandling.userFacing("java.lang.IllegalStateException: boom", "x"));
        assertEquals("x", ExceptionHandling.userFacing("Could not resolve [Profile#1]", "x"));
        assertEquals("x", ExceptionHandling.userFacing(null, "x"));
        assertEquals("x", ExceptionHandling.userFacing("malformed", "x"));
    }
}
