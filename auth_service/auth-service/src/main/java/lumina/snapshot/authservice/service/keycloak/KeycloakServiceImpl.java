package lumina.snapshot.authservice.service.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lumina.snapshot.authservice.exception.AuthenticationException;
import lumina.snapshot.authservice.exception.RegistrationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of KeycloakService for user management operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {
    
    private final Keycloak keycloakClient;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Override
    public UserRepresentation getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        
        try {
            List<UserRepresentation> users = keycloakClient.realm(realm)
                    .users()
                    .search(null, null, null, email, 0, 1);
            
            if (users.isEmpty()) {
                log.warn("User not found with email: {}", email);
                throw new AuthenticationException("User not found with email: " + email, "USER_NOT_FOUND");
            }
            
            log.debug("User found with email: {}", email);
            return users.get(0);
            
        } catch (Exception e) {
            log.error("Error fetching user by email: {}", email, e);
            throw new AuthenticationException("Failed to fetch user: " + e.getMessage(), "USER_FETCH_ERROR", e);
        }
    }
    
    @Override
    public UserRepresentation getUserById(String userId) {
        log.debug("Fetching user by ID: {}", userId);
        
        try {
            UserRepresentation user = keycloakClient.realm(realm)
                    .users()
                    .get(userId)
                    .toRepresentation();
            
            log.debug("User found with ID: {}", userId);
            return user;
            
        } catch (Exception e) {
            log.error("Error fetching user by ID: {}", userId, e);
            throw new AuthenticationException("Failed to fetch user: " + e.getMessage(), "USER_FETCH_ERROR", e);
        }
    }
    
    @Override
    public void updateUser(String userId, UserRepresentation user) {
        log.info("Updating user with ID: {}", userId);
        
        try {
            UserResource userResource = keycloakClient.realm(realm)
                    .users()
                    .get(userId);
            
            userResource.update(user);
            
            log.info("User updated successfully: {}", userId);
            
        } catch (Exception e) {
            log.error("Error updating user: {}", userId, e);
            throw new AuthenticationException("Failed to update user: " + e.getMessage(), "USER_UPDATE_ERROR", e);
        }
    }
    
    @Override
    public void resetPassword(String userId, String newPassword) {
        log.info("Resetting password for user: {}", userId);
        
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);
            
            keycloakClient.realm(realm)
                    .users()
                    .get(userId)
                    .resetPassword(credential);
            
            log.info("Password reset successfully for user: {}", userId);
            
        } catch (Exception e) {
            log.error("Error resetting password for user: {}", userId, e);
            throw new AuthenticationException("Failed to reset password: " + e.getMessage(), "PASSWORD_RESET_ERROR", e);
        }
    }
    
    @Override
    public void sendPasswordResetEmail(String userId) {
        log.info("Sending password reset email to user: {}", userId);
        
        try {
            keycloakClient.realm(realm)
                    .users()
                    .get(userId)
                    .executeActionsEmail(Collections.singletonList("UPDATE_PASSWORD"));
            
            log.info("Password reset email sent successfully to user: {}", userId);
            
        } catch (Exception e) {
            log.error("Error sending password reset email to user: {}", userId, e);
            throw new AuthenticationException("Failed to send password reset email: " + e.getMessage(), 
                    "PASSWORD_RESET_EMAIL_ERROR", e);
        }
    }
    
    @Override
    public boolean userExistsByEmail(String email) {
        log.debug("Checking if user exists with email: {}", email);
        
        try {
            List<UserRepresentation> users = keycloakClient.realm(realm)
                    .users()
                    .search(null, null, null, email, 0, 1);
            
            boolean exists = !users.isEmpty();
            log.debug("User exists with email {}: {}", email, exists);
            return exists;
            
        } catch (Exception e) {
            log.error("Error checking if user exists with email: {}", email, e);
            return false;
        }
    }
}
