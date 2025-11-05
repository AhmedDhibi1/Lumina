package com.snapshot.lumina.businesslogic.domain.exceptions.workspace;

public class InvalidWorkspaceNameException extends WorkspaceDomainException {

    private final String invalidValue;
    private final ValidationReason reason;

    public InvalidWorkspaceNameException(String invalidValue, ValidationReason reason) {
        super(buildMessage(invalidValue, reason));
        this.invalidValue = invalidValue;
        this.reason = reason;
    }

    private static String buildMessage(String invalidValue, ValidationReason reason) {
        return switch (reason) {
            case NULL -> "Workspace name cannot be null";
            case EMPTY -> "Workspace name cannot be empty";
            case TOO_SHORT -> String.format("Workspace name must be at least 8 characters, got: %d",
                    invalidValue != null ? invalidValue.length() : 0);
            case TOO_LONG -> String.format("Workspace name cannot exceed 16 characters, got: %d",
                    invalidValue.length());
            case INVALID_FORMAT -> String.format("Workspace name must contain only alphanumeric characters, got: '%s'",
                    invalidValue);
        };
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public ValidationReason getReason() {
        return reason;
    }

    public enum ValidationReason {
        NULL,
        EMPTY,
        TOO_SHORT,
        TOO_LONG,
        INVALID_FORMAT
    }
}
