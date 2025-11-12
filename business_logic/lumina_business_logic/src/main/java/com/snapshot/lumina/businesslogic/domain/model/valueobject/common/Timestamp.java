package com.snapshot.lumina.businesslogic.domain.model.valueobject.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timestamp {
    private LocalDateTime value;
    public static Timestamp now() {
        return new Timestamp(LocalDateTime.now());
    }

    public static Timestamp of(LocalDateTime value) {
        Objects.requireNonNull(value, "Timestamp cannot be null");
        return new Timestamp(value);
    }

    public boolean isBefore(Timestamp other) {
        return this.value.isBefore(other.value);
    }

    public boolean isAfter(Timestamp other) {
        return this.value.isAfter(other.value);
    }
}
