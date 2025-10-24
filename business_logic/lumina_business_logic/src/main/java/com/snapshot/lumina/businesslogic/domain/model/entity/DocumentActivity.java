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
}
