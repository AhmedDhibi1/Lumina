package com.snapshot.lumina.businesslogic.presentation.auth;

import com.snapshot.lumina.businesslogic.presentation.config.CookieUtil;
import com.snapshot.lumina.businesslogic.presentation.config.JwtUtil;
import com.snapshot.lumina.businesslogic.presentation.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    public UUID extractAuthenticatedUserId(HttpServletRequest request) {
        // Extract JWT from cookies
        String token = cookieUtil.extractJwtFromCookies(request)
                .orElseThrow(() -> {
                    log.warn("No JWT token found in cookies");
                    return new UnauthorizedException("Authentication required. Please log in.");
                });

        // Extract user ID
        try {
            UUID userId = jwtUtil.extractUserId(token);
            log.debug("Authenticated user ID extracted: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Failed to extract user ID from token: {}", e.getMessage());
            throw new UnauthorizedException("Invalid authentication token format.");
        }
    }

    // Extract username from JWT token (optional)

    public String extractAuthenticatedUsername(HttpServletRequest request) {
        String token = cookieUtil.extractJwtFromCookies(request)
                .orElseThrow(() -> new UnauthorizedException("Authentication required."));

        return jwtUtil.extractUsername(token);
    }
}
