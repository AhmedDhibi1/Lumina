package com.snapshot.lumina.businesslogic.domain.model.valueobject.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Keywords {
    private List<String> values;
}
