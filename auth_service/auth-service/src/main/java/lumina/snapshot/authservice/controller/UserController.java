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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user profile management endpoints
 */
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
                    .message("User profile retrieved successfully")
                    .data(userInfo)
                    .build());
            
        } catch (Exception e) {
            log.error("Failed to retrieve user profile", e);
            throw new AuthenticationException("Failed to retrieve profile: " + e.getMessage(), "PROFILE_FETCH_ERROR");
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
            
            // Get user from Keycloak
            UserRepresentation user = keycloakService.getUserById(userId);
            
            // Update user fields
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                // Check if email is already taken
                if (keycloakService.userExistsByEmail(request.getEmail())) {
                    throw new AuthenticationException("Email is already in use", "EMAIL_ALREADY_EXISTS");
                }
                user.setEmail(request.getEmail());
            }
            
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName());
            }
            
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName());
            }
            
            // Update user in Keycloak
            keycloakService.updateUser(userId, user);
            
            // Get updated user info
            UserInfoResponse updatedUserInfo = authService.getUserInfo(accessToken);
            
            log.info("User profile updated successfully for user: {}", userId);
            
            return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                    .success(true)
                    .message("Profile updated successfully")
                    .data(updatedUserInfo)
                    .build());
            
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user profile", e);
            throw new AuthenticationException("Failed to update profile: " + e.getMessage(), "PROFILE_UPDATE_ERROR");
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
            
            // Verify current password by attempting login
            LoginRequest loginRequest = new LoginRequest(userInfo.getEmail(), request.getCurrentPassword());
            try {
                authService.login(loginRequest);
            } catch (Exception e) {
                log.warn("Current password verification failed for user: {}", userId);
                throw new AuthenticationException("Current password is incorrect", "INVALID_CURRENT_PASSWORD");
            }
            
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
                    .message("Password changed successfully. You are now logged in with your new password.")
                    .data(authResponse)
                    .build());
            
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to change password", e);
            throw new AuthenticationException("Failed to change password: " + e.getMessage(), "PASSWORD_CHANGE_ERROR");
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
            
            // Send password reset email
            keycloakService.sendPasswordResetEmail(user.getId());
            
            log.info("Password reset email sent to: {}", request.getEmail());
            
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Password reset email has been sent. Please check your inbox.")
                    .build());
            
        } catch (Exception e) {
            // For security, don't reveal if email exists or not
            log.warn("Password reset request failed for email: {}", request.getEmail(), e);
            
            // Always return success to prevent email enumeration
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("If an account exists with this email, a password reset link has been sent.")
                    .build());
        }
    }
}
