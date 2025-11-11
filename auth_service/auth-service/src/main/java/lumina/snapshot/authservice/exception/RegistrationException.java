package lumina.snapshot.authservice.exception;

import lumina.snapshot.authservice.dto.ErrorCode;

public class RegistrationException extends RuntimeException {

    private final String errorCode;

    public RegistrationException(String message) {
        super(message);
        this.errorCode = ErrorCode.REGISTRATION_FAILED.getCode();
    }

    public RegistrationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RegistrationException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode.getCode();
    }

    public RegistrationException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode.getCode();
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.REGISTRATION_FAILED.getCode();
    }

    public RegistrationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
