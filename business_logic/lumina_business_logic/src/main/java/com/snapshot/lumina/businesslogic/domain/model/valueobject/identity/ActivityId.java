package com.snapshot.lumina.businesslogic.domain.model.valueobject.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ActivityId {
    private UUID id;
}
