package lumina.snapshot.authservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response with HTTP status and custom error codes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    // HTTP status information
    private int status;              // e.g., 401, 403, 429
    private String error;            // e.g., "Unauthorized", "Forbidden"

    // Application-specific error details
    private String code;             // e.g., "INVALID_CREDENTIALS", "TOKEN_EXPIRED"
    private String message;          // User-friendly message
    private String detail;           // Technical details (optional)

    // Additional context
    private LocalDateTime timestamp;
    private String path;             // Request path where error occurred

    // Field-specific errors for validation
    private Map<String, String> fieldErrors;

    // Suggestions for resolution (optional)
    private String suggestion;
}
