package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Schema(
        description = "Object attribute content for data with custom structure",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class ObjectAttributeContentV3 extends BaseAttributeContentV3<Serializable> {

    @Schema(description = "Object attribute content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private Serializable data;

    public ObjectAttributeContentV3() {
        setContentType(AttributeContentType.OBJECT);
    }

    public ObjectAttributeContentV3(Serializable data) {
        this.data = data;
        setContentType(AttributeContentType.OBJECT);
    }

    public ObjectAttributeContentV3(String reference, Serializable data) {
        super(reference);
        this.data = data;
        setContentType(AttributeContentType.OBJECT);
    }
}
