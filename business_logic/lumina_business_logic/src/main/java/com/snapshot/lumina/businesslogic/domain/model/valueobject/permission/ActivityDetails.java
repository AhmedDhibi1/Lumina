package com.snapshot.lumina.businesslogic.domain.model.valueobject.permission;

import lombok.*;

@Value
@Builder
public class ActivityDetails {
    String jsonData;

    public ActivityDetails(String jsonData) {
        this.jsonData = jsonData;
    }
}
