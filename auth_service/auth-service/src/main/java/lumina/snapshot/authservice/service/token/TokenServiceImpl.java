package lumina.snapshot.authservice.service.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lumina.snapshot.authservice.dto.TokenResponse;
import lumina.snapshot.authservice.dto.UserInfoResponse;
import lumina.snapshot.authservice.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Implementation of TokenService for token operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        log.debug("Refreshing access token");

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, entity, Map.class);

            Map body = response.getBody();

            if (body == null) {
                throw new AuthenticationException("Empty response from token refresh", "TOKEN_REFRESH_ERROR");
            }

            log.info("Token refresh successful");

            return TokenResponse.builder()
                    .accessToken((String) body.get("access_token"))
                    .refreshToken((String) body.get("refresh_token"))
                    .tokenType((String) body.get("token_type"))
                    .expiresIn((Integer) body.get("expires_in"))
                    .refreshExpiresIn((Integer) body.get("refresh_expires_in"))
                    .build();

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new AuthenticationException("Token refresh failed: " + e.getMessage(), "TOKEN_REFRESH_ERROR", e);
        }
    }

    @Override
    public UserInfoResponse getUserInfo(String accessToken) {
        log.debug("Fetching user info from access token");

        String userInfoUrl = String.format("%s/realms/%s/protocol/openid-connect/userinfo",
                authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> body = response.getBody();

            if (body == null) {
                throw new AuthenticationException("Empty response from user info endpoint", "USER_INFO_ERROR");
            }

            log.debug("User info retrieved successfully");

            return UserInfoResponse.builder()
                    .userId((String) body.get("sub"))
                    .username((String) body.get("preferred_username"))
                    .email((String) body.get("email"))
                    .firstName((String) body.get("given_name"))
                    .lastName((String) body.get("family_name"))
                    .emailVerified((Boolean) body.get("email_verified"))
                    .build();

        } catch (Exception e) {
            log.error("Failed to get user info", e);
            throw new AuthenticationException("Failed to get user info: " + e.getMessage(), "USER_INFO_ERROR", e);
        }
    }
}
