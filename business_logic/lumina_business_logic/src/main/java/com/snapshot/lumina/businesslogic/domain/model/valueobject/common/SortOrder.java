package com.snapshot.lumina.businesslogic.domain.model.valueobject.common;


enum SortOrder {
    ASC("ascending"),
    DESC("descending");

    private final String displayName;

    SortOrder(String displayName) {
        this.displayName = displayName;
    }

    public SortOrder reverse() {
        return this == ASC ? DESC : ASC;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return name();
    }
}
