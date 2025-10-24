package com.snapshot.lumina.businesslogic.domain.model.valueobject.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityType {
    private String type; // VIEWED, DOWNLOADED, SHARED, DELETED, UPDATED
}
