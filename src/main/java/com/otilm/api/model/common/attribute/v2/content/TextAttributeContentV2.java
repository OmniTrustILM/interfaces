package com.otilm.api.model.common.attribute.v2.content;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(
        description = "Text attribute content used to store longer formatted strings",
        type = "object")
public class TextAttributeContentV2 extends BaseAttributeContentV2<String> {

    @Schema(description = "Text attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    private String data;

    public TextAttributeContentV2() {
    }

    public TextAttributeContentV2(String data) {
        this.data = data;
    }

    public TextAttributeContentV2(String reference, String data) {
        super(reference);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextAttributeContentV2)) return false;
        if (!super.equals(o)) return false;
        TextAttributeContentV2 that = (TextAttributeContentV2) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
