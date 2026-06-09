package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

@Schema(
        description = "Integer attribute content for integer numbers",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class IntegerAttributeContentV3 extends BaseAttributeContentV3<Integer> {

    @Schema(description = "Integer attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer data;

    public IntegerAttributeContentV3() {
    }

    public IntegerAttributeContentV3(Integer data) {
        this.data = data;
        setContentType(AttributeContentType.INTEGER);
    }

    public IntegerAttributeContentV3(String reference, Integer data) {
        super(reference);
        this.data = data;
        setContentType(AttributeContentType.INTEGER);
    }

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public void setData(Integer data) {
        this.data = data;
    }
}
