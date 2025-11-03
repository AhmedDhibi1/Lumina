package com.snapshot.lumina.businesslogic.domain.model.valueobject.tagging;

import lombok.*;

@Value
@Builder
public class Color {
    String hexCode;

    public Color(String hexCode) {
        this.hexCode = hexCode;
    }
}
