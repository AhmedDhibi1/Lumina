package lumina.snapshot.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lumina.snapshot.authservice.dto.*;
import lumina.snapshot.authservice.exception.AuthenticationException;
import lumina.snapshot.authservice.service.auth.AuthService;
import lumina.snapshot.authservice.service.keycloak.KeycloakService;
import lumina.snapshot.authservice.util.CookieUtil;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final KeycloakService keycloakService;
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getProfile(
            @CookieValue(name = "accessToken") String accessToken) {

        log.info("Fetching user profile");

        try {
            UserInfoResponse userInfo = authService.getUserInfo(accessToken);

            log.info("User profile retrieved successfully for user: {}", userInfo.getUserId());

            return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                    .success(true)
                    .message("Profile retrieved successfully")
                    .data(userInfo)
                    .build());

        } catch (HttpClientErrorException e) {
            log.error("Failed to retrieve user profile - Status: {}", e.getStatusCode(), e);

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException(
                        "Your session has expired. Please log in again",
                        ErrorCode.TOKEN_EXPIRED.getCode()
                );
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new AuthenticationException(
                        "You don't have permission to access this profile",
                        ErrorCode.FORBIDDEN.getCode()
                );
            }

            throw e;

        } catch (AuthenticationException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error retrieving user profile", e);
            throw new AuthenticationException(
                    "Unable to retrieve your profile at this time. Please try again",
                    "PROFILE_FETCH_ERROR",
                    e
            );
        }
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateProfile(
            @CookieValue(name = "accessToken") String accessToken,
            @Valid @RequestBody UpdateUserRequest request) {

        log.info("Updating user profile");

        try {
            // Get current user info
            UserInfoResponse currentUser = authService.getUserInfo(accessToken);
            String userId = currentUser.getUserId();

            log.debug("Updating profile for user: {}", userId);

            // Get user from Keycloak
            UserRepresentation user = keycloakService.getUserById(userId);

            // Update user fields
            boolean hasChanges = false;

            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                // Check if email is already taken
                if (keycloakService.userExistsByEmail(request.getEmail())) {
                    throw new AuthenticationException(
                            "This email address is already registered to another account",
                            ErrorCode.EMAIL_ALREADY_EXISTS.getCode()
                    );
                }
                user.setEmail(request.getEmail());
                hasChanges = true;
            }

            if (request.getFirstName() != null && !request.getFirstName().equals(user.getFirstName())) {
                user.setFirstName(request.getFirstName());
                hasChanges = true;
            }

            if (request.getLastName() != null && !request.getLastName().equals(user.getLastName())) {
                user.setLastName(request.getLastName());
                hasChanges = true;
            }

            if (!hasChanges) {
                log.info("No changes detected for user: {}", userId);
                return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                        .success(true)
                        .message("No changes were made to your profile")
                        .data(currentUser)
                        .build());
            }

            // Update user in Keycloak
            keycloakService.updateUser(userId, user);

            // Get updated user info
            UserInfoResponse updatedUserInfo = authService.getUserInfo(accessToken);

            log.info("User profile updated successfully for user: {}", userId);

            return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                    .success(true)
                    .message("Your profile has been updated successfully")
                    .data(updatedUserInfo)
                    .build());

        } catch (HttpClientErrorException e) {
            log.error("Failed to update user profile - Status: {}", e.getStatusCode(), e);

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException(
                        "Your session has expired. Please log in again",
                        ErrorCode.TOKEN_EXPIRED.getCode()
                );
            } else if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new AuthenticationException(
                        "This email address is already in use",
                        ErrorCode.EMAIL_ALREADY_EXISTS.getCode()
                );
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new AuthenticationException(
                        "Invalid profile data. Please check your input",
                        ErrorCode.VALIDATION_ERROR.getCode()
                );
            }

            throw e;

        } catch (AuthenticationException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error updating user profile", e);
            throw new AuthenticationException(
                    "Unable to update your profile at this time. Please try again",
                    "PROFILE_UPDATE_ERROR",
                    e
            );
        }
    }

    /**
     * Change user password
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<AuthResponse>> changePassword(
            @CookieValue(name = "accessToken") String accessToken,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletResponse response) {

        log.info("Changing user password");

        try {
            // Get current user info
            UserInfoResponse userInfo = authService.getUserInfo(accessToken);
            String userId = userInfo.getUserId();

            log.debug("Verifying current password for user: {}", userId);

            // Verify current password by attempting login
            LoginRequest loginRequest = new LoginRequest(userInfo.getEmail(), request.getCurrentPassword());
            try {
                authService.login(loginRequest);
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    log.warn("Current password verification failed for user: {}", userId);
                    throw new AuthenticationException(
                            "The current password you entered is incorrect. Please try again",
                            ErrorCode.INVALID_CURRENT_PASSWORD.getCode()
                    );
                }
                throw e;
            } catch (Exception e) {
                log.warn("Current password verification failed for user: {}", userId);
                throw new AuthenticationException(
                        "Unable to verify your current password. Please try again",
                        ErrorCode.INVALID_CURRENT_PASSWORD.getCode()
                );
            }

            // Validate new password
            if (request.getCurrentPassword().equals(request.getNewPassword())) {
                throw new AuthenticationException(
                        "New password must be different from your current password",
                        "PASSWORD_SAME_AS_CURRENT"
                );
            }

            log.debug("Resetting password for user: {}", userId);

            // Reset password
            keycloakService.resetPassword(userId, request.getNewPassword());

            log.info("Password changed successfully for user: {}", userId);

            // Auto-login with new password to get fresh tokens
            LoginRequest newLoginRequest = new LoginRequest(userInfo.getEmail(), request.getNewPassword());
            TokenResponse tokenResponse = authService.login(newLoginRequest);

            // Set new tokens in HTTP-only cookies
            cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
            cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());

            // Get updated user info
            UserInfoResponse updatedUserInfo = authService.getUserInfo(tokenResponse.getAccessToken());

            // Return both user info and tokens in response body
            AuthResponse authResponse = AuthResponse.builder()
                    .user(updatedUserInfo)
                    .tokens(tokenResponse)
                    .build();

            return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                    .success(true)
                    .message("Your password has been changed successfully. You are now logged in with your new password.")
                    .data(authResponse)
                    .build());

        } catch (HttpClientErrorException e) {
            log.error("Failed to change password - Status: {}", e.getStatusCode(), e);

            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = e.getMessage();
                if (errorMessage != null && errorMessage.toLowerCase().contains("password")) {
                    throw new AuthenticationException(
                            "New password does not meet security requirements. Use at least 8 characters including letters, numbers, and special characters",
                            ErrorCode.WEAK_PASSWORD.getCode()
                    );
                }
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException(
                        "Your session has expired. Please log in again",
                        ErrorCode.TOKEN_EXPIRED.getCode()
                );
            }

            throw e;

        } catch (AuthenticationException e) {
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error changing password", e);
            throw new AuthenticationException(
                    "Unable to change your password at this time. Please try again",
                    "PASSWORD_CHANGE_ERROR",
                    e
            );
        }
    }

    /**
     * Request password reset email
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request) {

        log.info("Password reset requested for email: {}", request.getEmail());

        try {
            // Get user by email
            UserRepresentation user = keycloakService.getUserByEmail(request.getEmail());

            if (user != null) {
                // Send password reset email
                keycloakService.sendPasswordResetEmail(user.getId());
                log.info("Password reset email sent to: {}", request.getEmail());
            } else {
                log.warn("Password reset requested for non-existent email: {}", request.getEmail());
            }

            // Always return success to prevent email enumeration
            // Security best practice: don't reveal if email exists
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("If an account exists with this email address, a password reset link has been sent. Please check your inbox and spam folder.")
                    .build());

        } catch (Exception e) {
            // For security, don't reveal if email exists or not
            log.warn("Password reset request processing failed for email: {}", request.getEmail(), e);

            // Always return success to prevent email enumeration
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("If an account exists with this email address, a password reset link has been sent. Please check your inbox and spam folder.")
                    .build());
        }
    }
}