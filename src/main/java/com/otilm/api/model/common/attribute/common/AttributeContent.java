package com.otilm.api.model.common.attribute.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.otilm.api.config.serializer.AttributeContentDeserializer;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(description = "Attribute Content", type = "object",
        oneOf = {BaseAttributeContentV2.class, BaseAttributeContentV3.class}

)
@JsonDeserialize(using = AttributeContentDeserializer.class)
@JsonPropertyOrder({"reference", "data", "contentType"})
public abstract class AttributeContent implements Serializable {

    public abstract String getReference();

    public abstract <T> T getData();

    public abstract AttributeContentType getContentType();

}
