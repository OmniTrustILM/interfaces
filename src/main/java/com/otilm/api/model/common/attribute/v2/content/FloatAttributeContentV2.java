package com.otilm.api.model.common.attribute.v2.content;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(
        description = "Float attribute content for decimal numbers",
        type = "object")
public class FloatAttributeContentV2 extends BaseAttributeContentV2<Float> {

    @Schema(description = "Float attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float data;

    public FloatAttributeContentV2() {

    }

    public FloatAttributeContentV2(Float data) {
        super(data.toString());
        this.data = data;
    }

    public FloatAttributeContentV2(String reference, Float data) {
        super(reference);
        this.data = data;
    }

    public Float getData() {
        return data;
    }

    public void setData(Float data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FloatAttributeContentV2)) return false;
        if (!super.equals(o)) return false;
        FloatAttributeContentV2 that = (FloatAttributeContentV2) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
