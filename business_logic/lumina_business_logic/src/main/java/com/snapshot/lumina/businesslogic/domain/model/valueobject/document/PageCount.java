package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;

@Value
@Builder
public class PageCount {
    Integer count;

    public PageCount(Integer count) {
        this.count = count;
    }
}
