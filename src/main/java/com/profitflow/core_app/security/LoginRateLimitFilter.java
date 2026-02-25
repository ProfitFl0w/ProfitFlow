package com.profitflow.core_app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitflow.core_app.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory rate limiter for the login endpoint.
 * Allows max {@value #MAX_ATTEMPTS} failed attempts per IP within {@value #WINDOW_MILLIS}ms.
 * Successful requests do not count against the limit.
 */
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final int MAX_ATTEMPTS = 6;
    private static final long WINDOW_MILLIS = 10L * 60 * 1000; // 10 minutes

    private final ConcurrentHashMap<String, AttemptRecord> attemptStore = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !LOGIN_PATH.equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = resolveClientIp(request);

        if (isBlocked(clientIp)) {
            writeRateLimitResponse(response);
            return;
        }

        filterChain.doFilter(request, response);

        // Count only failed authentication attempts (4xx responses from auth endpoint)
        int status = response.getStatus();
        if (status == HttpStatus.UNAUTHORIZED.value() || status == HttpStatus.FORBIDDEN.value()) {
            recordFailedAttempt(clientIp);
        }
    }

    private boolean isBlocked(String clientIp) {
        AttemptRecord record = attemptStore.get(clientIp);
        if (record == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now - record.windowStart() > WINDOW_MILLIS) {
            attemptStore.remove(clientIp);
            return false;
        }
        return record.count() >= MAX_ATTEMPTS;
    }

    private void recordFailedAttempt(String clientIp) {
        long now = System.currentTimeMillis();
        attemptStore.compute(clientIp, (ip, existing) -> {
            if (existing == null || now - existing.windowStart() > WINDOW_MILLIS) {
                return new AttemptRecord(1, now);
            }
            return new AttemptRecord(existing.count() + 1, existing.windowStart());
        });
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        long retryAfterSeconds = WINDOW_MILLIS / 1000;
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("code", ErrorCode.RATE_LIMIT_EXCEEDED.getCode());
        body.put("message", ErrorCode.RATE_LIMIT_EXCEEDED.getMessage());

        objectMapper.writeValue(response.getWriter(), body);
    }

    private record AttemptRecord(int count, long windowStart) {}
}
