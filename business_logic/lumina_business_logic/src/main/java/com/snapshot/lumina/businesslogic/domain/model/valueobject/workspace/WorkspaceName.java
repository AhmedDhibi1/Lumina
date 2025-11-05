package com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace;

import com.snapshot.lumina.businesslogic.domain.exceptions.workspace.InvalidWorkspaceNameException;
import lombok.*;

import java.util.regex.Pattern;

@Value
@Builder
public class WorkspaceName {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 16;
    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    String value;

    private WorkspaceName(String value) {
        validate(value);
        this.value = value;
    }

    public static WorkspaceName of(String value) {
        return new WorkspaceName(value);
    }

    private void validate(String value) {
        if (value == null) {
            throw new InvalidWorkspaceNameException(null, InvalidWorkspaceNameException.ValidationReason.NULL);
        }

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            throw new InvalidWorkspaceNameException(trimmedValue, InvalidWorkspaceNameException.ValidationReason.EMPTY);
        }

        if (trimmedValue.length() < MIN_LENGTH) {
            throw new InvalidWorkspaceNameException(trimmedValue, InvalidWorkspaceNameException.ValidationReason.TOO_SHORT);
        }

        if (trimmedValue.length() > MAX_LENGTH) {
            throw new InvalidWorkspaceNameException(trimmedValue, InvalidWorkspaceNameException.ValidationReason.TOO_LONG);
        }

        if (!VALID_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidWorkspaceNameException(trimmedValue, InvalidWorkspaceNameException.ValidationReason.INVALID_FORMAT);
        }
    }

    public boolean equalsIgnoreCase(WorkspaceName other) {
        return other != null && this.value.equalsIgnoreCase(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
