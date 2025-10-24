package com.snapshot.lumina.businesslogic.domain.model.valueobject.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timestamp {
    private LocalDateTime value;
}
