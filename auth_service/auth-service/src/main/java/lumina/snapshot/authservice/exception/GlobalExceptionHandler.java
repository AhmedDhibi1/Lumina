package lumina.snapshot.authservice.exception;

import lombok.extern.slf4j.Slf4j;
import lumina.snapshot.authservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the auth service
 * Handles all exceptions and returns proper error responses
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage(), ex);
        
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", ex.getErrorCode());
        errorDetails.put("errorMessage", ex.getMessage());
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Authentication failed")
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle registration exceptions
     */
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleRegistrationException(RegistrationException ex) {
        log.error("Registration error: {}", ex.getMessage(), ex);
        
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", ex.getErrorCode());
        errorDetails.put("errorMessage", ex.getMessage());
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Registration failed")
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        errors.put("errorCode", "VALIDATION_ERROR");
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed. Please check your input.")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle HTTP client errors (from Keycloak communication)
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleHttpClientErrorException(
            HttpClientErrorException ex) {
        log.error("HTTP client error: {} - {}", ex.getStatusCode(), ex.getMessage());
        
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "HTTP_ERROR");
        errorDetails.put("statusCode", ex.getStatusCode().toString());
        
        String message = "External service error";
        if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            message = "Invalid credentials. Please check your email and password.";
            errorDetails.put("errorCode", "INVALID_CREDENTIALS");
        } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
            message = "User already exists with this email.";
            errorDetails.put("errorCode", "USER_EXISTS");
        }
        
        errorDetails.put("errorMessage", message);
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message(message)
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage(), ex);
        
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "INVALID_ARGUMENT");
        errorDetails.put("errorMessage", ex.getMessage());
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Invalid input provided")
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "INTERNAL_ERROR");
        errorDetails.put("errorMessage", "An unexpected error occurred. Please try again later.");
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Internal server error")
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
