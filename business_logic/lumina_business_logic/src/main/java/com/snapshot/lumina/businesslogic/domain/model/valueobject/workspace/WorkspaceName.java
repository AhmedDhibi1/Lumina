package com.snapshot.lumina.businesslogic.domain.model.valueobject.workspace;

import lombok.*;

@Value
@Builder
public class WorkspaceName {
    String value;

    public WorkspaceName(String value) {
        this.value = value;
    }
}
