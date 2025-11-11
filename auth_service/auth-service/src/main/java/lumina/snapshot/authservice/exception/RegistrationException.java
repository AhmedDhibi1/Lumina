package lumina.snapshot.authservice.exception;

/**
 * Custom exception for user registration errors
 */
public class RegistrationException extends RuntimeException {
    
    private final String errorCode;
    
    public RegistrationException(String message) {
        super(message);
        this.errorCode = "REGISTRATION_ERROR";
    }
    
    public RegistrationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "REGISTRATION_ERROR";
    }
    
    public RegistrationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
