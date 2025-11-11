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
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        log.info("Login attempt for user: {}", request.getEmail());

        try {
            TokenResponse tokenResponse = authService.login(request);

            // Set tokens in HTTP-only cookies
            cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
            cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());

            // Get user info
            UserInfoResponse userInfo = authService.getUserInfo(tokenResponse.getAccessToken());

            log.info("Login successful for user: {}", request.getEmail());

            // Return both user info and tokens in response body
            AuthResponse authResponse = AuthResponse.builder()
                    .user(userInfo)
                    .tokens(tokenResponse)
                    .build();

            return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                    .success(true)
                    .message("Login successful. Welcome back!")
                    .data(authResponse)
                    .build());

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getEmail(), e);
            throw new AuthenticationException("Login failed. Please check your email and password.", "LOGIN_FAILED");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {

        log.info("Registration attempt for user: {}", request.getEmail());

        try {
            authService.register(request);

            log.info("Registration successful for user: {}", request.getEmail());

            // Auto-login after registration
            LoginRequest loginRequest = new LoginRequest(request.getEmail(), request.getPassword());
            TokenResponse tokenResponse = authService.login(loginRequest);

            // Set tokens in HTTP-only cookies
            cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
            cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());

            // Get user info
            UserInfoResponse userInfo = authService.getUserInfo(tokenResponse.getAccessToken());

            log.info("Auto-login successful after registration for user: {}", request.getEmail());

            // Return both user info and tokens in response body
            AuthResponse authResponse = AuthResponse.builder()
                    .user(userInfo)
                    .tokens(tokenResponse)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<AuthResponse>builder()
                            .success(true)
                            .message("Registration successful! Your account has been created and you are now logged in.")
                            .data(authResponse)
                            .build());

        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getEmail(), e);
            throw new RegistrationException("Registration failed. " + e.getMessage(), "REGISTRATION_FAILED");
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
