package com.snapshot.lumina.businesslogic.domain.model.valueobject.common;

import lombok.Getter;

@Getter
public enum SortingOrder {
    ASC("ascending"),
    DESC("descending");

    private final String displayName;

    SortingOrder(String displayName) {
        this.displayName = displayName;
    }

    public SortingOrder reverse() {
        return this == ASC ? DESC : ASC;
    }

    @Override
    public String toString() {
        return name();
    }
}
