package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.*;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.common.Timestamp;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.DocumentId;
import com.snapshot.lumina.businesslogic.domain.model.valueobject.identity.UserId;

import java.util.List;

@Value
@Builder

public class ActivityAnalytics {
    DocumentId documentId;
    Integer totalViews;
    Integer totalDownloads;
    Integer totalShares;
    List<UserId> topAccessors;
    Timestamp lastAccessed;
    Timestamp createdAt;

    public ActivityAnalytics(
            DocumentId documentId,
            Integer totalViews,
            Integer totalShares,
            List<UserId> topAccessors,
            Timestamp lastAccessed,
            Integer totalDownloads,
            Timestamp createdAt
    ) {
        this.documentId = documentId;
        this.totalViews = totalViews;
        this.totalShares = totalShares;
        this.totalDownloads = totalDownloads;
        this.topAccessors = topAccessors;
        this.lastAccessed = lastAccessed;
        this.createdAt = createdAt;
    }
}
