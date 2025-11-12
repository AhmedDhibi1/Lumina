package lumina.snapshot.authservice.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lumina.snapshot.authservice.dto.LoginRequest;
import lumina.snapshot.authservice.dto.RegisterRequest;
import lumina.snapshot.authservice.dto.TokenResponse;
import lumina.snapshot.authservice.dto.UserInfoResponse;
import lumina.snapshot.authservice.exception.AuthenticationException;
import lumina.snapshot.authservice.exception.RegistrationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final Keycloak keycloakClient;
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
    public TokenResponse login(LoginRequest request) {
        log.debug("Attempting login for user: {}", request.getEmail());

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", request.getEmail());  // Use email as username
        formData.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, entity, Map.class);

            Map body = response.getBody();

            assert body != null;

            log.info("Login successful for user: {}", request.getEmail());

            return TokenResponse.builder()
                    .accessToken((String) body.get("access_token"))
                    .refreshToken((String) body.get("refresh_token"))
                    .tokenType((String) body.get("token_type"))
                    .expiresIn((Integer) body.get("expires_in"))
                    .refreshExpiresIn((Integer) body.get("refresh_expires_in"))
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getEmail(), e);
            throw new AuthenticationException("Invalid email or password. Please check your credentials.", "INVALID_CREDENTIALS");
        }
    }

    @Override
    public void register(RegisterRequest request) {
        log.debug("Attempting registration for user: {}", request.getEmail());

        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getEmail());  // Use email as username
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(false);  // Require email verification

            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));

            // Create user in Keycloak
            keycloakClient.realm(realm).users().create(user);

            log.info("User registered successfully: {}", request.getEmail());

        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getEmail(), e);
            if (e.getMessage() != null && e.getMessage().contains("409")) {
                throw new RegistrationException("User with this email already exists.", "USER_EXISTS");
            }
            throw new RegistrationException("Registration failed. Please try again.", "REGISTRATION_ERROR");
        }
    }

    @Override
    public void logout(String refreshToken) {
        log.debug("Attempting logout");

        String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout",
                authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        try {
            restTemplate.exchange(logoutUrl, HttpMethod.POST, entity, Void.class);
            log.info("Logout successful");
        } catch (Exception e) {
            log.warn("Logout failed, but proceeding with client-side cleanup", e);
        }
    }

    @Override
    public UserInfoResponse getUserInfo(String accessToken) {
        String userInfoUrl = String.format("%s/realms/%s/protocol/openid-connect/userinfo",
                authServerUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> body = response.getBody();

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
            throw new AuthenticationException("Failed to get user info");
        }
    }
}