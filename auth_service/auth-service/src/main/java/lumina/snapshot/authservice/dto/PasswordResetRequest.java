package lumina.snapshot.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * Request DTO for initiating password reset
 */
@Value
public class PasswordResetRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;
}
