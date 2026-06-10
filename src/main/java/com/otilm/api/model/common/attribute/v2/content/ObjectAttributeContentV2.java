package com.otilm.api.model.common.attribute.v2.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Schema(
        description = "Object attribute content for data with custom structure",
        type = "object")
public class ObjectAttributeContentV2 extends BaseAttributeContentV2<Serializable> {

    @Schema(description = "Object attribute content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private Serializable data;

    public ObjectAttributeContentV2() {

    }

    public ObjectAttributeContentV2(Serializable data) {
        this.data = data;
    }

    public ObjectAttributeContentV2(String reference, Serializable data) {
        super(reference);
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectAttributeContentV2 that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
