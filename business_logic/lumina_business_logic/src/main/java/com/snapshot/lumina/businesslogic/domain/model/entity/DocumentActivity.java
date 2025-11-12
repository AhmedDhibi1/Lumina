package com.snapshot.lumina.businesslogic.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.ActivityId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.permission.ActivityDetails;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.permission.ActivityType;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentActivity {
    private ActivityId activityId;
    private DocumentId documentId;
    private UserId userId;
    private ActivityType activityType;
    private ActivityDetails details;
    private Timestamp timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentActivity that)) return false;
        return Objects.equals(activityId, that.activityId);  // ✅ Null-safe
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(activityId);  // ✅ Null-safe
    }
}
