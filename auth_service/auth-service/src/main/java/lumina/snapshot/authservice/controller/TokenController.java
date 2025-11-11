package lumina.snapshot.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lumina.snapshot.authservice.dto.ApiResponse;
import lumina.snapshot.authservice.dto.AuthResponse;
import lumina.snapshot.authservice.dto.ErrorCode;
import lumina.snapshot.authservice.dto.TokenResponse;
import lumina.snapshot.authservice.dto.UserInfoResponse;
import lumina.snapshot.authservice.exception.AuthenticationException;
import lumina.snapshot.authservice.service.token.TokenService;
import lumina.snapshot.authservice.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {

        log.info("Token refresh request received");

        try {
            // Validate refresh token is not empty
            if (refreshToken == null || refreshToken.isBlank()) {
                log.warn("Empty refresh token provided");
                throw new AuthenticationException(
                        "Refresh token is missing. Please log in again",
                        ErrorCode.TOKEN_INVALID.getCode()
                );
            }

            log.debug("Attempting to refresh token");

            // Refresh the token
            TokenResponse tokenResponse = tokenService.refreshToken(refreshToken);

            // Update cookies with new tokens
            cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
            cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());

            // Get updated user info
            UserInfoResponse userInfo = tokenService.getUserInfo(tokenResponse.getAccessToken());

            log.info("Token refresh successful for user: {}", userInfo.getUserId());

            // Return both user info and tokens in response body
            AuthResponse authResponse = AuthResponse.builder()
                    .user(userInfo)
                    .tokens(tokenResponse)
                    .build();

            return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                    .success(true)
                    .message("Session refreshed successfully")
                    .data(authResponse)
                    .build());

        } catch (HttpClientErrorException e) {
            log.error("Token refresh failed - Status: {}", e.getStatusCode(), e);

            // Clear invalid cookies
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException(
                        "Your session has expired. Please log in again",
                        ErrorCode.TOKEN_EXPIRED.getCode()
                );
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new AuthenticationException(
                        "Invalid refresh token. Please log in again",
                        ErrorCode.TOKEN_INVALID.getCode()
                );
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new AuthenticationException(
                        "Too many refresh attempts. Please wait a moment before trying again",
                        ErrorCode.RATE_LIMIT_EXCEEDED.getCode()
                );
            }

            throw e;

        } catch (AuthenticationException e) {
            // Clear invalid cookies for authentication errors
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);

            // Clear invalid cookies
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);

            throw new AuthenticationException(
                    "Unable to refresh your session. Please log in again",
                    ErrorCode.TOKEN_INVALID.getCode(),
                    e
            );
        }
    }

    /**
     * Validate access token (optional endpoint for frontend to check token validity)
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<UserInfoResponse>> validateToken(
            @CookieValue(name = "accessToken") String accessToken) {

        log.info("Token validation request received");

        try {
            // Get user info to validate token
            UserInfoResponse userInfo = tokenService.getUserInfo(accessToken);

            log.info("Token validated successfully for user: {}", userInfo.getUserId());

            return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                    .success(true)
                    .message("Token is valid")
                    .data(userInfo)
                    .build());

        } catch (HttpClientErrorException e) {
            log.warn("Token validation failed - Status: {}", e.getStatusCode());

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException(
                        "Your session has expired. Please log in again",
                        ErrorCode.TOKEN_EXPIRED.getCode()
                );
            }

            throw new AuthenticationException(
                    "Invalid token. Please log in again",
                    ErrorCode.TOKEN_INVALID.getCode()
            );

        } catch (Exception e) {
            log.error("Unexpected error during token validation", e);
            throw new AuthenticationException(
                    "Unable to validate your session. Please log in again",
                    ErrorCode.TOKEN_INVALID.getCode(),
                    e
            );
        }
    }
}