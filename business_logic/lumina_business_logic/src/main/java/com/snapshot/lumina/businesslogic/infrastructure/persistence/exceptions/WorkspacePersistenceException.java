package com.snapshot.lumina.businesslogic.infrastructure.persistence.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

public class WorkspacePersistenceException extends RuntimeException {
    public WorkspacePersistenceException(String message, Exception e) {
        super(message);
    }
    public WorkspacePersistenceException(String message) {
        super(message);
    }
}
