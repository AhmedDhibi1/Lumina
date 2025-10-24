package com.snapshot.lumina.businesslogic.domain.model.valueobject.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenCount {
    private Integer count;
}
