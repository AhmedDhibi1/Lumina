package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTitle {
    private String value;
}
