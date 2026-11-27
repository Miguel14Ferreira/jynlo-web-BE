package com.example.desporto24.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs API requests that take longer than 1.5 s, so a "the app froze" report can be traced.
 * Only the method, a path with ids and usernames masked, the status and the time; never
 * query strings, bodies, tokens or user data.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SlowRequestLogFilter extends OncePerRequestFilter {
    static final long SLOW_MS = 1500;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (ms >= SLOW_MS && request.getRequestURI() != null && request.getRequestURI().startsWith("/api/")) {
                log.warn("JYNLO_SLOW_REQUEST method={} path={} status={} ms={}",
                        request.getMethod(), mask(request.getRequestURI()), response.getStatus(), ms);
            }
        }
    }

    /** /api/chat/me/joao.silva → /api/chat/me/{x}; /api/sessions/123/payments → /api/sessions/{n}/payments. */
    static String mask(String path) {
        String[] parts = path.split("/", -1);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i > 0) out.append('/');
            if (part.matches("\\d+")) out.append("{n}");
            else if (i >= 4 && !part.matches("[a-z-]+")) out.append("{x}");
            else out.append(part);
        }
        return out.length() > 160 ? out.substring(0, 160) : out.toString();
    }
}
