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
import org.springframework.web.client.HttpClientErrorException;

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

        } catch (HttpClientErrorException e) {
            log.error("Login failed for user: {} - Status: {}", request.getEmail(), e.getStatusCode(), e);

            // Handle specific Keycloak errors
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException(
                        "Invalid email or password. Please check your credentials and try again",
                        ErrorCode.INVALID_CREDENTIALS.getCode()
                );
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new AuthenticationException(
                        "Your account has been disabled. Please contact support for assistance",
                        ErrorCode.ACCOUNT_DISABLED.getCode()
                );
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new AuthenticationException(
                        "Too many login attempts. Please wait a moment before trying again",
                        ErrorCode.RATE_LIMIT_EXCEEDED.getCode()
                );
            }

            // Re-throw to be handled by global exception handler
            throw e;

        } catch (AuthenticationException e) {
            // Re-throw custom exceptions
            throw e;

        } catch (Exception e) {
            log.error("Unexpected login error for user: {}", request.getEmail(), e);
            throw new AuthenticationException(
                    "Login failed due to an unexpected error. Please try again",
                    ErrorCode.UNAUTHORIZED.getCode(),
                    e
            );
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

        } catch (HttpClientErrorException e) {
            log.error("Registration failed for user: {} - Status: {}", request.getEmail(), e.getStatusCode(), e);

            // Handle Keycloak-specific errors
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new RegistrationException(
                        "An account with this email address already exists. Try logging in instead",
                        ErrorCode.EMAIL_ALREADY_EXISTS.getCode()
                );
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = e.getMessage();

                if (errorMessage != null && errorMessage.toLowerCase().contains("password")) {
                    throw new RegistrationException(
                            "Password does not meet security requirements. Use at least 8 characters including letters, numbers, and special characters",
                            ErrorCode.WEAK_PASSWORD.getCode()
                    );
                } else if (errorMessage != null && errorMessage.toLowerCase().contains("email")) {
                    throw new RegistrationException(
                            "Invalid email format. Please provide a valid email address",
                            ErrorCode.INVALID_EMAIL_FORMAT.getCode()
                    );
                }
            }

            // Re-throw to be handled by global exception handler
            throw e;

        } catch (RegistrationException e) {
            // Re-throw custom exceptions
            throw e;

        } catch (Exception e) {
            log.error("Unexpected registration error for user: {}", request.getEmail(), e);

            // Check for common error patterns in exception message
            String errorMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (errorMessage.contains("duplicate") || errorMessage.contains("already exists")) {
                throw new RegistrationException(
                        "An account with this email address already exists",
                        ErrorCode.EMAIL_ALREADY_EXISTS.getCode(),
                        e
                );
            } else if (errorMessage.contains("password")) {
                throw new RegistrationException(
                        "Password does not meet security requirements",
                        ErrorCode.WEAK_PASSWORD.getCode(),
                        e
                );
            }

            throw new RegistrationException(
                    "Registration failed. Please check your information and try again",
                    ErrorCode.REGISTRATION_FAILED.getCode(),
                    e
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        log.info("Logout request received");

        try {
            if (refreshToken != null && !refreshToken.isBlank()) {
                authService.logout(refreshToken);
                log.info("User session invalidated");
            } else {
                log.info("No refresh token provided, clearing cookies only");
            }

            // Always clear cookies
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);

            log.info("Logout successful");

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("You have been logged out successfully")
                    .build());

        } catch (Exception e) {
            log.error("Logout failed", e);

            // Still clear cookies even if logout fails
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);

            throw new AuthenticationException(
                    "Logout failed, but your session has been cleared locally",
                    "LOGOUT_ERROR",
                    e
            );
        }
    }
}