package com.snapshot.lumina.businesslogic.domain.exceptions.workspace;

public class WorkspaceDomainException extends RuntimeException {
    public WorkspaceDomainException(String message) {
        super(message);
    }

    public WorkspaceDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
