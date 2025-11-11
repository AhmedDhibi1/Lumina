package lumina.snapshot.authservice.service.token;

import lumina.snapshot.authservice.dto.TokenResponse;
import lumina.snapshot.authservice.dto.UserInfoResponse;

/**
 * Service interface for token operations
 */
public interface TokenService {
    /**
     * Refresh an access token using a refresh token
     * @param refreshToken the refresh token
     * @return new token response with updated tokens
     */
    TokenResponse refreshToken(String refreshToken);
    
    /**
     * Get user information from an access token
     * @param accessToken the access token
     * @return user information
     */
    UserInfoResponse getUserInfo(String accessToken);
}
