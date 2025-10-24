package com.snapshot.lumina.businesslogic.domain.model.valueobject.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Email {
    private String value;
}
