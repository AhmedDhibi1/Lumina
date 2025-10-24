package com.snapshot.lumina.businesslogic.domain.event.workspace;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WorkspaceDeletedEvent extends WorkspaceEvent {
    private Timestamp deletedAt;
}
