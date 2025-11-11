package lumina.snapshot.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Value;

/**
 * Request DTO for updating user profile information
 */
@Value
public class UpdateUserRequest {
    @Email(message = "Email must be valid")
    String email;

    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    String lastName;
}
