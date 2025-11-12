package com.snapshot.lumina.businesslogic.domain.event.document;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.permission.Permission;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentSharedEvent extends DocumentEvent {
    private UserId sharedBy;
    private UserId sharedWith;
    private List<Permission> permissions;
    private Timestamp sharedAt;
}
