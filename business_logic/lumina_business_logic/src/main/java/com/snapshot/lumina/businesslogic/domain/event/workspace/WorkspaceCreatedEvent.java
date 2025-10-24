package com.snapshot.lumina.businesslogic.domain.event.workspace;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkspaceCreatedEvent extends WorkspaceEvent {
    private Timestamp createdAt;
}
