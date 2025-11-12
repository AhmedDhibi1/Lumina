package com.snapshot.lumina.businesslogic.domain.model.valueobject.document;

import lombok.*;

@Value
@Builder
public class EmbeddingModel {
    private String modelName;

    public EmbeddingModel(String modelName) {
        this.modelName = modelName;
    }
}
