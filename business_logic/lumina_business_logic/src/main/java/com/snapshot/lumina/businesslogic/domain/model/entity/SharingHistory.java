package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.ShareId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.permission.Permission;

import java.util.Objects;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharingHistory {
    private ShareId shareId;
    private DocumentId documentId;
    private UserId sharedBy;
    private UserId sharedWith;
    private Permission permissions;
    private Timestamp sharedAt;
    private Timestamp revokedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SharingHistory that)) return false;
        return Objects.equals(shareId, that.shareId);  // ✅ Null-safe
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(shareId);  // ✅ Null-safe
    }
}
