package com.snapshot.lumina.businesslogic.domain.model.valueobject.tagging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagName {
    private String value;
}
