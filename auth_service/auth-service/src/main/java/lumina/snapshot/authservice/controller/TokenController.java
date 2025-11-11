package lumina.snapshot.authservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lumina.snapshot.authservice.dto.ApiResponse;
import lumina.snapshot.authservice.dto.TokenResponse;
import lumina.snapshot.authservice.dto.UserInfoResponse;
import lumina.snapshot.authservice.exception.AuthenticationException;
import lumina.snapshot.authservice.service.token.TokenService;
import lumina.snapshot.authservice.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<UserInfoResponse>> refresh(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {

        log.info("Token refresh request");

        try {
            TokenResponse tokenResponse = tokenService.refreshToken(refreshToken);

            // Update cookies with new tokens
            cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
            cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());

            // Get updated user info
            UserInfoResponse userInfo = tokenService.getUserInfo(tokenResponse.getAccessToken());

            log.info("Token refresh successful");

            return ResponseEntity.ok(ApiResponse.<UserInfoResponse>builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .data(userInfo)
                    .build());

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            // Clear invalid cookies
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);
            throw new AuthenticationException("Token refresh failed: " + e.getMessage());
        }
    }
}