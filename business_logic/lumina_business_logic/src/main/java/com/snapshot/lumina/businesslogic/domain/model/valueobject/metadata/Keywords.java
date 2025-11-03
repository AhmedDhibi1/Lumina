package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.*;

import java.util.List;

@Builder
@Value
public class Keywords {
    List<String> values;

    public Keywords(List<String> values) {
        this.values = values;
    }
}
