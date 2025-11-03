package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;

@Value
@Builder
public class Filename {
    String value;


    // Validation in constructor
    private Filename(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("Filename too long");
        }
        this.value = value.trim();
    }

    // Factory method
    public static Filename of(String value) {
        return new Filename(value);
    }

    // Business methods (return new instances)
    public Filename withExtension(String extension) {
        return new Filename(value + "." + extension);
    }

    public String getExtension() {
        int dotIndex = value.lastIndexOf('.');
        return dotIndex > 0 ? value.substring(dotIndex + 1) : "";
    }
}
