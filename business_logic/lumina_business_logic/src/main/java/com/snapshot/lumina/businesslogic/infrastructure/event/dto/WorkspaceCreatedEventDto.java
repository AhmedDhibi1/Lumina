package com.snapshot.lumina.businesslogic.infrastructure.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceCreatedEventDto {

    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("workspace_id")
    private UUID workspaceId;

    @JsonProperty("workspace_name")
    private String workspaceName;

    @JsonProperty("created_by")
    private UUID createdBy;


    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("occurred_on")
    private LocalDateTime occurredAt;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("event_version")
    private int eventVersion;
}
