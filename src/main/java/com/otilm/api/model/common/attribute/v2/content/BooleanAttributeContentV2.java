package com.otilm.api.model.common.attribute.v2.content;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(
        description = "Boolean attribute content to store true/false values",
        type = "object")
public class BooleanAttributeContentV2 extends BaseAttributeContentV2<Boolean> {

    @Schema(description = "Boolean attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean data;


    public BooleanAttributeContentV2() {
    }

    public BooleanAttributeContentV2(Boolean data) {
        this.data = data;
    }

    public BooleanAttributeContentV2(String reference, Boolean data) {
        super(reference);
        this.data = data;
    }

    @Override
    public Boolean getData() {
        return data;
    }

    @Override
    public void setData(Boolean data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanAttributeContentV2)) return false;
        if (!super.equals(o)) return false;
        BooleanAttributeContentV2 that = (BooleanAttributeContentV2) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }

}
