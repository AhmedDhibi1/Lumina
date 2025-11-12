package com.snapshot.lumina.businesslogic.domain.event.workspace;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class WorkspaceDeletedEvent extends WorkspaceEvent {
    private Timestamp deletedAt;
}
