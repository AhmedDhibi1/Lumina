package com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace;

import com.snapshot.lumina.businesslogic.domain.exceptions.workspace.InvalidWorkspaceDescriptionException;
import lombok.*;

import java.util.regex.Pattern;

@Builder
@Value
public class Description {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 150;
    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    String value;

    public Description(String value) {
        this.value = value;
    }

    public static Description of(String desc) {
        return new Description(desc);
    }

    private void validate(String value) {
        if (value == null) {
            throw new InvalidWorkspaceDescriptionException(null, InvalidWorkspaceDescriptionException.ValidationReason.NULL);
        }

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            throw new InvalidWorkspaceDescriptionException(trimmedValue, InvalidWorkspaceDescriptionException.ValidationReason.EMPTY);
        }

        if (trimmedValue.length() < MIN_LENGTH) {
            throw new InvalidWorkspaceDescriptionException(trimmedValue, InvalidWorkspaceDescriptionException.ValidationReason.TOO_SHORT);
        }

        if (trimmedValue.length() > MAX_LENGTH) {
            throw new InvalidWorkspaceDescriptionException(trimmedValue, InvalidWorkspaceDescriptionException.ValidationReason.TOO_LONG);
        }

        if (!VALID_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidWorkspaceDescriptionException(trimmedValue, InvalidWorkspaceDescriptionException.ValidationReason.INVALID_FORMAT);
        }
    }
}
