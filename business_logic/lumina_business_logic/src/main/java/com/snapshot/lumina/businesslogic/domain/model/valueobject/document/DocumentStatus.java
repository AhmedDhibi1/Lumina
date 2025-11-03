package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;

import java.util.Set;

@Value
@Builder
public class DocumentStatus {
    String status; // UPLOADED, PROCESSING, INDEXED, FAILED
    /*public DocumentStatus(String status) {
        this.status = status;
    }*/
    private static final Set<String> VALID_STATUSES =
            Set.of("pending", "processing", "indexed", "failed", "archived");

    private DocumentStatus(String status) {
        String normalized = status.toLowerCase().trim();
        if (!VALID_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        this.status = normalized;
    }

    public static DocumentStatus pending() {
        return new DocumentStatus("pending");
    }

    public static DocumentStatus processing() {
        return new DocumentStatus("processing");
    }

    public static DocumentStatus indexed() {
        return new DocumentStatus("indexed");
    }

    public static DocumentStatus failed() {
        return new DocumentStatus("failed");
    }

    public static DocumentStatus archived() {
        return new DocumentStatus("archived");
    }

    public boolean canTransitionTo(DocumentStatus newStatus) {
        return switch (this.status) {
            case "pending" -> newStatus.status.equals("processing");
            case "processing" -> Set.of("indexed", "failed").contains(newStatus.status);
            case "failed" -> newStatus.status.equals("processing");
            case "indexed" -> newStatus.status.equals("archived");
            case "archived" -> false;
            default -> false;
        };
    }

    public void validateTransition(DocumentStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Cannot transition from " + this.status + " to " + newStatus.status);
        }
    }

    public boolean isPending() {
        return this.status.equals("pending");
    }

    public boolean isProcessing() {
        return this.status.equals("processing");
    }
    public boolean isArchived() {
        return this.status.equals("archived");
    }

    public boolean isFailed() {
        return this.status.equals("failed");
    }

    public boolean isIndexed() {
        return this.status.equals("indexed");
    }

}
