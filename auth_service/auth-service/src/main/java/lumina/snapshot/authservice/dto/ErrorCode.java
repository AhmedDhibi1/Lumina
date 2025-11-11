package lumina.snapshot.authservice.dto;

public enum ErrorCode {

    // Authentication errors (401)
    INVALID_CREDENTIALS("AUTH001", "Invalid email or password"),
    TOKEN_EXPIRED("AUTH002", "Your session has expired. Please log in again"),
    TOKEN_INVALID("AUTH003", "Invalid authentication token"),
    UNAUTHORIZED("AUTH004", "You are not authorized to access this resource"),

    // Authorization errors (403)
    FORBIDDEN("AUTH005", "You don't have permission to perform this action"),
    ACCOUNT_DISABLED("AUTH006", "Your account has been disabled"),
    EMAIL_NOT_VERIFIED("AUTH007", "Please verify your email before accessing this resource"),

    // Registration errors (400/409)
    EMAIL_ALREADY_EXISTS("REG001", "An account with this email already exists"),
    WEAK_PASSWORD("REG002", "Password does not meet security requirements"),
    INVALID_EMAIL_FORMAT("REG003", "Please provide a valid email address"),
    REGISTRATION_FAILED("REG004", "Registration failed. Please try again"),

    // Profile errors (400)
    INVALID_CURRENT_PASSWORD("PROF001", "Current password is incorrect"),
    PASSWORD_CHANGE_FAILED("PROF002", "Failed to change password"),
    PROFILE_UPDATE_FAILED("PROF003", "Failed to update profile"),

    // Rate limiting (429)
    RATE_LIMIT_EXCEEDED("RATE001", "Too many requests. Please try again later"),

    // Validation errors (400)
    VALIDATION_ERROR("VAL001", "Please check your input and try again"),
    MISSING_REQUIRED_FIELD("VAL002", "Required field is missing"),

    // Server errors (500)
    INTERNAL_ERROR("SYS001", "An unexpected error occurred. Please try again later"),
    SERVICE_UNAVAILABLE("SYS002", "Service is temporarily unavailable");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}