package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Schema(
        description = "Boolean attribute content to store true/false values",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class BooleanAttributeContentV3 extends BaseAttributeContentV3<Boolean> {

    @Schema(description = "Boolean attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean data;

    @Schema(description = "Boolean attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    @Setter
    private AttributeContentType contentType = AttributeContentType.BOOLEAN;

    public BooleanAttributeContentV3() {
    }

    public BooleanAttributeContentV3(Boolean data) {
        this.data = data;
        setContentType(AttributeContentType.BOOLEAN);
    }

    public BooleanAttributeContentV3(String reference, Boolean data) {
        super(reference);
        this.data = data;
        setContentType(AttributeContentType.BOOLEAN);
    }

    @Override
    public Boolean getData() {
        return data;
    }

    @Override
    public void setData(Boolean data) {
        this.data = data;
    }
}
