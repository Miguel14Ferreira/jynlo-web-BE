package com.example.desporto24.configuration;

import com.example.desporto24.filter.JwtAccessDeniedHandler;
import com.example.desporto24.filter.JwtAuthenticationEntryPoint;
import com.example.desporto24.filter.JwtAuthorizationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityCorsAllowlistTest {
    @Test
    void stagingOriginIsAllowedWithoutWeakeningTheProductionCorsAllowlist() {
        var security=new SecurityConfiguration(
                mock(JwtAuthorizationFilter.class),
                mock(JwtAuthenticationEntryPoint.class),
                mock(JwtAccessDeniedHandler.class));
        ReflectionTestUtils.setField(security, "allowedOrigins",
                "https://d24sports.com, https://d24sports.com");
        var source=(UrlBasedCorsConfigurationSource)security.corsConfigurationSource();
        var cors=source.getCorsConfiguration(
                new MockHttpServletRequest("GET", "/api/teams/mine"));

        assertNotNull(cors);
        assertEquals(List.of(
                "https://d24sports.com",
                "https://desporto24-full-preview.onrender.com"),cors.getAllowedOrigins());
        assertEquals(Boolean.TRUE,cors.getAllowCredentials());
        assertFalse(cors.getAllowedOrigins().contains("*"));
        assertFalse(cors.getAllowedOrigins().contains("https://untrusted.example.com"));
        assertTrue(cors.getAllowedMethods().contains("OPTIONS"));
        assertTrue(cors.getAllowedMethods().contains("GET"));
    }
}
