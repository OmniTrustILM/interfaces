package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(
        description = "Text attribute content used to store longer formatted strings",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class TextAttributeContentV3 extends BaseAttributeContentV3<String> {

    @Schema(description = "Text attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    private String data;

    public TextAttributeContentV3() {
        setContentType(AttributeContentType.TEXT);
    }

    public TextAttributeContentV3(String data) {
        this.data = data;
        setContentType(AttributeContentType.TEXT);
    }

    public TextAttributeContentV3(String reference, String data) {
        super(reference);
        this.data = data;
        setContentType(AttributeContentType.TEXT);
    }
}
