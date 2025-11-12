package com.snapshot.lumina.businesslogic.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Validation Errors (4xxx)
    VALIDATION_ERROR("VALIDATION_ERROR", "Invalid input data"),
    INVALID_WORKSPACE_NAME("INVALID_WORKSPACE_NAME", "Invalid workspace name"),
    INVALID_DESCRIPTION("INVALID_DESCRIPTION", "Invalid description"),

    // Business Logic Errors (5xxx)
    DUPLICATE_WORKSPACE("DUPLICATE_WORKSPACE", "Workspace already exists"),
    WORKSPACE_NOT_FOUND("WORKSPACE_NOT_FOUND", "Workspace not found"),

    // Infrastructure Errors (6xxx)
    DATABASE_ERROR("DATABASE_ERROR", "Database operation failed"),
    CACHE_ERROR("CACHE_ERROR", "Cache operation failed"),

    // System Errors (9xxx)
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal server error"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "Service temporarily unavailable");

    private final String code;
    private final String message;
}
