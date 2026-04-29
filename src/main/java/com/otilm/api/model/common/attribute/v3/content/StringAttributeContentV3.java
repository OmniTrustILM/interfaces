package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(
        description = "String attribute content",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class StringAttributeContentV3 extends BaseAttributeContentV3<String> {

    @Schema(description = "String attribute value", requiredMode = Schema.RequiredMode.REQUIRED)
    private String data;

    public StringAttributeContentV3() {
        setContentType(AttributeContentType.STRING);
    }

    public StringAttributeContentV3(String data) {
        super(data);
        this.data = data;
        setContentType(AttributeContentType.STRING);
    }

    public StringAttributeContentV3(String reference, String data) {
        super(reference);
        this.data = data;
        setContentType(AttributeContentType.STRING);
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }
}
