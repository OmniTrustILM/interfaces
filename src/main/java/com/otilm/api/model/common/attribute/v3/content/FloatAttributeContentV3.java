package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(
        description = "Float attribute content for decimal numbers",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class FloatAttributeContentV3 extends BaseAttributeContentV3<Float> {

    @Schema(description = "Float attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float data;

    public FloatAttributeContentV3() {
        setContentType(AttributeContentType.FLOAT);
    }

    public FloatAttributeContentV3(Float data) {
        super(data.toString());
        this.data = data;
        setContentType(AttributeContentType.FLOAT);
    }

    public FloatAttributeContentV3(String reference, Float data) {
        super(reference);
        this.data = data;
        setContentType(AttributeContentType.FLOAT);
    }

}
