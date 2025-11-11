package lumina.snapshot.authservice.exception;

import lumina.snapshot.authservice.dto.ErrorCode;

public class AuthenticationException extends RuntimeException {

    private final String errorCode;

    public AuthenticationException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNAUTHORIZED.getCode();
    }

    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode.getCode();
    }

    public AuthenticationException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode.getCode();
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.UNAUTHORIZED.getCode();
    }

    public AuthenticationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
