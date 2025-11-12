package com.snapshot.lumina.businesslogic.domain.model.valueobject.permission;

import lombok.*;

@Value
@Builder
public class ActivityType {
    String type; // VIEWED, DOWNLOADED, SHARED, DELETED, UPDATED

    public ActivityType(String type) {
        this.type = type;
    }
}
