package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    private String value;
}
