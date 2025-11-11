package lumina.snapshot.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lumina.snapshot.authservice.dto.*;
import lumina.snapshot.authservice.exception.AuthenticationException;
import lumina.snapshot.authservice.exception.RegistrationException;
import lumina.snapshot.authservice.service.auth.AuthService;
import lumina.snapshot.authservice.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserInfoResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        log.info("Login attempt for user: {}", request.getUsername());

        try {
            TokenResponse tokenResponse = authService.login(request);

            // Set tokens in HTTP-only cookies
            cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
            cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());

            // Get user info
            UserInfoResponse userInfo = authService.getUserInfo(tokenResponse.getAccessToken());

            log.info("Login successful for user: {}", request.getUsername());

            return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                    .success(true)
                    .message("Login successful")
                    .data(userInfo)
                    .build());

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            throw new AuthenticationException("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Registration attempt for user: {}", request.getUsername());

        try {
            authService.register(request);

            log.info("Registration successful for user: {}", request.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<String>builder()
                            .success(true)
                            .message("Registration successful. Please login.")
                            .build());

        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getUsername(), e);
            throw new RegistrationException("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        log.info("Logout request");

        try {
            if (refreshToken != null) {
                authService.logout(refreshToken);
            }

            // Clear cookies
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);

            log.info("Logout successful");

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Logout successful")
                    .build());

        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new AuthenticationException("Logout failed: " + e.getMessage());
        }
    }
}
