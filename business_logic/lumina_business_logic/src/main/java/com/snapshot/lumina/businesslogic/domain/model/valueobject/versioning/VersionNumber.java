package com.snapshot.lumina.businesslogic.domain.model.valueobject.versioning;

import lombok.*;

@Value
@Builder
public class VersionNumber {
    Integer number;

    public VersionNumber(Integer number) {
        this.number = number;
    }
}
