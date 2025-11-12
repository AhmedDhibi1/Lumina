package lumina.snapshot.authservice.service.keycloak;

import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

/**
 * Service interface for Keycloak user management operations
 */
public interface KeycloakService {
    /**
     * Get user by email
     * @param email user email
     * @return user representation
     */
    UserRepresentation getUserByEmail(String email);

    /**
     * Get user by user ID
     * @param userId Keycloak user ID
     * @return user representation
     */
    UserRepresentation getUserById(String userId);

    /**
     * Update user details
     * @param userId Keycloak user ID
     * @param user updated user representation
     */
    void updateUser(String userId, UserRepresentation user);

    /**
     * Reset user password
     * @param userId Keycloak user ID
     * @param newPassword new password
     */
    void resetPassword(String userId, String newPassword);

    /**
     * Send password reset email
     * @param userId Keycloak user ID
     */
    void sendPasswordResetEmail(String userId);

    /**
     * Check if user exists by email
     * @param email user email
     * @return true if user exists
     */
    boolean userExistsByEmail(String email);
}
